package com.stcau.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@SpringBootApplication
@EnableFeignClients
public class EsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsServiceApplication.class, args);
    }

}
