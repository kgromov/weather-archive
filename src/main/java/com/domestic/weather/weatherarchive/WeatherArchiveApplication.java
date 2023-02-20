package com.domestic.weather.weatherarchive;

import com.domestic.weather.weatherarchive.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@Slf4j
public class WeatherArchiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherArchiveApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(SyncService syncService) {
        return args -> {
           syncService.syncDailyTemperature();
        };
    }
}
