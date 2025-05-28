package com.rentacar.repository;

import com.rentacar.entity.County;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountyRepository extends JpaRepository<County, Integer> {
    List<County> findByCity_CityID(Integer cityId);
} 