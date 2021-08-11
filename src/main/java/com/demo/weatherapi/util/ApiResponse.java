package com.demo.weatherapi.util;

import lombok.*;

@Builder
@Data
public class ApiResponse {
    private String status;
    private Object payload;
}
