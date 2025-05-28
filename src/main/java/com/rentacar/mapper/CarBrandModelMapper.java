package com.rentacar.mapper;

import com.rentacar.dto.CarBrandModelDTO;
import com.rentacar.entity.CarBrandModel;
import com.rentacar.entity.CarBrand; // Required for mapping brandID to CarBrand entity
import com.rentacar.service.CarBrandService; // Required for fetching CarBrand entity if only brandID is available
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {CarBrandMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CarBrandModelMapper {

    @Autowired
    protected CarBrandService carBrandService; // For converting brandID to CarBrand entity

    @Mapping(source = "brand.brandID", target = "brandID")
    @Mapping(source = "brand.brandName", target = "brandName")
    public abstract CarBrandModelDTO toDto(CarBrandModel carBrandModel);

    @Mapping(source = "brandID", target = "brand", qualifiedByName = "brandIdToCarBrand")
    public abstract CarBrandModel toEntity(CarBrandModelDTO carBrandModelDTO);

    @Named("brandIdToCarBrand")
    protected CarBrand brandIdToCarBrand(Integer brandID) {
        if (brandID == null) {
            return null;
        }
        return carBrandService.getCarBrandById(brandID) // Changed to use existing getCarBrandById
            .orElseThrow(() -> new IllegalArgumentException("Invalid Brand ID: " + brandID));
    }
} 