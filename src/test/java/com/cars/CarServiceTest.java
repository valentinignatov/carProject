package com.cars;

import com.cars.enums.CarPriceType;
import com.cars.enums.Offer;
import com.cars.model.Car;
import com.cars.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@SpringBootTest
public class CarServiceTest {

    @Autowired
    CarService carService;

    @Test
    public void getAllCarModelsTest() {
        System.out.println("Hello world");

        carService.getAllCarModels().forEach(System.out::println);
    }

    @Test
    public void transactionTest() {
        try {
            System.out.println("After "+carService.findAll().get().size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findAllTestSorted() {

        List<Car> unsortedCarList = null;
        try {
            unsortedCarList = new ArrayList<>(carService.findAll().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        List<Car> sortedCarList = carService.findAll().stream().sorted(Comparator.comparing(Car::getModel)).collect(Collectors.toList());
//        sortedCarList = carService.findAll().stream().sorted(Comparator.comparing(Car::getRegYear)).collect(Collectors.toList());
//        boolean b = sortedCarList.stream().allMatch(car -> car.getRegYear().compareTo(1999) > 0);
//
//        int i = 0;
//        System.out.println("Unsorted");
//        while (i < unsortedCarList.size() - 1) {
//            System.out.print(unsortedCarList.get(i).getModel());
//            System.out.print(" ");
//            if (i % 10 == 0) {
//                System.out.println();
//            }
//            i++;
//        }
//
//        i = 0;
//        System.out.println("");
//        System.out.println("Sorted");
//        while (i < sortedCarList.size() - 1) {
//            System.out.print(sortedCarList.get(i).getModel());
//            System.out.print(" ");
//            if (i % 10 == 0) {
//                System.out.println();
//            }
//            i++;
//        }
//
//        i = 0;
//        System.out.println("");
//        System.out.println("Sorted descendent");
//        sortedCarList = sortedCarList.stream().sorted(Comparator.comparing(Car::getModel).reversed()).collect(Collectors.toList());
//        while (i < sortedCarList.size() - 1) {
//            System.out.print(sortedCarList.get(i).getModel());
//            System.out.print(" ");
//            if (i % 10 == 0) {
//                System.out.println();
//            }
//            i++;
//        }
//
//        i = 0;
//        System.out.println("");
//        System.out.println("Sorted ascendent by model and then by submodel");
//        sortedCarList = sortedCarList.stream()
//                .sorted(Comparator.comparing(Car::getModel)
//                        .thenComparing(Car::getSubmodel))
//                .collect(Collectors.toList());
//        while (i < sortedCarList.size() - 1) {
//            System.out.print(sortedCarList.get(i).getModel() + " " + sortedCarList.get(i).getSubmodel());
//            System.out.print(" ");
//            if (i % 10 == 0) {
//                System.out.println();
//            }
//            i++;
//        }

//        Map<String, List<Car>> stringListCarModelMap = carService.findAll().stream().collect(Collectors.groupingBy(Car::getModel));
//        Map<String, List<Car>> stringListCarSubModelMap = carService.findAll().stream().collect(Collectors.groupingBy(Car::getSubmodel));
////        System.out.println(stringListCarModelMap);
//
//        unsortedCarList = carService.findAll();
//        unsortedCarList.get(1).getPrice().get(CarPriceType.EURO);
//
//        Set<String> carModelSet = carService.findAll().stream()
//                .filter(car -> car.getOfferType().equals(Offer.BUY))
//                .sorted(Comparator.comparing(Car::getModel))
//                .map(Car::getModel)
//                .collect(Collectors.toCollection(TreeSet::new));
//
////        Boolean collect = carService.findAll().stream()
////                .allMatch(car -> car.getPrice().get(CarPriceType.EURO) > 0); //null pointer
//
//
//        carService.findAll().forEach(car -> System.out.println(car.getPrice()));
    }
}
