package com.jumia.phonenumbersapi.IT;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jumia.phonenumbersapi.model.PhoneNumbersFiltersModel;
import com.jumia.phonenumbersapi.model.PhoneNumbersResponseModel;
import com.jumia.phonenumbersapi.utils.JsonTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.jumia.phonenumbersapi.IT.TestRestTemplate.prepareParams;
import static com.jumia.phonenumbersapi.IT.TestRestTemplate.restCall;
import static com.jumia.phonenumbersapi.utils.JsonTestUtil.readJsonNodePropertyValueAsString;
import static com.jumia.phonenumbersapi.utils.JsonTestUtil.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("unchecked")
@ExtendWith({SpringExtension.class, ITBaseContextExtension.class})
@Slf4j
public class PhoneNumbersControllerIT {
    @Test
    void getPhoneNumbersFilters_successfully() {
        try {
            ResponseEntity<?> responseEntity = restCall("/phoneNumbersFilters", HttpMethod.GET,
                    new HashMap<>(1), PhoneNumbersFiltersModel.class, new HashMap<>(1), true);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals(
                    readJsonNodePropertyValueAsString(
                            "PhoneNumbersControllerITData.json",
                            "getPhoneNumbersFilters_successfully",
                            "expectedResult")
                    , objectToJson(responseEntity.getBody()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }
    @Test
    void getCustomerPhonesNumbers_page_0_size_10_successfully() {
        try {
            List<String> queryParams = (List<String>) JsonTestUtil.readJsonNodePropertyValue(
                    "PhoneNumbersControllerITData.json",
                    "getCustomerPhonesNumbers_page_0_size_10_successfully",
                    "inputQueryParameters",
                    new TypeReference<List<String>>() {
                    });
            ResponseEntity<?> responseEntity = restCall("/customerPhonesNumbers", HttpMethod.GET,
                    prepareParams(queryParams.toArray(new String[0]))
                    , PhoneNumbersResponseModel.class, new HashMap<>(1), true);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals(
                    readJsonNodePropertyValueAsString(
                            "PhoneNumbersControllerITData.json",
                            "getCustomerPhonesNumbers_page_0_size_10_successfully",
                            "expectedResult")
                    , objectToJson(responseEntity.getBody()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }
    @Test
    void getCustomerPhonesNumbers_page_0_size_10_with_a_country_filter_successfully() {
        try {
            List<String> queryParams = (List<String>) JsonTestUtil.readJsonNodePropertyValue(
                    "PhoneNumbersControllerITData.json",
                    "getCustomerPhonesNumbers_page_0_size_10_with_a_country_filter_successfully",
                    "inputQueryParameters",
                    new TypeReference<List<String>>() {
                    });
            ResponseEntity<?> responseEntity = TestRestTemplate.restCall("/customerPhonesNumbers", HttpMethod.GET,
                    prepareParams(queryParams.toArray(new String[0]))
                    , PhoneNumbersResponseModel.class, new HashMap<>(1), true);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals(
                    readJsonNodePropertyValueAsString(
                            "PhoneNumbersControllerITData.json",
                            "getCustomerPhonesNumbers_page_0_size_10_with_a_country_filter_successfully",
                            "expectedResult")
                    , objectToJson(responseEntity.getBody()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }
    @Test
    void getCustomerPhonesNumbers_page_0_size_10_with_country_and_state_filters_successfully() {
        try {
            List<String> queryParams = (List<String>) JsonTestUtil.readJsonNodePropertyValue(
                    "PhoneNumbersControllerITData.json",
                    "getCustomerPhonesNumbers_page_0_size_10_with_country_and_state_filters_successfully",
                    "inputQueryParameters",
                    new TypeReference<List<String>>() {
                    });
            ResponseEntity<?> responseEntity = TestRestTemplate.restCall("/customerPhonesNumbers", HttpMethod.GET,
                    prepareParams(queryParams.toArray(new String[0]))
                    , PhoneNumbersResponseModel.class, new HashMap<>(1), true);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals(
                    readJsonNodePropertyValueAsString(
                            "PhoneNumbersControllerITData.json",
                            "getCustomerPhonesNumbers_page_0_size_10_with_country_and_state_filters_successfully",
                            "expectedResult")
                    , objectToJson(responseEntity.getBody()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }

}
