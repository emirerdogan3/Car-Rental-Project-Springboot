package com.rentacar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "EmployeePayments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeePaymentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeID")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountID")
    private MoneyAccount account;

    private Double amount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paidByUserID") // Manager who paid the salary
    private User paidBy;
} 