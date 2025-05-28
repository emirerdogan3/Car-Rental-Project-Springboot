package com.rentacar.repository;

import com.rentacar.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer>, JpaSpecificationExecutor<Feedback> {
    long countByReservation_Branch_BranchID(Integer branchId);
    boolean existsByReservation_ReservationID(Integer reservationId);
} 