package com.rentacar.service.impl;

import com.rentacar.dto.CityDTO;
import com.rentacar.entity.City;
import com.rentacar.mapper.CityMapper;
import com.rentacar.repository.CityRepository;
import com.rentacar.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Override
    public City createCity(City city) {
        return cityRepository.save(city);
    }

    @Override
    public City updateCity(Integer id, City city) {
        city.setCityID(id);
        return cityRepository.save(city);
    }

    @Override
    public void deleteCity(Integer id) {
        cityRepository.deleteById(id);
    }

    @Override
    public Optional<City> getCityById(Integer id) {
        return cityRepository.findById(id);
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public List<CityDTO> getAllCityDTOs() {
        return cityRepository.findAll().stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CityDTO> getCitiesByCountryId(Integer countryId) {
        return cityRepository.findByCountry_CountryID(countryId).stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList());
    }
} 