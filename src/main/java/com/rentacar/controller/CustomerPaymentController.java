package com.rentacar.controller;

import com.rentacar.dto.CustomerPaymentDTO;
import com.rentacar.entity.CustomerPayment;
import com.rentacar.mapper.CustomerPaymentMapper;
import com.rentacar.service.CustomerPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/payments")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerPaymentController {
    private final CustomerPaymentService paymentService;
    private final CustomerPaymentMapper paymentMapper;

    @Autowired
    public CustomerPaymentController(CustomerPaymentService paymentService, CustomerPaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping
    public CustomerPaymentDTO makePayment(@RequestBody CustomerPaymentDTO paymentDTO) {
        CustomerPayment payment = paymentMapper.toEntity(paymentDTO);
        CustomerPayment saved = paymentService.makePayment(payment);
        return paymentMapper.toDto(saved);
    }
} 