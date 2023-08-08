package com.cars.controller.parametrization;

import com.cars.model.CarModelName;
import com.cars.model.CarModelSubname;
import com.cars.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@Controller
@RequestMapping(path = "api/car/parametrization")
public class CarParametrizationController {

    private final CarService carService;

    public CarParametrizationController(CarService carService) {
        this.carService = carService;
    }

    /**
     * add new car models
     * http://localhost:8080/api/car/parametrization/addNewModel
     * @param models
     * @return
     */
    @PostMapping("/addNewModel")
    public ResponseEntity<Boolean> addNewModel(@RequestBody Optional<List<CarModelName>> models) {
        return new ResponseEntity<>(carService.addNewModel(models), HttpStatus.OK);
    }

    /**
     * add new car submodels
     * http://localhost:8080/api/car/parametrization/addNewSubModel
     * @param models
     * @return
     */
    @PostMapping("/addNewSubModel")
    public ResponseEntity<Boolean>  addNewSubModel(@RequestParam(name = "modelId") String modelId,
                                                   @RequestBody Optional<List<CarModelSubname>> models) {
        return new ResponseEntity<>(carService.addNewSubModel(models, modelId), HttpStatus.OK);
    }
}
