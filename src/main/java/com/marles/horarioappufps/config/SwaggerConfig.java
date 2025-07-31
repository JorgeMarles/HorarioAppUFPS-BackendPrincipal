package com.marles.horarioappufps.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Web Backend")
            .version("v1")
            .description("Service for academic schedule simulation for Francisco de Paula Santander University"))
        	.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        	.components(
        		new Components()
                .addSecuritySchemes(
                	"bearerAuth",
                	new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                )
            );
    }
}
