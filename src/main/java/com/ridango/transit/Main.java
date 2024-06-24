package com.ridango.transit;

import com.ridango.transit.schedule.BusStopInfo;
import com.ridango.transit.schedule.BusSchedule;
import com.ridango.transit.parameter.ParameterException;
import com.ridango.transit.parameter.Parameters;

import java.time.LocalDateTime;

/**
 * Argumenti aplikacije:
 * - id postaje
 * - število naslednjih avtobusov
 * - <relative|absolute> - v kakšni obliki vrne čase naslednjih prihodov
 *
 * Primer 1:
 * cmd> busTrips 12345 5 absolute
 * Postajališče Gospodarsko razstavišče
 * 6: 12:10, 12:15, 12:40
 * 1: 13:30
 *
 * Primer 2:
 * cmd> busTrips 12345 3 relative
 * Postajališče Gospodarsko razstavišče
 * 6: 10min, 15min, 40min
 * 1: 90min
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // args = new String[] { "10", "5", "absolute" };
        // args = new String[] { "10", "5", "relative" };

        try {
            Parameters parameters = parameters(args);
            BusSchedule busSchedule = new BusSchedule();
            busSchedule.initialize();
            LocalDateTime now = LocalDateTime.now();
            BusStopInfo busStops = busSchedule.nextBusStops(parameters.stopId, parameters.limit, now, now.plusHours(2));
            busSchedule.printBusStopInfo(busStops, parameters.relative);
        } catch (ParameterException e) {
            System.out.println("""
                    Argumenti aplikacije:
                    - id postaje
                    - število naslednjih avtobusov
                    - <relative|absolute> - v kakšni obliki vrne čase naslednjih prihodov
                    
                    Primer 1:
                    cmd> busTrips 12345 5 absolute
                    Postajališče Gospodarsko razstavišče
                    6: 12:10, 12:15, 12:40
                    1: 13:30
                    
                    Primer 2:
                    cmd> busTrips 12345 3 relative
                    Postajališče Gospodarsko razstavišče
                    6: 10min, 15min, 40min
                    1: 90min
                    """);
        }
    }

    private static Parameters parameters(String[] args) throws ParameterException {
        if (args.length == 3) {
            Parameters parameters = new Parameters();
            try {
                parameters.stopId = Integer.parseInt(args[0]);
                parameters.limit = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new ParameterException();
            }
            if (args[2].equals("absolute")) {
                parameters.relative = false;
            } else if (args[2].equals("relative")) {
                parameters.relative = true;
            } else {
                throw new ParameterException();
            }
            return parameters;
        } else {
            throw new ParameterException();
        }
    }
}