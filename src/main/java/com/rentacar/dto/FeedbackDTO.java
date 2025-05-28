package com.rentacar.dto;

import lombok.Data;
import java.util.Date;

@Data
public class FeedbackDTO {
    private Integer feedbackID;
    private Integer customerID;
    private String customerName;
    private Integer reservationID;
    private Integer carID;
    private String carIdentifier;
    private Integer rating;
    private String comment;
    private Date createdDate;
    private Integer branchId;
    private String branchName;
} 