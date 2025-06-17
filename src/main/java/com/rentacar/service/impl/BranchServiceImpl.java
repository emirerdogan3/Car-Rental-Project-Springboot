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

import com.rentacar.exception.ResourceInUseException;
import com.rentacar.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

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
    // KONTROL İÇİN GEREKLİ REPOSITORY'LERİ EKLEYİN
    private final CarRepository carRepository;
    private final EmployeeRepository employeeRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final EmployeePaymentRepository employeePaymentRepository;
    private final BranchManagerRepository branchManagerRepository;

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

    /*
    @Override
    @Transactional
    public void deleteBranch(Integer id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with id: " + id));
        moneyAccountRepository.findByBranch_BranchID(id).ifPresent(moneyAccountRepository::delete);
        
        branchRepository.delete(branch);
    }*/

    // deleteBranch metodunu aşağıdaki gibi tamamen güncelleyin:
    @Override
    @Transactional
    public void deleteBranch(Integer id) {
        if (!branchRepository.existsById(id)) {
            throw new EntityNotFoundException("Branch not found with id: " + id);
        }

        // Şubenin kullanılıp kullanılmadığını kontrol et
        if (carRepository.existsByBranch_BranchID(id)) {
            throw new ResourceInUseException("This branch cannot be deleted because it has cars assigned to it.");
        }
        if (employeeRepository.existsByBranch_BranchID(id)) {
            throw new ResourceInUseException("This branch cannot be deleted because it has employees assigned to it.");
        }
        if (reservationRepository.existsByBranch_BranchID(id)) {
            throw new ResourceInUseException("This branch cannot be deleted because it is associated with reservations.");
        }
        if (branchManagerRepository.existsByBranch_BranchID(id)) {
            throw new ResourceInUseException("This branch cannot be deleted because it has managers assigned to it. Please remove managers first.");
        }
        // Finansal işlemleri kontrol et (hatayı tetikleyen asıl sebep)
        if (customerPaymentRepository.existsByAccount_Branch_BranchID(id) || employeePaymentRepository.existsByAccount_Branch_BranchID(id)) {
            throw new ResourceInUseException("This branch cannot be deleted as it has been involved in financial transactions.");
        }

        // Bütün kontrollerden geçerse, şubeyi sil
        branchRepository.deleteById(id);
    }
} 