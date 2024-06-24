package com.ridango.transit.schedule.model;

/**
 * route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color
 * 101,Short name,101,,,3,,964E00,000000
 * 102,Short name,102,,,3,,4F054F,000000
 * 103,Short name,103,,,3,,666666,000000
 */
public class Route {
    public int route_id;
    public String agency_id;
    public int route_short_name;
    public String route_long_name;
    public String route_desc;
    public int route_type;
    public String route_url;
    public Object route_color;
    public String route_text_color;
}
