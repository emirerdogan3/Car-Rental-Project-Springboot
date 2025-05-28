package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Customers") // SQL'deki tablo adıyla eşleşiyor
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID", nullable = false, unique = true) // Users tablosuna FK, bir kullanıcı sadece bir müşteri olabilir
    private User user;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(length = 50, unique = true) // Ehliyet no unique olabilir
    private String licenseNumber;

    @Column(length = 255)
    private String address;
} 