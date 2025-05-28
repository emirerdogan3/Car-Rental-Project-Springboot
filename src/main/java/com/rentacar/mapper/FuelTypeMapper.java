package com.rentacar.mapper;

import com.rentacar.dto.FuelTypeDTO;
import com.rentacar.entity.FuelType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FuelTypeMapper {
    FuelTypeMapper INSTANCE = Mappers.getMapper(FuelTypeMapper.class);

    FuelTypeDTO toDto(FuelType fuelType);
    FuelType toEntity(FuelTypeDTO fuelTypeDTO);
} 