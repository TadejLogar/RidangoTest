package com.ridango.transit.parameter;

public class Parameters {
    public int stopId;
    public int limit;
    public boolean relative;

    public Parameters() {
    }

    public Parameters(int stopId, int limit, boolean relative) {
        this.stopId = stopId;
        this.limit = limit;
        this.relative = relative;
    }
}
