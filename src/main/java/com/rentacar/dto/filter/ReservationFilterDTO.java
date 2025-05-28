package com.rentacar.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationFilterDTO {
    private Integer branchId; // Should be set by the controller based on manager's branch
    private Integer customerId;
    private Integer carId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date endDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date endDateTo;

    private String status; // Pending, Confirmed, Cancelled, Rented, Completed
} 