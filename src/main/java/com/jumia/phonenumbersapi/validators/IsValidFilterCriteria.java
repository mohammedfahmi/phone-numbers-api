package com.jumia.phonenumbersapi.validators;

import com.google.common.collect.ImmutableSet;
import com.jumia.phonenumbersapi.configuration.phoneNumber.PhoneNumberConfiguration;
import com.jumia.phonenumbersapi.model.CustomersPhoneNumbersFilterCriteria;
import com.jumia.phonenumbersapi.model.PhoneNumbersFilterCriteria;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import javax.validation.*;
import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.PARAMETER})
@Constraint(validatedBy = IsValidFilterCriteriaImpl.class)
public @interface IsValidFilterCriteria {
    String message() default "Some of the filter Criteria are invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
@AllArgsConstructor
class IsValidFilterCriteriaImpl
        implements ConstraintValidator<IsValidFilterCriteria, CustomersPhoneNumbersFilterCriteria> {

    private PhoneNumberConfiguration phoneNumberConfiguration;

    @Override
    public void initialize(final IsValidFilterCriteria constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(
            CustomersPhoneNumbersFilterCriteria filterCriteria, ConstraintValidatorContext constraintValidatorContext) {
        Optional<List<PhoneNumbersFilterCriteria>> optionalCriteria =  Optional.ofNullable(filterCriteria.getPhoneNumbersFilterCriteria());
        if(optionalCriteria.isPresent()){
            List<PhoneNumbersFilterCriteria> phoneNumbersFilterCriteria = optionalCriteria.get();
            if(!phoneNumbersFilterCriteria.isEmpty()) {
                List<PhoneNumbersFilterCriteria> invalidCriteria = phoneNumbersFilterCriteria.stream().filter(
                        filter -> !phoneNumberConfiguration.isFilterCriteriaValid(filter)).collect(Collectors.toList());
                if(!invalidCriteria.isEmpty()){
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext
                            .buildConstraintViolationWithTemplate(getInvalidCriteriaErrorMessage(invalidCriteria))
                            .addConstraintViolation();
                    return false;
                }
            }
        } else {
            filterCriteria.setPhoneNumbersFilterCriteria(new ArrayList<>());
        }
        return true;
    }

    private String getInvalidCriteriaErrorMessage(List<PhoneNumbersFilterCriteria> invalidCriteria) {
        StringBuilder logMessage = new StringBuilder("#validator ");
        logMessage.append("Some of the filter Criteria are invalid: [");
        logMessage.append(invalidCriteria.stream().map(PhoneNumbersFilterCriteria::toString).collect(Collectors.joining(",")));
        logMessage.append("]#validator");
        return logMessage.toString();
    }
}
