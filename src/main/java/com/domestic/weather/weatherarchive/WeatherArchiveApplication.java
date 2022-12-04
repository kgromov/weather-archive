package com.domestic.weather.weatherarchive;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.DailyTemperature;
import com.domestic.weather.weatherarchive.service.SinoptikExtractot;
import com.domestic.weather.weatherarchive.service.TemperatureService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
public class WeatherArchiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherArchiveApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(TemperatureService temperatureService, SinoptikExtractot sinoptikExtractot) {
        return args -> {
//        DailyTemperatureDto todayTemperature = extractor.getTemperatureAt(City.ODESSA, LocalDate.now());
//        temperatureService.saveTemperature(new DailyTemperature(todayTemperature));
//            List<DailyTemperature> temperatureForYearsInCity = temperatureService.getTemperatureForYearsInCity(City.ODESSA, 2021, 2022);
            LocalDate startDate = Year.of(2021).atDay(1);
            LocalDate endDate = LocalDate.now();
            List<DailyTemperature> temperatureForYearsInCity = temperatureService.getTemperatureForYearsInCity(City.ODESSA, startDate, endDate);
            temperatureService.saveTemperature(temperatureForYearsInCity);
        };
    }
}
