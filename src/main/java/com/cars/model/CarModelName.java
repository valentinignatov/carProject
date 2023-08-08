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
public class CarModelName {
    /**
     * String type because if use Long it
     * Cannot autogenerate id of type java.lang.Long
     * for entity of type com.cars.model.CarModelName!
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String modelName;

}
