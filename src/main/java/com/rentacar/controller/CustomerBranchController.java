package com.rentacar.controller;

import com.rentacar.dto.BranchDTO;
import com.rentacar.dto.CityDTO;
import com.rentacar.dto.CountryDTO;
import com.rentacar.dto.CountyDTO;
import com.rentacar.dto.filter.BranchFilterDTO;
import com.rentacar.service.BranchService;
import com.rentacar.service.CityService;
import com.rentacar.service.CountryService;
import com.rentacar.service.CountyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/customer/branches")
@RequiredArgsConstructor
public class CustomerBranchController {

    private final BranchService branchService;
    private final CountryService countryService;
    private final CityService cityService;
    private final CountyService countyService;

    @GetMapping
    public String listBranches(@ModelAttribute("filter") BranchFilterDTO filter,
                               Model model,
                               @PageableDefault(size = 9) Pageable pageable) {
        Page<BranchDTO> branchPage = branchService.findBranchesByFilters(filter, pageable);
        List<CountryDTO> countries = countryService.getAllCountryDTOs();

        model.addAttribute("branchPage", branchPage);
        model.addAttribute("branches", branchPage.getContent());
        model.addAttribute("countries", countries);
        model.addAttribute("filter", filter); // Filtre değerlerini forma geri göndermek için
        
        // Add selected filter values for template dropdowns
        model.addAttribute("selectedCountryId", filter.getCountryId());
        model.addAttribute("selectedCityId", filter.getCityId());
        model.addAttribute("selectedCountyId", filter.getCountyId());

        // Eğer ülke seçilmişse şehirleri, şehir seçilmişse ilçeleri yükle (sayfa ilk açıldığında)
        if (filter.getCountryId() != null) {
            model.addAttribute("cities", cityService.getCitiesByCountryId(filter.getCountryId()));
        }
        if (filter.getCityId() != null) {
            model.addAttribute("counties", countyService.getCountiesByCityId(filter.getCityId()));
        }
        return "customer/branches"; // templates/customer/branches.html
    }

    // AJAX için şehirleri getiren endpoint
    @GetMapping("/api/cities")
    @ResponseBody
    public List<CityDTO> getCitiesByCountry(@RequestParam("countryId") Integer countryId) {
        if (countryId == null) return Collections.emptyList();
        return cityService.getCitiesByCountryId(countryId);
    }

    // AJAX için ilçeleri getiren endpoint
    @GetMapping("/api/counties")
    @ResponseBody
    public List<CountyDTO> getCountiesByCity(@RequestParam("cityId") Integer cityId) {
        if (cityId == null) return Collections.emptyList();
        return countyService.getCountiesByCityId(cityId);
    }
} 