package com.playkuround.playkuroundserver.global.config;

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
                description = "playkuround에 사용되는 API 명세서",
                version = "v2"
        )
)
@Configuration
public class SwaggerConfig {

    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    @Bean
    // 운영 환경에는 Swagger를 비활성화하기 위해 추가했습니다.
    @Profile("!Prod")
    public OpenAPI openAPI() {
        String jwtSchemeName = "Authorization";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(BEARER_TOKEN_PREFIX));
        //.bearerFormat(JwtTokenProvider.TYPE));

        // Swagger UI 접속 후, 딱 한 번만 accessToken을 입력해주면 모든 API에 토큰 인증 작업이 적용됩니다.
        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }

}