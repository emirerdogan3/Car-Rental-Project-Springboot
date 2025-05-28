package com.rentacar.service;

import com.rentacar.entity.CustomerPayment;

public interface CustomerPaymentService {
    CustomerPayment makePayment(CustomerPayment payment);
} 