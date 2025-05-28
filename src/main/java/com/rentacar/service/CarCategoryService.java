package com.rentacar.service;

import com.rentacar.dto.CarCategoryDTO;
import com.rentacar.entity.CarCategory;

import java.util.List;
import java.util.Optional;

public interface CarCategoryService {
    List<CarCategoryDTO> getAllCarCategories();
    Optional<CarCategoryDTO> getCarCategoryDtoById(Integer id);
    CarCategoryDTO createCarCategory(CarCategoryDTO carCategoryDTO);
    CarCategoryDTO updateCarCategory(Integer id, CarCategoryDTO carCategoryDTO);
    void deleteCarCategory(Integer id);
    // Internal use for mapper if needed
    Optional<CarCategory> getCarCategoryById(Integer id);
} 