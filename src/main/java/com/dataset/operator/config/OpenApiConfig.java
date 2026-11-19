package com.dataset.operator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI datasetOperatorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JSON Dataset Group-By & Sort-By Operator API")
                        .version("1.0.0")
                        .description("RESTful Spring Boot service for persisting dynamic JSON dataset records in a relational database and executing dynamic Group-By and Sort-By query operators.")
                        .contact(new Contact()
                                .name("Backend Engineering")
                                .email("engineering@dataset-operator.dev"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
