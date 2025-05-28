package com.rentacar.repository.specification;

import com.rentacar.dto.filter.BranchFilterDTO;
import com.rentacar.entity.Branch;
import com.rentacar.entity.City;
import com.rentacar.entity.Country;
import com.rentacar.entity.County;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BranchSpecification {

    public Specification<Branch> findByCriteria(final BranchFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCountryId() != null) {
                Join<Branch, Country> countryJoin = root.join("country");
                predicates.add(criteriaBuilder.equal(countryJoin.get("countryID"), filter.getCountryId()));
            }
            if (filter.getCityId() != null) {
                Join<Branch, City> cityJoin = root.join("city");
                predicates.add(criteriaBuilder.equal(cityJoin.get("cityID"), filter.getCityId()));
            }
            if (filter.getCountyId() != null) {
                Join<Branch, County> countyJoin = root.join("county");
                predicates.add(criteriaBuilder.equal(countyJoin.get("countyID"), filter.getCountyId()));
            }
            if (filter.getBranchName() != null && !filter.getBranchName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("branchName")), "%" + filter.getBranchName().toLowerCase() + "%"));
            }

            // Önemli: Fetch join'ler N+1 problemini çözmek için eklenebilir, özellikle listeleme yapılıyorsa.
            // query.distinct(true); // Eğer join'ler multiple bag fetch yapıyorsa distinct gerekebilir.
            // if (query.getResultType() != Long.class && query.getResultType() != long.class) { // Count sorguları için join yapma
            //     root.fetch("country", jakarta.persistence.criteria.JoinType.LEFT);
            //     root.fetch("city", jakarta.persistence.criteria.JoinType.LEFT);
            //     root.fetch("county", jakarta.persistence.criteria.JoinType.LEFT);
            //     root.fetch("moneyAccount", jakarta.persistence.criteria.JoinType.LEFT);
            //     root.fetch("branchManagers", jakarta.persistence.criteria.JoinType.LEFT).fetch("managerUser", jakarta.persistence.criteria.JoinType.LEFT);
            // }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 