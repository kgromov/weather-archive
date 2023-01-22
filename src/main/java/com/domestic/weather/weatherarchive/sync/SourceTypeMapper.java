package com.domestic.weather.weatherarchive.sync;

import events.SyncEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SourceTypeMapper {
    private final Environment environment;

    public SyncEvent buildSyncEvent(LocalDate startDate, LocalDate endDate) {
        DbSourceType inputType = this.getInputType();
        return SyncEvent.builder()
                .id(UUID.randomUUID())
                .startDate(startDate)
                .endDate(endDate)
                .inputType(inputType)
                .outputType(DbSourceType.MONGODB)
                .build();
    }

    private DbSourceType getInputType() {
        List<String> activeProfiles = List.of(environment.getActiveProfiles());
        return Arrays.stream(DbSourceType.values())
                .filter(dbSourceType -> activeProfiles.contains(dbSourceType.name().toLowerCase()))
                .findFirst()
                .orElse(DbSourceType.MYSQL);
    }
}
