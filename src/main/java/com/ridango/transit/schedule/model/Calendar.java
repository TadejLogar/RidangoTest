package com.ridango.transit.schedule.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.ridango.transit.csv.converter.BooleanConverter;
import com.ridango.transit.csv.converter.LocalDateConverter;

import java.time.LocalDate;

/**
 * service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
 * 1,1,1,1,1,1,1,1,20200215,20200515
 */
public class Calendar {
    @CsvBindByName(column = "service_id")
    public int serviceId;

    @CsvCustomBindByName(converter = BooleanConverter.class)
    public boolean monday;

    @CsvCustomBindByName(converter = BooleanConverter.class)
    public boolean tuesday;

    @CsvCustomBindByName(converter = BooleanConverter.class)
    public boolean wednesday;

    @CsvCustomBindByName(converter = BooleanConverter.class)
    public boolean thursday;

    @CsvCustomBindByName(converter = BooleanConverter.class)
    public boolean friday;

    @CsvCustomBindByName(converter = BooleanConverter.class)
    public boolean saturday;

    @CsvCustomBindByName(converter = BooleanConverter.class)
    public boolean sunday;

    @CsvCustomBindByName(column = "start_date", converter = LocalDateConverter.class)
    public LocalDate startDate;

    @CsvCustomBindByName(column = "end_date", converter = LocalDateConverter.class)
    public LocalDate endDate;
}