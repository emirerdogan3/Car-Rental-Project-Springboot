package com.rentacar.service;

import com.rentacar.dto.CarDTO;
import com.rentacar.entity.Car;
import com.rentacar.mapper.CarMapper;
import com.rentacar.repository.CarRepository;
import com.rentacar.service.impl.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {
    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCarDtoById() {
        Car car = new Car();
        car.setCarID(1);
        CarDTO carDTO = new CarDTO();
        carDTO.setCarID(1);

        when(carRepository.findById(1)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carDTO);

        Optional<CarDTO> resultOpt = carService.getCarDtoById(1);
        assertTrue(resultOpt.isPresent());
        CarDTO result = resultOpt.get();
        assertNotNull(result);
        assertEquals(1, result.getCarID());
    }

    @Test
    void testCreateCar() {
        CarDTO carDTO = new CarDTO();
        carDTO.setPlateNumber("34ABC123");
        
        Car carEntity = new Car();
        carEntity.setPlateNumber("34ABC123");

        when(carMapper.toEntity(any(CarDTO.class))).thenReturn(carEntity);
        when(carRepository.save(any(Car.class))).thenReturn(carEntity);
        when(carMapper.toDto(any(Car.class))).thenReturn(carDTO);

        CarDTO result = carService.createCar(carDTO);
        assertNotNull(result);
        assertEquals("34ABC123", result.getPlateNumber());
        verify(carRepository, times(1)).save(carEntity);
    }
} 