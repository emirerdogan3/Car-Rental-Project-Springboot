package com.rentacar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    // Fields from UserDTO (excluding password)
    private Integer userID;
    private String username;
    private String email;
    private String phone;
    private String roleName; // Role name for display
    private boolean enabled;

    // Fields from CustomerDTO
    private Integer customerID;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private String address;
    
    // Rental Statistics
    private Long totalRentals;
    private Long completedRentals;
    private Double averageRating;
    private Double totalSpent;

    // If UserDTO and CustomerDTO are directly embedded:
    // private UserDTO userDetails;
    // private CustomerDTO customerDetails;
} 