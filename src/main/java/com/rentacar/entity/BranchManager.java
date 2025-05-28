package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BranchManagers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(BranchManagerId.class) // Kompozit anahtar sınıfı
public class BranchManager {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchID")
    private Branch branch;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ManagerID") // Manager tablosundaki ManagerID kolonu
    private Manager manager; // Manager entity'si

    // Ek alanlar eklenebilir, örneğin atanma tarihi vb.
    // @Temporal(TemporalType.TIMESTAMP)
    // private Date assignedDate;
} 