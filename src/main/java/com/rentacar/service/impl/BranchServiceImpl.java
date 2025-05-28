package com.rentacar.service.impl;

import com.rentacar.dto.BranchDTO;
import com.rentacar.dto.filter.BranchFilterDTO;
import com.rentacar.entity.Branch;
import com.rentacar.entity.MoneyAccount;
import com.rentacar.entity.County;
import com.rentacar.mapper.BranchMapper;
import com.rentacar.repository.BranchRepository;
import com.rentacar.repository.MoneyAccountRepository;
import com.rentacar.repository.CountyRepository;
import com.rentacar.repository.specification.BranchSpecification;
import com.rentacar.service.BranchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    private final BranchSpecification branchSpecification;
    private final MoneyAccountRepository moneyAccountRepository;
    private final CountyRepository countyRepository;

    @Override
    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(branchMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BranchDTO> findBranchesByFilters(BranchFilterDTO filter, Pageable pageable) {
        Specification<Branch> spec = branchSpecification.findByCriteria(filter);
        return branchRepository.findAll(spec, pageable).map(branchMapper::toDto);
    }

    @Override
    public Optional<BranchDTO> getBranchDtoById(Integer id) {
        return branchRepository.findById(id).map(branchMapper::toDto);
    }

    @Override
    @Transactional
    public BranchDTO createBranch(BranchDTO branchDTO) {
        if (branchDTO.getBranchName() != null && branchRepository.existsByBranchName(branchDTO.getBranchName())) {
            throw new IllegalArgumentException("Branch with name " + branchDTO.getBranchName() + " already exists.");
        }
        Branch branch = branchMapper.toEntity(branchDTO);
        Branch savedBranch = branchRepository.save(branch);

        if (moneyAccountRepository.findByBranch_BranchID(savedBranch.getBranchID()).isEmpty()) {
            Double initialBalance = branchDTO.getAccountBalance() != null ? branchDTO.getAccountBalance() : 0.0;
            MoneyAccount newAccount = MoneyAccount.builder()
                    .branch(savedBranch)
                    .moneyBalance(initialBalance)
                    .build();
            moneyAccountRepository.save(newAccount);
        }
        return branchMapper.toDto(branchRepository.findById(savedBranch.getBranchID()).orElseThrow());
    }

    @Override
    @Transactional
    public BranchDTO updateBranch(Integer id, BranchDTO branchDTO) {
        Branch existingBranch = branchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with id: " + id));

        if (branchDTO.getBranchName() != null && !branchDTO.getBranchName().equals(existingBranch.getBranchName())) {
            if (branchRepository.existsByBranchNameAndBranchIDNot(branchDTO.getBranchName(), id)) {
                throw new IllegalArgumentException("Another branch with name " + branchDTO.getBranchName() + " already exists.");
            }
        }

        // Update fields manually to preserve manager assignments and other relationships
        if (branchDTO.getBranchName() != null) {
            existingBranch.setBranchName(branchDTO.getBranchName());
        }
        if (branchDTO.getPhoneNumber() != null) {
            existingBranch.setPhoneNumber(branchDTO.getPhoneNumber());
        }
        if (branchDTO.getCountyID() != null) {
            County county = countyRepository.findById(branchDTO.getCountyID())
                    .orElseThrow(() -> new EntityNotFoundException("County not found with id: " + branchDTO.getCountyID()));
            existingBranch.setCounty(county);
        }

        Branch updatedBranch = branchRepository.save(existingBranch);
        
        // Update MoneyAccount balance if provided
        if (branchDTO.getAccountBalance() != null) {
            MoneyAccount moneyAccount = moneyAccountRepository.findByBranch_BranchID(id)
                    .orElseGet(() -> {
                        MoneyAccount newAccount = MoneyAccount.builder()
                                .branch(updatedBranch)
                                .moneyBalance(branchDTO.getAccountBalance())
                                .build();
                        return moneyAccountRepository.save(newAccount);
                    });
            moneyAccount.setMoneyBalance(branchDTO.getAccountBalance());
            moneyAccountRepository.save(moneyAccount);
        }
        
        return branchMapper.toDto(updatedBranch);
    }

    @Override
    @Transactional
    public void deleteBranch(Integer id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with id: " + id));
        moneyAccountRepository.findByBranch_BranchID(id).ifPresent(moneyAccountRepository::delete);
        
        branchRepository.delete(branch);
    }
} 