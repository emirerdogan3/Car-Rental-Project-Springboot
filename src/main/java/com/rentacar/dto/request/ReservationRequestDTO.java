package com.rentacar.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Data
public class ReservationRequestDTO {

    @NotNull(message = "Car ID cannot be null")
    private Integer carId;

    @NotNull(message = "Branch ID cannot be null")
    private Integer branchId;

    // Customer ID will be derived from the authenticated user

    @NotNull(message = "Start date cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent(message = "Start date must be today or in the future")
    private Date startDate;

    @NotNull(message = "End date cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Future(message = "End date must be in the future") // Or @FutureOrPresent depending on min rental period
    private Date endDate;

    @NotNull(message = "Total price cannot be null")
    @Positive(message = "Total price must be positive")
    private Double totalPrice; // This should be validated against a server-side calculation

    // Symbolic payment details (in a real app, this would be handled by a payment gateway)
    @NotBlank(message = "Cardholder name cannot be blank")
    private String cardHolderName;

    @NotBlank(message = "Card number cannot be blank")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits") // Basic validation
    private String cardNumber;

    @NotBlank(message = "Expiry month cannot be blank")
    private String expiryMonth; // e.g., "02"

    @NotBlank(message = "Expiry year cannot be blank")
    private String expiryYear;  // e.g., "2025"

    @NotBlank(message = "CVV cannot be blank")
    @Size(min = 3, max = 4, message = "CVV must be 3 or 4 digits")
    private String cvv;
} 