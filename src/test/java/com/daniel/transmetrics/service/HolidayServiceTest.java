package com.daniel.transmetrics.service;

import com.daniel.transmetrics.TestUtil;
import com.daniel.transmetrics.repository.HolidayRepository;
import com.daniel.transmetrics.repository.entity.Holiday;
import com.daniel.transmetrics.rest.model.PaginatedHolidayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.daniel.transmetrics.TestUtil.createTestHoliday;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class HolidayServiceTest {

    private HolidayService underTest;

    private HolidayRepository mockRepository;

    private DateTimeApiService mockApiService;

    @BeforeEach
    public void setUp() {
        ServerProperties serverProperties = new ServerProperties();
        serverProperties.setPort(8080);
        mockApiService = Mockito.mock(DateTimeApiService.class);
        mockRepository = Mockito.mock(HolidayRepository.class);
        underTest = new HolidayService(mockRepository, mockApiService, serverProperties, TestUtil.MAPPER);
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

    @Test
    public void testGetPaginatedHolidaysByCountryForAGivenYearShouldCallAPIAndReturnPaginatedResponse() throws MalformedURLException, URISyntaxException {
        Integer year = 1997;
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        String countryCode = "test";
        Holiday testHoliday = createTestHoliday(countryCode);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Holiday> holidayPage = constructResultPage(List.of(testHoliday), pageable);
        URI expectedNExtPage = URI.create("http://127.0.0.1:8080/holidays/paginated?country-code=test&year=1997&page-number=1&page-size=10");

        Mockito.when(mockRepository.existsByCountryIdAndDateBetween(countryCode, startDate, endDate))
                .thenReturn(false);

        Mockito.when(mockApiService.requestHolidaysOfYearForCountry(countryCode, year))
                .thenReturn(List.of(testHoliday));

        Mockito.when(mockRepository.findAllByCountryIdAndDateBetweenOrderByDate(countryCode, startDate, endDate, pageable))
                .thenReturn(holidayPage);

        PaginatedHolidayResponse response = underTest.getPaginatedHolidaysByCountryForAGivenYear(countryCode, year, 10, 0);

        verify(mockRepository, times(1)).existsByCountryIdAndDateBetween(countryCode, startDate, endDate);
        verify(mockApiService, times(1)).requestHolidaysOfYearForCountry(countryCode, year);
        verify(mockRepository, times(1)).saveAll(Mockito.any());
        verify(mockRepository, times(1)).findAllByCountryIdAndDateBetweenOrderByDate(countryCode, startDate, endDate, pageable);
        assertEquals(expectedNExtPage, response.getNextPage());
        assertEquals(15, response.getTotalElements());
        assertTrue(response.getFirst());
        assertFalse(response.getLast());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(2, response.getTotalPages());
    }

    @Test
    public void testWriteHolidaysByCountryForAGivenYearAsStreamShouldCallAPIAndWriteToResponseAsStream() throws IOException, URISyntaxException {
        Integer year = 1997;
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        String countryCode = "test";
        Holiday testHoliday = createTestHoliday(countryCode);
        List<Integer> streamOutput = new ArrayList<>();
        ServletOutputStream outputStream = getServletOutputStream(streamOutput);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getOutputStream()).thenReturn(outputStream);

        Mockito.when(mockRepository.existsByCountryIdAndDateBetween(countryCode, startDate, endDate))
                .thenReturn(false);

        Mockito.when(mockApiService.requestHolidaysOfYearForCountry(countryCode, year))
                .thenReturn(List.of(testHoliday));

        Mockito.when(mockRepository.findAllByCountryIdAndDateBetweenAsStream(countryCode, startDate, endDate))
                .thenReturn(Stream.of(testHoliday));

        underTest.writeHolidaysByCountryForAGivenYearAsStream(countryCode, year, response);

        verify(mockRepository, times(1)).existsByCountryIdAndDateBetween(countryCode, startDate, endDate);
        verify(mockApiService, times(1)).requestHolidaysOfYearForCountry(countryCode, year);
        verify(mockRepository, times(1)).saveAll(Mockito.any());
        verify(mockRepository, times(1)).findAllByCountryIdAndDateBetweenAsStream(countryCode, startDate, endDate);
        assertFalse(streamOutput.isEmpty());
    }

    private ServletOutputStream getServletOutputStream(List<Integer> streamOutput) {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) {
                streamOutput.add(b);
            }
        };
    }

    private Page<Holiday> constructResultPage(List<Holiday> holidays, Pageable pageable) {
        return new PageImpl<>(holidays, pageable, 15);
    }

}
