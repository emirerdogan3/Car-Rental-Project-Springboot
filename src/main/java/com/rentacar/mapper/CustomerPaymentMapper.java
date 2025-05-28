package com.rentacar.mapper;

import com.rentacar.dto.CustomerPaymentDTO;
import com.rentacar.entity.CustomerPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

// Drastically simplified for testing MapStruct generation
@Mapper(componentModel = "spring", uses = {ReservationMapper.class, MoneyAccountMapper.class, CustomerMapper.class}) // Added Mappers
public interface CustomerPaymentMapper {
    CustomerPaymentMapper INSTANCE = Mappers.getMapper(CustomerPaymentMapper.class);

    // Map only direct, non-nested fields for toDto
    @Mapping(target = "customerPaymentID", source = "customerPaymentID")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "paymentDate", source = "paymentDate")
    @Mapping(target = "description", source = "description")
    // Temporarily ignore complex fields for toDto
    @Mapping(target = "reservationID", ignore = true)
    @Mapping(target = "accountID", ignore = true)
    @Mapping(target = "customerID", ignore = true)
    CustomerPaymentDTO toDto(CustomerPayment payment);

    // Map only direct, non-nested fields for toEntity
    @Mapping(target = "customerPaymentID", source = "customerPaymentID")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "paymentDate", source = "paymentDate")
    @Mapping(target = "description", source = "description")
    // Temporarily ignore complex fields for toEntity
    @Mapping(target = "reservation", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "customer", ignore = true)
    CustomerPayment toEntity(CustomerPaymentDTO paymentDTO);
} 