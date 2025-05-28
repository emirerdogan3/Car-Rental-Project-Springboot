package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CarBrandModel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarBrandModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer modelID;

    @Column(length = 50, nullable = false, unique = true)
    private String modelName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brandID")
    private CarBrand brand;
} 