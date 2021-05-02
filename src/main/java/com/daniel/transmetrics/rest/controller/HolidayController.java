package com.daniel.transmetrics.rest.controller;

import com.daniel.transmetrics.repository.entity.Holiday;
import com.daniel.transmetrics.rest.model.PaginatedHolidayResponse;
import com.daniel.transmetrics.service.HolidayService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/holidays")
@AllArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping(path = "/simple", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Holiday>> holidays(@RequestParam("country-code") String countryCode, @RequestParam("year") Integer year) {
        List<Holiday> response = holidayService.getHolidaysByCountryForAGivenYear(countryCode, year);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/paginated", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedHolidayResponse> paginatedHolidays(@RequestParam("country-code") String countryCode,
                                                                      @RequestParam("year") Integer year,
                                                                      @RequestParam(defaultValue = "0", name = "page-number") Integer pageNumber,
                                                                      @RequestParam(defaultValue = "10", name = "page-size") Integer pageSize) throws MalformedURLException, URISyntaxException {
        PaginatedHolidayResponse response = holidayService.getPaginatedHolidaysByCountryForAGivenYear(countryCode,
                year,
                pageSize,
                pageNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/stream")
    @Transactional
    public void streamHolidays(@RequestParam("country-code") String countryCode, @RequestParam("year") Integer year,
                               HttpServletResponse response) throws IOException {
        holidayService.writeHolidaysByCountryForAGivenYearAsStream(countryCode, year, response);
    }
}
