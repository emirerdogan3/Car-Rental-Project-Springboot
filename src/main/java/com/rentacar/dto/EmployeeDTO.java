package com.rentacar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Integer employeeID;
    private Integer userID;
    private String firstName;
    private String lastName;
    private Double salary;
    private String positionTitle;
    private Integer branchID;
    private String username;
    private String email;
    private String phone;
    private String password;
    @Builder.Default
    private Boolean enabled = true;
} 