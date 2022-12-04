package com.domestic.weather.weatherarchive.service;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.DailyTemperatureDto;
import com.domestic.weather.weatherarchive.domain.WeatherMeasurementDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class SinoptikExtractor implements Extractor {
    private static final String WEATHER_ARCHIVE_URL = "https://ua.sinoptik.ua/";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H :mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Optional<DailyTemperatureDto> getTemperatureAt(City city, LocalDate measurementDate) {
        long start = System.nanoTime();
        log.info("Collecting daily temperature for city {}, at {}", city, measurementDate);
        String dateFormatted = DATE_FORMATTER.format(measurementDate);
        try {
            String encodedCityName = URLEncoder.encode(city.getKeyWord(), "UTF-8");
            String url = WEATHER_ARCHIVE_URL + '/' + encodedCityName + '/' + dateFormatted;
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Element weatherTable = document.selectFirst("table.weatherDetails");
            Elements timeCells = weatherTable.select("tbody>tr.gray.time>td");
            Elements temperatureCells = weatherTable.select("tbody>tr.temperature>td");

            List<WeatherMeasurementDto> dailyMeasurements = IntStream.range(0, timeCells.size()).boxed()
                    .map(index -> Pair.of(timeCells.get(index), temperatureCells.get(index)))
                    .map(data -> mapToWeatherMeasurementDto(data.getFirst(), data.getSecond()))
                    .collect(Collectors.toList());

            DailyTemperatureDto dailyTemperatureDto = new DailyTemperatureDto();
            dailyTemperatureDto.setDate(measurementDate);
            dailyTemperatureDto.setDailyMeasurements(dailyMeasurements);
            return Optional.of(dailyTemperatureDto);
        } catch (NullPointerException e) {
            log.error("No weather for specified date {} in city = {}", dateFormatted, city);
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Time to extract cell values = {} ms", TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));
        }
    }

    private static WeatherMeasurementDto mapToWeatherMeasurementDto(Element timeElement,Element tempElement){
        String time = timeElement.text().trim();
        String temperature = tempElement.text().trim();
        LocalTime parsedTime = LocalTime.parse(time, TIME_FORMATTER);
        int parsedTemp = Integer.parseInt(temperature.substring(0, temperature.length() - 1));
        return new WeatherMeasurementDto(parsedTime, parsedTemp);
    }

    public static void main(String[] args) throws IOException{
        City city = City.ODESSA;
        String encodedCityName = URLEncoder.encode(city.getKeyWord(), "UTF-8");
        String url = WEATHER_ARCHIVE_URL + '/' + encodedCityName + '/' + "2022-12-05";
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();

        Element weatherTable = document.selectFirst("table.weatherDetails");
        Elements timeCells = weatherTable.select("tbody>tr.gray.time>td");
        Elements temperatureCells = weatherTable.select("tbody>tr.temperature>td");

        List<WeatherMeasurementDto> dailyMeasurements = IntStream.range(0, timeCells.size()).boxed()
                .map(index -> Pair.of(timeCells.get(index), temperatureCells.get(index)))
                .map(data -> mapToWeatherMeasurementDto(data.getFirst(), data.getSecond()))
                .collect(Collectors.toList());
        System.out.println(dailyMeasurements);
    }
}
