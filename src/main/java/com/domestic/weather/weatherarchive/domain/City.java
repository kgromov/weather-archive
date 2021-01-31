package com.domestic.weather.weatherarchive.domain;

public enum City {
    ODESSA("odessa", 111);

    private final String name;
    private final int code;

    City(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return getName();
    }
}
