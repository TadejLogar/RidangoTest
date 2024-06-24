package com.ridango.transit.schedule;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.ridango.transit.schedule.model.Calendar;
import com.ridango.transit.schedule.model.*;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class BusSchedule {
    private static final String DIR = "data/gtfs/";
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private GtfsData data = new GtfsData();

    /**
     * Inicializira podatke (prebere GTFS podatke iz datotek) in podatke shrani v obliki za hitrejšo poizvedbo.
     * Če se zaganja na strežniku se lahko ustvari singelton tega objekta in se pokliče to metodo samo enkrat.
     */
    public void initialize() throws Exception {
        initializeFromCsv();
    }

    private void initializeFromCsv() throws Exception {
        // V com.ridango.transit.busTrip.model so definirani razredi za vse tabele/csv datotek.
        // Na tem mestu se preberejo tisti, ki jih potrebujemo za postanke naslednjih avtobusov na določeni postaji.
        List<Calendar> services = readCsv(new File(DIR + "calendar.txt"), Calendar.class);
        List<Trip> trips = readCsv(new File(DIR + "trips.txt"), Trip.class);
        List<Stop> stops = readCsv(new File(DIR + "stops.txt"), Stop.class);
        List<StopTime> stopTimes = readCsv(new File(DIR + "stop_times.txt"), StopTime.class);

        data.servicesMap = services.stream().collect(Collectors.toMap(
                key -> key.serviceId,
                value -> value
        ));
        data.tripsMap = trips.stream().collect(Collectors.toMap(
                key -> key.tripId,
                value -> value
        ));
        data.stopsMap = stops.stream().collect(Collectors.toMap(
                key -> key.stopId,
                value -> value
        ));

        // Mapira "stop time" seznam s "trips" objektom (zapiše se samo referenca, zato da se pazi na porabo pomnilnika)
        stopTimes.forEach(it -> {
            it.trip = data.tripsMap.get(it.tripId);
            it.tripId = null; // Odstrani tripId - v "stop times" ta podatek zasede največ pomnila in lahko to vrednosti še vedno dostopamo preko it.trip.tripId
        });

        // Naredi map "stop times", kjer je ključ "serviceId_stopId" za hitrejše iskanje po voznih redih
        data.stopTimesMap = new HashMap<>();
        for (var stopTime : stopTimes) {
            String key = stopTime.getServiceId() + "_" + stopTime.stopId;
            List<StopTime> list;
            if (data.stopTimesMap.containsKey(key)) {
                list = data.stopTimesMap.get(key);
            } else {
                list = new ArrayList<>();
                data.stopTimesMap.put(key, list);
            }
            list.add(stopTime);
        }

        // Sortira "stop times" po času prihoda za hitrejšo poizvedbo pri iskanju voznega reda
        for (String key : data.stopTimesMap.keySet()) {
            data.stopTimesMap.get(key).sort(Comparator.comparing(stopTime -> stopTime.arrivalTime));
        }
    }

    /**
     * @param stopId       id postaje
     * @param limit        število naslednjih avtobusov
     * @param fromDateTime začetni datum čas
     * @param toDateTime   končni datum čas (max 24 ur za fromDateTime)
     * @return
     * @throws Exception
     */
    public BusStopInfo nextBusStops(int stopId, int limit, LocalDateTime fromDateTime, LocalDateTime toDateTime) throws Exception {
        Stop stop = data.stopsMap.getOrDefault(stopId, null);
        if (stop == null) {
            throw new Exception("Stop does not exists");
        }
        Duration duration = Duration.between(fromDateTime, toDateTime);
        if (duration.toHours() >= 24) {
            throw new Exception("The duration between the start time and the end time is more than 24 hours");
        }

        LocalDate fromDate = fromDateTime.toLocalDate();
        LocalDate toDate = toDateTime.toLocalDate();

        LocalTime fromTime = fromDateTime.toLocalTime();
        LocalTime toTime = toDateTime.toLocalTime();

        BusStopInfo busStopInfo = new BusStopInfo(stop, fromDateTime.toLocalTime(), limit);

        Collection<Calendar> allServices = data.servicesMap.values();
        List<Calendar> services = allServices.stream()
                .filter(it -> (it.startDate.isAfter(fromDate) || it.startDate.equals(fromDate)) && (it.endDate.isAfter(toDate) || it.endDate.equals(toDate))) // filtrira po obdobju
                .filter(it -> isActive(fromDateTime, toDateTime, it)) // obdobje dodatno filtrira glede na dan vožnje (če je končni datum naslednji dan, se preveri v service/calendar preveri za današnji in jutrišnji dan)
                .toList();
        if (services.isEmpty()) {
            // throw new Exception("Invalid date range (calander/service does not exist)");

            // Dodano samo za potrebe te naloge. Podatki so samo za preteklo obdobje. Če i podatkov na trenutno obdobje vzame prvi service/calendar.
            Calendar service = allServices.stream().findFirst().orElse(null);
            services = Arrays.asList(service);
        }

        // Vsi "stop timi" za dani service/calander in id postaje
        List<StopTime> stopTimes = new ArrayList<>();
        for (Calendar service : services) {
            stopTimes.addAll(data.stopTimesMap.get(service.serviceId + "_" + stopId));
        }

        for (StopTime stopTime : stopTimes) {
            LocalDate arrivalDate = fromDate; // datum prihoda je trenutni dan
            if (toTime.isBefore(fromTime) && stopTime.arrivalTime.isBefore(fromTime)) { // naslednji dan && čas prihoda je naslednji dan
                arrivalDate = toDate; // nastavi datum prihoda na naslednji dan
            }

            // preveri ali je čas prihoda v obdobju od fromTime do toTime &&
            // preveri na kateri dan v tednu pade čas prihoda in ali je calendar/service aktiven za ta dan v tednu
            if (isBetween(stopTime.arrivalTime, fromTime, toTime) && isActive(arrivalDate, data.servicesMap.get(stopTime.getServiceId()))) {
                Trip trip = data.tripsMap.get(stopTime.trip.tripId);
                int routeId = trip.routeId;
                stopTime.trip = trip;
                busStopInfo.add(routeId, stopTime); // doda v seznam naslednjih avtobusnih postankov, če limit še ni dosežen
            }
        }

        return busStopInfo;
    }

    /**
     * Preveri ali sta datuma aktivna za dani calendar/service
     *
     * @param dateTime1
     * @param dateTime2
     * @param service
     * @return
     */
    private boolean isActive(LocalDateTime dateTime1, LocalDateTime dateTime2, Calendar service) {
        DayOfWeek dayOfWeek1 = dateTime1.getDayOfWeek();
        DayOfWeek dayOfWeek2 = dateTime2.getDayOfWeek();

        return ((dayOfWeek1 == DayOfWeek.MONDAY || dayOfWeek2 == DayOfWeek.MONDAY) && service.monday) ||
                ((dayOfWeek1 == DayOfWeek.TUESDAY || dayOfWeek2 == DayOfWeek.TUESDAY) && service.tuesday) ||
                ((dayOfWeek1 == DayOfWeek.WEDNESDAY || dayOfWeek2 == DayOfWeek.WEDNESDAY) && service.wednesday) ||
                ((dayOfWeek1 == DayOfWeek.THURSDAY || dayOfWeek2 == DayOfWeek.THURSDAY) && service.thursday) ||
                ((dayOfWeek1 == DayOfWeek.FRIDAY || dayOfWeek2 == DayOfWeek.FRIDAY) && service.friday) ||
                ((dayOfWeek1 == DayOfWeek.SATURDAY || dayOfWeek2 == DayOfWeek.SATURDAY) && service.saturday) ||
                ((dayOfWeek1 == DayOfWeek.SUNDAY || dayOfWeek2 == DayOfWeek.SUNDAY) && service.sunday)
                ;
    }

    private boolean isActive(LocalDate date, Calendar service) {
        DayOfWeek dayOfWeek1 = date.getDayOfWeek();

        return ((dayOfWeek1 == DayOfWeek.MONDAY) && service.monday) ||
                ((dayOfWeek1 == DayOfWeek.TUESDAY) && service.tuesday) ||
                ((dayOfWeek1 == DayOfWeek.WEDNESDAY) && service.wednesday) ||
                ((dayOfWeek1 == DayOfWeek.THURSDAY) && service.thursday) ||
                ((dayOfWeek1 == DayOfWeek.FRIDAY) && service.friday) ||
                ((dayOfWeek1 == DayOfWeek.SATURDAY) && service.saturday) ||
                ((dayOfWeek1 == DayOfWeek.SUNDAY) && service.sunday)
                ;
    }

    /**
     * <relative|absolute> - v kakšni obliki vrne čase naslednjih prihodov
     * <p>
     * Absolutno:
     * Postajališče Gospodarsko razstavišče
     * 6: 12:10, 12:15, 12:40
     * 1: 13:30
     * <p>
     * Relativno:
     * Postajališče Gospodarsko razstavišče
     * 6: 10min, 15min, 40min
     * 1: 90min
     */
    public void printBusStopInfo(BusStopInfo busStopInfo, boolean relative) {
        Hashtable<Integer, List<StopTime>> table = busStopInfo.getTable();
        List<Integer> routeIds = table.keySet().stream().sorted().toList();

        System.out.println("Postajališče " + busStopInfo.getStop().stopName);
        for (Integer routeId : routeIds) {
            List<LocalTime> times = busStopInfo.getTable().get(routeId).stream().map(it -> it.arrivalTime).sorted().toList();
            List<String> timesFormatted;
            if (relative) {
                timesFormatted = times.stream().map(it -> relativeTime(it, busStopInfo.getFromTime())).toList();
            } else {
                timesFormatted = times.stream().map(it -> it.format(timeFormatter)).toList();
            }
            System.out.println(routeId + ": " + String.join(", ", timesFormatted));
        }
    }

    private <T> List<T> readCsv(File file, Class<T> class0) throws Exception {
        // BOMInputStream se uporablja zato, ker testna .csv datoteka vsebuje na začetku BOM oznako (EF BB BF), ki to oznako odstrani
        // Za branje csv datotek se uporablja knjižnica opencsv
        try (Reader reader = new InputStreamReader(BOMInputStream
                .builder()
                .setFile(file)
                .setByteOrderMarks(ByteOrderMark.UTF_8)
                .setInclude(false)
                .get())) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(class0)
                    .build();
            List<T> calendar = csvToBean.parse();
            return calendar;
        }
    }

    public static boolean isBetween(LocalTime arrivalTime, LocalTime fromTime, LocalTime toTime) {
        boolean nextDay = toTime.isBefore(fromTime);
        if (nextDay) {
            // Preveri čas za naslednji dan
            // Primer:
            // arrivalTime | startTime | endTime
            // 0:30       | 23:00      | 1:00
            return !arrivalTime.isBefore(fromTime) || !arrivalTime.isAfter(toTime);
        } else {
            // Preveri čas za trenutni dan
            // Primer:
            // arrivalTime | startTime | endTime
            // 10:00       | 11:00     | 10:30
            return !arrivalTime.isBefore(fromTime) && !arrivalTime.isAfter(toTime);
        }
    }

    private String relativeTime(LocalTime startTime, LocalTime endTime) {
        Duration duration;

        if (endTime.isBefore(startTime)) {
            duration = Duration.between(startTime, LocalTime.MIDNIGHT).plus(Duration.between(LocalTime.MIDNIGHT, endTime));
        } else {
            duration = Duration.between(startTime, endTime);
        }
        duration = duration.abs();

        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();

        if (minutes == 0 && seconds == 0) {
            return "0s";
        } else {
            return (minutes > 0 ? minutes + " min" : "") + (seconds > 0 ? (minutes > 0 ? " " : "") + seconds + "s" : "");
        }
    }
}
