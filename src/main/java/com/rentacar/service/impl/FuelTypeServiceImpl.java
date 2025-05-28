package com.rentacar.service.impl;

import com.rentacar.dto.FuelTypeDTO;
import com.rentacar.entity.FuelType;
import com.rentacar.mapper.FuelTypeMapper;
import com.rentacar.repository.FuelTypeRepository;
import com.rentacar.service.FuelTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelTypeServiceImpl implements FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;
    private final FuelTypeMapper fuelTypeMapper;

    @Override
    public List<FuelTypeDTO> getAllFuelTypes() {
        return fuelTypeRepository.findAll().stream()
                .map(fuelTypeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FuelTypeDTO> getFuelTypeDtoById(Integer id) {
        return fuelTypeRepository.findById(id).map(fuelTypeMapper::toDto);
    }

    @Override
    public Optional<FuelType> getFuelTypeById(Integer id) {
        return fuelTypeRepository.findById(id);
    }

    @Override
    @Transactional
    public FuelTypeDTO createFuelType(FuelTypeDTO fuelTypeDTO) {
        FuelType fuelType = fuelTypeMapper.toEntity(fuelTypeDTO);
        return fuelTypeMapper.toDto(fuelTypeRepository.save(fuelType));
    }

    @Override
    @Transactional
    public FuelTypeDTO updateFuelType(Integer id, FuelTypeDTO fuelTypeDTO) {
        FuelType existingFuelType = fuelTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FuelType not found with id: " + id));
        existingFuelType.setFuelTypeName(fuelTypeDTO.getFuelTypeName());
        return fuelTypeMapper.toDto(fuelTypeRepository.save(existingFuelType));
    }

    @Override
    @Transactional
    public void deleteFuelType(Integer id) {
        if (!fuelTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("FuelType not found with id: " + id);
        }
        fuelTypeRepository.deleteById(id);
    }
} 