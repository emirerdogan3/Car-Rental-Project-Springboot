package com.rentacar.service;

import com.rentacar.entity.Branch;

public interface MoneyAccountService {
    void updateBranchBalance(Branch branch, Double amountToAdd);
    // void createMoneyAccountForBranch(Branch branch); // Şube oluşturulurken otomatik yapılabilir
} 