package com.daniel.transmetrics.repository;

import com.daniel.transmetrics.repository.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
}
