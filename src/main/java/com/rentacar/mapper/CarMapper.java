package com.rentacar.mapper;

import com.rentacar.dto.CarDTO;
import com.rentacar.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarMapper {
    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    @Mapping(source = "brand.brandID", target = "brandID")
    @Mapping(source = "brand.brandName", target = "brandName")
    @Mapping(source = "model.modelID", target = "modelID")
    @Mapping(source = "model.modelName", target = "modelName")
    @Mapping(source = "category.categoryID", target = "categoryID")
    @Mapping(source = "category.categoryName", target = "categoryName")
    @Mapping(source = "fuelType.fuelTypeID", target = "fuelTypeID")
    @Mapping(source = "fuelType.fuelTypeName", target = "fuelTypeName")
    @Mapping(source = "color.colorID", target = "colorID")
    @Mapping(source = "color.colorName", target = "colorName")
    @Mapping(source = "branch.branchID", target = "branchID")
    @Mapping(source = "branch.branchName", target = "branchName")
    CarDTO toDto(Car car);

    @Mapping(source = "brandID", target = "brand", qualifiedByName = "mapBrandIdToCarBrand")
    @Mapping(source = "modelID", target = "model", qualifiedByName = "mapModelIdToCarBrandModel")
    @Mapping(source = "categoryID", target = "category", qualifiedByName = "mapCategoryIdToCarCategory")
    @Mapping(source = "fuelTypeID", target = "fuelType", qualifiedByName = "mapFuelTypeIdToFuelType")
    @Mapping(source = "colorID", target = "color", qualifiedByName = "mapColorIdToColor")
    @Mapping(source = "branchID", target = "branch", qualifiedByName = "mapBranchIdToBranch")
    Car toEntity(CarDTO carDTO);

    @Named("mapBrandIdToCarBrand")
    default CarBrand mapBrandIdToCarBrand(Integer id) {
        if (id == null) return null;
        CarBrand entity = new CarBrand();
        entity.setBrandID(id);
        return entity;
    }

    @Named("mapModelIdToCarBrandModel")
    default CarBrandModel mapModelIdToCarBrandModel(Integer id) {
        if (id == null) return null;
        CarBrandModel entity = new CarBrandModel();
        entity.setModelID(id);
        return entity;
    }

    @Named("mapCategoryIdToCarCategory")
    default CarCategory mapCategoryIdToCarCategory(Integer id) {
        if (id == null) return null;
        CarCategory entity = new CarCategory();
        entity.setCategoryID(id);
        return entity;
    }

    @Named("mapFuelTypeIdToFuelType")
    default FuelType mapFuelTypeIdToFuelType(Integer id) {
        if (id == null) return null;
        FuelType entity = new FuelType();
        entity.setFuelTypeID(id);
        return entity;
    }

    @Named("mapColorIdToColor")
    default Color mapColorIdToColor(Integer id) {
        if (id == null) return null;
        Color entity = new Color();
        entity.setColorID(id);
        return entity;
    }

    @Named("mapBranchIdToBranch")
    default Branch mapBranchIdToBranch(Integer id) {
        if (id == null) return null;
        Branch entity = new Branch();
        entity.setBranchID(id);
        return entity;
    }
} 