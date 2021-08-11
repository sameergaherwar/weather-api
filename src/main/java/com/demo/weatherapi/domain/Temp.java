package com.demo.weatherapi.domain;

import lombok.Data;

@Data
public class Temp {
    public double day;
    public double min;
    public double max;
    public double night;
    public double eve;
    public double morn;
}
