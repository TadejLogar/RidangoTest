package com.ridango.transit.schedule.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GtfsData {
    public Map<Integer, Stop> stopsMap;
    public Map<String, Trip> tripsMap;
    public Map<Integer, Calendar> servicesMap;
    public Map<String, List<StopTime>> stopTimesMap = new HashMap<>(); // key = serviceId "_" stopId
}
