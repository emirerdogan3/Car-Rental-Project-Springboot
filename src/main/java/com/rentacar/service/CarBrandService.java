package com.rentacar.service;

import com.rentacar.entity.CarBrand;
import java.util.List;
import java.util.Optional;

public interface CarBrandService {
    CarBrand createCarBrand(CarBrand carBrand);
    CarBrand updateCarBrand(Integer id, CarBrand carBrand);
    void deleteCarBrand(Integer id);
    Optional<CarBrand> getCarBrandById(Integer id);
    List<CarBrand> getAllCarBrands();
} 