package com.ridango.transit.schedule;

import com.ridango.transit.schedule.model.Stop;
import com.ridango.transit.schedule.model.StopTime;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BusStopInfo {
    private final LocalTime fromTime;
    private final Stop stop;
    private final int limit;

    private Hashtable<Integer, List<StopTime>> table = new Hashtable<>();

    public BusStopInfo(Stop stop, LocalTime fromTime, int limit) {
        this.stop = stop;
        this.fromTime = fromTime;
        this.limit = limit;
    }

    public boolean add(int routeId, StopTime stopTime) {
        List<StopTime> list = table.get(routeId);
        if (list == null) {
            list = new ArrayList<>();
            table.put(routeId, list);
        }
        if (list.size() <= limit) {
            return list.add(stopTime);
        }
        return false;
    }

    public Hashtable<Integer, List<StopTime>> getTable() {
        return table;
    }

    public Stop getStop() {
        return stop;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }
}
