package com.bankapp.standartmock;

import com.bankapp.controller.AccountController;
import com.bankapp.service.AccountService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackages = "com.bankapp")
@EnableJpaRepositories("com.bankapp.repository")
@EntityScan("com.bankapp.model")
public class MyMock {
    public static void main(String[] args) {
        SpringApplication.run(MyMock.class, args);
    }
}
//Spring Boot
//Spring Framework
//ApplicationServer - Tomcat
//        Map<String,Object>