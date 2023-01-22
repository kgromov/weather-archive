package com.domestic.weather.weatherarchive;

import com.domestic.weather.weatherarchive.config.BrokerSettings;
import com.domestic.weather.weatherarchive.sync.SyncService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties(BrokerSettings.class)
public class WeatherArchiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherArchiveApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(value = "weather.populate", havingValue = "true")
    ApplicationRunner applicationRunner(SyncService syncService) {
        return args -> {
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
