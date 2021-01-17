package com.domestic.weather.weatherarchive.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyTemperature {
    private LocalDate date;
    private List<WeatherMeasurementDto> dailyMeasurements;

    public double getMorningTemperature(PartOfTheDay partOfTheDay) {
        return getAvgTemperatureAtDayPart(partOfTheDay.getStart(), partOfTheDay.getEnd());
    }

    private double getAvgTemperatureAtDayPart(LocalTime min, LocalTime max) {
        return dailyMeasurements.stream()
                .filter(m -> m.getTime().isAfter(min))
                .filter(m -> m.getTime().isBefore(max))
                .mapToInt(WeatherMeasurementDto::getTemperature)
                .average()
                .orElse( 0.0);
    };

}
