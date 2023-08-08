package com.cars.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class CarModelSubname {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String carModelNameId;

    private String subname;
}
