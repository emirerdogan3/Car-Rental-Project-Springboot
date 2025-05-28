package com.rentacar.dto;

import lombok.Data;
import java.util.Date;

@Data
public class CustomerPaymentDTO {
    private Integer customerPaymentID;
    private Integer reservationID;
    private Integer accountID;
    private Integer customerID;
    private Double amount;
    private Date paymentDate;
    private String description;
} 