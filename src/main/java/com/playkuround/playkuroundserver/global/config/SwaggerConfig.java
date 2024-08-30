package com.playkuround.playkuroundserver.global.config;

import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@OpenAPIDefinition(
        info = @Info(
                title = "playkuround API 명세서",
                version = "v2.0.7"
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    @Profile("!prod")
    public OpenAPI openAPI() {
        String jwtSchemeName = "Authorization";

        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList(jwtSchemeName);

        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme(GrantType.BEARER.getType());

        Components components = new Components();
        components.addSecuritySchemes(jwtSchemeName, securityScheme);

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }

}