package com.rentacar.dto;

import lombok.Data;

@Data
public class CarDTO {
    private Integer carID;

    // Marka
    private String brandName;
    private Integer brandID; // Form için

    // Model
    private String modelName;
    private Integer modelID; // Form için

    // Kategori
    private String categoryName;
    private Integer categoryID; // Form için

    private String plateNumber;
    private Integer kilometer;
    private Double pricePerDay;

    // Yakıt Tipi
    private String fuelTypeName;
    private Integer fuelTypeID; // Form için

    private String status;
    private Integer modelYear;

    // Renk
    private String colorName;
    private Integer colorID; // Form için

    // Şube
    private Integer branchID;
    private String branchName; // Listeleme için eklendi
} 