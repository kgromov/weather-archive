package com.domestic.weather.weatherarchive;

import com.aqmp.example.config.BrokerSettings;
import com.domestic.weather.weatherarchive.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@Slf4j
public class WeatherArchiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherArchiveApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(value = "weather.populate", havingValue = "true")
    ApplicationRunner applicationRunner(SyncService syncService, BrokerSettings brokerSettings) {
        return args -> {
            log.info("Broker settings: {}", brokerSettings);
           syncService.syncDailyTemperature();
        };
    }

    private static class PopulateTemperatureCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return Boolean.parseBoolean(context.getEnvironment().getProperty("weather.populate", "true"));
        }
    }
}
