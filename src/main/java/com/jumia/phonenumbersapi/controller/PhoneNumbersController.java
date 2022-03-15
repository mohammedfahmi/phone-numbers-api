package com.jumia.phonenumbersapi.controller;

import com.jumia.phonenumbersapi.api.CustomerPhonesNumbersApi;
import com.jumia.phonenumbersapi.configuration.phoneNumber.PhoneNumberConfiguration;
import com.jumia.phonenumbersapi.model.CustomersPhoneNumbersFilterCriteria;
import com.jumia.phonenumbersapi.model.PhoneNumbersFiltersModel;
import com.jumia.phonenumbersapi.model.PhoneNumbersResponseModel;
import com.jumia.phonenumbersapi.service.CustomersPhonesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@AllArgsConstructor
@RestController
public class PhoneNumbersController implements CustomerPhonesNumbersApi {
    private CustomersPhonesService customersPhonesService;
    private PhoneNumberConfiguration phoneNumberConfiguration;


    @Override
    public ResponseEntity<PhoneNumbersResponseModel> getCustomerPhonesNumbers(
            @Valid CustomersPhoneNumbersFilterCriteria customersPhoneNumbersFilterCriteria) {

        return new ResponseEntity<>(
                customersPhonesService.getPhoneNumbersPage(customersPhoneNumbersFilterCriteria), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PhoneNumbersFiltersModel> getPhoneNumbersFilters() {
        return new ResponseEntity<>(
                new PhoneNumbersFiltersModel(phoneNumberConfiguration.toConfigFiltersModel()), HttpStatus.OK);
    }
}
