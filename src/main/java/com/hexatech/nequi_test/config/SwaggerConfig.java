package com.hexatech.nequi_test.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Nequi Test API",
        version = "1.0",
        description = "API documentation for the Nequi Test application"
    )
)
public class SwaggerConfig {
  
    
}
