package com.rentacar.service;

import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rentacar.entity.Car;
import com.rentacar.entity.Reservation;
import com.rentacar.repository.CarRepository;
import com.rentacar.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationSchedulerService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void updateExpiredReservations() {
        log.info("Starting automated reservation status update...");
        Date currentDate = new Date();
        
        List<Reservation> expiredReservations = reservationRepository.findExpiredConfirmedReservations(currentDate);
        int updatedCount = 0;
        
        for (Reservation reservation : expiredReservations) {
            try {
                String oldStatus = reservation.getStatus();
                reservation.setStatus("Completed");
                reservationRepository.save(reservation);
                
                Car car = reservation.getCar();
                if (car != null) {
                    boolean hasOtherActiveReservations = reservationRepository.existsOtherActiveReservationsForCar(car.getCarID(), reservation.getReservationID());
                    if (!hasOtherActiveReservations) {
                        car.setStatus("Available");
                        carRepository.save(car);
                    }
                }
                updatedCount++;
            } catch (Exception e) {
                log.error("Error updating reservation: " + e.getMessage());
            }
        }
        log.info("Updated " + updatedCount + " reservations to Completed status.");
    }

    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void updateStartedReservations() {
        log.info("Starting car status update...");
        Date currentDate = new Date();
        
        List<Reservation> startedReservations = reservationRepository.findStartedConfirmedReservations(currentDate);
        int updatedCount = 0;
        
        for (Reservation reservation : startedReservations) {
            try {
                Car car = reservation.getCar();
                if (car != null && "Available".equals(car.getStatus())) {
                    car.setStatus("Rented");
                    carRepository.save(car);
                    updatedCount++;
                }
            } catch (Exception e) {
                log.error("Error updating car status: " + e.getMessage());
            }
        }
        log.info("Updated " + updatedCount + " cars to Rented status.");
    }

    @Transactional
    public void manualUpdateExpiredReservations() {
        log.info("Manual reservation update triggered...");
        updateExpiredReservations();
    }
} 