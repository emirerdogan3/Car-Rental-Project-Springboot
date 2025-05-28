package com.rentacar.dto;

import lombok.Data;
import java.util.List;

@Data
public class BranchDTO {
    private Integer branchID;
    private String branchName;
    private String countryName;
    private String cityName;
    private String countyName;
    private Integer countryID;
    private Integer cityID;
    private Integer countyID;
    private String phoneNumber;
    private Double accountBalance;
    private List<String> managerNames;
} 