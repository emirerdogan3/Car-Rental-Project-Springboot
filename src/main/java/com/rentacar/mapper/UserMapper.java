package com.rentacar.mapper;

import com.rentacar.dto.UserDTO;
import com.rentacar.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    @Mapping(source = "enabled", target = "enabled")
    UserDTO toDto(User user);

    @Mapping(source = "password", target = "passwordHash")
    @Mapping(source = "enabled", target = "enabled")
    User toEntity(UserDTO userDTO);
} 