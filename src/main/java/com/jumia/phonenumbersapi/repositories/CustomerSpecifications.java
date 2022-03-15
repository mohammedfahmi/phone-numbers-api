package com.jumia.phonenumbersapi.repositories;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.jumia.phonenumbersapi.entities.Customer;
import com.jumia.phonenumbersapi.entities.Customer_;
import org.springframework.util.Assert;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerSpecifications {
    public static Specification<Customer> likePhones(List<String> phones) {
        Assert.notEmpty(phones, "phone numbers criteria cannot be empty");
        return (root, query, cb) -> cb.or(
                phones.stream().map( phone -> cb.like(root.get(Customer_.PHONE), phone + "%"))
                        .collect(Collectors.toList()).toArray(new Predicate[phones.size()])
        );
    }
}
