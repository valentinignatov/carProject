package com.cars.controller;

import com.cars.model.*;
import com.cars.service.CarService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CrossOrigin
@Controller
@RequestMapping(path = "api/car")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Hello World Test api
     * http://localhost:8080/api/car/simple
     *
     * @return
     */
    @GetMapping(path = "/simple")
    public ResponseEntity<String> simple(@RequestBody Car car) {
        String s = "Hello World";
        carService.countCarsWithImage();
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @GetMapping(path = "/async")
    public ResponseEntity async() {
        carService.asyncMethod();
        return new ResponseEntity<>(HttpStatus.OK);
    }

     /**
     * http://localhost:8080/api/car/findAll
     * @return all car objects
     */
    @GetMapping(path = "/findAll")
    public ResponseEntity<List<Car>> findAll() throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(carService.findAll().get(), HttpStatus.OK);
    }

    /**
     * http://localhost:8080/api/car/findAllPageable?page=0&size=10&sort=ascendent
     * @return all car objects pageable
     */
    @GetMapping(path = "/findAllPageable")
    public ResponseEntity<Page<Car>> findAll(Pageable pageable) {
        return new ResponseEntity<>(carService.findAll(pageable), HttpStatus.OK);
    }

    /**
     * @param id
     * @return a car object by id
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> findById(@PathVariable( "id" ) String id) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "*");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(carService.findById(id));
    }

    /**
     * Save a new car object
     * http://localhost:8080/api/car/save
     * @param car
     * @return
     */
    @PostMapping(value = "/save")
    public ResponseEntity<Object> save(@RequestBody Car car) {
        return new ResponseEntity<>(carService.save(car), HttpStatus.OK);
    }

    /**
     * Search a car by filters
     * http://localhost:8080/api/car/search?page=0&size=10
     *
     * {
     *     "offerType":"SELL",
     *     "carPriceType":"EURO",
     *     "priceFrom":85000,
     *     "priceTo":90000,
     *     "negotiable":false,
     *     "authorType":"INDIVIDUAL",
     *     "model":["Skoda","Ford","Toyota"],
     *     "submodel":[],
     *     "condition":"NEW",
     *     "registration": "MOLDOVA"
     * }
     *
     * @param carSearchBean
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestBody CarSearchBean carSearchBean, Pageable pageable) {
        return new ResponseEntity<>(carService.search(carSearchBean, pageable), HttpStatus.OK);
    }

    /**
     * http://localhost:8080/api/car/addCarImage?carId=3000
     * @param carId
     * @param image Body -> form-data -> Key: Select File, name it "image". Value: select path to file
     * @return
     */
    @PostMapping(value = "/addCarImage")
    public ResponseEntity<Boolean> addCarImage(@RequestParam(name = "carId", required = false) String carId,
                                              MultipartFile image) {
        return new ResponseEntity<>(carService.addCarImage(carId, image), HttpStatus.OK);
    }

    @GetMapping(path = "/addRandomCars")
    public ResponseEntity<Boolean> addRandomCars() {
        return new ResponseEntity<>(carService.addRandomCars(), HttpStatus.OK);
    }

    /**
     * http://localhost:8080/api/car/getCarImage/2
     * @param carId Id of the car which image you need
     * @param response
     * @throws IOException
     */
    @GetMapping(path = "/getCarImage/{id}")
    public void getCarImage(@PathVariable(name = "id", required = false) String carId,
                            HttpServletResponse response) throws IOException {
        IOUtils.copy(carService.getCarImageStream(carId), response.getOutputStream());
    }

    /**
     * http://localhost:8080/api/car/getAllCarModels
     * @return
     */
    @GetMapping(path = "/getAllCarModels")
    public ResponseEntity<List<CarModelBean>> getAllCarModels() {
        return new ResponseEntity<>(carService.getAllCarModels(), HttpStatus.OK);
    }

    /**
     * Export cars to excel
     * now exports only 200 to improve performance
     * http://localhost:8080/api/car/export/excel
     * @param response
     * @throws IOException
     */
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=cars_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        carService.exportExcel(response);
    }

    @DeleteMapping("/deleteAllCars")
    public ResponseEntity<Boolean> deleteAllCars() {
        return new ResponseEntity<>(carService.deleteAllCars(), HttpStatus.OK);
    }
}
