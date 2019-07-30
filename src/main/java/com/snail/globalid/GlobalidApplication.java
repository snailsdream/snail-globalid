package com.snail.globalid;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = {"com.snail.globalid.mapper"})
public class GlobalidApplication {

    public static void main(String[] args) {
       // DistributedAtomicLong
        //InterProcessMutex
        SpringApplication.run(GlobalidApplication.class, args);
    }
}
