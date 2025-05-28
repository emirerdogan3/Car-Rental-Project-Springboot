package com.rentacar.service.impl;

import com.rentacar.entity.CarBrand;
import com.rentacar.repository.CarBrandRepository;
import com.rentacar.service.CarBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarBrandServiceImpl implements CarBrandService {
    private final CarBrandRepository carBrandRepository;

    @Autowired
    public CarBrandServiceImpl(CarBrandRepository carBrandRepository) {
        this.carBrandRepository = carBrandRepository;
    }

    @Override
    public CarBrand createCarBrand(CarBrand carBrand) {
        return carBrandRepository.save(carBrand);
    }

    @Override
    public CarBrand updateCarBrand(Integer id, CarBrand carBrand) {
        carBrand.setBrandID(id);
        return carBrandRepository.save(carBrand);
    }

    @Override
    public void deleteCarBrand(Integer id) {
        carBrandRepository.deleteById(id);
    }

    @Override
    public Optional<CarBrand> getCarBrandById(Integer id) {
        return carBrandRepository.findById(id);
    }

    @Override
    public List<CarBrand> getAllCarBrands() {
        return carBrandRepository.findAll();
    }
} 