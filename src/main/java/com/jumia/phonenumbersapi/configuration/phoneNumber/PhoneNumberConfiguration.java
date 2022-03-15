package com.jumia.phonenumbersapi.configuration.phoneNumber;

import com.jumia.phonenumbersapi.configuration.propertyFactory.YamlPropertySourceFactory;
import com.jumia.phonenumbersapi.model.PhoneNumberFilterModel;
import com.jumia.phonenumbersapi.model.PhoneNumberModel;
import com.jumia.phonenumbersapi.model.PhoneNumbersFilterCriteria;
import com.jumia.phonenumbersapi.model.StateModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@Component
@PropertySource(value = "classpath:phoneNumberConfiguration.yaml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "phone-number-configuration")
public class PhoneNumberConfiguration {
    private List<PhoneNumbersConfiguration> phoneNumbersConfigurations;
    @Data
    public static class PhoneNumbersConfiguration {
        private String phoneRegex;
        private String countryName;
        private String code;
        private List<PhoneNumbersStateConfiguration> phoneNumbersStatesConfigurations;

        @Data
        public static class PhoneNumbersStateConfiguration {
            private String stateRegex;
            private String stateName;
            private String stateCode;

            public boolean isMatching(String phoneNumber) {
                return  isMatchingPattern(stateRegex, phoneNumber);
            }
        }

        public boolean isMatching(String phoneNumber) {
            return  isMatchingPattern(phoneRegex, phoneNumber);
        }
    }

    public Optional<PhoneNumberModel> toPhoneModel(String customerPhoneNumber) {
        Optional<PhoneNumbersConfiguration> phoneNumbersConfig =
                phoneNumbersConfigurations.stream().filter(
                        config ->  config.isMatching(customerPhoneNumber)
                ).findFirst();
        PhoneNumberModel.PhoneNumberModelBuilder phoneNumberModelBuilder = PhoneNumberModel.builder();
        if(phoneNumbersConfig.isPresent()) {
            PhoneNumbersConfiguration phoneNumbersConfiguration = phoneNumbersConfig.get();
            phoneNumberModelBuilder
                    .country(phoneNumbersConfiguration.getCountryName())
                    .countryCode(phoneNumbersConfiguration.getCode())
                    .phone(customerPhoneNumber.split("\\)")[1].trim());
            Optional<PhoneNumbersConfiguration.PhoneNumbersStateConfiguration> stateConfiguration =
                    phoneNumbersConfiguration.getPhoneNumbersStatesConfigurations().stream().filter(
                            stateConfig -> stateConfig.isMatching(customerPhoneNumber)
                    ).findFirst();
            if(stateConfiguration.isPresent()) {
                phoneNumberModelBuilder.state(stateConfiguration.get().getStateName());
            } else {
                phoneNumberModelBuilder.state("");
            }
            return Optional.ofNullable(phoneNumberModelBuilder.build());
        } else {
            return Optional.empty();
        }
    }
    public List<PhoneNumberFilterModel> toConfigFiltersModel() {
        return phoneNumbersConfigurations.stream().map(
                phoneConfig ->  PhoneNumberFilterModel.builder()
                        .countryName(phoneConfig.getCountryName())
                        .code(phoneConfig.getCode())
                        .states(phoneConfig.getPhoneNumbersStatesConfigurations().stream().map(
                                stateConfig -> StateModel.builder()
                                        .stateName(stateConfig.getStateName())
                                        .stateCode(stateConfig.getStateCode())
                                        .build()
                        ).collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList());
    }
    public boolean isFilterCriteriaValid (PhoneNumbersFilterCriteria criteria) {
        return phoneNumbersConfigurations.stream().anyMatch(
                config ->
                        config.getCountryName().equals(criteria.getCountryName()) &&
                        config.getCode().equals(criteria.getCountryCode()) &&
                                isStateFilterCriteriaValid(criteria, config.getPhoneNumbersStatesConfigurations()));
    }
    private boolean isStateFilterCriteriaValid(PhoneNumbersFilterCriteria criteria,
                                               List<PhoneNumbersConfiguration.PhoneNumbersStateConfiguration>  statesConfig) {
        if(statesConfig.isEmpty())
            return (criteria.getStateName().equals("") && criteria.getStateCode().equals(""));
        else if(criteria.getStateName().equals("") && criteria.getStateCode().equals(""))
            return true;
        else
            return statesConfig.stream().anyMatch(
                        configState ->
                                configState.getStateName().equals(criteria.getStateName()) &&
                                configState.getStateCode().equals(criteria.getStateCode()));
    }
    public static Boolean isMatchingPattern (String regx, String value) {
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
