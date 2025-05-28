package com.rentacar.mapper;

import com.rentacar.dto.ColorDTO;
import com.rentacar.entity.Color;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ColorMapper {
    ColorMapper INSTANCE = Mappers.getMapper(ColorMapper.class);

    ColorDTO toDto(Color color);
    Color toEntity(ColorDTO colorDTO);
} 