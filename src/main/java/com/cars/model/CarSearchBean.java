package com.cars.model;

import com.cars.enums.Author;
import com.cars.enums.CarPriceType;
import com.cars.enums.Offer;
import com.cars.enums.Registration;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CarSearchBean {

    private Offer offerType;

    private CarPriceType carPriceType;

    private Integer priceFrom;

    private Integer priceTo;

    private Boolean negotiable;

    private Author authorType;

    private ArrayList<String> model;

    private ArrayList<String> submodel;

    private Condition condition;

    @Nullable
    private Registration registration;

    private Integer regYearFrom;

    private Integer regYearTo;
}
