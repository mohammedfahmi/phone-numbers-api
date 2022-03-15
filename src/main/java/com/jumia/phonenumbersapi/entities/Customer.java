package com.jumia.phonenumbersapi.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    public CustomerDataCleansing toCustomerCleansingEntity () {
        return CustomerDataCleansing.builder()
                .customerId(this.getId())
                .name(this.getName())
                .phone(this.getPhone())
                .build();
    }
}