package com.zerobase.reserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.zerobase")
@EnableJpaRepositories(basePackages = "com.zerobase")
public class ReserveApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReserveApiApplication.class, args);
    }
}
