package com.jumia.phonenumbersapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jumia.phonenumbersapi.entities.Customer;
import com.jumia.phonenumbersapi.model.CustomersPhoneNumbersFilterCriteria;
import com.jumia.phonenumbersapi.model.PhoneNumbersFilterCriteria;
import com.jumia.phonenumbersapi.model.PhoneNumbersResponseModel;
import com.jumia.phonenumbersapi.repositories.CustomersRepository;
import com.jumia.phonenumbersapi.utils.JsonTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jumia.phonenumbersapi.utils.JsonTestUtil.objectToJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
@SpringBootTest
class CustomersPhonesServiceTest {
    @MockBean
    private CustomersRepository customersRepository;
    @Autowired
    private CustomersPhonesService customersPhonesService;

    @Test
    public void getCustomersPhoneNumber_page_0_size_10_successfully() {
        try {
            PageImpl customerPhones = new PageImpl( (List<Customer>) JsonTestUtil.readJsonNodePropertyValue(
                    "CustomerPhonesServiceTestData.json",
                    "getCustomersPhoneNumber_Page_0_size_10_successfully", "content",
                    new TypeReference<List<Customer>>(){}),
                    PageRequest.of(0, 10, Sort.by("id")) , 41);
        log.warn(objectToJson(customerPhones));
        when(customersRepository.findAll(PageRequest.of(0, 10, Sort.by("id")))).thenReturn(customerPhones);
        Page<Customer> expectedCustomerPhonesPage = customersPhonesService.getCustomersPage(new CustomersPhoneNumbersFilterCriteria(0, 10, new ArrayList<>()));
            assertEquals(objectToJson(expectedCustomerPhonesPage),objectToJson(customerPhones));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }
    @Test
    public void getCustomersPhoneNumber_page_9_size_10_unsuccessfully() {
        try {
            PageImpl customerPhones = new PageImpl( (List<Customer>) JsonTestUtil.readJsonNodePropertyValue(
                    "CustomerPhonesServiceTestData.json",
                    "getCustomersPhoneNumber_page_9_size_10_unsuccessfully", "content",
                    new TypeReference<List<Customer>>(){}),
                    PageRequest.of(9, 10, Sort.by("id")) , 41);
        when(customersRepository.findAll(PageRequest.of(9, 10, Sort.by("id")))).thenReturn(customerPhones);
        customersPhonesService.getPhoneNumbersPage(new CustomersPhoneNumbersFilterCriteria(9, 10, new ArrayList<>()));
        } catch (IOException| ValidationException e) {
            log.error(e.getMessage(), e);
            assertEquals("Page number 9 is not available", e.getMessage());
        }
    }

    @Test
    public void getCustomersPhoneNumber_filters_specifications_successfully() {
        try {
            PhoneNumbersResponseModel expectedResponse = (PhoneNumbersResponseModel) JsonTestUtil.readJsonNodePropertyValue(
                    "CustomerPhonesServiceTestData.json",
                    "getCustomersPhoneNumber_filters_specifications_successfully",
                    "expectedPhoneNumberResponseModel",
                    new TypeReference<PhoneNumbersResponseModel>(){});
            PageImpl expectedCustomerPhonePage =   new PageImpl( (List<Customer>) JsonTestUtil.readJsonNodePropertyValue(
                    "CustomerPhonesServiceTestData.json",
                    "getCustomersPhoneNumber_Page_0_size_10_successfully", "content",
                    new TypeReference<List<Customer>>(){}),
                    PageRequest.of(0, 10, Sort.by("id")) , 41);
            List<PhoneNumbersFilterCriteria> inputFilters = (List<PhoneNumbersFilterCriteria>) JsonTestUtil.readJsonNodePropertyValue(
                    "CustomerPhonesServiceTestData.json",
                    "testWasRedundantFiltersRemoved_whenOneCountryAtLeastHasNoMatchingAllStatesFilter",
                    "inputPhoneNumbersFilterCriteria",
                    new TypeReference<List<PhoneNumbersFilterCriteria>>(){});
            when(customersRepository
                    .findAll((Specification<Customer>) any(), (Pageable) any())).thenReturn(expectedCustomerPhonePage);
            PhoneNumbersResponseModel actualResponse = customersPhonesService.getPhoneNumbersPage(
                    CustomersPhoneNumbersFilterCriteria.builder().page(0).size(10)
                            .phoneNumbersFilterCriteria(inputFilters).build());
            assertEquals(objectToJson(expectedResponse),objectToJson(actualResponse));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }

    @Test
    public void testWasRedundantFiltersRemoved_whenAllCountriesHasAtLeastSingleMatchingAllStatesFilter() {
        try {
            List<PhoneNumbersFilterCriteria> inputFilters = (List<PhoneNumbersFilterCriteria>) JsonTestUtil.readJsonNodePropertyValue(
                        "CustomerPhonesServiceTestData.json",
                        "testWasRedundantFiltersRemoved_whenAllCountriesHasAtLeastSingleMatchingAllStatesFilter",
                        "inputPhoneNumbersFilterCriteria",
                        new TypeReference<List<PhoneNumbersFilterCriteria>>(){});
            List<PhoneNumbersFilterCriteria> expectedOutputFilters = (List<PhoneNumbersFilterCriteria>) JsonTestUtil.readJsonNodePropertyValue(
                        "CustomerPhonesServiceTestData.json",
                        "testWasRedundantFiltersRemoved_whenAllCountriesHasAtLeastSingleMatchingAllStatesFilter",
                        "expectedOutputPhoneNumbersFilterCriteria",
                        new TypeReference<List<PhoneNumbersFilterCriteria>>(){});

            List<PhoneNumbersFilterCriteria> actualOutputFilters = customersPhonesService.getUniqueFilters(inputFilters);
            assertEquals(objectToJson(expectedOutputFilters),objectToJson(actualOutputFilters));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }
    @Test
    public void testWasRedundantFiltersRemoved_whenOneCountryAtLeastHasNoMatchingAllStatesFilter() {
        try {
            List<PhoneNumbersFilterCriteria> inputFilters = (List<PhoneNumbersFilterCriteria>) JsonTestUtil.readJsonNodePropertyValue(
                        "CustomerPhonesServiceTestData.json",
                        "testWasRedundantFiltersRemoved_whenOneCountryAtLeastHasNoMatchingAllStatesFilter",
                        "inputPhoneNumbersFilterCriteria",
                        new TypeReference<List<PhoneNumbersFilterCriteria>>(){});
            List<PhoneNumbersFilterCriteria> expectedOutputFilters = (List<PhoneNumbersFilterCriteria>) JsonTestUtil.readJsonNodePropertyValue(
                        "CustomerPhonesServiceTestData.json",
                        "testWasRedundantFiltersRemoved_whenOneCountryAtLeastHasNoMatchingAllStatesFilter",
                        "expectedOutputPhoneNumbersFilterCriteria",
                        new TypeReference<List<PhoneNumbersFilterCriteria>>(){});

            List<PhoneNumbersFilterCriteria> actualOutputFilters = customersPhonesService.getUniqueFilters(inputFilters);
            assertEquals(objectToJson(expectedOutputFilters),objectToJson(actualOutputFilters));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }
}