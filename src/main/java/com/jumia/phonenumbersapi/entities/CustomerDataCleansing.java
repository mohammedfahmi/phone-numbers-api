package com.jumia.phonenumbersapi.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_data_cleansing")
@Entity
public class CustomerDataCleansing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cleansing_id" ,nullable = false)
    private Integer cleansingId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;
}
