package com.rentacar.service;

import com.rentacar.dto.BranchDTO;
import com.rentacar.dto.filter.BranchFilterDTO;
// import com.rentacar.entity.Branch; // Kaldırıldı
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List; // getAllBranches() için List<BranchDTO> dönebiliriz (sayfasız)
import java.util.Optional;

public interface BranchService {
    List<BranchDTO> getAllBranches(); // Sayfasız tüm şubeler (DTO)
    Page<BranchDTO> findBranchesByFilters(BranchFilterDTO filter, Pageable pageable); // Filtreli ve sayfalı

    // List<Branch> getBranchesByLocation(Integer countryId, Integer cityId, Integer countyId); // Eski, findBranchesByFilters ile değiştirildi

    Optional<BranchDTO> getBranchDtoById(Integer id); // Branch yerine Optional<BranchDTO>

    BranchDTO createBranch(BranchDTO branchDTO); // Parametre ve dönüş BranchDTO

    BranchDTO updateBranch(Integer id, BranchDTO branchDTO); // Parametre ve dönüş BranchDTO

    void deleteBranch(Integer id);

    // Branch getBranchById(Integer id); // Eski, Optional<BranchDTO> getBranchDtoById ile değiştirildi
} 