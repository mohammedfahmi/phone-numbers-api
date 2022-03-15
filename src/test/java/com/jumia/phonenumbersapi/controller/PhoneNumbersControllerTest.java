package com.jumia.phonenumbersapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jumia.phonenumbersapi.configuration.phoneNumber.PhoneNumberConfiguration;
import com.jumia.phonenumbersapi.configuration.propertyFactory.YamlPropertySourceFactory;
import com.jumia.phonenumbersapi.configuration.security.SecurityConfiguration;
import com.jumia.phonenumbersapi.model.PhoneNumbersResponseModel;
import com.jumia.phonenumbersapi.service.CustomersPhonesService;
import com.jumia.phonenumbersapi.utils.JsonTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jumia.phonenumbersapi.utils.TestUtil.prepareQueryParams;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("unchecked")
@Slf4j
@WebMvcTest
class PhoneNumbersControllerTest {
    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomersPhonesService service;

    @TestConfiguration
    @EnableConfigurationProperties
    @PropertySource(value = "classpath:phoneNumberConfiguration.yaml", factory = YamlPropertySourceFactory.class)
    static class AdditionalConfig {
        @Bean
        @ConfigurationProperties(prefix = "phone-number-configuration")
        public PhoneNumberConfiguration phoneNumberConfiguration() {
            return new PhoneNumberConfiguration();
        }
    }

    @Test
    void getCustomerPhonesNumbers_successfully() {
        try {
            List<String> inputPhoneNumbersFilterCriteriaParams = (List<String>) JsonTestUtil.readJsonNodePropertyValue(
                    "PhoneNumbersControllerTestData.json",
                    "getCustomerPhonesNumbers_successfully",
                    "InputPhoneNumbersFilterCriteriaParams",
                    new TypeReference<List<String>>() {
                    });
            PhoneNumbersResponseModel expectedServiceResponse =
                    (PhoneNumbersResponseModel) JsonTestUtil.readJsonNodePropertyValue(
                            "PhoneNumbersControllerTestData.json",
                            "getCustomerPhonesNumbers_successfully",
                            "expectedPhoneNumberResponseModel",
                            new TypeReference<PhoneNumbersResponseModel>() {
                            });
            String expectedResponse = JsonTestUtil.readJsonNodePropertyValueAsString(
                    "PhoneNumbersControllerTestData.json",
                    "getCustomerPhonesNumbers_successfully",
                    "expectedPhoneNumberResponseModel");
            when(service.getPhoneNumbersPage(any())).thenReturn(expectedServiceResponse);
            ResultActions result = mockMvc.perform(
                    prepareQueryParams(
                            get("/api/v1/rest/customerPhonesNumbers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())),
                            inputPhoneNumbersFilterCriteriaParams)
            );
            result.andExpect(status().isOk())
                    .andExpect(content().string(expectedResponse));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }
    @Test
    void getCustomerPhonesNumbers_unsuccessful_page_number_BadRequest() {
        try {
            List<String> inputPhoneNumbersFilterCriteriaParams = new ArrayList<>(Arrays.asList("page", "9", "size", "10"));
            when(service.getPhoneNumbersPage(any())).thenThrow(new ValidationException("Page number 9 is not available"));
            ResultActions result = mockMvc.perform(
                    prepareQueryParams(
                            get("/api/v1/rest/customerPhonesNumbers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())),
                            inputPhoneNumbersFilterCriteriaParams)
            );
            result.andExpect(status().isBadRequest())
                    .andExpect(content().string("{\"error\":\"Validation failed, Page number 9 is not available.\"}"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }

    @Test
    void getCustomerPhonesNumbers_unsuccessful_CustomersPhoneNumbersFilterCriteria_BadRequest() {
        try {
            List<String> inputPhoneNumbersFilterCriteriaParams = (List<String>) JsonTestUtil.readJsonNodePropertyValue(
                    "PhoneNumbersControllerTestData.json",
                    "getCustomerPhonesNumbers_unsuccessful_CustomersPhoneNumbersFilterCriteria_BadRequest",
                    "inputPhoneNumbersFilterCriteriaParams",
                    new TypeReference<List<String>>() {
                    });
            ResultActions result = mockMvc.perform(
                    prepareQueryParams(
                            get("/api/v1/rest/customerPhonesNumbers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())),
                            inputPhoneNumbersFilterCriteriaParams)
            );
            MvcResult mvcResult = result.andExpect(status().isBadRequest()).andReturn();
            assertEquals(
                    "{\"error\":\"SomeofthefilterCriteriaareinvalid:[classPhoneNumbersFilterCriteria{\\ncountryName:notacountry\\ncountryCode:badcode\\nstateName:notastate\\nstateCode:badcode\\n}]\"}",
                    StringUtils.trimAllWhitespace(mvcResult.getResponse().getContentAsString()));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }

    @Test
    void getPhoneNumbersFilters_successfully() {
        try {
            String expectedFilters = JsonTestUtil.readJsonNodePropertyValueAsString(
                    "PhoneNumbersControllerTestData.json",
                    "getPhoneNumbersFilters_successfully",
                    "expectedFilters"
            );
            ResultActions result = mockMvc.perform(
                    get("/api/v1/rest/phoneNumbersFilters")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword()))
            );
            result.andExpect(status().isOk())
                    .andExpect(content().string(expectedFilters));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }

    }
}