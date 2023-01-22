package com.domestic.weather.weatherarchive.sync;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.DailyTemperature;
import com.domestic.weather.weatherarchive.service.TemperatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncService {
    private final TemperatureService temperatureService;
    private final SyncEventPublisher syncEventPublisher;

    public void syncDailyTemperature() {
        DailyTemperature latestDateTemperature = temperatureService.getLatestDateTemperature();
        LocalDate startDate = latestDateTemperature.getDate().plusDays(1);
        LocalDate endDate = LocalDate.now();
        List<DailyTemperature> temperatureForYearsInCity = temperatureService.getTemperatureForYearsInCity(City.ODESSA, startDate, endDate);
        temperatureService.saveTemperature(temperatureForYearsInCity);
        syncEventPublisher.sendSynEvent(startDate, endDate);
    }
}
