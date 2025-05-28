package com.rentacar.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RoleDTO {
    private Integer roleID;
    @NotBlank(message = "Rol adı boş olamaz")
    private String roleName;
} 