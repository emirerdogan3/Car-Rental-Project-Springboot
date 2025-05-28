package com.rentacar.service.impl;

import com.rentacar.dto.CarDTO;
import com.rentacar.dto.filter.CarFilterDTO;
import com.rentacar.entity.Car;
import com.rentacar.mapper.CarMapper;
import com.rentacar.repository.CarRepository;
import com.rentacar.repository.ReservationRepository;
import com.rentacar.repository.specification.CarSpecification;
import com.rentacar.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final CarSpecification carSpecification;
    private final ReservationRepository reservationRepository;

    @Override
    public Page<CarDTO> findCarsByFilters(CarFilterDTO filter, Pageable pageable) {
        Specification<Car> spec = carSpecification.findByCriteria(filter);
        return carRepository.findAll(spec, pageable).map(carMapper::toDto);
    }

    @Override
    public Page<CarDTO> findCarsByBranchAndFilters(Integer branchId, CarFilterDTO filter, Pageable pageable) {
        CarFilterDTO effectiveFilter = (filter == null) ? CarFilterDTO.builder().build() : filter;
        effectiveFilter.setBranchId(branchId);
        Specification<Car> spec = carSpecification.findByCriteria(effectiveFilter);
        return carRepository.findAll(spec, pageable).map(carMapper::toDto);
    }

    @Override
    public Page<CarDTO> findAvailableCarsByBranchAndFilters(Integer branchId, CarFilterDTO filter, Pageable pageable) {
        CarFilterDTO effectiveFilter = (filter == null) ? CarFilterDTO.builder().build() : filter;
        Specification<Car> spec = carSpecification.findAvailableCarsByBranch(branchId, effectiveFilter);
        return carRepository.findAll(spec, pageable).map(carMapper::toDto);
    }

    @Override
    public Page<CarDTO> findAvailableCarsByBranchAndFilters(Integer branchId, CarFilterDTO filter, Pageable pageable, java.util.Date startDate, java.util.Date endDate) {
        // First get all cars from the branch that match the filters
        CarFilterDTO effectiveFilter = (filter == null) ? CarFilterDTO.builder().build() : filter;
        Specification<Car> spec = carSpecification.findAvailableCarsByBranch(branchId, effectiveFilter);
        Page<Car> allCars = carRepository.findAll(spec, pageable);
        
        // If no date range specified, return all cars
        if (startDate == null || endDate == null) {
            return allCars.map(carMapper::toDto);
        }
        
        // Filter cars that are actually available during the specified date range
        List<CarDTO> availableCars = allCars.getContent().stream()
                .filter(car -> {
                    // Check if car is available during the specified period
                    return !reservationRepository.existsOverlappingReservation(car.getCarID(), startDate, endDate);
                })
                .map(carMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
        
        // Convert to Page
        return new org.springframework.data.domain.PageImpl<>(
            availableCars, 
            pageable, 
            availableCars.size()
        );
    }

    @Override
    public Optional<CarDTO> getCarDtoById(Integer id) {
        return carRepository.findById(id).map(carMapper::toDto);
    }

    @Override
    @Transactional
    public CarDTO createCar(CarDTO carDTO) {
        if (carDTO.getPlateNumber() != null && carRepository.existsByPlateNumber(carDTO.getPlateNumber())){
             throw new IllegalArgumentException("Car with plate number " + carDTO.getPlateNumber() + " already exists.");
        }
        Car car = carMapper.toEntity(carDTO);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    @Transactional
    public CarDTO updateCar(Integer id, CarDTO carDTO) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));

        if (carDTO.getPlateNumber() != null && !carDTO.getPlateNumber().equals(existingCar.getPlateNumber())) {
            if (carRepository.existsByPlateNumberAndCarIDNot(carDTO.getPlateNumber(), id)) {
                throw new IllegalArgumentException("Another car with plate number " + carDTO.getPlateNumber() + " already exists.");
            }
        }

        Car carToUpdate = carMapper.toEntity(carDTO);
        carToUpdate.setCarID(existingCar.getCarID());

        return carMapper.toDto(carRepository.save(carToUpdate));
    }

    @Override
    @Transactional
    public void deleteCar(Integer id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Car not found with id: " + id);
        }
        carRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCarsByBranch(Integer branchId) {
        if (branchId == null) return 0;
        return carRepository.countByBranch_BranchID(branchId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAvailableCarsByBranch(Integer branchId) {
        if (branchId == null) return 0;
        return carRepository.countByBranch_BranchIDAndStatus(branchId, "Available");
    }
} 