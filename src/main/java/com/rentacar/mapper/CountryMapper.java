package com.rentacar.mapper;

import com.rentacar.dto.CountryDTO;
import com.rentacar.entity.Country;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);
    CountryDTO toDto(Country country);
    Country toEntity(CountryDTO countryDTO);
} 