package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FuelType")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fuelTypeID;

    @Column(length = 50, nullable = false, unique = true)
    private String fuelTypeName;
} 