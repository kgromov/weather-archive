package com.domestic.weather.weatherarchive.service;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.TemperatureMeasurementsDto;
import lombok.SneakyThrows;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Extractor {
    default List<TemperatureMeasurementsDto> getTemperatureForRange(City city, LocalDate startDate, LocalDate endDate) {
        return Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .map(date -> getTemperatureAt(city, date))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    Optional<TemperatureMeasurementsDto> getTemperatureAt(City city, LocalDate date);
}
