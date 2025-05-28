package com.rentacar.repository;

import com.rentacar.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer>, JpaSpecificationExecutor<Reservation> {
    long countByStatusIn(List<String> statuses);

    long countByBranch_BranchIDAndStatusIn(Integer branchId, List<String> statuses);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.car.carID = :carId " +
           "AND r.status NOT IN ('Cancelled', 'Completed', 'Iptal', 'Tamamlandi') " +
           "AND r.startDate < :endDate AND r.endDate > :startDate")
    boolean existsOverlappingReservation(@Param("carId") Integer carId, 
                                         @Param("startDate") Date startDate, 
                                         @Param("endDate") Date endDate);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.car.carID = :carId " +
           "AND r.reservationID <> :excludedReservationId " +
           "AND r.status IN ('Confirmed', 'Rented', 'Aktif')")
    boolean existsOtherActiveReservationsForCar(@Param("carId") Integer carId, 
                                                @Param("excludedReservationId") Integer excludedReservationId);

    List<Reservation> findByCustomer_CustomerIDAndStatusIn(Integer customerId, List<String> statuses);
    
    /**
     * Bitiş tarihi geçmiş ve hala "Confirmed" durumunda olan rezervasyonları bulur
     */
    @Query("SELECT r FROM Reservation r WHERE r.status = 'Confirmed' " +
           "AND r.endDate < :currentDate")
    List<Reservation> findExpiredConfirmedReservations(@Param("currentDate") Date currentDate);
    
    /**
     * Başlangıç tarihi gelmiş ve hala "Confirmed" durumunda olan rezervasyonları bulur
     */
    @Query("SELECT r FROM Reservation r WHERE r.status = 'Confirmed' " +
           "AND r.startDate <= :currentDate AND r.endDate > :currentDate")
    List<Reservation> findStartedConfirmedReservations(@Param("currentDate") Date currentDate);
} 