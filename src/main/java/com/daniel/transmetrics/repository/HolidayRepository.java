package com.daniel.transmetrics.repository;

import com.daniel.transmetrics.repository.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findAllByCountryIdAndDateBetween(String countryId, LocalDate fromDate, LocalDate endDate);

    Boolean existsByCountryIdAndDateBetween(String countryId, LocalDate fromDate, LocalDate endDate);
}
