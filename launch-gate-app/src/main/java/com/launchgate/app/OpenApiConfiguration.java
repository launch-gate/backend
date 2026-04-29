package com.launchgate.app;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

        @Bean
        public OpenAPI openApi() {
                var server = new Server();
                server.setUrl("/");
                server.setDescription("Server");
                final String securitySchemeName = "bearerAuth";
                return new OpenAPI().info(new io.swagger.v3.oas.models.info.Info().title("Web День открытых дверей").description("API документация").version("1"))
                        .components(new Components()).addServersItem(server).addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                        .components(
                                new Components()
                                        .addSecuritySchemes(securitySchemeName,
                                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                                        .scheme("bearer")
                                                        .bearerFormat("JWT")));

        }
}
