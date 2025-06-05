package io.goorm.config.swagger;

import io.goorm.config.environment.EnvironmentConstants;
import io.goorm.config.environment.UrlConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static io.swagger.v3.oas.models.security.SecurityScheme.*;

@Configuration
public class SwaggerConfig {

    @Value("${server.environment}")
    private String environment;

    private UrlConstants urlConstants;
    private EnvironmentConstants environmentConstants;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .version("0.0.1")
                        .description("Scuba Backend API")
                        .title("Scuba Backend API"))
                .servers(List.of(new Server().url(getServerUrl()).description("scuba member backend")))
                .components(authSetting())
                .addSecurityItem(
                        new SecurityRequirement().addList("accessToken")
                );
    }

    private String getServerUrl() {
        return switch (environment) {
            case EnvironmentConstants.Constants.PROD -> UrlConstants.PROD_SERVER_URL.getValue();
            default -> UrlConstants.LOCAL_SERVER_URL.getValue();
        };
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
