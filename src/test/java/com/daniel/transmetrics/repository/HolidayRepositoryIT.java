package com.daniel.transmetrics.repository;


import com.daniel.transmetrics.TestUtil;
import com.daniel.transmetrics.TransmetricsApplication;
import com.daniel.transmetrics.repository.entity.Holiday;
import com.daniel.transmetrics.rest.model.DateApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TransmetricsApplication.class)
@ActiveProfiles("it")
public class HolidayRepositoryIT {

    @Autowired
    private HolidayRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void testFindAllByCountryAndDateBetweenReturnSavedHolidays() throws JsonProcessingException {
        String countryCode = "ro";
        LocalDate start = LocalDate.of(2022, 1, 1);
        LocalDate end = LocalDate.of(2022, 12, 31);

        List<Holiday> holidays = TestUtil.getFromFileAsObject(DateApiResponse.class, "fixtures/api_response_ro_2022.json").getHolidays();
        repository.saveAll(holidays);
        List<Holiday> persistedHolidays = repository.findAllByCountryIdAndDateBetween(countryCode, start, end);

        assertThat(persistedHolidays).containsAll(holidays);
    }

    @Test
    public void testFindAllByCountryAndDateBetweenDoesReturnZero() throws JsonProcessingException {
        String countryCode = "ro";
        LocalDate start = LocalDate.of(2022, 1, 1);
        LocalDate end = LocalDate.of(2022, 12, 31);

        List<Holiday> holidays = TestUtil.getFromFileAsObject(DateApiResponse.class, "fixtures/api_response_ro_2022.json").getHolidays();
        repository.saveAll(holidays);
        List<Holiday> persistedHolidays = repository.findAllByCountryIdAndDateBetween(countryCode, start.plusYears(2), end.plusYears(3));

        assertThat(persistedHolidays).isEmpty();
    }

    @Test
    public void testExistsByCountryIdAndDateBetweenReturnsTrue() throws JsonProcessingException {
        String countryCode = "ro";
        LocalDate start = LocalDate.of(2022, 1, 1);
        LocalDate end = LocalDate.of(2022, 12, 31);

        List<Holiday> holidays = TestUtil.getFromFileAsObject(DateApiResponse.class, "fixtures/api_response_ro_2022.json").getHolidays();
        repository.saveAll(holidays);
        Boolean dataExistsInDb = repository.existsByCountryIdAndDateBetween(countryCode, start, end);

        assertThat(dataExistsInDb).isTrue();
    }

    @Test
    public void testExistsByCountryIdAndDateBetweenReturnsFalse() {
        String countryCode = "ro";
        LocalDate start = LocalDate.of(2022, 1, 1);
        LocalDate end = LocalDate.of(2022, 12, 31);
        Boolean dataExistsInDb = repository.existsByCountryIdAndDateBetween(countryCode, start, end);

        assertThat(dataExistsInDb).isFalse();
    }

}
