package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .info(new Info().title("Music Catalog API").version("1.0").description("..."))
                        .tags(Arrays.asList(
                                new Tag().name("01. Auth").description("Endpoints de Autenticação"),
                                new Tag().name("Álbuns").description("Gerenciamento de Álbuns"),
                                new Tag().name("Artistas").description("Gerenciamento de Artistas"),
                                new Tag().name("Regionais").description("Gerenciamento de Regionais")
                        ));
        }
}