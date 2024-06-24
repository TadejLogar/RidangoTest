package com.ridango.transit.schedule.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.ridango.transit.csv.converter.LocalTimeConverter;

import java.time.LocalTime;

/**
 * trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled,timepoint
 * NORMAL_03_101_Return_22:10,22:10:00,22:10:00,2,1,,,,,
 * NORMAL_03_101_Return_22:10,22:17:38,22:17:38,3,2,,,,,
 * NORMAL_03_103_Go_07:20,07:20:00,07:20:00,10,1,,,,,
 */
public class StopTime {
    @CsvBindByName(column = "trip_id")
    public String tripId;

    @CsvCustomBindByName(column = "arrival_time", converter = LocalTimeConverter.class)
    public LocalTime arrivalTime;

    @CsvCustomBindByName(column = "departure_time", converter = LocalTimeConverter.class)
    public LocalTime departureTime;

    @CsvBindByName(column = "stop_id")
    public int stopId;

    @CsvBindByName(column = "stop_sequence")
    public int stopSequence;

    @CsvBindByName(column = "stop_headsign")
    public String stopHeadsign;

    @CsvBindByName(column = "pickup_type")
    public String pickupType;

    @CsvBindByName(column = "drop_off_type")
    public String dropOffType;

    @CsvBindByName(column = "shape_dist_traveled")
    public String shapeDistTraveled;

    @CsvBindByName(column = "timepoint")
    public String timepoint;

    public Trip trip;

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public Integer getServiceId() {
        return trip == null ? null : trip.serviceId;
    }

    public Integer getRouteId() {
        return trip == null ? null : trip.routeId;
    }

    public String toString() {
        return "arrivalTime: " + arrivalTime + ", tripId = " + tripId + ", routeId = " + (trip == null ? "/" : trip.routeId);
    }
}
