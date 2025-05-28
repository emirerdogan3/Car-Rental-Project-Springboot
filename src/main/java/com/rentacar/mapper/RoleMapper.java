package com.rentacar.mapper;

import com.rentacar.dto.RoleDTO;
import com.rentacar.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDTO toDto(Role role);
    Role toEntity(RoleDTO roleDTO);
} 