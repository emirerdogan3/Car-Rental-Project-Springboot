package com.rentacar.service.impl;

import com.rentacar.dto.CustomerDTO;
import com.rentacar.entity.Customer;
import com.rentacar.entity.User;
import com.rentacar.mapper.CustomerMapper;
import com.rentacar.repository.CustomerRepository;
import com.rentacar.repository.UserRepository; // User varlığını kontrol için
import com.rentacar.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final UserRepository userRepository; // User ile ilişki için

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO, User user) {
        if (user == null || user.getUserID() == null) {
            throw new IllegalArgumentException("User entity with a valid ID must be provided to create a customer.");
        }
        // User'ın varlığını db'de kontrol et (opsiyonel, eğer user zaten save edilmişse)
        // User userEntity = userRepository.findById(user.getUserID())
        //        .orElseThrow(() -> new EntityNotFoundException("Associated user not found with ID: " + user.getUserID()));

        // Kullanıcının zaten bir müşteri kaydı var mı kontrol et
        if(customerRepository.findByUser_UserID(user.getUserID()).isPresent()){
            throw new IllegalStateException("User already has an associated customer profile.");
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        customer.setUser(user); // İlişkili User entity'sini set et
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(savedCustomer);
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId).map(customerMapper::toDto);
    }

    @Override
    public Optional<CustomerDTO> getCustomerByUserId(Integer userId) {
        return customerRepository.findByUser_UserID(userId).map(customerMapper::toDto);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Integer customerId, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));

        // Sadece Customer'a ait alanları güncelle, User ilişkisi genellikle buradan yönetilmez.
        existingCustomer.setFirstName(customerDTO.getFirstName());
        existingCustomer.setLastName(customerDTO.getLastName());
        existingCustomer.setLicenseNumber(customerDTO.getLicenseNumber());
        existingCustomer.setAddress(customerDTO.getAddress());
        // customer.setUser() -> userID DTO'da varsa ve User değişecekse bu da yönetilebilir.
        // Ancak customer profili güncellemesi genelde User'ı değiştirmez.

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer not found with id: " + customerId);
        }
        // İlişkili User silinmeli mi? Genelde hayır. Sadece Customer profili silinir.
        // Eğer User da silinecekse, bu işlem UserService üzerinden çağrılmalı veya burada kaskad ayarları olmalı.
        customerRepository.deleteById(customerId);
    }
} 