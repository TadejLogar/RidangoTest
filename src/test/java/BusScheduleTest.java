import com.ridango.transit.schedule.BusStopInfo;
import com.ridango.transit.schedule.BusSchedule;
import com.ridango.transit.schedule.model.StopTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BusScheduleTest {
    static BusSchedule busSchedule = new BusSchedule();

    @BeforeAll
    static void initialize() throws Exception {
        busSchedule.initialize();
    }

    @Test
    void nextBusStops1() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        now = now.with(LocalTime.of(15, 54));
        BusStopInfo busStopInfo = busSchedule.nextBusStops(10, 5, now, now.plusHours(2));
        busSchedule.printBusStopInfo(busStopInfo, false);
        System.out.println();
        busSchedule.printBusStopInfo(busStopInfo, true);
        System.out.println();

        Hashtable<Integer, List<StopTime>> table = busStopInfo.getTable();
        assertEquals(5, table.keySet().size());
        assertIterableEquals(Arrays.asList(102, 103, 104, 105, 107), table.keySet().stream().sorted().toList());
    }

    @Test
    void nextBusStops2() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        now = now.with(LocalTime.of(23, 40));
        BusStopInfo busStopInfo = busSchedule.nextBusStops(10, 5, now, now.plusHours(2));
        busSchedule.printBusStopInfo(busStopInfo, false);
        System.out.println();
        busSchedule.printBusStopInfo(busStopInfo, true);
        System.out.println();

        Hashtable<Integer, List<StopTime>> table = busStopInfo.getTable();
        assertEquals(1, table.keySet().size());
        assertIterableEquals(Arrays.asList(105), table.keySet().stream().sorted().toList());
        assertEquals(LocalTime.of(23, 46, 27), table.values().stream().toList().getFirst().getFirst().arrivalTime);
    }

    @Test
    void nextBusStops3() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        now = now.with(LocalTime.of(8, 0));
        BusStopInfo busStopInfo = busSchedule.nextBusStops(5, 10, now, now.plusHours(2));
        busSchedule.printBusStopInfo(busStopInfo, false);
        System.out.println();
        busSchedule.printBusStopInfo(busStopInfo, true);
        System.out.println();

        Hashtable<Integer, List<StopTime>> table = busStopInfo.getTable();
        assertEquals(1, table.keySet().size());
        assertIterableEquals(Arrays.asList(105), table.keySet().stream().sorted().toList());
        assertEquals(8, table.values().stream().toList().getFirst().size());
    }

    @Test
    void isBetween() {
        assertTrue(BusSchedule.isBetween(
                LocalTime.of(23, 0),
                LocalTime.of(1, 0),
                LocalTime.of(23, 40)
        ));
        assertTrue(BusSchedule.isBetween(
                LocalTime.of(23, 0),
                LocalTime.of(1, 0),
                LocalTime.of(0, 30)
        ));
        assertTrue(BusSchedule.isBetween(
                LocalTime.of(5, 30),
                LocalTime.of(7, 30),
                LocalTime.of(6, 0)
        ));

        assertFalse(BusSchedule.isBetween(
                LocalTime.of(23, 0),
                LocalTime.of(1, 0),
                LocalTime.of(22, 30)
        ));
        assertFalse(BusSchedule.isBetween(
                LocalTime.of(23, 0),
                LocalTime.of(1, 0),
                LocalTime.of(2, 0)
        ));
        assertFalse(BusSchedule.isBetween(
                LocalTime.of(5, 30),
                LocalTime.of(7, 30),
                LocalTime.of(4, 0)
        ));
        assertFalse(BusSchedule.isBetween(
                LocalTime.of(5, 30),
                LocalTime.of(7, 30),
                LocalTime.of(8, 0)
        ));
    }
}