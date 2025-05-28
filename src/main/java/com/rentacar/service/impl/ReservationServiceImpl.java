package com.rentacar.service.impl;

import com.rentacar.entity.Car;
import com.rentacar.entity.Reservation;
import com.rentacar.repository.CarRepository;
import com.rentacar.repository.ReservationRepository;
import com.rentacar.service.ReservationService;
import com.rentacar.dto.ReservationDTO;
import com.rentacar.dto.filter.ReservationFilterDTO;
import com.rentacar.dto.request.ReservationRequestDTO;
import com.rentacar.mapper.ReservationMapper;
import com.rentacar.repository.CustomerRepository;
import com.rentacar.repository.UserRepository;
import com.rentacar.repository.specification.ReservationSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jakarta.persistence.EntityNotFoundException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import com.rentacar.service.BranchService;
import com.rentacar.service.CustomerPaymentService;
import com.rentacar.service.MoneyAccountService;
import com.rentacar.entity.User;
import com.rentacar.entity.Customer;
import com.rentacar.entity.CustomerPayment;
import com.rentacar.repository.FeedbackRepository;
import com.rentacar.repository.CustomerPaymentRepository;
import java.util.Calendar;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationSpecification reservationSpecification;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BranchService branchService;
    private final CustomerPaymentService customerPaymentService;
    private final MoneyAccountService moneyAccountService;
    private final FeedbackRepository feedbackRepository;
    private final CustomerPaymentRepository customerPaymentRepository;

    @Override
    @Transactional
    public ReservationDTO createReservationAndProcessPayment(ReservationRequestDTO requestDTO, UserDetails currentUser) {
        // 1. Get User and Customer
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentUser.getUsername()));
        Customer customer = customerRepository.findByUser_UserID(user.getUserID())
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found for user: " + currentUser.getUsername()));

        // 2. Get Car and Branch
        Car car = carRepository.findById(requestDTO.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + requestDTO.getCarId()));
        
        // Branch DTO'dan alınabilir veya Car'dan alınabilir. RequestDTO'da branchId var.
        com.rentacar.dto.BranchDTO branchDTO = branchService.getBranchDtoById(requestDTO.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + requestDTO.getBranchId()));
        
        // Validate car belongs to the branch from request (optional, but good for consistency)
        if(!car.getBranch().getBranchID().equals(requestDTO.getBranchId())){
            throw new IllegalArgumentException("Car with ID " + car.getCarID() + " does not belong to branch ID " + requestDTO.getBranchId());
        }

        // 3. Validate price (client-side price can be manipulated)
        double serverCalculatedPrice = calculateTotalPrice(car.getCarID(), requestDTO.getStartDate(), requestDTO.getEndDate());
        if (Math.abs(serverCalculatedPrice - requestDTO.getTotalPrice()) > 0.01) { // Allow for small floating point differences
            throw new IllegalArgumentException("Price mismatch. Client price: " + requestDTO.getTotalPrice() + ", Server price: " + serverCalculatedPrice);
        }

        // 4. Check car availability again (as a final check within the transaction)
        if (!isCarAvailable(car.getCarID(), requestDTO.getStartDate(), requestDTO.getEndDate())) {
            throw new IllegalStateException("Car is no longer available for the selected dates.");
        }
        // Note: We don't check car status here as cars can be reserved for future dates
        // even when they might be currently rented to someone else

        // 5. Create Reservation
        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setCar(car);
        // Convert BranchDTO to Branch entity for Reservation. Set it from the Car's branch.
        reservation.setBranch(car.getBranch()); 
        reservation.setStartDate(requestDTO.getStartDate());
        reservation.setEndDate(requestDTO.getEndDate());
        reservation.setTotalPrice(serverCalculatedPrice); // Use server calculated price
        reservation.setStatus("Confirmed"); // Or "Pending Payment" if payment is a separate step not yet confirmed
        
        Reservation savedReservation = reservationRepository.save(reservation);

        // 6. Create CustomerPayment (without MoneyAccount for now since payment is external)
        CustomerPayment payment = new CustomerPayment();
        payment.setCustomer(customer);
        payment.setReservation(savedReservation);
        payment.setPaymentDate(new Date());
        payment.setAmount(serverCalculatedPrice);
        payment.setDescription("Card payment for reservation ID: " + savedReservation.getReservationID());
        // payment.setAccount(null); // External payment, no internal account needed
        // Save payment record without updating MoneyAccount
        customerPaymentRepository.save(payment);

        // 7. Car status remains "Available" until rental period starts
        // Don't change car status immediately - only change when rental period begins

        // 8. Update Branch MoneyAccount
        moneyAccountService.updateBranchBalance(car.getBranch(), serverCalculatedPrice);

        // 9. Transaction is handled by @Transactional annotation

        // 10. Return DTO
        return reservationMapper.toDto(savedReservation);
    }

    // Commenting out or removing old getReservationsByCustomer method if no longer used directly
    /* 
    @Override
    public List<Reservation> getReservationsByCustomer(Integer customerId) {
        // This might need to be updated to return Page<ReservationDTO> and use UserDetails
        return reservationRepository.findAll().stream()
                .filter(r -> r.getCustomer() != null && r.getCustomer().getUser() != null && r.getCustomer().getUser().getUserID().equals(customerId))
                .collect(Collectors.toList());
    }
    */

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveReservationsByBranch(Integer branchId) {
        if (branchId == null) return 0;
        // Define "Active" statuses. Consider using constants or an Enum for statuses.
        List<String> activeStatuses = List.of("Confirmed", "Rented", "Aktif"); // "Aktif" from CustomerController
        return reservationRepository.countByBranch_BranchIDAndStatusIn(branchId, activeStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingApprovalReservationsByBranch(Integer branchId) {
        if (branchId == null) return 0;
        // Define "Pending Approval" statuses
        List<String> pendingStatuses = List.of("Beklemede", "Pending Approval", "Pending"); // "Pending" from CustomerController
        return reservationRepository.countByBranch_BranchIDAndStatusIn(branchId, pendingStatuses);
    }

    @Override
    public Page<ReservationDTO> getReservationsByBranchAndFilters(Integer branchId, ReservationFilterDTO filter, Pageable pageable) {
        ReservationFilterDTO effectiveFilter = (filter == null) ? ReservationFilterDTO.builder().build() : filter;
        effectiveFilter.setBranchId(branchId); // Ensure branchId from path is used

        Specification<Reservation> spec = reservationSpecification.findByCriteria(effectiveFilter);
        return reservationRepository.findAll(spec, pageable).map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCarAvailable(Integer carId, Date startDate, Date endDate) {
        if (carId == null || startDate == null || endDate == null || startDate.after(endDate)) {
            // Invalid parameters, or start date is after end date
            return false; 
        }
        
        // Check if car exists
        Car car = carRepository.findById(carId).orElse(null);
        if (car == null) {
            return false; // Car doesn't exist
        }
        
        // Only check for overlapping reservations - car status is not relevant for future bookings
        return !reservationRepository.existsOverlappingReservation(carId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateTotalPrice(Integer carId, Date startDate, Date endDate) {
        if (carId == null || startDate == null || endDate == null || startDate.after(endDate)) {
            throw new IllegalArgumentException("Invalid parameters for price calculation.");
        }

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + carId));

        if (car.getPricePerDay() == null || car.getPricePerDay() <= 0) {
            throw new IllegalStateException("Car price per day is not set or invalid for car ID: " + carId);
        }

        // Aynı tarih kontrolü - sadece yıl, ay, gün karşılaştır
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        
        // Aynı gün mü kontrol et
        boolean sameDay = startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR) &&
                         startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH) &&
                         startCal.get(Calendar.DAY_OF_MONTH) == endCal.get(Calendar.DAY_OF_MONTH);
        
        long diffInDays;
        if (sameDay) {
            // Aynı gün seçilirse 1 gün ücreti al
             diffInDays = 1;
        } else {
            // Farklı günler ise gün farkı + 1 (dahil bitiş tarihi)
            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            
            // Eğer endDate startDate'den sonra ise 1 gün ekle (inclusive end date)
            if (startDate.before(endDate)) {
                diffInDays += 1;
            }
        }

        return car.getPricePerDay() * diffInDays;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getReservationsByCurrentUser(ReservationFilterDTO filter, Pageable pageable, UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentUser.getUsername()));
        Customer customer = customerRepository.findByUser_UserID(user.getUserID())
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found for user: " + currentUser.getUsername()));

        ReservationFilterDTO effectiveFilter = (filter == null) ? ReservationFilterDTO.builder().build() : filter;
        effectiveFilter.setCustomerId(customer.getCustomerID()); // Set customerId for the specification
        // Ensure other filter fields like branchId are not set if this is purely for customer's reservations
        // effectiveFilter.setBranchId(null); // Or handle this in the specification if a customer can see reservations across branches they made

        Specification<Reservation> spec = reservationSpecification.findByCriteria(effectiveFilter);
        return reservationRepository.findAll(spec, pageable).map(reservationMapper::toDto);
    }

    @Override
    @Transactional
    public ReservationDTO cancelReservation(Integer reservationId, UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentUser.getUsername()));
        Customer customer = customerRepository.findByUser_UserID(user.getUserID())
            .orElseThrow(() -> new EntityNotFoundException("Customer not found for user: " + currentUser.getUsername()));

        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + reservationId));

        // Check if the reservation belongs to the current customer
        if (!reservation.getCustomer().getCustomerID().equals(customer.getCustomerID())) {
            throw new SecurityException("Customer is not authorized to cancel this reservation.");
        }

        // Check if reservation can be cancelled (e.g., not already completed or cancelled, and within cancellation window)
        // For simplicity, we'll allow cancellation if status is Confirmed or Rented and start date is in future.
        // More complex rules (e.g. no cancellation 24h before pickup) can be added.
        if (!List.of("Confirmed", "Rented").contains(reservation.getStatus())) {
            throw new IllegalStateException("Reservation cannot be cancelled. Status: " + reservation.getStatus());
        }

        // Optional: Check if startDate is in the future
        // if (reservation.getStartDate().before(new Date())) {
        //     throw new IllegalStateException("Cannot cancel a reservation that has already started or is in the past.");
        // }

        String oldStatus = reservation.getStatus();
        reservation.setStatus("Cancelled");
        Reservation updatedReservation = reservationRepository.save(reservation);

        // If car was "Rented" due to this reservation, make it "Available" again,
        // but only if there are no other "Rented" or "Confirmed" reservations for this car for the same/overlapping period.
        // This logic can be complex. For now, simplify: if it was Rented, make it Available.
        // A more robust check would involve `isCarAvailable` for a very short period around now to see if it should truly be available.
        Car car = updatedReservation.getCar();
        if ("Rented".equals(oldStatus) || "Confirmed".equals(oldStatus)) {
            // Check if there are other active (Confirmed/Rented) reservations for this car
            // If not, set to available.
            boolean otherActiveReservations = reservationRepository.existsOtherActiveReservationsForCar(
                car.getCarID(), 
                updatedReservation.getReservationID()
            );
            if (!otherActiveReservations) {
                 car.setStatus("Available");
                 carRepository.save(car);
            }
        }
        
        // TODO: Handle potential refunds if payment was already processed.
        // This would involve the PaymentService and potentially a new transaction status.

        return reservationMapper.toDto(updatedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDTO> getCompletedReservationsWithoutFeedback(UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentUser.getUsername()));
        Customer customer = customerRepository.findByUser_UserID(user.getUserID())
            .orElseThrow(() -> new EntityNotFoundException("Customer profile not found for user: " + currentUser.getUsername()));

        // Define completed statuses - include more statuses to allow feedback
        List<String> completedStatuses = List.of("Completed", "Tamamlandi", "Confirmed"); 

        // 1. Get all reservations for the customer with eligible statuses
        List<Reservation> eligibleReservations = reservationRepository
            .findByCustomer_CustomerIDAndStatusIn(customer.getCustomerID(), completedStatuses);

        // 2. Filter reservations: either "Completed"/"Tamamlandi" OR "Confirmed" with past end date
        Date currentDate = new Date();
        List<Reservation> reservationsEligibleForFeedback = eligibleReservations.stream()
            .filter(reservation -> {
                String status = reservation.getStatus();
                if ("Completed".equalsIgnoreCase(status) || "Tamamlandi".equalsIgnoreCase(status)) {
                    return true; // Always eligible if completed
                }
                if ("Confirmed".equalsIgnoreCase(status)) {
                    // Only if end date is in the past (rental period finished)
                    return reservation.getEndDate() != null && reservation.getEndDate().before(currentDate);
                }
                return false;
            })
            .filter(reservation -> !feedbackRepository.existsByReservation_ReservationID(reservation.getReservationID()))
            .collect(Collectors.toList());

        // 3. Map to DTOs
        return reservationsEligibleForFeedback.stream()
            .map(reservationMapper::toDto)
            .collect(Collectors.toList());
    }
} 