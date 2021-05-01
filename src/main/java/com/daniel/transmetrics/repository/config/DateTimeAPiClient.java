package com.daniel.transmetrics.repository.config;

import com.daniel.transmetrics.rest.model.DateApiResponse;
import feign.QueryMap;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Map;
import java.util.Optional;

@FeignClient(name = "dateTimeAPiClient")
public interface DateTimeAPiClient {

    @RequestLine("GET /holidays")
    Optional<DateApiResponse> requestHolidaysForCountryAndYear(@QueryMap Map<String, Object> queryMap);

}
