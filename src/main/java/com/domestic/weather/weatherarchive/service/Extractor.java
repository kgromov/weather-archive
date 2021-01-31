package com.domestic.weather.weatherarchive.service;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.DailyTemperatureDto;
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
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class Extractor {
    private static final String WEATHER_ARCHIVE_URL = "https://meteo.ua/archive/";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");

    public List<DailyTemperatureDto> getTemperatureForRange(City city, LocalDate startDate, LocalDate endDate) {
        return Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .map(date -> getTemperatureAt(city, date))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public Optional<DailyTemperatureDto> getTemperatureAt(City city, LocalDate date) {
        long start = System.nanoTime();
        log.info("Collecting daily temperature for city {}, at {}", city, date);
        String dateFormatted = DATE_FORMATTER.format(date);
        String url = WEATHER_ARCHIVE_URL + city.getCode() + '/' + city.getName() + '/' + dateFormatted;
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();
        try {
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
                        int tempParsed = Integer.parseInt(temperature.substring(0, temperature.length() - 2));
                        return new WeatherMeasurementDto(timeParsed, tempParsed);
                    })
                    .collect(Collectors.toList());
            DailyTemperatureDto dailyTemperatureDto = new DailyTemperatureDto();
            dailyTemperatureDto.setDate(date);
            dailyTemperatureDto.setDailyMeasurements(dailyMeasurements);
            return Optional.of(dailyTemperatureDto);
        } catch (NullPointerException e) {
            log.error("No weather for specified date {} in city = {}", dateFormatted, city);
            return Optional.empty();
        } finally {
            log.info("Time to extract cell values = {} ms", TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));
        }
    }

    public static void main(String[] args) throws IOException {
        DailyTemperatureDto temp = new Extractor().getTemperatureAt(City.ODESSA, LocalDate.of(2011, 1, 2)).get();

        log.info("Start getting content html page");
        Connection connection = Jsoup.connect("https://meteo.ua/archive/111/odessa/2010-10-31");
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
     /*   Elements times = weatherTable.select("td.at_time");
        Elements temperatures = weatherTable.select("td.at_temp");
        IntStream.range(0, times.size()).boxed()
                .forEach(i ->
                        log.info("At {} o'clock temperature was {}", times.get(i).text(), temperatures.get(i).text()));*/
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
