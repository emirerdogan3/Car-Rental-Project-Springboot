package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CarCategories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryID;

    @Column(length = 50, nullable = false, unique = true)
    private String categoryName;
} 