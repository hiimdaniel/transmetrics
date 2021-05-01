package com.daniel.transmetrics.service;

import com.daniel.transmetrics.repository.HolidayRepository;
import com.daniel.transmetrics.repository.entity.Holiday;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@AllArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;

    private final DateTimeApiService dateTimeApiService;

    public List<Holiday> getHolidaysByCountryForAGivenYear(String countryCode, Integer year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        if (!checkIfHolidayExistsAtYearForCountry(countryCode, startDate)) {
            List<Holiday> fromApi = dateTimeApiService.requestHolidaysOfYearForCountry(countryCode, year);
            holidayRepository.saveAll(fromApi);
        }
        return getHolidaysByCountryWithinTimeRange(countryCode, startDate, endDate);
    }

    private List<Holiday> getHolidaysByCountryWithinTimeRange(String countryCode, LocalDate startDate, LocalDate endDate) {
        return holidayRepository.findAllByCountryIdAndDateBetween(countryCode, startDate, endDate);
    }

    private Boolean checkIfHolidayExistsAtYearForCountry(String countryCode, LocalDate date) {
        LocalDate startDate = date.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfYear());
        return holidayRepository.existsByCountryIdAndDateBetween(countryCode, startDate, endDate);
    }


}
