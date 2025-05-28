package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MoneyAccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"branch"})
public class MoneyAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchID", unique = true)
    private Branch branch;

    private Double moneyBalance;
} 