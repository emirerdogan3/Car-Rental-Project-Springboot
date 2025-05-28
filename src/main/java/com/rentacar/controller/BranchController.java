package com.rentacar.controller;

import com.rentacar.dto.BranchDTO;
import com.rentacar.dto.filter.BranchFilterDTO;
import com.rentacar.mapper.BranchMapper;
import com.rentacar.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/branches")
@PreAuthorize("hasRole('ADMIN')")
public class BranchController {
    private final BranchService branchService;
    private final BranchMapper branchMapper;

    @Autowired
    public BranchController(BranchService branchService, BranchMapper branchMapper) {
        this.branchService = branchService;
        this.branchMapper = branchMapper;
    }

    @GetMapping
    public List<BranchDTO> getBranches(@RequestParam(required = false) Integer countryId,
                                       @RequestParam(required = false) Integer cityId,
                                       @RequestParam(required = false) Integer countyId) {
        if (countryId == null && cityId == null && countyId == null) {
            return branchService.getAllBranches();
        } else {
            BranchFilterDTO filter = BranchFilterDTO.builder()
                    .countryId(countryId)
                    .cityId(cityId)
                    .countyId(countyId)
                    .build();
            return branchService.findBranchesByFilters(filter, Pageable.unpaged()).getContent();
        }
    }

    @PostMapping
    public BranchDTO createBranch(@RequestBody BranchDTO branchDTO) {
        return branchService.createBranch(branchDTO);
    }

    @PutMapping("/{id}")
    public BranchDTO updateBranch(@PathVariable Integer id, @RequestBody BranchDTO branchDTO) {
        return branchService.updateBranch(id, branchDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Integer id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable Integer id) {
        Optional<BranchDTO> branchDtoOpt = branchService.getBranchDtoById(id);
        return branchDtoOpt.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }
} 