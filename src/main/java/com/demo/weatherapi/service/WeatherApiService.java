package com.demo.weatherapi.service;

import com.demo.weatherapi.domain.Daily;
import com.demo.weatherapi.domain.WeatherApiResponse;
import com.demo.weatherapi.exception.RetryableException;
import com.demo.weatherapi.util.ApiResponse;
import com.demo.weatherapi.util.ApiResponseEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class WeatherApiService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestClientService restClientService;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApiService.class);

    public Daily getWarmestDay(Double latitude, Double longitude) throws RetryableException, JsonProcessingException {
        ResponseEntity responseEntity = restClientService.callWeatherApi(latitude, longitude);
        WeatherApiResponse weatherApiResponse = objectMapper.readValue(responseEntity.getBody().toString(), WeatherApiResponse.class);
        return calculateWarmestDay(weatherApiResponse.daily);
    }

    private Daily calculateWarmestDay(List<Daily> dailyTemperatures) {
        Map<Double, List<Daily>> maxTempMap= new HashMap<>();
        AtomicDouble maxTemp = new AtomicDouble(-999);

        dailyTemperatures.stream().forEach(dailyTemperature -> {
            if(BigDecimal.valueOf(dailyTemperature.temp.max).compareTo(BigDecimal.valueOf(maxTemp.doubleValue())) == 1) {
                maxTemp.set(dailyTemperature.temp.max);
                List<Daily> tempDaily = maxTempMap.get(dailyTemperature.temp.max);
                if (tempDaily == null) {
                    tempDaily = new ArrayList<>();
                }
                tempDaily.add(dailyTemperature);
                maxTempMap.put(dailyTemperature.temp.max, tempDaily);
            }
        });

        List<Daily> tempDaily = maxTempMap.get(maxTemp.doubleValue());

        List<Daily> humidityDaily = null;

        if (tempDaily.size() == 1) {
            return tempDaily.get(0);
        } else if (tempDaily.size() > 1) {
            humidityDaily = getMaxHumidityDays(tempDaily);
        }

        if (humidityDaily.size() == 1) {
            return humidityDaily.get(0);
        } else if (humidityDaily.size() > 1) {
            return getFirstDay(humidityDaily);
        }

        return null;
    }

    private List<Daily> getMaxHumidityDays(List<Daily> dailyHumidities) {
        Map<Integer, List<Daily>> maxHumidityMap= new HashMap<>();
        AtomicInteger maxHumidity = new AtomicInteger(-999);

        dailyHumidities.stream().forEach(dailyHumidity -> {
            if(dailyHumidity.humidity > maxHumidity.get()) {
                maxHumidity.set(dailyHumidity.humidity);
                List<Daily> humidityDaily = maxHumidityMap.get(dailyHumidity.humidity);
                if (humidityDaily == null) {
                    humidityDaily = new ArrayList<>();
                }
                humidityDaily.add(dailyHumidity);
                maxHumidityMap.put(dailyHumidity.humidity, humidityDaily);
            }
        });

        return maxHumidityMap.get(maxHumidity.get());
    }

    private Daily getFirstDay(List<Daily> dailyList) {
        AtomicInteger minFirstDay = new AtomicInteger(Integer.MAX_VALUE);
        Map<Integer, Daily> firstDay = new HashMap<>();
        dailyList.stream().forEach(daily -> {
            if(daily.dt < minFirstDay.get()) {
                minFirstDay.set(daily.dt);
                firstDay.put(minFirstDay.get(), daily);
            }
        });

        return firstDay.get(minFirstDay.get());
    }
}
