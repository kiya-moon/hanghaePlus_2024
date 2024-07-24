package com.hhplus.concert_ticketing.presentation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "ConcertDto Ticketing API",
        version = "1.0",
        description = "API documentation for the ConcertDto Ticketing application",
        contact = @Contact(name = "kiya", email = "kiyamoon@daum.net"),
        license = @io.swagger.v3.oas.annotations.info.License(name = "Apache 2.0", url = "http://springdoc.org")
))
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("ConcertDto Ticketing API")
                        .version("1.0")
                        .description("API documentation for the ConcertDto Ticketing application")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
