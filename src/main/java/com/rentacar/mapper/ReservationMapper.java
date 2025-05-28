package com.rentacar.mapper;

import com.rentacar.dto.ReservationDTO;
import com.rentacar.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, CarMapper.class, BranchMapper.class, UserMapper.class})
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(target = "customerID", source = "customer.customerID")
    @Mapping(target = "carID", source = "car.carID")
    @Mapping(target = "branchID", source = "branch.branchID")
    ReservationDTO toDto(Reservation reservation);
    
    @Mapping(target = "customer.customerID", source = "customerID")
    @Mapping(target = "car.carID", source = "carID")
    @Mapping(target = "branch.branchID", source = "branchID")
    Reservation toEntity(ReservationDTO reservationDTO);

    @AfterMapping
    default void fillNames(Reservation reservation, @MappingTarget ReservationDTO dto) {
        if (reservation.getCar() != null) {
            String brand = reservation.getCar().getBrand() != null ? reservation.getCar().getBrand().getBrandName() : "";
            String model = reservation.getCar().getModel() != null ? reservation.getCar().getModel().getModelName() : "";
            dto.setCarName((brand + " " + model).trim());
            dto.setCarBrand(brand);
            dto.setCarModel(model);
        }
        if (reservation.getBranch() != null) {
            dto.setBranchName(reservation.getBranch().getBranchName());
        }
        if (reservation.getCustomer() != null) {
            String firstName = reservation.getCustomer().getFirstName() != null ? reservation.getCustomer().getFirstName() : "";
            String lastName = reservation.getCustomer().getLastName() != null ? reservation.getCustomer().getLastName() : "";
            dto.setCustomerName((firstName + " " + lastName).trim());
        }
    }
} 