package com.demo.weatherapi.service;

import com.demo.weatherapi.exception.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class RestClientService {

    @Value("${weatherApi.url}")
    private String url;

    @Value("${weatherApi.appId}")
    private String appId;

    @Autowired
    private RestTemplate restTemplate;

    @Retryable(value = RetryableException.class, backoff = @Backoff(delay = 100))
    public ResponseEntity callWeatherApi(Double latitude, Double longitude) throws RetryableException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>("", headers);

        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append(url);
        fullUrl.append("?lat=");
        fullUrl.append(latitude.doubleValue());
        fullUrl.append("&lon=");
        fullUrl.append(longitude.doubleValue());
        fullUrl.append("&appid=");
        fullUrl.append(appId);

        ResponseEntity<String> response = Optional.ofNullable(restTemplate.exchange(fullUrl.toString(), HttpMethod.GET, entity, String.class)).orElseThrow();

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            return response;
        }

        if (HttpStatus.GATEWAY_TIMEOUT.equals(response.getStatusCode())
                || HttpStatus.REQUEST_TIMEOUT.equals(response.getStatusCode())) {
            throw new RetryableException();
        }

        return response;
    }
}
