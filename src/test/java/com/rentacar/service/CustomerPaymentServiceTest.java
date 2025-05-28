package com.rentacar.service;

import com.rentacar.entity.CustomerPayment;
import com.rentacar.entity.MoneyAccount;
import com.rentacar.repository.CustomerPaymentRepository;
import com.rentacar.repository.MoneyAccountRepository;
import com.rentacar.service.impl.CustomerPaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerPaymentServiceTest {
    @Mock
    private CustomerPaymentRepository paymentRepository;

    @Mock
    private MoneyAccountRepository accountRepository;

    @InjectMocks
    private CustomerPaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMakePayment() {
        CustomerPayment payment = new CustomerPayment();
        MoneyAccount account = new MoneyAccount();
        account.setAccountID(1);
        account.setMoneyBalance(100.0);
        payment.setAccount(account);
        payment.setAmount(50.0);
        when(paymentRepository.save(payment)).thenReturn(payment);
        CustomerPayment result = paymentService.makePayment(payment);
        assertNotNull(result);
        verify(paymentRepository, times(1)).save(payment);
    }
} 