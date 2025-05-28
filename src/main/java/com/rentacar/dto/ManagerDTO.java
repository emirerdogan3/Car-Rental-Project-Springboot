package com.rentacar.dto;

import lombok.Data;

@Data
public class ManagerDTO {
    private Integer managerID;
    private Integer userID;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private String password;
    private Boolean enabled;
} 