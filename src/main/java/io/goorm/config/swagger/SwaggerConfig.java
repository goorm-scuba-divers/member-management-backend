package io.goorm.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.*;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .version("0.0.1")
                        .description("Scuba Backend API")
                        .title("Scuba Backend API"))
                .components(authSetting())
                .addSecurityItem(
                        new SecurityRequirement().addList("accessToken")
                );
    }



    private Components authSetting() {
        return new Components()
                .addSecuritySchemes(
                        "accessToken",
                                new SecurityScheme()
                                        .type(Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(In.HEADER)
                                        .name("Authorization"));
    }
}
