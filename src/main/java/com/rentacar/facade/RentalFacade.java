package com.rentacar.facade;

import com.rentacar.entity.Car;
import com.rentacar.entity.Reservation;
import com.rentacar.entity.CustomerPayment;
import com.rentacar.dto.CarDTO;
import com.rentacar.mapper.CarMapper;
import com.rentacar.service.CarService;
import com.rentacar.service.ReservationService;
import com.rentacar.service.CustomerPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Facade Pattern Kullanımı

@Component
public class RentalFacade {
    private final CarService carService;
    private final ReservationService reservationService;
    private final CustomerPaymentService paymentService;
    private final CarMapper carMapper;

    @Autowired
    public RentalFacade(CarService carService, ReservationService reservationService, CustomerPaymentService paymentService, CarMapper carMapper) {
        this.carService = carService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.carMapper = carMapper;
    }

    // Facade ile tek adımda araç kiralama ve ödeme işlemi
    public Reservation rentAndPay(Car car, Reservation reservation, CustomerPayment payment) {
        // CarDTO carDto = carMapper.toDto(car); // Bu facade'ın mantığı güncel servislerle uyumsuz.
        // CarDTO updatedCarDto = carService.updateCar(car.getCarID(), carDto); // Car status update is handled by ReservationService
        // Reservation savedReservation = reservationService.createReservation(reservation); // PROBLEM HERE
        // paymentService.makePayment(payment); // Payment is handled by ReservationService
        // return savedReservation;
        throw new UnsupportedOperationException("RentalFacade.rentAndPay is outdated. Use ReservationService.createReservationAndProcessPayment.");
    }
} 