package com.daniel.transmetrics.service;

import com.daniel.transmetrics.repository.HolidayRepository;
import com.daniel.transmetrics.repository.entity.Holiday;
import com.fasterxml.jackson.databind.node.DecimalNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class HolidayServiceTest {

    private HolidayService underTest;

    private HolidayRepository mockRepository;

    private DateTimeApiService mockApiService;

    @BeforeEach
    public void setUp() {
        mockApiService = Mockito.mock(DateTimeApiService.class);
        mockRepository = Mockito.mock(HolidayRepository.class);
        underTest = new HolidayService(mockRepository, mockApiService);
    }

    @Test
    public void testGetHolidaysByCountryForAGivenYearShouldCallAPI() {
        Integer year = 1997;
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        String countryCode = "test";
        Holiday testHoliday = createTestHoliday(countryCode);

        Mockito.when(mockRepository.existsByCountryIdAndDateBetween(countryCode, startDate, endDate))
                .thenReturn(false);

        Mockito.when(mockApiService.requestHolidaysOfYearForCountry(countryCode, year))
                .thenReturn(List.of(testHoliday));

        Mockito.when(mockRepository.findAllByCountryIdAndDateBetween(countryCode, startDate, endDate))
                .thenReturn(List.of(testHoliday));

        underTest.getHolidaysByCountryForAGivenYear(countryCode, year);

        verify(mockRepository, times(1)).existsByCountryIdAndDateBetween(countryCode, startDate, endDate);
        verify(mockApiService, times(1)).requestHolidaysOfYearForCountry(countryCode, year);
        verify(mockRepository, times(1)).saveAll(Mockito.any());
        verify(mockRepository, times(1)).findAllByCountryIdAndDateBetween(countryCode, startDate, endDate);
    }

    @Test
    public void testGetHolidaysByCountryForAGivenYearShouldNotCallAPI() {
        Integer year = 1997;
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        String countryCode = "test";
        Holiday testHoliday = createTestHoliday(countryCode);

        Mockito.when(mockRepository.existsByCountryIdAndDateBetween(countryCode, startDate, endDate))
                .thenReturn(true);

        Mockito.when(mockRepository.findAllByCountryIdAndDateBetween(countryCode, startDate, endDate))
                .thenReturn(List.of(testHoliday));

        underTest.getHolidaysByCountryForAGivenYear(countryCode, year);

        verify(mockRepository, times(1)).existsByCountryIdAndDateBetween(countryCode, startDate, endDate);
        verify(mockApiService, never()).requestHolidaysOfYearForCountry(countryCode, year);
        verify(mockRepository, never()).saveAll(Mockito.any());
        verify(mockRepository, times(1)).findAllByCountryIdAndDateBetween(countryCode, startDate, endDate);
    }

    private Holiday createTestHoliday(String countryCode) {
        return Holiday.builder()
                .countryId(countryCode)
                .date(LocalDate.now())
                .id(1L)
                .name("Test holiday")
                .oneLiner("Oneliner description of the test holiday")
                .subtype(new DecimalNode(BigDecimal.ONE))
                .types(new DecimalNode(BigDecimal.ONE))
                .uid("test uid")
                .url("Test url")
                .urlId("test url ID")
                .build();
    }
}
