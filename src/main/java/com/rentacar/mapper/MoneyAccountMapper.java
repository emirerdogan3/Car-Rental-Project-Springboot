package com.rentacar.mapper;

import com.rentacar.dto.MoneyAccountDTO;
import com.rentacar.entity.MoneyAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {BranchMapper.class})
public interface MoneyAccountMapper {
    MoneyAccountMapper INSTANCE = Mappers.getMapper(MoneyAccountMapper.class);

    @Mapping(target = "branchID", source = "branch.branchID")
    MoneyAccountDTO toDto(MoneyAccount moneyAccount);

    @Mapping(target = "branch.branchID", source = "branchID")
    MoneyAccount toEntity(MoneyAccountDTO moneyAccountDTO);
} 