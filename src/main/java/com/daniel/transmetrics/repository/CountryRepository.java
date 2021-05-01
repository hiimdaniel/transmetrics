package com.daniel.transmetrics.repository;

import com.daniel.transmetrics.repository.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country,String> {
}
