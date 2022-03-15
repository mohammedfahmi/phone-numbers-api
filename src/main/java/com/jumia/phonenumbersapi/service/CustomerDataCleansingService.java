package com.jumia.phonenumbersapi.service;

import com.jumia.phonenumbersapi.configuration.phoneNumber.PhoneNumberConfiguration;
import com.jumia.phonenumbersapi.entities.Customer;
import com.jumia.phonenumbersapi.entities.CustomerDataCleansing;
import com.jumia.phonenumbersapi.model.PhoneNumberModel;
import com.jumia.phonenumbersapi.repositories.CustomerCleansingDataRepository;
import com.jumia.phonenumbersapi.repositories.CustomersRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

//TODO JMA-002 create a command microservice and remove this service and Liquibase docs to it,
// this microservice should be a Query microservice only
/**
 * this service runs on start up to clean the Customer table
 * from all the data with wrong phone number, the invalid data will be backed up in customer_data_cleansing table
 * this behaviour is to allow pagination on the customer table,
 * this service behavior is also not sustainable long term,
 * as it will take too long when the Customer table grows and need to be separated in a Cron job on a persistence DB
 * in another microservice.
 */
@Slf4j
@AllArgsConstructor
@Service
public class CustomerDataCleansingService {

    private CustomersRepository customersRepository;
    private CustomerCleansingDataRepository customerCleansingDataRepository;
    private PhoneNumberConfiguration phoneNumberConfiguration;

    @Transactional
    @PostConstruct
    public void initDataCleansing() {
        List<Customer> allCustomers = customersRepository.findAll();
        allCustomers.forEach(
                customer -> {
                    Optional<PhoneNumberModel> optional = phoneNumberConfiguration.toPhoneModel(customer.getPhone());
                    if(!optional.isPresent()) {
                        CustomerDataCleansing customerCleansingData = customer.toCustomerCleansingEntity();
                        log.info("");
                        customerCleansingDataRepository.save(customerCleansingData);
                        customersRepository.delete(customer);
                        customerCleansingDataRepository.flush();
                        customersRepository.flush();
                    }
                }
        );
    }
}
