package com.jumia.phonenumbersapi.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jumia.phonenumbersapi.entities.Customer;
import com.jumia.phonenumbersapi.utils.JsonTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.jumia.phonenumbersapi.utils.JsonTestUtil.readJsonNodePropertyValueAsString;
import static com.jumia.phonenumbersapi.utils.JsonTestUtil.objectToJson;
@SuppressWarnings("unchecked")
@Slf4j
@SpringBootTest
class CustomersRepositoryTest {
    @Autowired
    private CustomersRepository customersRepository;

    @Test
    public void getCustomersPhoneNumber_Page_0_size_10_successfully() {
        Pageable customerPhoneNumberById = PageRequest.of(0, 10, Sort.by("id"));
        Page<Customer> customers =  customersRepository.findAll(customerPhoneNumberById);
        try {
            assertEquals(
                    readJsonNodePropertyValueAsString("CustomersRepositoryTestData.json", "getCustomersPhoneNumber_Page_0_size_10_successfully", "content"),
                    objectToJson(customers.getContent())
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }

    @Test
    public void getCustomersPhoneNumber_Page_0_size_10_with_specifications_successfully() {
        try {
            List<String> inputLikeClause = (List<String>) JsonTestUtil.readJsonNodePropertyValue(
                    "CustomersRepositoryTestData.json",
                    "getCustomersPhoneNumber_Page_0_size_10_with_specifications_successfully",
                    "specificationLikeClause",
                    new TypeReference<List<String>>(){});
            List<Customer> expectedCustomerList = (List<Customer>) JsonTestUtil.readJsonNodePropertyValue(
                    "CustomersRepositoryTestData.json",
                    "getCustomersPhoneNumber_Page_0_size_10_with_specifications_successfully",
                    "expectedResponse",
                    new TypeReference<List<Customer>>(){});
            Pageable customerPhoneNumberById = PageRequest.of(0, 10, Sort.by("id"));
            Page<Customer> actualCustomerPhonePage =  customersRepository.findAll(CustomerSpecifications.likePhones(inputLikeClause), customerPhoneNumberById);
            assertEquals(objectToJson(expectedCustomerList), objectToJson(actualCustomerPhonePage.getContent()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }

}