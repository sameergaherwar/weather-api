package com.demo.weatherapi.domain;

import lombok.Data;

@Data
public class FeelsLike {
    public double day;
    public double night;
    public double eve;
    public double morn;
}
