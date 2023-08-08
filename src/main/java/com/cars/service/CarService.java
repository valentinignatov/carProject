package com.cars.service;

import com.cars.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CarService {

    void exportExcel(HttpServletResponse response);

    Boolean addRandomCars();

    CompletableFuture<List<Car>> findAll();

    Page<Car> findAll(Pageable pageable);

    void asyncMethod();

    Car findById(String id);

    Car save(Car car);

    InputStream getCarImageStream(String carId);

    Boolean addCarImage(String carId, MultipartFile image);

    List<CarModelBean> getAllCarModels();

    Boolean addNewModel(Optional<List<CarModelName>> models);

    Boolean addNewSubModel(Optional<List<CarModelSubname>> models, String modelId);

    Boolean deleteAllCars();

    Object search(CarSearchBean carSearchBean, Pageable pageable);

    void countCarsWithImage();
}
