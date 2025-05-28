package com.rentacar.service;

import com.rentacar.entity.City;
import com.rentacar.dto.CityDTO;
import java.util.List;
import java.util.Optional;

public interface CityService {
    City createCity(City city);
    City updateCity(Integer id, City city);
    void deleteCity(Integer id);
    Optional<City> getCityById(Integer id);
    List<City> getAllCities();
    List<CityDTO> getAllCityDTOs();
    List<CityDTO> getCitiesByCountryId(Integer countryId);
} 