package com.rentacar.repository.specification;

import com.rentacar.dto.filter.ReservationFilterDTO;
import com.rentacar.entity.Reservation;
import com.rentacar.entity.Customer;
import com.rentacar.entity.Car;
import com.rentacar.entity.Branch;
import com.rentacar.entity.User; // For Customer -> User join
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservationSpecification {

    public Specification<Reservation> findByCriteria(final ReservationFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getBranchId() != null) {
                Join<Reservation, Branch> branchJoin = root.join("branch");
                predicates.add(criteriaBuilder.equal(branchJoin.get("branchID"), filter.getBranchId()));
            }

            if (filter.getCustomerId() != null) {
                Join<Reservation, Customer> customerJoin = root.join("customer");
                predicates.add(criteriaBuilder.equal(customerJoin.get("customerID"), filter.getCustomerId()));
            }

            if (filter.getCarId() != null) {
                Join<Reservation, Car> carJoin = root.join("car");
                predicates.add(criteriaBuilder.equal(carJoin.get("carID"), filter.getCarId()));
            }

            if (filter.getStartDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDateFrom()));
            }
            if (filter.getStartDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), filter.getStartDateTo()));
            }

            if (filter.getEndDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), filter.getEndDateFrom()));
            }
            if (filter.getEndDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), filter.getEndDateTo()));
            }

            if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), filter.getStatus().toLowerCase()));
            }

            // Default sort by reservation ID descending if no other sort is specified by Pageable
            query.orderBy(criteriaBuilder.desc(root.get("reservationID")));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 