package com.rentacar.service.impl;

import com.rentacar.dto.CountyDTO;
import com.rentacar.entity.County;
import com.rentacar.mapper.CountyMapper;
import com.rentacar.repository.CountyRepository;
import com.rentacar.service.CountyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountyServiceImpl implements CountyService {
    private final CountyRepository countyRepository;
    private final CountyMapper countyMapper;

    @Override
    public County createCounty(County county) {
        return countyRepository.save(county);
    }

    @Override
    public County updateCounty(Integer id, County county) {
        county.setCountyID(id);
        return countyRepository.save(county);
    }

    @Override
    public void deleteCounty(Integer id) {
        countyRepository.deleteById(id);
    }

    @Override
    public Optional<County> getCountyById(Integer id) {
        return countyRepository.findById(id);
    }

    @Override
    public List<County> getAllCounties() {
        return countyRepository.findAll();
    }

    @Override
    public List<CountyDTO> getAllCountyDTOs() {
        return countyRepository.findAll().stream()
                .map(countyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CountyDTO> getCountiesByCityId(Integer cityId) {
        return countyRepository.findByCity_CityID(cityId).stream()
                .map(countyMapper::toDto)
                .collect(Collectors.toList());
    }
} 