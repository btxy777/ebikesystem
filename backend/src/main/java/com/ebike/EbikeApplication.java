package com.ebike;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ebike.mapper")
public class EbikeApplication {
    public static void main(String[] args) {
        SpringApplication.run(EbikeApplication.class, args);
    }
}
