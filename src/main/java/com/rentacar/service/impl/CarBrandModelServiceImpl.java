package com.rentacar.service.impl;

import com.rentacar.dto.CarBrandModelDTO;
import com.rentacar.entity.CarBrandModel;
import com.rentacar.mapper.CarBrandModelMapper;
import com.rentacar.repository.CarBrandModelRepository;
import com.rentacar.service.CarBrandModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarBrandModelServiceImpl implements CarBrandModelService {
    private final CarBrandModelRepository carBrandModelRepository;
    private final CarBrandModelMapper carBrandModelMapper;

    @Override
    public CarBrandModel createCarBrandModel(CarBrandModel carBrandModel) {
        return carBrandModelRepository.save(carBrandModel);
    }

    @Override
    public CarBrandModel updateCarBrandModel(Integer id, CarBrandModel carBrandModel) {
        carBrandModel.setModelID(id);
        return carBrandModelRepository.save(carBrandModel);
    }

    @Override
    public void deleteCarBrandModel(Integer id) {
        carBrandModelRepository.deleteById(id);
    }

    @Override
    public Optional<CarBrandModel> getCarBrandModelById(Integer id) {
        return carBrandModelRepository.findById(id);
    }

    @Override
    public List<CarBrandModel> getAllCarBrandModels() {
        return carBrandModelRepository.findAll();
    }

    @Override
    public List<CarBrandModelDTO> getAllCarBrandModelDTOs() {
        return carBrandModelRepository.findAll().stream()
                .map(carBrandModelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarBrandModelDTO> getModelsByBrandId(Integer brandId) {
        return carBrandModelRepository.findByBrandBrandID(brandId).stream()
                .map(carBrandModelMapper::toDto)
                .collect(Collectors.toList());
    }
} 