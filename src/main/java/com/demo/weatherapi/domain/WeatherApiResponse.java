package com.demo.weatherapi.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class WeatherApiResponse {
    public double lat;
    public double lon;
    public String timezone;
    public int timezone_offset;
    public Current current;
    public List<Minutely> minutely;
    public List<Hourly> hourly;
    public List<Daily> daily;
}
