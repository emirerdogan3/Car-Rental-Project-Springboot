package com.rentacar.service.impl;

import com.rentacar.entity.CustomerPayment;
import com.rentacar.entity.MoneyAccount;
import com.rentacar.repository.CustomerPaymentRepository;
import com.rentacar.repository.MoneyAccountRepository;
import com.rentacar.service.CustomerPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerPaymentServiceImpl implements CustomerPaymentService {
    private final CustomerPaymentRepository paymentRepository;
    private final MoneyAccountRepository accountRepository;

    @Autowired
    public CustomerPaymentServiceImpl(CustomerPaymentRepository paymentRepository, MoneyAccountRepository accountRepository) {
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public CustomerPayment makePayment(CustomerPayment payment) {
        // MoneyAccount bakiyesini güncelle
        MoneyAccount account = payment.getAccount();
        account.setMoneyBalance(account.getMoneyBalance() + payment.getAmount());
        accountRepository.save(account);
        return paymentRepository.save(payment);
    }
} 