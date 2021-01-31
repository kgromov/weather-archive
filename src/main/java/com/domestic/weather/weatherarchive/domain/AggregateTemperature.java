package com.domestic.weather.weatherarchive.domain;

public interface AggregateTemperature<T> {

    // where range could be:
    // part of a day for Day
    // average for day; month; year
    double getTemperature(T range);
}
