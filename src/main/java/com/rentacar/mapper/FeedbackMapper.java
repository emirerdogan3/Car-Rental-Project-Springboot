package com.rentacar.mapper;

import com.rentacar.dto.FeedbackDTO;
import com.rentacar.entity.Feedback;
import com.rentacar.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, ReservationMapper.class, UserMapper.class, CarMapper.class, BranchMapper.class})
public interface FeedbackMapper {
    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);

    @Mapping(target = "customerID", source = "customer.user.userID")
    @Mapping(target = "reservationID", source = "reservation.reservationID")
    @Mapping(target = "carID", source = "reservation.car.carID")
    @Mapping(target = "branchId", source = "reservation.branch.branchID")
    FeedbackDTO toDto(Feedback feedback);

    @Mapping(target = "customer.user.userID", source = "customerID")
    @Mapping(target = "reservation.reservationID", source = "reservationID")
    Feedback toEntity(FeedbackDTO feedbackDTO);

    @AfterMapping
    default void fillDetails(Feedback feedback, @MappingTarget FeedbackDTO dto) {
        if (feedback.getCustomer() != null && feedback.getCustomer().getUser() != null) {
            String firstName = feedback.getCustomer().getFirstName() != null ? feedback.getCustomer().getFirstName() : "";
            String lastName = feedback.getCustomer().getLastName() != null ? feedback.getCustomer().getLastName() : "";
            dto.setCustomerName((firstName + " " + lastName).trim());
        }
        if (feedback.getReservation() != null && feedback.getReservation().getCar() != null) {
            Car car = feedback.getReservation().getCar();
            String plate = car.getPlateNumber();
            String brand = car.getBrand() != null ? car.getBrand().getBrandName() : "";
            String model = car.getModel() != null ? car.getModel().getModelName() : "";
            if (plate != null && !plate.isBlank()){
                dto.setCarIdentifier(plate);
            } else {
                dto.setCarIdentifier((brand + " " + model).trim());
            }
        }
        if (feedback.getReservation() != null && feedback.getReservation().getBranch() != null) {
            dto.setBranchName(feedback.getReservation().getBranch().getBranchName());
        }
    }
} 