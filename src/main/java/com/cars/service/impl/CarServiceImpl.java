package com.cars.service.impl;

import com.cars.enums.Author;
import com.cars.enums.CarPriceType;
import com.cars.enums.Offer;
import com.cars.enums.Registration;
import com.cars.model.*;
import com.cars.repository.CarModelNameRepository;
import com.cars.repository.CarModelSubnameRepository;
import com.cars.repository.CarRepository;
import com.cars.service.CarService;
import com.cars.service.ExcelExporter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ExcelExporter excelExporter;
    private final CarModelNameRepository carModelNameRepository;
    private final CarModelSubnameRepository carModelSubnameRepository;
    private final MongoTemplate mongoTemplate;

    public CarServiceImpl(CarRepository carRepository,
                          ExcelExporter excelExporter,
                          CarModelNameRepository carModelNameRepository,
                          CarModelSubnameRepository carModelSubnameRepository,
                          MongoTemplate mongoTemplate) {
        this.carRepository = carRepository;
        this.excelExporter = excelExporter;
        this.carModelNameRepository = carModelNameRepository;
        this.carModelSubnameRepository = carModelSubnameRepository;
        this.mongoTemplate = mongoTemplate;
    }

    private final Integer EURO_COURSE = Integer.parseInt(readPropertieValue("money.course.euro.lei"));
    private final Integer DOLLAR_COURSE = Integer.parseInt(readPropertieValue("money.course.dollar.lei"));

    @Override
    @Async
    public CompletableFuture<List<Car>> findAll() {
        System.out.println(Thread.currentThread().getName());
        List<Car> all = carRepository.findAll();
        return CompletableFuture.completedFuture(all);
    }

    @Override
    public Page<Car> findAll(Pageable pageable) {return carRepository.findAll(pageable);}

    @Override
    @Async
    public void asyncMethod() {
        try {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0 ; i < 5 ; i++) {
            System.out.println(i);
        }
        System.out.println("");
    }

    @Override
    @Async
    public Car findById(String id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
    }

    @Override
    public Car save(Car car) {
        if (carRepository.findById(car.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Car already exists");
        } else
        return carRepository.save(car);
    }

    @Override
    public InputStream getCarImageStream(String carId) {
        if (findById(carId).getImage() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no image");
        }
        Byte[] image = carRepository.findById(carId).get().getImage();
        byte[] imageBytes = new byte[image.length];
        int i = 0;
        for (byte b : image) {
            imageBytes[i++] = b;
        }
        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        return inputStream;
    }

    @Override
    @Transactional
    public Boolean addCarImage(String carId, MultipartFile image) {
        try {
            Byte[] imageBytes = new Byte[image.getBytes().length];
            int i = 0;
            for (byte b : image.getBytes()) {
                imageBytes[i++] = b;
            }

            Optional<Car> carById = carRepository.findById(carId);
            carById.get().setImage(imageBytes);
            //carRepository.deleteById(carId);
            carRepository.save(carById.get());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<CarModelBean> getAllCarModels() {

        return carModelNameRepository.findAll()
                .stream()
                .map(carModelName ->
                        CarModelBean.builder()
                                .carModelName(carModelName)
                                .carModelSubnameList(carModelSubnameRepository.findAll()
                                        .stream()
                                        .filter(carModelSubname ->
                                                carModelSubname.getCarModelNameId().equals(carModelName.getId()))
                                        .collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList());
    }

    @Override
    public Boolean addNewModel(Optional<List<CarModelName>> models) {

        if (models.isPresent()) {
            carModelNameRepository.saveAll(models.get());
            return true;
        } else return false;
    }

    @Override
    public Boolean addNewSubModel(Optional<List<CarModelSubname>> models, String modelId) {

        if (carModelNameRepository.findById(modelId).isPresent()) {
            models.get().forEach(carModelSubname -> {
                carModelSubname.setCarModelNameId(modelId);
            });

            carModelSubnameRepository.saveAll(models.get());

            return true;
        } else return false;
    }

    @Override
    public List<Car> search(CarSearchBean carSearchBean, Pageable pageable) {

        Query query = new Query().with(pageable);

        if (carSearchBean.getOfferType() != null) {
            query.addCriteria(Criteria.where("offerType").is(carSearchBean.getOfferType()));
        }

        if (carSearchBean.getNegotiable() != null) {
            query.addCriteria(Criteria.where("negotiable").is(carSearchBean.getNegotiable()));
        }

        if (carSearchBean.getNegotiable() != null) {
            query.addCriteria(Criteria.where("authorType").is(carSearchBean.getAuthorType()));
        }

        if (carSearchBean.getModel() != null && carSearchBean.getModel().size() > 0) {
            query.addCriteria(Criteria.where("model").in(carSearchBean.getModel()));
        }

        if (carSearchBean.getSubmodel() != null && carSearchBean.getSubmodel().size() > 0) {
            query.addCriteria(Criteria.where("submodel").in(carSearchBean.getSubmodel()));
        }

        if (carSearchBean.getCondition() != null) {
            query.addCriteria(Criteria.where("condition").is(carSearchBean.getCondition()));
        }

        if (carSearchBean.getRegistration() != null) {
            query.addCriteria(Criteria.where("registration").is(carSearchBean.getRegistration()));
        }

        if (carSearchBean.getRegYearFrom() != null) {
            if (carSearchBean.getRegYearTo() > carSearchBean.getRegYearFrom()) {
                query.addCriteria(Criteria.where("regYear").in(getRegYears(carSearchBean.getRegYearFrom(), carSearchBean.getRegYearTo())));
            }
        }

        List<Car> carList = mongoTemplate.find(query, Car.class);

//        mongoTemplate.find(pageable);

        //Filter by price
        if (carSearchBean.getPriceTo() != null && carSearchBean.getPriceFrom() != null) {

            if (carSearchBean.getPriceTo() > carSearchBean.getPriceFrom()) {

                List<Car> priceSortedCarList = new ArrayList<>();

                for (Car car : carList) {
                    if (car.getPrice().get(carSearchBean.getCarPriceType()) > carSearchBean.getPriceFrom() &&
                            car.getPrice().get(carSearchBean.getCarPriceType()) < carSearchBean.getPriceTo()) {
                        priceSortedCarList.add(car);
                    }
                }

                if (!priceSortedCarList.isEmpty()) {
                    carList.clear();
                    carList = priceSortedCarList;
                }
            }

        } else {
            if (carSearchBean.getPriceFrom() != null) {
                carList = carList.stream()
                        .filter(car -> {
                            return car.getPrice().get(carSearchBean.getCarPriceType()) > carSearchBean.getPriceFrom();
                        })
                        .collect(Collectors.toList());
            }

            if (carSearchBean.getPriceTo() != null) {
                carList = carList.stream()
                        .filter(car -> {
                            return car.getPrice().get(carSearchBean.getCarPriceType()) < carSearchBean.getPriceTo();
                        })
                        .collect(Collectors.toList());
            }
        }

        return carList;
    }

    @Override
    public void countCarsWithImage() {
//        carRepository.findAll().stream().filter(x-> x.getImage()!=null).forEach(System.out::println);
        System.out.println(carRepository.findAll().stream().filter(x-> x.getImage()!=null).count());
    }

    @Override
    public void exportExcel(HttpServletResponse response) {
        List<Car> carList = carRepository.findAll();
        excelExporter.exportExcel(carList, response);
    }

    @Override
    public Boolean addRandomCars() {

        getAllCarModels().forEach(carModelBean -> {
            System.out.println(carModelBean.getCarModelName().getModelName());
            carModelBean.getCarModelSubnameList().forEach(carModelSubname -> {
                System.out.println("\t" + carModelSubname.getSubname());
            });
        });

        getAllCarModels().forEach(carModelBean -> {
            carModelBean.getCarModelSubnameList().forEach(carModelSubname -> {
                carRepository.save(Car.builder()
                        .offerType(Offer.SELL)
                        .price(getConvertedPriceToLei(Map.of(CarPriceType.EURO, Integer.parseInt(getRandomMoney()))))
                        .negotiable(true)
                        .authorType(Author.INDIVIDUAL)
                        .model(carModelBean.getCarModelName().getModelName())
                        .submodel(carModelSubname.getSubname())
                        .condition(Condition.USED)
                        .registration(Registration.MOLDOVA)
                        .regYear(Integer.parseInt("20" + getRandomNumber()))
                        .build());
            });
        });

        getAllCarModels().forEach(carModelBean -> {
            carModelBean.getCarModelSubnameList().forEach(carModelSubname -> {
                carRepository.save(Car.builder()
                        .offerType(Offer.CHANGE)
                        .price(getConvertedPriceToLei(Map.of(CarPriceType.DOLLAR, Integer.parseInt(getRandomMoney()))))
                        .negotiable(true)
                        .authorType(Author.DEALER)
                        .model(carModelBean.getCarModelName().getModelName())
                        .submodel(carModelSubname.getSubname())
                        .condition(Condition.DAMAGED)
                        .registration(Registration.TRANSNISTRIA)
                        .regYear(Integer.parseInt("20" + getRandomNumber()))
                        .build());
            });
        });

        getAllCarModels().forEach(carModelBean -> {
            carModelBean.getCarModelSubnameList().forEach(carModelSubname -> {
                carRepository.save(Car.builder()
                        .offerType(Offer.BUY)
                        .price(getConvertedPriceToLei(Map.of(CarPriceType.EURO, Integer.parseInt(getRandomMoney()))))
                        .negotiable(true)
                        .authorType(Author.INDIVIDUAL)
                        .model(carModelBean.getCarModelName().getModelName())
                        .submodel(carModelSubname.getSubname())
                        .condition(Condition.USED)
                        .registration(Registration.MOLDOVA)
                        .regYear(Integer.parseInt("20" + getRandomNumber()))
                        .build());
            });
        });


//        //Car 1
//        Map<CarPriceType, Integer> mapPrice = new HashMap<>();
//        mapPrice.put(CarPriceType.EURO, Integer.parseInt(getRandomMoney()));
//
//        CarModelName model = carModelNameRepository.findAll()
//                .stream()
//                .findAny()
//                .get();
//
//        System.out.println(model.getModelName());
//
//        CarModelSubname carModelSubname = carModelSubnameRepository.findAll()
//                .stream()
//                .filter(carModelSubnameX -> carModelSubnameX.getCarModelNameId()
//                        .equals(model.getId())).findAny().get();
//
//        carRepository.save(Car.builder()
//                        .offerType(Offer.SELL)
//                        .price(mapPrice)
//                        .negotiable(false)
//                        .authorType(Author.INDIVIDUAL)
//                        .model(model.getModelName())
//                        .submodel(carModelSubname.getSubname())
//                        .condition(Condition.USED)
//                        .registration(Registration.MOLDOVA)
//                        .regYear(Integer.parseInt("20"+getRandomNumber()))
//                .build());
//
//        //Car 2
//        Map<CarPriceType, Integer> mapPrice2 = new HashMap<>();
//        mapPrice2.put(CarPriceType.EURO, Integer.parseInt(getRandomMoney()));
//
//        CarModelName model2 = carModelNameRepository.findAll()
//                .stream()
//                .findAny()
//                .get();
//
//        CarModelSubname carModelSubname2 = carModelSubnameRepository.findAll()
//                .stream()
//                .filter(carModelSubnameX -> carModelSubnameX.getCarModelNameId()
//                        .equals(model2.getId())).findAny().get();
//
//        carRepository.save(Car.builder()
//                .offerType(Offer.BUY)
//                .price(mapPrice2)
//                .negotiable(true)
//                .authorType(Author.DEALER)
//                .model(model2.getModelName())
//                .submodel(carModelSubname2.getSubname())
//                .condition(Condition.DAMAGED)
//                .registration(Registration.TRANSNISTRIA)
//                .regYear(Integer.parseInt("20"+getRandomNumber()))
//                .build());
//
//        //Car 3
//        Map<CarPriceType, Integer> mapPrice3 = new HashMap<>();
//        mapPrice3.put(CarPriceType.EURO, Integer.parseInt(getRandomMoney()));
//
//        CarModelName model3 = carModelNameRepository.findAll()
//                .stream()
//                .findAny()
//                .get();
//
//        CarModelSubname carModelSubname3 = carModelSubnameRepository.findAll()
//                .stream()
//                .filter(carModelSubnameX -> carModelSubnameX.getCarModelNameId()
//                        .equals(model3.getId())).findAny().get();
//
//        carRepository.save(Car.builder()
//                .offerType(Offer.CHANGE)
//                .price(mapPrice3)
//                .negotiable(true)
//                .authorType(Author.INDIVIDUAL)
//                .model(model3.getModelName())
//                .submodel(carModelSubname3.getSubname())
//                .condition(Condition.NEW)
//                .registration(Registration.OTHER)
//                .regYear(Integer.parseInt("20"+getRandomNumber()))
//                .build());

//        carRepository.save(Car.builder()
//                .id(1L)
//                .offerType(Offer.BUY)
//                .price(Map.of(6000, CarPriceType.EURO))
//                .negotiable(true)
//                .authorType(Author.INDIVIDUAL)
//                .model(Model.builder()
//                        .ford(Ford.FIESTA)
//                        .build())
//                .condition(Condition.USED)
//                .registration(Registration.MOLDOVA)
//                .regYear(2014)
//                .build());
//        carRepository.save(Car.builder()
//                .id(2L)
//                .offerType(Offer.SELL)
//                .price(Map.of(10000, CarPriceType.DOLLAR))
//                .negotiable(false)
//                .authorType(Author.DEALER)
//                .model(Model.builder()
//                        .ford(Ford.FOCUS)
//                        .build())
//                .condition(Condition.USED)
//                .registration(Registration.MOLDOVA)
//                .regYear(2014)
//                .build());


        /*for (long i = 3L; i<500; i++) {
            carRepository.save(Car.builder()
                    .id(i)
                    .offerType(Offer.BUY)
                    .price(Map.of(Integer.parseInt(getRandomMoney()), CarPriceType.EURO))
                    .negotiable(false)
                    .authorType(Author.DEALER)
                    .model(Model.builder()
                            .ford(Ford.FOCUS)
                            .build())
                    .condition(Condition.USED)
                    .registration(Registration.MOLDOVA)
                    .regYear(Integer.parseInt("20"+getRandomNumber()))
                    .build());
        }
        for (long i = 501L; i<1000; i++) {
            carRepository.save(Car.builder()
                    .id(i)
                    .offerType(Offer.CHANGE)
                    .price(Map.of(Integer.parseInt(getRandomMoney()), CarPriceType.DOLLAR))
                    .negotiable(true)
                    .authorType(Author.INDIVIDUAL)
                    .model(Model.builder()
                            .ford(Ford.FOCUS)
                            .build())
                    .condition(Condition.NEW)
                    .registration(Registration.TRANSNISTRIA)
                    .regYear(Integer.parseInt("20"+getRandomNumber()))
                    .build());
        }

        for (long i = 1001L; i<1500; i++) {
            carRepository.save(Car.builder()
                    .id(i)
                    .offerType(Offer.CHANGE)
                    .price(Map.of(Integer.parseInt(getRandomMoney()), CarPriceType.DOLLAR))
                    .negotiable(false)
                    .authorType(Author.DEALER)
                    .model(Model.builder()
                            .volkswagen(Volkswagen.JETTA)
                            .build())
                    .condition(Condition.DAMAGED)
                    .registration(Registration.TRANSNISTRIA)
                    .regYear(Integer.parseInt("20"+getRandomNumber()))
                    .build());
        }
        for (long i = 1501L; i<2000; i++) {
            carRepository.save(Car.builder()
                    .id(i)
                    .offerType(Offer.SELL)
                    .price(Map.of(Integer.parseInt(getRandomMoney()), CarPriceType.EURO))
                    .negotiable(true)
                    .authorType(Author.INDIVIDUAL)
                    .model(Model.builder()
                            .volkswagen(Volkswagen.JETTA)
                            .build())
                    .condition(Condition.NEW)
                    .registration(Registration.MOLDOVA)
                    .regYear(Integer.parseInt("20"+getRandomNumber()))
                    .build());
        }

        for (long i = 2001L; i<2500; i++) {
            carRepository.save(Car.builder()
                    .id(i)
                    .offerType(Offer.SELL)
                    .price(Map.of(Integer.parseInt(getRandomMoney()), CarPriceType.LEI))
                    .negotiable(false)
                    .authorType(Author.INDIVIDUAL)
                    .model(Model.builder()
                            .toyota(Toyota.COROLLA)
                            .build())
                    .condition(Condition.NEW)
                    .registration(Registration.OTHER)
                    .regYear(Integer.parseInt("20"+getRandomNumber()))
                    .build());
        }
        for (long i = 2501L; i<3000; i++) {
            carRepository.save(Car.builder()
                    .id(i)
                    .offerType(Offer.CHANGE)
                    .price(Map.of(Integer.parseInt(getRandomMoney()), CarPriceType.DOLLAR))
                    .negotiable(true)
                    .authorType(Author.DEALER)
                    .model(Model.builder()
                            .toyota(Toyota.COROLLA)
                            .build())
                    .condition(Condition.DAMAGED)
                    .registration(Registration.MOLDOVA)
                    .regYear(Integer.parseInt("20"+getRandomNumber()))
                    .build());
        }*/

        return true;
    }

    protected static String getRandomNumber() {
        String SALTCHARS = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 2) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    protected static String getRandomMoney() {
        String SALTCHARS = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    @Override
    public Boolean deleteAllCars() {

        carRepository.deleteAll();

        if (carRepository.findAll().isEmpty())
            return true;

        else return false;
    }

    protected Map<CarPriceType, Integer> getConvertedPriceToLei(Map<CarPriceType, Integer> mapPrice) {

        if (mapPrice.size() == 1) {

            if (mapPrice.containsKey(CarPriceType.EURO)) {

                Map<CarPriceType, Integer> convertedMapPrice = new HashMap<>(mapPrice);

                convertedMapPrice.put(CarPriceType.LEI, mapPrice.get(CarPriceType.EURO) * EURO_COURSE);

                return convertedMapPrice;
            }

            if (mapPrice.containsKey(CarPriceType.DOLLAR)) {

                Map<CarPriceType, Integer> convertedMapPrice = new HashMap<>(mapPrice);

                convertedMapPrice.put(CarPriceType.LEI, mapPrice.get(CarPriceType.DOLLAR) * DOLLAR_COURSE);

                return convertedMapPrice;
            }
        }

        return mapPrice;
    }

    protected String readPropertieValue(String propName) {

        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("src\\main\\resources\\application.properties")) {

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop.getProperty(propName);
    }

    protected ArrayList<Integer> getRegYears(Integer yearFrom, Integer yearTo) {

        ArrayList<Integer> integers = new ArrayList<>();

        for (int i = yearFrom; i < yearTo; i++) {
            integers.add(i);
        }

        return integers;
    }
}
