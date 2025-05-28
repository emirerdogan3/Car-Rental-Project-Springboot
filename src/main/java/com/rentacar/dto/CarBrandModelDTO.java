package com.rentacar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarBrandModelDTO {
    private Integer modelID;
    private String modelName;
    private Integer brandID;
    private String brandName; // For display purposes
} 