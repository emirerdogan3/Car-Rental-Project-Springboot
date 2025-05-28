package com.rentacar.repository.specification;

import com.rentacar.dto.filter.CarFilterDTO;
import com.rentacar.entity.*; // Car, CarBrand, CarBrandModel, etc.
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component // Servislerde inject edilebilir veya statik metotlar kullanılabilir.
public class CarSpecification {

    public Specification<Car> findByCriteria(final CarFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getBrandId() != null) {
                Join<Car, CarBrand> brandJoin = root.join("brand");
                predicates.add(criteriaBuilder.equal(brandJoin.get("brandID"), filter.getBrandId()));
            }
            if (filter.getModelId() != null) {
                Join<Car, CarBrandModel> modelJoin = root.join("model");
                predicates.add(criteriaBuilder.equal(modelJoin.get("modelID"), filter.getModelId()));
            }
            if (filter.getCategoryId() != null) {
                Join<Car, CarCategory> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("categoryID"), filter.getCategoryId()));
            }
            if (filter.getFuelTypeId() != null) {
                Join<Car, FuelType> fuelTypeJoin = root.join("fuelType");
                predicates.add(criteriaBuilder.equal(fuelTypeJoin.get("fuelTypeID"), filter.getFuelTypeId()));
            }
            if (filter.getColorId() != null) {
                Join<Car, Color> colorJoin = root.join("color");
                predicates.add(criteriaBuilder.equal(colorJoin.get("colorID"), filter.getColorId()));
            }
            if (filter.getBranchId() != null) {
                Join<Car, Branch> branchJoin = root.join("branch");
                predicates.add(criteriaBuilder.equal(branchJoin.get("branchID"), filter.getBranchId()));
            }
            if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), filter.getStatus().toLowerCase()));
            }
            if (filter.getMinModelYear() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("modelYear"), filter.getMinModelYear()));
            }
            if (filter.getMaxModelYear() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("modelYear"), filter.getMaxModelYear()));
            }
            if (filter.getMinPricePerDay() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("pricePerDay"), filter.getMinPricePerDay()));
            }
            if (filter.getMaxPricePerDay() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("pricePerDay"), filter.getMaxPricePerDay()));
            }
            if (filter.getPlateNumber() != null && !filter.getPlateNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("plateNumber")), "%" + filter.getPlateNumber().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Sadece belirtilen şubedeki araçları filtreleyen specification (status kontrolü kaldırıldı)
    public Specification<Car> findAvailableCarsByBranch(Integer branchId, final CarFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Her zaman belirli bir şube
            if (branchId != null) {
                Join<Car, Branch> branchJoin = root.join("branch");
                predicates.add(criteriaBuilder.equal(branchJoin.get("branchID"), branchId));
            }

            // Diğer filtreler (CarFilterDTO'dan gelenler)
            if (filter != null) {
                if (filter.getBrandId() != null) {
                    Join<Car, CarBrand> brandJoin = root.join("brand");
                    predicates.add(criteriaBuilder.equal(brandJoin.get("brandID"), filter.getBrandId()));
                }
                if (filter.getModelId() != null) {
                    Join<Car, CarBrandModel> modelJoin = root.join("model");
                    predicates.add(criteriaBuilder.equal(modelJoin.get("modelID"), filter.getModelId()));
                }
                if (filter.getCategoryId() != null) {
                    Join<Car, CarCategory> categoryJoin = root.join("category");
                    predicates.add(criteriaBuilder.equal(categoryJoin.get("categoryID"), filter.getCategoryId()));
                }
                if (filter.getFuelTypeId() != null) {
                    Join<Car, FuelType> fuelTypeJoin = root.join("fuelType");
                    predicates.add(criteriaBuilder.equal(fuelTypeJoin.get("fuelTypeID"), filter.getFuelTypeId()));
                }
                if (filter.getColorId() != null) {
                    Join<Car, Color> colorJoin = root.join("color");
                    predicates.add(criteriaBuilder.equal(colorJoin.get("colorID"), filter.getColorId()));
                }
                // Diğer filtreler (modelYear, pricePerDay vb.)
                if (filter.getMinModelYear() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("modelYear"), filter.getMinModelYear()));
                }
                if (filter.getMaxModelYear() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("modelYear"), filter.getMaxModelYear()));
                }
                if (filter.getMinPricePerDay() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("pricePerDay"), filter.getMinPricePerDay()));
                }
                if (filter.getMaxPricePerDay() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("pricePerDay"), filter.getMaxPricePerDay()));
                }
                if (filter.getPlateNumber() != null && !filter.getPlateNumber().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("plateNumber")), "%" + filter.getPlateNumber().toLowerCase() + "%"));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 