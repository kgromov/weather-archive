package com.domestic.weather.weatherarchive.service;

import com.domestic.weather.weatherarchive.domain.City;
import com.domestic.weather.weatherarchive.domain.TemperatureMeasurementsDto;
import com.domestic.weather.weatherarchive.domain.WeatherMeasurementDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlPage;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SinoptikExtractor implements Extractor {
    private static final String WEATHER_ARCHIVE_URL = "https://ua.sinoptik.ua/";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H :mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final WebClient client;

    @Override
    public Optional<TemperatureMeasurementsDto> getTemperatureAt(City city, LocalDate measurementDate) {
        long start = System.nanoTime();
        log.info("Collecting daily temperature for city {}, at {}", city, measurementDate);
        String dateFormatted = DATE_FORMATTER.format(measurementDate);
        String encodedCityName = URLEncoder.encode(city.getKeyWord(), StandardCharsets.UTF_8);
        String url = WEATHER_ARCHIVE_URL + '/' + encodedCityName + '/' + dateFormatted;
        try {
            HtmlPage page = client.getPage(url);
//            HtmlTable table = page.getFirstByXPath("//table[@class='weatherDetails']");
            DomNode weatherTable = page.querySelector("table.weatherDetails");
            var timeCells = weatherTable.querySelectorAll("tbody>tr.gray.time>td");
            var temperatureCells = weatherTable.querySelectorAll("tbody>tr.temperature>td");
            List<WeatherMeasurementDto> dailyMeasurements = IntStream.range(0, timeCells.size()).boxed()
                    .map(index -> Pair.of(timeCells.get(index), temperatureCells.get(index)))
                    .map(data -> mapToWeatherMeasurementDto(data.getFirst(), data.getSecond()))
                    .collect(Collectors.toList());

            TemperatureMeasurementsDto temperatureMeasurementsDto = new TemperatureMeasurementsDto();
            temperatureMeasurementsDto.setDate(measurementDate);
            temperatureMeasurementsDto.setDailyMeasurements(dailyMeasurements);
            return Optional.of(temperatureMeasurementsDto);
        } catch (NullPointerException e) {
            log.error("No weather for specified date {} in city = {}", dateFormatted, city);
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Time to extract cell values = {} ms", TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));
        }
    }

    private static WeatherMeasurementDto mapToWeatherMeasurementDto(DomNode timeElement, DomNode tempElement) {
        String time = timeElement.getTextContent().trim();
        String temperature = tempElement.getTextContent().trim();
        LocalTime parsedTime = LocalTime.parse(time, TIME_FORMATTER);
        int parsedTemp = Integer.parseInt(temperature.substring(0, temperature.length() - 1));
        return new WeatherMeasurementDto(parsedTime, parsedTemp);
    }

    public static void main(String[] args) throws IOException {
        try (WebClient client = new WebClient(BrowserVersion.CHROME)) {
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            client.getOptions().setUseInsecureSSL(true);
            client.getOptions().setThrowExceptionOnScriptError(false);
            new SinoptikExtractor(client)
                    .getTemperatureAt(City.ODESSA, LocalDate.parse("2024-09-04"))
                    .ifPresent(System.out::println);
        }

    }
}
