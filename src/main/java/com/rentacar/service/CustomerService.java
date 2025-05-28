package com.rentacar.service;

import com.rentacar.dto.CustomerDTO;
import com.rentacar.entity.User; // User entity'si Customer oluştururken lazım olacak

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO customerDTO, User user); // User entity'sini alır
    Optional<CustomerDTO> getCustomerById(Integer customerId);
    Optional<CustomerDTO> getCustomerByUserId(Integer userId);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO updateCustomer(Integer customerId, CustomerDTO customerDTO);
    void deleteCustomer(Integer customerId);
} 