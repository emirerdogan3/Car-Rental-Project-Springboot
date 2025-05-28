package com.rentacar.dto.filter;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchFilterDTO {
    private Integer countryId;
    private Integer cityId;
    private Integer countyId;
    private String branchName; // Şube adına göre arama için

    // Sayfalama ve sıralama bilgileri de eklenebilir
} 