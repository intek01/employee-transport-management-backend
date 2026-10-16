package com.intekab.transport.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transportOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Employee Transport Management Backend API")
                .version("1.0.0")
                .description("REST API for managing employee shuttle routes, vehicles and seat bookings. "
                        + "Interactive docs are served at /swagger-ui.html."));
    }
}
