package com.rentacar.mapper;

import com.rentacar.dto.EmployeeDTO;
import com.rentacar.entity.Employee;
import com.rentacar.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {BranchMapper.class, UserMapper.class})
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mappings({
        @Mapping(target = "userID", source = "user.userID"),
        @Mapping(target = "username", source = "user.username"),
        @Mapping(target = "email", source = "user.email"),
        @Mapping(target = "phone", source = "user.phone"),
        @Mapping(target = "enabled", source = "user.enabled"),
        @Mapping(target = "branchID", source = "branch.branchID")
    })
    EmployeeDTO toDto(Employee employee);

    @Mappings({
        @Mapping(target = "user", source = "employeeDTO", qualifiedByName = "employeeDtoToUser"),
        @Mapping(target = "branch.branchID", source = "branchID"),
        @Mapping(target = "employeeID", ignore = true)
    })
    Employee toEntity(EmployeeDTO employeeDTO);

    @Named("employeeDtoToUser")
    default User employeeDtoToUser(EmployeeDTO dto) {
        if (dto == null || dto.getUserID() == null) {
            return null;
        }
        User user = new User();
        user.setUserID(dto.getUserID());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(dto.getPassword());
        if (dto.getEnabled() != null) {
            user.setEnabled(dto.getEnabled());
        } else {
            user.setEnabled(true);
        }
        return user;
    }
} 