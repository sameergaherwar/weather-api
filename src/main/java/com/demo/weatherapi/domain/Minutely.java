package com.demo.weatherapi.domain;

import lombok.Data;

@Data
public class Minutely {
    public int dt;
    public int precipitation;
}
