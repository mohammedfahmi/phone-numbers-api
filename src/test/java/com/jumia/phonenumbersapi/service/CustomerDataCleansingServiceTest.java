package com.jumia.phonenumbersapi.service;

import com.jumia.phonenumbersapi.configuration.phoneNumber.PhoneNumberConfiguration;
import com.jumia.phonenumbersapi.entities.Customer;
import com.jumia.phonenumbersapi.entities.CustomerDataCleansing;
import com.jumia.phonenumbersapi.repositories.CustomerCleansingDataRepository;
import com.jumia.phonenumbersapi.repositories.CustomersRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomerDataCleansingServiceTest {
    @Mock
    private static CustomersRepository customersRepository;
    @Mock
    private static CustomerCleansingDataRepository customerCleansingDataRepository;
    @Mock
    private PhoneNumberConfiguration phoneNumberConfiguration;
    @InjectMocks
    private CustomerDataCleansingService customerDataCleansingService;

    @Test
    void initDataCleansing() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        Customer customer = Customer.builder()
                .id(33).name("ARREYMANYOR ROLAND TABOT").phone("(237) 6A0311634").build();
        List<Customer> customers = new ArrayList<>(Arrays.asList(customer));
        CustomerDataCleansing customerDataCleansing = customer.toCustomerCleansingEntity();
        when(phoneNumberConfiguration.toPhoneModel(customer.getPhone())).thenReturn(Optional.empty());
        when(customersRepository.findAll()).thenReturn(customers);
        when(customerCleansingDataRepository.save(any())).thenReturn(customerDataCleansing);
        doNothing().when(customersRepository).delete(any());
        doNothing().when(customersRepository).flush();
        doNothing().when(customerCleansingDataRepository).flush();

        //use reflection to call post-constructor -> make sure that CustomerDataCleansingService method name match the parameter
        Method postConstruct =  CustomerDataCleansingService.class.getDeclaredMethod("initDataCleansing", null);
        postConstruct.setAccessible(true);
        postConstruct.invoke(customerDataCleansingService);
        verify(customersRepository,times(1)).findAll();
        verify(customersRepository,times(1)).delete(any());
        verify(customerCleansingDataRepository,times(1)).save(any());
    }
}