package com.domestic.weather.weatherarchive.service;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.DailyTemperature;
import com.domestic.weather.weatherarchive.domain.DailyTemperatureDto;
import com.domestic.weather.weatherarchive.repositories.DailyTemperatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemperatureService {
    private final DailyTemperatureRepository temperatureRepository;
//    private final MeteoExtractor meteoExtractor;
    private final SinoptikExtractor sinoptikExtractor;

    public List<DailyTemperature> getTemperatureForYearsInCity(City city, int startYear, int endYear) {
        LocalDate startDate = Year.of(startYear).atDay(1);
        LocalDate endDate = Year.of(endYear).atDay(1);
        return getTemperatureForYearsInCity(city, startDate, endDate);
    }

    public List<DailyTemperature> getTemperatureForYearsInCity(City city, LocalDate startDate, LocalDate endDate) {
        List<DailyTemperatureDto> temperatures = sinoptikExtractor.getTemperatureForRange(city, startDate, endDate);
        return temperatures.stream()
                .parallel()
                .map(DailyTemperature::new)
                .sorted(Comparator.comparing(DailyTemperature::getDate))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveTemperature(Collection<DailyTemperature> daysTemperature) {
        temperatureRepository.saveAll(daysTemperature);
    }

    @Transactional
    public void saveTemperature(DailyTemperature dailyTemperature) {
        temperatureRepository.save(dailyTemperature);
    }
}
