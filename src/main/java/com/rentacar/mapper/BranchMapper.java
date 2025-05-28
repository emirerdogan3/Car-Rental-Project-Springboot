package com.rentacar.mapper;

import com.rentacar.dto.BranchDTO;
import com.rentacar.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    BranchMapper INSTANCE = Mappers.getMapper(BranchMapper.class);

    @Mapping(source = "country.countryID", target = "countryID")
    @Mapping(source = "country.countryName", target = "countryName")
    @Mapping(source = "city.cityID", target = "cityID")
    @Mapping(source = "city.cityName", target = "cityName")
    @Mapping(source = "county.countyID", target = "countyID")
    @Mapping(source = "county.countyName", target = "countyName")
    @Mapping(target = "accountBalance", ignore = true) // MoneyAccount'tan sonra set edilecek
    @Mapping(target = "managerNames", ignore = true) // BranchManager'lardan sonra set edilecek
    BranchDTO toDto(Branch branch);

    @Mapping(source = "countryID", target = "country", qualifiedByName = "mapCountryIdToCountry")
    @Mapping(source = "cityID", target = "city", qualifiedByName = "mapCityIdToCity")
    @Mapping(source = "countyID", target = "county", qualifiedByName = "mapCountyIdToCounty")
    @Mapping(target = "moneyAccount", ignore = true) // DTO'da accountBalance var, MoneyAccount değil.
    @Mapping(target = "branchManagers", ignore = true) // DTO'da managerNames var, Set<BranchManager> değil.
    Branch toEntity(BranchDTO branchDTO);

    @AfterMapping
    default void mapAccountBalanceAndManagersToDto(Branch branch, @MappingTarget BranchDTO dto) {
        if (branch.getMoneyAccount() != null) {
            dto.setAccountBalance(branch.getMoneyAccount().getMoneyBalance());
        }
        if (branch.getBranchManagers() != null) {
            List<String> managerUsernames = branch.getBranchManagers().stream()
                    .map(bm -> bm.getManager() != null && bm.getManager().getUser() != null ? bm.getManager().getUser().getUsername() : "N/A")
                    .collect(Collectors.toList());
            dto.setManagerNames(managerUsernames);
        } else {
            dto.setManagerNames(Collections.emptyList());
        }
    }

    @Named("mapCountryIdToCountry")
    default Country mapCountryIdToCountry(Integer id) {
        if (id == null) return null;
        Country entity = new Country();
        entity.setCountryID(id);
        return entity;
    }

    @Named("mapCityIdToCity")
    default City mapCityIdToCity(Integer id) {
        if (id == null) return null;
        City entity = new City();
        entity.setCityID(id);
        return entity;
    }

    @Named("mapCountyIdToCounty")
    default County mapCountyIdToCounty(Integer id) {
        if (id == null) return null;
        County entity = new County();
        entity.setCountyID(id);
        return entity;
    }
} 