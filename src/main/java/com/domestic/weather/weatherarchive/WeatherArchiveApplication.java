package com.domestic.weather.weatherarchive;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.DailyTemperature;
import com.domestic.weather.weatherarchive.repository.DailyTemperatureRepository;
import com.domestic.weather.weatherarchive.service.SinoptikExtractot;
import com.domestic.weather.weatherarchive.service.TemperatureService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.domain.Sort;
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
    @ConditionalOnProperty(value = "weather.populate", havingValue = "true")
    ApplicationRunner applicationRunner(TemperatureService temperatureService, DailyTemperatureRepository temperatureRepository) {
        return args -> {
            DailyTemperature latestDateTemperature = temperatureRepository.findLatestDateTemperature();
            LocalDate startDate = latestDateTemperature.getDate().plusDays(1);
            LocalDate endDate = LocalDate.now();
            List<DailyTemperature> temperatureForYearsInCity = temperatureService.getTemperatureForYearsInCity(City.ODESSA, startDate, endDate);
            temperatureService.saveTemperature(temperatureForYearsInCity);
        };
    }

    private static class PopulateTemperatureCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return Boolean.parseBoolean(context.getEnvironment().getProperty("weather.populate", "true"));
        }
    }
}
