package com.drinkspeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DrinkSpeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrinkSpeedApplication.class, args);
    }

}
