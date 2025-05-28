package com.rentacar.service.impl;

import com.rentacar.dto.FeedbackDTO;
import com.rentacar.entity.Customer;
import com.rentacar.entity.Feedback;
import com.rentacar.entity.Reservation;
import com.rentacar.entity.User;
import com.rentacar.mapper.FeedbackMapper;
import com.rentacar.repository.CustomerRepository;
import com.rentacar.repository.FeedbackRepository;
import com.rentacar.repository.ReservationRepository;
import com.rentacar.repository.UserRepository;
import com.rentacar.service.FeedbackService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final UserRepository userRepository; // Added
    private final CustomerRepository customerRepository; // Added
    private final ReservationRepository reservationRepository; // Added

    // @Override // Old method, to be removed or refactored from leaveFeedback(Feedback feedback)
    // public Feedback leaveFeedback(Feedback feedback) { ... }

    @Override
    @Transactional
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO, UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentUser.getUsername()));
        Customer customer = customerRepository.findByUser_UserID(user.getUserID())
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found for user: " + currentUser.getUsername()));

        if (feedbackDTO.getReservationID() == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null for feedback.");
        }

        Reservation reservation = reservationRepository.findById(feedbackDTO.getReservationID())
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + feedbackDTO.getReservationID()));

        // Validate customer owns the reservation
        if (!reservation.getCustomer().getCustomerID().equals(customer.getCustomerID())) {
            throw new SecurityException("Customer is not authorized to leave feedback for this reservation.");
        }

        // Validate reservation status - allow feedback for completed or past confirmed reservations
        String status = reservation.getStatus();
        Date currentDate = new Date();
        
        if ("Completed".equalsIgnoreCase(status) || "Tamamlandi".equalsIgnoreCase(status)) {
            // Always allow feedback for completed reservations
        } else if ("Confirmed".equalsIgnoreCase(status)) {
            // Allow feedback only if rental period has ended
            if (reservation.getEndDate() == null || !reservation.getEndDate().before(currentDate)) {
                throw new IllegalStateException("Feedback can only be left for confirmed reservations after the rental period has ended.");
            }
        } else {
            throw new IllegalStateException("Feedback can only be left for completed or finished confirmed reservations. Status: " + status);
        }


        // Check if feedback already exists for this reservation
        if (feedbackRepository.existsByReservation_ReservationID(reservation.getReservationID())) {
            throw new IllegalStateException("Feedback has already been submitted for this reservation.");
        }

        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setReservation(reservation);
        feedback.setRating(feedbackDTO.getRating());
        feedback.setComment(feedbackDTO.getComment());
        feedback.setCreatedDate(new Date());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return feedbackMapper.toDto(savedFeedback);
    }

    @Override
    public Page<FeedbackDTO> getFeedbacksByBranch(Integer branchId, Pageable pageable) {
        Specification<Feedback> spec = (root, query, criteriaBuilder) -> {
            if (branchId == null) {
                return criteriaBuilder.conjunction();
            }
            Predicate branchPredicate = criteriaBuilder.equal(root.get("reservation").get("branch").get("branchID"), branchId);
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return branchPredicate;
        };
        return feedbackRepository.findAll(spec, pageable).map(feedbackMapper::toDto);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long countFeedbackByBranch(Integer branchId) {
        if (branchId == null) return 0;
        return feedbackRepository.countByReservation_Branch_BranchID(branchId);
    }

    @Override
    @Transactional
    public void deleteFeedback(Integer feedbackId, UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentUser.getUsername()));
        Customer customer = customerRepository.findByUser_UserID(user.getUserID())
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found for user: " + currentUser.getUsername()));

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found with ID: " + feedbackId));

        // Validate customer owns the feedback
        if (!feedback.getCustomer().getCustomerID().equals(customer.getCustomerID())) {
            throw new SecurityException("Customer is not authorized to delete this feedback.");
        }

        feedbackRepository.delete(feedback);
    }
} 