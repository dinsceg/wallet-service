package com.leovegas.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class WalletConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.leovegas.wallet"))
                .paths(PathSelectors.ant("/wallet/**"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Wallet Service",
                "Wallet service is used for getting the current balance, transfer funds and history of the transaction",
                "1.0.0-SNAPSHOT",
                "Terms of service",
                new Contact("Dinesh Venkatesan", "", "dinsceg@gmail.com"),
                "License of API", "API license URL", Collections.emptyList());
    }
}
