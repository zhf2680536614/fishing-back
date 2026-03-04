package com.fishing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fishing.mapper")
public class FishingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishingApplication.class, args);
    }

}
