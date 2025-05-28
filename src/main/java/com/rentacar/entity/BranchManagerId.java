package com.rentacar.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Kompozit anahtar için önemli
public class BranchManagerId implements Serializable {
    private Integer branch; // Branch entity'sindeki alan adı ile aynı olmalı
    private Integer manager; // Manager entity'sindeki alan adı (ManagerID) ile aynı olmalı
} 