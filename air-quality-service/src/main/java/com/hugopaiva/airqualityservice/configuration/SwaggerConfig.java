package com.hugopaiva.airqualityservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static springfox.documentation.builders.PathSelectors.regex;


@Configuration
public class SwaggerConfig {

    @Bean
    public Docket airQualityServiceApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(regex("/api.*"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().
                title("Air Quality REST API")
                .description("API that gives Air Quality for locations")
                .version("0.0.1")
                .contact(new Contact("Hugo Paiva de Almeida", "www.hugopaiva.com", "hugofpaiva@ua.pt"))
                .build();
    }
}
