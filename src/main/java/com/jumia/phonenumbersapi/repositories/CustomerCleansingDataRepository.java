package com.jumia.phonenumbersapi.repositories;

import com.jumia.phonenumbersapi.entities.CustomerDataCleansing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCleansingDataRepository extends JpaRepository<CustomerDataCleansing, Integer> {
}
