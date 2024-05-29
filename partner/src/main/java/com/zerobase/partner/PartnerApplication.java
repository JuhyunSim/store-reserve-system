package com.zerobase.partner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PartnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PartnerApplication.class, args);
    }
}
