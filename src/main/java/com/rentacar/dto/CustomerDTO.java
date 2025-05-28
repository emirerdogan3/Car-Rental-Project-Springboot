package com.rentacar.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CustomerDTO {
    private Integer customerID; // Genellikle entity'den maplenirken kullanılır
    private Integer userID; // İlişkili User için, opsiyonel, UserDTO'dan da gelebilir

    @NotBlank(message = "İsim boş olamaz")
    private String firstName;

    @NotBlank(message = "Soyisim boş olamaz")
    private String lastName;

    @NotBlank(message = "Ehliyet numarası boş olamaz")
    private String licenseNumber;

    private String address;
} 