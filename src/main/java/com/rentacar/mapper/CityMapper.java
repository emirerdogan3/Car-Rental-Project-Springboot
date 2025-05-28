package com.rentacar.mapper;

import com.rentacar.dto.CityDTO;
import com.rentacar.entity.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CityMapper {
    CityMapper INSTANCE = Mappers.getMapper(CityMapper.class);

    @Mapping(target = "countryID", source = "country.countryID")
    @Mapping(target = "countryName", source = "country.countryName")
    CityDTO toDto(City city);

    @Mapping(target = "country.countryID", source = "countryID")
    City toEntity(CityDTO cityDTO);
} 