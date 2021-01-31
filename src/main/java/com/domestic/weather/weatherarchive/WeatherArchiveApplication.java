package com.domestic.weather.weatherarchive;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.DailyTemperature;
import com.domestic.weather.weatherarchive.domain.DailyTemperatureDto;
import com.domestic.weather.weatherarchive.service.Extractor;
import com.domestic.weather.weatherarchive.service.TemperatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
public class WeatherArchiveApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WeatherArchiveApplication.class, args);
//        Extractor extractor = context.getBean(Extractor.class);
//        DailyTemperatureDto todayTemperature = extractor.getTemperatureAt(City.ODESSA, LocalDate.now());
        TemperatureService temperatureService = context.getBean(TemperatureService.class);
//        temperatureService.saveTemperature(new DailyTemperature(todayTemperature));
        List<DailyTemperature> temperatureForYearsInCity = temperatureService.getTemperatureForYearsInCity(City.ODESSA, 2010, 2021);
        temperatureService.saveTemperature(temperatureForYearsInCity);
    }

}
