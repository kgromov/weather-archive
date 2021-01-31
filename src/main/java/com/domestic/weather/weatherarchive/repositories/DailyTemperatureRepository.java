package com.domestic.weather.weatherarchive.repositories;

import com.domestic.weather.weatherarchive.domain.DailyTemperature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyTemperatureRepository extends JpaRepository<DailyTemperature, Long> {
}
