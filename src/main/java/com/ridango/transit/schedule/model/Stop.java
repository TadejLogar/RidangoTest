package com.ridango.transit.schedule.model;

import com.opencsv.bean.CsvBindByName;

/**
 * stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url,location_type,parent_station,stop_timezone,wheelchair_boarding
 * 2,2001,AL Masjid Al-nabawi (Clock Roundabout),,24.47141,39.61112,,,,,,
 * 3,2002,Uhud battlefield,,24.50056,39.6115,,,,,,
 * 4,2003,Al Quiblatain Mosque,,24.48463,39.57871,,,,,,
 */
public class Stop {
    @CsvBindByName(column = "stop_id")
    public int stopId; // id postaje

    @CsvBindByName(column = "stop_code")
    public int stopCode;

    @CsvBindByName(column = "stop_name")
    public String stopName;

    @CsvBindByName(column = "stop_desc")
    public String stopDesc;

    @CsvBindByName(column = "stop_lat")
    public double stopLat;

    @CsvBindByName(column = "stop_lon")
    public double stopLon;

    @CsvBindByName(column = "zone_id")
    public String zoneId;

    @CsvBindByName(column = "stop_url")
    public String stopUrl;

    @CsvBindByName(column = "location_type")
    public String locationType;

    @CsvBindByName(column = "parent_station")
    public String parentStation;

    @CsvBindByName(column = "stop_timezone")
    public String stopTimezone;

    @CsvBindByName(column = "wheelchair_boarding")
    public String wheelchairBoarding;
}
