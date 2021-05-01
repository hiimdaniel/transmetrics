package com.daniel.transmetrics.service;

import com.daniel.transmetrics.repository.config.DateTimeAPiClient;
import com.daniel.transmetrics.repository.entity.Holiday;
import com.daniel.transmetrics.service.exception.HolidayApiException;
import com.daniel.transmetrics.service.exception.HolidayNotFoundException;
import com.daniel.transmetrics.rest.model.DateApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DateTimeApiService {

    private final DateTimeAPiClient dateTimeAPiClient;

    public List<Holiday> requestHolidaysOfYearForCountry(String countryCode, Integer year) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("country", countryCode);
        queryMap.put("year", year);
        Optional<DateApiResponse> apiResponse = dateTimeAPiClient.requestHolidaysForCountryAndYear(queryMap);
        if (apiResponse.isPresent()) {
            if (apiResponse.get().getErrors().isEmpty()) {
                return apiResponse.get().getHolidays();
            } else {
                throw new HolidayApiException(countryCode, year, apiResponse.get().getErrors().get(0));
            }
        } else {
            throw new HolidayNotFoundException(countryCode, year);
        }
    }
}
