package com.rentacar.service;

import com.rentacar.entity.County;
import com.rentacar.dto.CountyDTO;
import java.util.List;
import java.util.Optional;

public interface CountyService {
    County createCounty(County county);
    County updateCounty(Integer id, County county);
    void deleteCounty(Integer id);
    Optional<County> getCountyById(Integer id);
    List<County> getAllCounties();
    List<CountyDTO> getAllCountyDTOs();
    List<CountyDTO> getCountiesByCityId(Integer cityId);
} 