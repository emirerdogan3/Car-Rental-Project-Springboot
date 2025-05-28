package com.rentacar.dto.filter;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarFilterDTO {
    // String brandName; // Veya brandId
    // String modelName; // Veya modelId
    // String categoryName; // Veya categoryId
    // String fuelTypeName; // Veya fuelTypeId
    // String colorName; // Veya colorId

    private Integer brandId;
    private Integer modelId;
    private Integer categoryId;
    private Integer fuelTypeId;
    private Integer colorId;
    private Integer branchId; // Zaten metot parametresi olarak da gelebilir
    private String status; // "Available", "Rented", "Maintenance"
    private Integer minModelYear;
    private Integer maxModelYear;
    private Double minPricePerDay;
    private Double maxPricePerDay;
    private String plateNumber; // Tam eşleşme veya partial

    // Sayfalama ve sıralama bilgileri de eklenebilir
    // private int page = 0;
    // private int size = 10;
    // private String sortBy = "pricePerDay";
    // private String sortDir = "asc";
} 