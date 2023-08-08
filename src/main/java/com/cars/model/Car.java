package com.cars.model;

import com.cars.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.Map;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private Offer offerType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "price_type")
    @Column(name = "price")
    @MapKey(name = "type")
    private Map<CarPriceType, Integer> price;

    private Boolean negotiable;

    private Author authorType;

    private String model;

    private String submodel;

    private Condition condition;

    private Registration registration;

    private Integer regYear;

    @Lob
    @JsonIgnore
    private Byte[] image;
}
