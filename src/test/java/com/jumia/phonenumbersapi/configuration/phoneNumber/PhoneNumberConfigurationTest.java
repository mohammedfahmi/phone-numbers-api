package com.jumia.phonenumbersapi.configuration.phoneNumber;

import com.jumia.phonenumbersapi.model.PhoneNumbersFilterCriteria;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static com.jumia.phonenumbersapi.utils.JsonTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class PhoneNumberConfigurationTest {
    @Autowired
    private PhoneNumberConfiguration phoneNumberConfiguration;

    @Test
    public void load_PhoneNumberConfiguration_from_properties_file_successfully() {
        try {
            String actual = objectToJson(phoneNumberConfiguration.getPhoneNumbersConfigurations());
            String expected = readJsonNodeValueAsString(
                    "PhoneNumbersConfigurationTestData.json",
                    "load_PhoneNumberConfiguration_from_properties_file_successfully");

            assertEquals(expected, actual);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            fail();
        }
    }

    @Test
    public void load_filterModels_successfully() {
        try {
            String actual = objectToJson(phoneNumberConfiguration.toConfigFiltersModel());
            String expected = readJsonNodeValueAsString(
                    "PhoneNumbersConfigurationTestData.json",
                    "load_filterModels_successfully");

            assertEquals(expected, actual);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            fail();
        }
    }

    @Test
    public void load_phoneNumberModel_from_phoneNumber_successfully() {
        try {
            String valid_Ethiopia_Addis_Ababa_phone_number= "(251) 911203317";
            String actual = objectToJson(phoneNumberConfiguration.toPhoneModel(valid_Ethiopia_Addis_Ababa_phone_number).get());
            String expected = readJsonNodePropertyValueAsString(
                    "PhoneNumbersConfigurationTestData.json",
                    "load_phoneNumberModel_from_phoneNumber_successfully",
                    "valid_Ethiopia_Addis_Ababa_phone_number_model");

            assertEquals(expected, actual);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            fail();
        }
    }

    @Test
    public void load_phoneNumberModel_from_phoneNumber_with_no_state_successfully() {
        try {
            String valid_Ethiopia_phone_number= "(251) 988203317";
            String actual = objectToJson(phoneNumberConfiguration.toPhoneModel(valid_Ethiopia_phone_number).get());
            String expected = readJsonNodePropertyValueAsString(
                    "PhoneNumbersConfigurationTestData.json",
                    "load_phoneNumberModel_from_phoneNumber_with_no_state_successfully",
                    "valid_Ethiopia_phone_number_model");

            assertEquals(expected, actual);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            fail();
        }
    }

    @Test
    public void fail_to_map_phoneNumberModel_from_phoneNumber() {
        String invalid_Ethiopia_phone_number= "(251) 78820331788";
        assertTrue(!phoneNumberConfiguration.toPhoneModel(invalid_Ethiopia_phone_number).isPresent());
    }
    @Test
    public void  isFilterCriteriaValid_should_pass() {
        PhoneNumbersFilterCriteria input =
                PhoneNumbersFilterCriteria.builder()
                        .countryName("Morocco")
                        .countryCode("212")
                        .stateName("")
                        .stateCode("").build();
        assertTrue(phoneNumberConfiguration.isFilterCriteriaValid(input));
    }
    @Test
    public void  isFilterCriteriaValid_should_faile() {
        PhoneNumbersFilterCriteria input =
                PhoneNumbersFilterCriteria.builder()
                        .countryName("Morocco")
                        .countryCode("212")
                        .stateName("wrong input")
                        .stateCode("").build();
        assertTrue(!phoneNumberConfiguration.isFilterCriteriaValid(input));
    }

    @Test
    public void  isFilterCriteriaWithStateValid_should_pass() {
        PhoneNumbersFilterCriteria input =
                PhoneNumbersFilterCriteria.builder()
                        .countryName("Ethiopia")
                        .countryCode("251")
                        .stateName("Addis Ababa")
                        .stateCode("911").build();
        assertTrue(phoneNumberConfiguration.isFilterCriteriaValid(input));
    }
    @Test
    public void  isFilterCriteriaWithStateValid_should_faile() {
        PhoneNumbersFilterCriteria input =
                PhoneNumbersFilterCriteria.builder()
                        .countryName("Ethiopia")
                        .countryCode("251")
                        .stateName("wrong input")
                        .stateCode("wrongCode").build();
        assertTrue(!phoneNumberConfiguration.isFilterCriteriaValid(input));
    }
}