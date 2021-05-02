package com.daniel.transmetrics.repository;

import com.daniel.transmetrics.repository.entity.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public interface HolidayRepository extends CrudRepository<Holiday, String> {

    List<Holiday> findAllByCountryIdAndDateBetween(String countryId, LocalDate fromDate, LocalDate endDate);

    @Query(value = "SELECT h from Holiday h where h.countryId = :countryId and h.date between :fromDate and :endDate order by h.date")
    Stream<Holiday> findAllByCountryIdAndDateBetweenAsStream(@Param("countryId") String countryId,
                                                             @Param("fromDate") LocalDate fromDate,
                                                             @Param("endDate") LocalDate endDate);

    Page<Holiday> findAllByCountryIdAndDateBetweenOrderByDate(String countryId, LocalDate fromDate, LocalDate endDate, Pageable pageable);

    Boolean existsByCountryIdAndDateBetween(String countryId, LocalDate fromDate, LocalDate endDate);
}
