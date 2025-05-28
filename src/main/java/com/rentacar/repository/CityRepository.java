package com.rentacar.repository;

import com.rentacar.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findByCountry_CountryID(Integer countryId);
} 