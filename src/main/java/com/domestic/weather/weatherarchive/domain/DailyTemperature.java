package com.domestic.weather.weatherarchive.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "DayTemperature")
@NoArgsConstructor
@Getter
@Setter
public class DailyTemperature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Double morningTemperature;
    private Double afternoonTemperature;
    private Double eveningTemperature;
    private Double nightTemperature;

    public DailyTemperature(DailyTemperatureDto dailyTemperatureDto) {
        this.date = dailyTemperatureDto.getDate();
        this.morningTemperature = dailyTemperatureDto.getMorningTemperature();
        this.afternoonTemperature = dailyTemperatureDto.getAfternoonTemperature();
        this.eveningTemperature = dailyTemperatureDto.getEveningTemperature();
        this.nightTemperature = dailyTemperatureDto.getNightTemperature();
    }
}
