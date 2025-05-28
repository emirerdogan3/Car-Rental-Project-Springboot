package com.rentacar.mapper;

import com.rentacar.dto.CarCategoryDTO;
import com.rentacar.entity.CarCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarCategoryMapper {
    CarCategoryMapper INSTANCE = Mappers.getMapper(CarCategoryMapper.class);

    CarCategoryDTO toDto(CarCategory carCategory);
    CarCategory toEntity(CarCategoryDTO carCategoryDTO);
} 