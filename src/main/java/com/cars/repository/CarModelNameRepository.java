package com.cars.repository;

import com.cars.model.CarModelName;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarModelNameRepository extends MongoRepository<CarModelName, String> {}