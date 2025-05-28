package com.rentacar.service;

import com.rentacar.dto.CarDTO;
import com.rentacar.dto.filter.CarFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CarService {
    Page<CarDTO> findCarsByFilters(CarFilterDTO filter, Pageable pageable);

    Page<CarDTO> findCarsByBranchAndFilters(Integer branchId, CarFilterDTO filter, Pageable pageable);

    Page<CarDTO> findAvailableCarsByBranchAndFilters(Integer branchId, CarFilterDTO filter, Pageable pageable);

    // New method for checking availability with date range
    Page<CarDTO> findAvailableCarsByBranchAndFilters(Integer branchId, CarFilterDTO filter, Pageable pageable, java.util.Date startDate, java.util.Date endDate);

    Optional<CarDTO> getCarDtoById(Integer id);

    CarDTO createCar(CarDTO carDTO);

    CarDTO updateCar(Integer id, CarDTO carDTO);

    void deleteCar(Integer id);

    // New methods for dashboard stats
    long countCarsByBranch(Integer branchId);
    long countAvailableCarsByBranch(Integer branchId);
} 