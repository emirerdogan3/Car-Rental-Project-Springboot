package com.rentacar.repository;

import com.rentacar.entity.CarBrandModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarBrandModelRepository extends JpaRepository<CarBrandModel, Integer> {
    List<CarBrandModel> findByBrandBrandID(Integer brandId);
} 