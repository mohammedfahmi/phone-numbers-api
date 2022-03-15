package com.jumia.phonenumbersapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class PhoneNumbersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoneNumbersApiApplication.class, args);

    }

}
