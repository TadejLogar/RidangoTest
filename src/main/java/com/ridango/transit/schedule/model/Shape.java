package com.ridango.transit.schedule.model;

/**
 * shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence,shape_dist_traveled
 * 102,24.50055,39.6115,1,
 * 102,24.5006,39.61077,2,
 * 102,24.50048,39.61059,3,
 * 102,24.50031,39.61045,4,
 */
public class Shape {
    public int shape_id;
    public double shape_pt_lat;
    public double shape_pt_lon;
    public int shape_pt_sequence;
    public String shape_dist_traveled;
}
