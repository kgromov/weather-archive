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
  /*  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
    @SequenceGenerator(name = "users_seq_gen", sequenceName = "users_id_seq")*/
    private Long id;
    @JoinColumn(unique = true)
    private LocalDate date;
    @Column(name = "morningTemperature")
    private Double morningTemperature;
    @Column(name = "afternoonTemperature")
    private Double afternoonTemperature;
    @Column(name = "eveningTemperature")
    private Double eveningTemperature;
    @Column(name = "nightTemperature")
    private Double nightTemperature;

    public DailyTemperature(TemperatureMeasurementsDto temperatureMeasurementsDto) {
        this.date = temperatureMeasurementsDto.getDate();
        this.morningTemperature = temperatureMeasurementsDto.getMorningTemperature();
        this.afternoonTemperature = temperatureMeasurementsDto.getAfternoonTemperature();
        this.eveningTemperature = temperatureMeasurementsDto.getEveningTemperature();
        this.nightTemperature = temperatureMeasurementsDto.getNightTemperature();
    }
}
