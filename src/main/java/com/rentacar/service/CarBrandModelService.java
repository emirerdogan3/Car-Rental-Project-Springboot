package com.rentacar.service;

import com.rentacar.entity.CarBrandModel;
import com.rentacar.dto.CarBrandModelDTO;
import java.util.List;
import java.util.Optional;

public interface CarBrandModelService {
    CarBrandModel createCarBrandModel(CarBrandModel carBrandModel);
    CarBrandModel updateCarBrandModel(Integer id, CarBrandModel carBrandModel);
    void deleteCarBrandModel(Integer id);
    Optional<CarBrandModel> getCarBrandModelById(Integer id);
    List<CarBrandModel> getAllCarBrandModels();
    List<CarBrandModelDTO> getModelsByBrandId(Integer brandId);
    List<CarBrandModelDTO> getAllCarBrandModelDTOs();
} 