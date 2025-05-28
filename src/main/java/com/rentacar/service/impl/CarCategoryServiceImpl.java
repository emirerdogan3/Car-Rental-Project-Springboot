package com.rentacar.service.impl;

import com.rentacar.dto.CarCategoryDTO;
import com.rentacar.entity.CarCategory;
import com.rentacar.mapper.CarCategoryMapper;
import com.rentacar.repository.CarCategoryRepository;
import com.rentacar.service.CarCategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarCategoryServiceImpl implements CarCategoryService {

    private final CarCategoryRepository carCategoryRepository;
    private final CarCategoryMapper carCategoryMapper;

    @Override
    public List<CarCategoryDTO> getAllCarCategories() {
        return carCategoryRepository.findAll().stream()
                .map(carCategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CarCategoryDTO> getCarCategoryDtoById(Integer id) {
        return carCategoryRepository.findById(id)
                .map(carCategoryMapper::toDto);
    }
    
    @Override
    public Optional<CarCategory> getCarCategoryById(Integer id) { // For internal use (e.g. mappers)
        return carCategoryRepository.findById(id);
    }

    @Override
    @Transactional
    public CarCategoryDTO createCarCategory(CarCategoryDTO carCategoryDTO) {
        CarCategory carCategory = carCategoryMapper.toEntity(carCategoryDTO);
        // Add any validation if needed, e.g., check for unique name
        return carCategoryMapper.toDto(carCategoryRepository.save(carCategory));
    }

    @Override
    @Transactional
    public CarCategoryDTO updateCarCategory(Integer id, CarCategoryDTO carCategoryDTO) {
        CarCategory existingCategory = carCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CarCategory not found with id: " + id));
        // Update fields
        existingCategory.setCategoryName(carCategoryDTO.getCategoryName());
        // Add any other updatable fields
        return carCategoryMapper.toDto(carCategoryRepository.save(existingCategory));
    }

    @Override
    @Transactional
    public void deleteCarCategory(Integer id) {
        if (!carCategoryRepository.existsById(id)) {
            throw new EntityNotFoundException("CarCategory not found with id: " + id);
        }
        // Add check for dependencies (e.g., if any Car uses this category) before deleting
        carCategoryRepository.deleteById(id);
    }
} 