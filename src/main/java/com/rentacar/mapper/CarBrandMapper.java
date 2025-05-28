package com.rentacar.mapper;

import com.rentacar.dto.CarBrandDTO;
import com.rentacar.entity.CarBrand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarBrandMapper {
    CarBrandDTO toDto(CarBrand carBrand);
    CarBrand toEntity(CarBrandDTO carBrandDTO);
} 