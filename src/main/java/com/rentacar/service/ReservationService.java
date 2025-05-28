package com.rentacar.service;

import com.rentacar.entity.Reservation;
import java.util.List;
import com.rentacar.dto.ReservationDTO;
import com.rentacar.dto.filter.ReservationFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Date;
import com.rentacar.dto.request.ReservationRequestDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface ReservationService {
    Page<ReservationDTO> getReservationsByCurrentUser(ReservationFilterDTO filter, Pageable pageable, UserDetails currentUser);
    Page<ReservationDTO> getReservationsByBranchAndFilters(Integer branchId, ReservationFilterDTO filter, Pageable pageable);
    List<Reservation> getAllReservations();

    // New methods for dashboard stats
    long countActiveReservationsByBranch(Integer branchId);
    long countPendingApprovalReservationsByBranch(Integer branchId);

    // Methods for reservation form
    boolean isCarAvailable(Integer carId, Date startDate, Date endDate);
    double calculateTotalPrice(Integer carId, Date startDate, Date endDate);

    ReservationDTO createReservationAndProcessPayment(ReservationRequestDTO requestDTO, UserDetails currentUser);
    
    ReservationDTO cancelReservation(Integer reservationId, UserDetails currentUser);

    List<ReservationDTO> getCompletedReservationsWithoutFeedback(UserDetails currentUser);
} 