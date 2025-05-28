package com.rentacar.mapper;

import com.rentacar.dto.ManagerDTO;
import com.rentacar.entity.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ManagerMapper {
    ManagerMapper INSTANCE = Mappers.getMapper(ManagerMapper.class);

    @Mapping(target = "userID", source = "user.userID")
    ManagerDTO toDto(Manager manager);

    @Mapping(target = "user.userID", source = "userID")
    Manager toEntity(ManagerDTO managerDTO);
} 