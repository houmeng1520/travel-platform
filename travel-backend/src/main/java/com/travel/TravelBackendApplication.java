package com.travel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.travel.mapper")
public class TravelBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelBackendApplication.class, args);

    }

}


