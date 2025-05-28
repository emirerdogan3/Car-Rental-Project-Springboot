package com.rentacar.dto;

import lombok.Data;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

@Data
public class ReservationDTO {
    private Integer reservationID;
    private Integer customerID;
    private Integer carID;
    private Date startDate;
    private Date endDate;
    private Double totalPrice;
    private String status;
    private Integer branchID;
    private String carName;      // Örn: "BMW 3 Series"
    private String branchName;   // Örn: "Istanbul Kadikoy Branch"
    private String customerName; // Örn: "John Doe"
    
    // Additional fields for template
    private String carBrand;     // Car brand name
    private String carModel;     // Car model name
    private Double totalAmount;  // Alias for totalPrice
    private Long totalDays;      // Duration in days
    
    // Calculated properties
    public Double getTotalAmount() {
        return totalPrice;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalPrice = totalAmount;
    }
    
    public Long getTotalDays() {
        if (startDate != null && endDate != null) {
            // Aynı tarih kontrolü
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(startDate);
            
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            
            // Aynı gün mü kontrol et
            boolean sameDay = startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR) &&
                             startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH) &&
                             startCal.get(Calendar.DAY_OF_MONTH) == endCal.get(Calendar.DAY_OF_MONTH);
            
            if (sameDay) {
                return 1L; // Aynı gün ise 1 gün
            } else {
            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
                long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                
                // Eğer endDate startDate'den sonra ise 1 gün ekle (inclusive end date)
                if (startDate.before(endDate)) {
                    diffInDays += 1;
                }
                return diffInDays;
            }
        }
        return totalDays != null ? totalDays : 0L;
    }
} 