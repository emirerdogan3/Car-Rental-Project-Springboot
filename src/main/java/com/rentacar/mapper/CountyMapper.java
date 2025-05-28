package com.rentacar.mapper;

import com.rentacar.dto.CountyDTO;
import com.rentacar.entity.County;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CountyMapper {
    CountyMapper INSTANCE = Mappers.getMapper(CountyMapper.class);

    @Mapping(target = "cityID", source = "city.cityID")
    @Mapping(target = "cityName", source = "city.cityName")
    CountyDTO toDto(County county);

    @Mapping(target = "city.cityID", source = "cityID")
    County toEntity(CountyDTO countyDTO);
} 