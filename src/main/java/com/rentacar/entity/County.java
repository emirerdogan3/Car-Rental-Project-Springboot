package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "County")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class County {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer countyID;

    @Column(unique = true, length = 50)
    private String countyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cityID")
    private City city;
} 