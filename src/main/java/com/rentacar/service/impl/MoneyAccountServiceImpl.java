package com.rentacar.service.impl;

import com.rentacar.entity.Branch;
import com.rentacar.entity.MoneyAccount;
import com.rentacar.repository.MoneyAccountRepository;
import com.rentacar.service.MoneyAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoneyAccountServiceImpl implements MoneyAccountService {

    private final MoneyAccountRepository moneyAccountRepository;

    @Override
    @Transactional
    public void updateBranchBalance(Branch branch, Double amountToAdd) {
        if (branch == null || branch.getBranchID() == null) {
            throw new IllegalArgumentException("Branch cannot be null and must have an ID.");
        }
        if (amountToAdd == null) {
            throw new IllegalArgumentException("Amount to add cannot be null.");
        }

        MoneyAccount account = moneyAccountRepository.findByBranch_BranchID(branch.getBranchID())
                .orElseGet(() -> {
                    // Eğer şubenin para hesabı yoksa, oluştur (bu senaryo normalde şube oluşturulurken ele alınmalı)
                    MoneyAccount newAccount = MoneyAccount.builder()
                                                .branch(branch)
                                                .moneyBalance(0.0)
                                                .build();
                    return moneyAccountRepository.save(newAccount);
                });

        account.setMoneyBalance(account.getMoneyBalance() + amountToAdd);
        moneyAccountRepository.save(account);
    }
    
    // Opsiyonel: Şube oluşturulurken otomatik para hesabı oluşturma metodu
    // @Override
    // @Transactional
    // public void createMoneyAccountForBranch(Branch branch) {
    //     if (moneyAccountRepository.findByBranch_BranchID(branch.getBranchID()).isEmpty()) {
    //         MoneyAccount newAccount = MoneyAccount.builder()
    //                                         .branch(branch)
    //                                         .moneyBalance(0.0) // Başlangıç bakiyesi
    //                                         .build();
    //         moneyAccountRepository.save(newAccount);
    //     }
    // }
} 