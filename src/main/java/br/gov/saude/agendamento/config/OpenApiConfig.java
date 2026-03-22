package br.gov.saude.agendamento.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Agendamento de Consultas - e-SUS APS / PEC")
                .version("v1")
                .description("API REST para fluxo completo de agendamento de consultas")
                .contact(new Contact().name("Ministerio da Saude")));
    }
}

