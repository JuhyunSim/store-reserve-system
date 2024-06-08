package com.zerobase.partner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.zerobase"})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.zerobase", repositoryFactoryBeanClass =
        EnversRevisionRepositoryFactoryBean.class)
@EntityScan("com.zerobase")
@ComponentScan("com.zerobase")
public class PartnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PartnerApplication.class, args);
    }
}
