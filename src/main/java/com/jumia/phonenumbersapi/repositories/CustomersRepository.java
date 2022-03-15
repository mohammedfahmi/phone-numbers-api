package com.jumia.phonenumbersapi.repositories;

import com.jumia.phonenumbersapi.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomersRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}