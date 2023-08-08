package com.cars.configuration;

import com.cars.repository.CarRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = CarRepository.class)
@Configuration
public class MongoDBConfig {

//    @Bean
//    CommandLineRunner commandLineRunner(CarRepository carRepository) {
//        return String -> {
//            carRepository.save(Car.builder()
//                    .id(1L)
//                    .offerType(Offer.BUY)
//                    .price(Map.of(6000L, CarPriceType.EURO))
//                    .negotiable(true)
//                    .authorType(Author.INDIVIDUAL)
//                    .model(Model.builder()
//                            .ford(Ford.FIESTA)
//                            .build())
//                    .build());
//            carRepository.save(Car.builder()
//                    .id(2L)
//                    .offerType(Offer.SELL)
//                    .price(Map.of(9000L, CarPriceType.EURO))
//                    .negotiable(false)
//                    .authorType(Author.DEALER)
//                    .model(Model.builder()
//                            .ford(Ford.FOCUS)
//                            .build())
//                    .build());
//        };
//    }
}
