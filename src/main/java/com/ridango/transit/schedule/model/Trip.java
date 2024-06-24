package com.ridango.transit.schedule.model;

import com.opencsv.bean.CsvBindByName;

/**
 * route_id,service_id,trip_id,trip_headsign,trip_short_name,direction_id,block_id,shape_id,wheelchair_accessible,bikes_allowed,duty,duty_sequence_number,run_sequence_number
 * 101,1,NORMAL_03_101_Return_22:10,Uhud battlefield,,1,101_03,41,,,101_032,24,48
 * 103,1,NORMAL_03_103_Go_07:20,AL Masjid Al-nabawi (Manakha Square),,1,103_01,125,,,103_011,5,5
 */
public class Trip {
    @CsvBindByName(column = "route_id")
    public int routeId;

    @CsvBindByName(column = "service_id")
    public int serviceId;

    @CsvBindByName(column = "trip_id")
    public String tripId;

    @CsvBindByName(column = "trip_headsign")
    public String tripHeadsign;

    @CsvBindByName(column = "trip_short_name")
    public String tripShortName;

    @CsvBindByName(column = "direction_id")
    public int directionId;

    @CsvBindByName(column = "block_id")
    public String blockId;

    @CsvBindByName(column = "shape_id")
    public int shapeId;

    @CsvBindByName(column = "wheelchair_accessible")
    public String wheelchairAccessible;

    @CsvBindByName(column = "bikes_allowed")
    public String bikesAllowed;

    @CsvBindByName(column = "duty")
    public String duty;

    @CsvBindByName(column = "duty_sequence_number")
    public int dutySequenceNumber;

    @CsvBindByName(column = "run_sequence_number")
    public int runSequenceNumber;
}
