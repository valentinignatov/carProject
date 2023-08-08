package com.cars.repository;

import com.cars.model.CarModelSubname;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarModelSubnameRepository  extends MongoRepository<CarModelSubname, String> {}
