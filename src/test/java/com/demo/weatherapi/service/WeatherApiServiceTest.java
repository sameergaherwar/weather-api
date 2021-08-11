package com.demo.weatherapi.service;

import com.demo.weatherapi.domain.Daily;
import com.demo.weatherapi.domain.Temp;
import com.demo.weatherapi.domain.WeatherApiResponse;
import com.demo.weatherapi.exception.RetryableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class WeatherApiServiceTest {

    @Mock
    RestClientService restClientService;

    @Mock
    RestTemplate restTemplate;

    @Spy
    ObjectMapper objectMapper;

    @InjectMocks
    private WeatherApiService weatherApiService;

    @Test
    public void test_warmestday_having_different_max_temperature() throws RetryableException, JsonProcessingException {
        WeatherApiResponse weatherApiResponse = new WeatherApiResponse();
        List<Daily> dailyList = new ArrayList<>();
        Daily daily = new Daily();
        daily.dt = 1628402400;
        Temp temp = new Temp();
        temp.max = 102.87;
        daily.humidity = 76;
        daily.temp = temp;
        dailyList.add(daily);

        Daily daily1 = new Daily();
        daily1.dt = 1628406000;
        Temp temp1 = new Temp();

        temp1.max = 100.87;
        daily1.temp = temp1;
        daily1.humidity = 77;
        dailyList.add(daily1);
        weatherApiResponse.daily = dailyList;

        String responseJson = objectMapper.writeValueAsString(weatherApiResponse);
        Mockito.when(restClientService.callWeatherApi(any(), any())).thenReturn(new ResponseEntity<>(responseJson, HttpStatus.OK));

        Daily warmestDay = weatherApiService.getWarmestDay(any(), any());
        Assertions.assertThat(warmestDay.temp.max).isEqualTo(daily.temp.max);
    }

    @Test
    public void test_warmestday_having_same_max_temperature() throws RetryableException, JsonProcessingException {
        WeatherApiResponse weatherApiResponse = new WeatherApiResponse();
        List<Daily> dailyList = new ArrayList<>();
        Daily daily = new Daily();
        daily.dt = 1628402400;
        Temp temp = new Temp();
        temp.max = 102.87;
        daily.humidity = 76;
        daily.temp = temp;
        dailyList.add(daily);

        Daily daily1 = new Daily();
        daily1.dt = 1628406000;
        Temp temp1 = new Temp();

        temp1.max = 102.87;
        daily1.temp = temp1;
        daily1.humidity = 77;
        dailyList.add(daily1);
        weatherApiResponse.daily = dailyList;

        String responseJson = objectMapper.writeValueAsString(weatherApiResponse);
        Mockito.when(restClientService.callWeatherApi(any(), any())).thenReturn(new ResponseEntity<>(responseJson, HttpStatus.OK));

        Daily warmestDay = weatherApiService.getWarmestDay(any(), any());
        Assertions.assertThat(warmestDay.temp.max).isEqualTo(daily1.temp.max);
    }

    @Test
    public void test_warmestday_having_same_max_temp_and_humidities() throws RetryableException, JsonProcessingException {
        WeatherApiResponse weatherApiResponse = new WeatherApiResponse();
        List<Daily> dailyList = new ArrayList<>();
        Daily daily = new Daily();
        daily.dt = 1628402400;
        Temp temp = new Temp();
        temp.max = 102.87;
        daily.humidity = 76;
        daily.temp = temp;
        dailyList.add(daily);

        Daily daily1 = new Daily();
        daily1.dt = 1628406000;
        Temp temp1 = new Temp();

        temp1.max = 102.87;
        daily1.temp = temp1;
        daily1.humidity = 76;
        dailyList.add(daily1);
        weatherApiResponse.daily = dailyList;

        String responseJson = objectMapper.writeValueAsString(weatherApiResponse);
        Mockito.when(restClientService.callWeatherApi(any(), any())).thenReturn(new ResponseEntity<>(responseJson, HttpStatus.OK));

        Daily warmestDay = weatherApiService.getWarmestDay(any(), any());
        Assertions.assertThat(warmestDay.dt).isEqualTo(daily.dt);
    }
}
