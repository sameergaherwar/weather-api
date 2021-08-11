package com.demo.weatherapi.controller;

import com.demo.weatherapi.service.WeatherApiService;
import com.demo.weatherapi.util.ApiResponse;
import com.demo.weatherapi.util.ApiResponseEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@RequestMapping("/api/v1")
@EnableSwagger2
public class WeatherApiController {

    @Autowired
    private WeatherApiService weatherApiService;

    @ApiOperation(value = "Gets warmest day", notes = "Gets warmest day")
    @GetMapping(value = "warmestday", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getWarmestDay(@RequestParam(name = "lat") Double latitude, @RequestParam(name = "long") Double longitude) {
        try {
            return ResponseEntity.ok(ApiResponse.builder().payload(weatherApiService.getWarmestDay(latitude, longitude)).status(ApiResponseEnum.SUCCESS.toString()).build());
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.builder().payload("Error occured while processing the request").status(ApiResponseEnum.ERROR.toString()).build());
        }
    }
}
