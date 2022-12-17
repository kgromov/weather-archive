package com.domestic.weather.weatherarchive.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "DayTemperature")
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "date"})
@Builder
public class DailyTemperature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(unique = true)
    private LocalDate date;
    private Double morningTemperature;
    private Double afternoonTemperature;
    private Double eveningTemperature;
    private Double nightTemperature;

    public DailyTemperature(TemperatureMeasurementsDto temperatureMeasurementsDto) {
        this.date = temperatureMeasurementsDto.getDate();
        this.morningTemperature = temperatureMeasurementsDto.getMorningTemperature();
        this.afternoonTemperature = temperatureMeasurementsDto.getAfternoonTemperature();
        this.eveningTemperature = temperatureMeasurementsDto.getEveningTemperature();
        this.nightTemperature = temperatureMeasurementsDto.getNightTemperature();
    }
}
