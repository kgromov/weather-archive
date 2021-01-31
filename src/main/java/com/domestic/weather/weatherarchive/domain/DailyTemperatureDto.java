package com.domestic.weather.weatherarchive.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.domestic.weather.weatherarchive.domain.PartOfTheDay.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyTemperatureDto {
    private LocalDate date;
    private List<WeatherMeasurementDto> dailyMeasurements;

    public double getMorningTemperature() {
        return getAvgTemperatureAtDayPart(MORNING);
    }

    public double getAfternoonTemperature() {
        return getAvgTemperatureAtDayPart(AFTERNOON);
    }

    public double getEveningTemperature() {
        return getAvgTemperatureAtDayPart(EVENING);
    }

    public double getNightTemperature() {
        return getAvgTemperatureAtDayPart(NIGHT);
    }
    private double getAvgTemperatureAtDayPart(PartOfTheDay partOfTheDay) {
        LocalTime min = partOfTheDay.getStart();
        LocalTime max = partOfTheDay.getEnd();
        return dailyMeasurements.stream()
                .filter(m -> m.getTime().isAfter(min))
                .filter(m -> m.getTime().isBefore(max))
                .mapToInt(WeatherMeasurementDto::getTemperature)
                .average()
                .orElse( 0.0);
    };

}
