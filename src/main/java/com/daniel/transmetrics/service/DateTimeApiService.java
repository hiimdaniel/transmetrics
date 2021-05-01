package com.daniel.transmetrics.service;

import com.daniel.transmetrics.repository.entity.Holiday;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DateTimeApiService {

    public List<Holiday> requestHolidaysOfYearForCountry(String countryCode,Integer year) {
        //TODO
        return List.of();
    }
}
