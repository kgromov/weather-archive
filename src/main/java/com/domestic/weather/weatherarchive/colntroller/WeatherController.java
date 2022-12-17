package com.domestic.weather.weatherarchive.colntroller;

import com.domestic.weather.weatherarchive.domain.DailyTemperature;
import com.domestic.weather.weatherarchive.dtos.DailyTemperatureDto;
import com.domestic.weather.weatherarchive.repository.DailyTemperatureRepository;
import com.domestic.weather.weatherarchive.service.DailyTemperatureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/weather")
@CrossOrigin("*")
@RequiredArgsConstructor
public class WeatherController {
    private final DailyTemperatureRepository temperatureRepository;
    private final DailyTemperatureMapper temperatureMapper;

    @GetMapping("/current")
    public DailyTemperatureDto getCurrentTemperature() {
        LocalDate currentDate = LocalDate.now();
        DailyTemperature temperature = temperatureRepository.findByDate(currentDate);
        return temperatureMapper.mapToDto(temperature);
    }

    @GetMapping("/single/{date}")
    public DailyTemperatureDto getTemperatureAtDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        DailyTemperature temperature = temperatureRepository.findByDate(date);
        return temperatureMapper.mapToDto(temperature);
    }

    @GetMapping("/{date}")
    public List<DailyTemperatureDto> getTemperatureInRange(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                           @RequestParam(required = false) Integer years) {
        List<DailyTemperature> temperatures = isNull(years) ? temperatureRepository.findByDateNative(date) : temperatureRepository.findByDateInRange(date, years);
        return temperatures.stream()
                .map(temperatureMapper::mapToDto)
                .sorted(Comparator.comparing(DailyTemperatureDto::getDate))
                .collect(Collectors.toList());
    }
}
