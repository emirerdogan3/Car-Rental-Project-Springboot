package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "CustomerPayments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerPaymentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservationID")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountID")
    private MoneyAccount account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerID")
    private Customer customer;

    private Double amount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    private String description;
} 