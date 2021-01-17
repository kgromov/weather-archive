package com.domestic.weather.weatherarchive.service;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.DailyTemperature;
import com.domestic.weather.weatherarchive.domain.WeatherMeasurementDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class Extractor {
    private static final String WEATHER_ARCHIVE_URL = "https://meteo.ua/archive/";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");

    @SneakyThrows
    public DailyTemperature getTemperatureAt(City city, LocalDate date) {
        long start = System.nanoTime();
        log.info("Collecting daily temperature for city {}, at {}", city, date);
        String dateFormatted = DATE_FORMATTER.format(date);
        String url = WEATHER_ARCHIVE_URL + city.getCode() + '/' + city.getName() + '/' + dateFormatted;
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();
        Element weatherTable = document.selectFirst(".archive_table");
        Elements rows = weatherTable.select("tbody>tr");
        List<WeatherMeasurementDto> dailyMeasurements = rows.stream()
                .skip(1)
                .map(row ->
                {
                    Elements columns = row.getElementsByTag("td");
                    String time = columns.get(0).text();
                    String temperature = columns.get(2).text();
                    LocalTime timeParsed = LocalTime.parse(time, TIME_FORMATTER);
                    String firstChar = time.substring(0, 1);
                    int tempParsed = Integer.parseInt(temperature.substring(0, temperature.length() - 2));
                    return new WeatherMeasurementDto(timeParsed, tempParsed);
                })
                .collect(Collectors.toList());
        DailyTemperature dailyTemperature = new DailyTemperature();
        dailyTemperature.setDate(date);
        dailyTemperature.setDailyMeasurements(dailyMeasurements);
        log.info("Time to extract cell values = {} ms", System.currentTimeMillis() - start);
        return dailyTemperature;
    }

    public static void main(String[] args) throws IOException {
        DailyTemperature temp = new Extractor().getTemperatureAt(City.ODESSA, LocalDate.of(2010, 1, 2));

        log.info("Start getting content html page");
        Connection connection = Jsoup.connect("https://meteo.ua/archive/111/odessa/2010-1-2");
        Document document = connection.get();
        log.info("Received content of html page");
        Element weatherTable = document.selectFirst(".archive_table");
        long start = System.currentTimeMillis();
        // [13-15] ms but is not distinguishable
       /* Elements cells = weatherTable.getElementsByTag("td");
        cells.stream()
                .filter(c -> c.className().contains("at_time") || c.className().contains("at_temp"))
                .forEach(c -> log.info(c.text()));*/
        // [23; 30] ms
        Elements times = weatherTable.select("td.at_time");
        Elements temperatures = weatherTable.select("td.at_temp");
        IntStream.range(0, times.size()).boxed()
                .forEach(i ->
                        log.info("At {} o'clock temperature was {}", times.get(i).text(), temperatures.get(i).text()));
        // [16-20] ms
        Elements rows = weatherTable.select("tbody>tr");
        rows.stream()
                .skip(1)
                .forEach(row ->
                {
                    Elements columns = row.getElementsByTag("td");
                    Element time = columns.get(0);
                    Element temperature = columns.get(2);
                    log.info("At {} o'clock temperature was {}", time.text(), temperature.text());
                });
        log.info("Time to extract cell values = {} ms", System.currentTimeMillis() - start);
    }
}
