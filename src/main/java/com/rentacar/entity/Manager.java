package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Manager")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer managerID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID", unique = true)
    private User user;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;
} 