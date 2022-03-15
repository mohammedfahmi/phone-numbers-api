package com.jumia.phonenumbersapi.service;

import com.jumia.phonenumbersapi.configuration.phoneNumber.PhoneNumberConfiguration;
import com.jumia.phonenumbersapi.entities.Customer;
import com.jumia.phonenumbersapi.model.*;
import com.jumia.phonenumbersapi.repositories.CustomerSpecifications;
import com.jumia.phonenumbersapi.repositories.CustomersRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
@Slf4j
@AllArgsConstructor
@Service
public class CustomersPhonesService {
    private CustomersRepository customersRepository;
    private PhoneNumberConfiguration phoneNumberConfiguration;

    public PhoneNumbersResponseModel getPhoneNumbersPage(CustomersPhoneNumbersFilterCriteria filterCriteria) {
        Page<Customer> customerPage = getCustomersPage(filterCriteria);
        if(customerPage.getTotalPages() != 0 && filterCriteria.getPage() > customerPage.getTotalPages()-1) {
            log.warn(String.valueOf(customerPage.getTotalPages()));
            throw new ValidationException(format("Page number {0} is not available", filterCriteria.getPage()));
        }
        List<PhoneNumberModel> phoneNumbers = customerPage.getContent().stream()
                .map( customer -> phoneNumberConfiguration.toPhoneModel(customer.getPhone()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        PaginationLinkModel paginationLinks = PaginationLinkModel.builder()
                .firstPage(0)
                .currentPage(customerPage.getPageable().getPageNumber())
                .lastPage(customerPage.getTotalPages() == 0 ? customerPage.getTotalPages() : customerPage.getTotalPages()-1)
                .totalPages(customerPage.getTotalPages())
                .totalElements((int) customerPage.getTotalElements())
                .size(customerPage.getSize())
                .build();
        return PhoneNumbersResponseModel.builder()
                .phoneNumbers(phoneNumbers)
                .paginationLinks(paginationLinks)
                .build();
    }

    public Page<Customer> getCustomersPage(CustomersPhoneNumbersFilterCriteria filterCriteria) {
        Integer page = filterCriteria.getPage();
        Integer size = filterCriteria.getSize();
        Pageable customerPhoneNumberById = PageRequest.of(page, size, Sort.by("id"));
        if(filterCriteria.getPhoneNumbersFilterCriteria().isEmpty()) {
            return customersRepository.findAll(customerPhoneNumberById);
        } else {
            List<PhoneNumbersFilterCriteria> filters = getUniqueFilters(filterCriteria.getPhoneNumbersFilterCriteria());
            Specification<Customer> phoneNumbersLikeSpecs = getPhoneNumberLikeClauseSpecWithAndWithoutSpaces(filters);
            return customersRepository.findAll(phoneNumbersLikeSpecs, customerPhoneNumberById);
        }
    }

    public Specification<Customer> getPhoneNumberLikeClauseSpecWithAndWithoutSpaces(List<PhoneNumbersFilterCriteria> filters) {
        List<String> phoneNumbersLikeClauses = new ArrayList<>();
        filters.forEach( filter -> {
            phoneNumbersLikeClauses.add(getPhoneFiltersAsString(filter, true));
            phoneNumbersLikeClauses.add(getPhoneFiltersAsString(filter, false));
        });
        return CustomerSpecifications.likePhones(phoneNumbersLikeClauses);
    }

    public String getPhoneFiltersAsString(PhoneNumbersFilterCriteria filter, boolean hasSpaceBetweenCodeAndNumber) {
        StringBuilder phoneNumberLikeClause = new StringBuilder();
        phoneNumberLikeClause.append("(").append(filter.getCountryCode()).append(")");
        if (hasSpaceBetweenCodeAndNumber)
            phoneNumberLikeClause.append(" ");
        phoneNumberLikeClause.append(filter.getStateCode());
        return phoneNumberLikeClause.toString();
    }

    public List<PhoneNumbersFilterCriteria> getUniqueFilters (List<PhoneNumbersFilterCriteria> allPhoneFilters) {
        Map<String, List<PhoneNumbersFilterCriteria>> countryGroupedFilters = groupFiltersByCountry(allPhoneFilters);
        return combineUniqueFilterGroups(countryGroupedFilters);
    }

    public Map<String, List<PhoneNumbersFilterCriteria>> groupFiltersByCountry(List<PhoneNumbersFilterCriteria> allPhoneFilters) {
        Map<String, List<PhoneNumbersFilterCriteria>> countryFilterGroup = new HashMap<>();
        allPhoneFilters.forEach(
                filter -> {
                    if(isFilterMatchingAllCountryPhones(filter)) {
                        countryFilterGroup.put(filter.getCountryName(), new ArrayList<>(Collections.singletonList(filter)));
                    }
                    else {
                        if(countryFilterGroup.containsKey(filter.getCountryName())) {
                            if(!isCountryFilterGroupHasAMatchingAllStatesFilter(countryFilterGroup.get(filter.getCountryName()))) {
                                countryFilterGroup.get(filter.getCountryName()).add(filter);
                            }
                        } else {
                            countryFilterGroup.put(filter.getCountryName(), new ArrayList<>(Arrays.asList(filter)));
                        }
                    }
                });
        return countryFilterGroup;
    }

    public List<PhoneNumbersFilterCriteria> combineUniqueFilterGroups (Map<String, List<PhoneNumbersFilterCriteria>> countryFilters) {
        List<PhoneNumbersFilterCriteria> combinedFilters = new ArrayList<>();
        for( Map.Entry<String, List<PhoneNumbersFilterCriteria>> filterGroupEntry : countryFilters.entrySet()) {
            combinedFilters = Stream.concat(combinedFilters.stream(), filterGroupEntry.getValue().stream())
                    .collect(Collectors.toList());
        }
        return  combinedFilters;
    }

    private boolean isFilterMatchingAllCountryPhones(PhoneNumbersFilterCriteria filter) {
        return filter.getStateName().equals("");
    }
    private boolean isCountryFilterGroupHasAMatchingAllStatesFilter(List<PhoneNumbersFilterCriteria> filters) {
        return filters.stream().anyMatch(this::isFilterMatchingAllCountryPhones);
    }



}
