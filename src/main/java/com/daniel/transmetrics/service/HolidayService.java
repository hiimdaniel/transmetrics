package com.daniel.transmetrics.service;

import com.daniel.transmetrics.repository.HolidayRepository;
import com.daniel.transmetrics.repository.entity.Holiday;
import com.daniel.transmetrics.rest.model.PaginatedHolidayResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class HolidayService {

    private final HolidayRepository holidayRepository;

    private final DateTimeApiService dateTimeApiService;

    private final ServerProperties serverProperties;

    private final ObjectMapper objectMapper;

    public PaginatedHolidayResponse getPaginatedHolidaysByCountryForAGivenYear(String countryId, Integer year, Integer pageSize, Integer pageNumber) throws MalformedURLException, URISyntaxException {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        checkAndCallAPIIfNeeded(countryId, year, startDate);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Holiday> result = holidayRepository.findAllByCountryIdAndDateBetweenOrderByDate(countryId, startDate, endDate, pageable);
        return buildPaginatedHolidayResponse(result, countryId, year);
    }

    public List<Holiday> getHolidaysByCountryForAGivenYear(String countryId, Integer year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        checkAndCallAPIIfNeeded(countryId, year, startDate);
        return getHolidaysByCountryWithinTimeRange(countryId, startDate, endDate);
    }

    @Transactional
    public void writeHolidaysByCountryForAGivenYearAsStream(String countryId, Integer year, HttpServletResponse response) throws IOException {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        checkAndCallAPIIfNeeded(countryId, year, startDate);
        try (Stream<Holiday> stream = holidayRepository.findAllByCountryIdAndDateBetweenAsStream(countryId, startDate, endDate)) {
            final Writer writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
            objectMapper.writerFor(Iterator.class).writeValue(writer, stream.iterator());
        }
    }

    private void checkAndCallAPIIfNeeded(String countryId, Integer year, LocalDate startDate) {
        if (!checkIfHolidayExistsAtYearForCountry(countryId, startDate)) {
            log.debug("Holidays for country {} for year {} could not be found in DB. Requesting data from API", countryId, year);
            List<Holiday> fromApi = dateTimeApiService.requestHolidaysOfYearForCountry(countryId, year);
            log.debug("Holidays for country {} for year {} requested from API. Quantity of results: {}. Persisting returned holidays.", countryId, year, fromApi.size());
            holidayRepository.saveAll(fromApi);
        }
    }

    private List<Holiday> getHolidaysByCountryWithinTimeRange(String countryId, LocalDate startDate, LocalDate endDate) {
        return holidayRepository.findAllByCountryIdAndDateBetween(countryId, startDate, endDate);
    }

    private Boolean checkIfHolidayExistsAtYearForCountry(String countryId, LocalDate date) {
        LocalDate startDate = date.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfYear());
        return holidayRepository.existsByCountryIdAndDateBetween(countryId, startDate, endDate);
    }

    private PaginatedHolidayResponse buildPaginatedHolidayResponse(Page<Holiday> currentPage, String countryId, Integer year) throws URISyntaxException {
        Integer nextPageNo = currentPage.getNumber() + 1;
        String query = "country-code=" + countryId + "&year=" + year + "&page-number=" + nextPageNo + "&page-size=" + currentPage.getSize();

        PaginatedHolidayResponse.NextPage nextPage = PaginatedHolidayResponse.NextPage.builder()
                .host(InetAddress.getLoopbackAddress().getHostAddress())
                .path("/holidays/paginated")
                .port(serverProperties.getPort())
                .protocol("http")
                .nextPageNo(nextPageNo)
                .queryString(query)
                .build();

        return PaginatedHolidayResponse.builder()
                .first(currentPage.isFirst())
                .holidays(currentPage.getContent())
                .nextPage(nextPage.getURI())
                .last(currentPage.isLast())
                .pageNumber(currentPage.getNumber())
                .pageSize(currentPage.getSize())
                .totalElements(currentPage.getTotalElements())
                .totalPages(currentPage.getTotalPages())
                .build();
    }


}
