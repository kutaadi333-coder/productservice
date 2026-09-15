package com.ecommerce.productservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productServiceOpenAPI() {

        Server localServer = new Server()
                .url("http://localhost:8083")
                .description("Local Development Server");

        Contact contact = new Contact()
                .name("E-Commerce Product Service Team");

        License license = new License()
                .name("Internal API");

        Info info = new Info()
                .title("E-Commerce Product Service API")
                .version("1.0.0")
                .description(
                        "Professional API documentation for the E-Commerce Product Service. " +
                                "This API provides product creation, pagination, sorting, filtering, " +
                                "search, and API versioning."
                )
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}