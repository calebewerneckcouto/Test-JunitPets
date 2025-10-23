package br.com.alura.adopet.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de Desenvolvimento");

        Contact contact = new Contact();
        contact.setEmail("suporte@adopet.com");
        contact.setName("Suporte Adopet");
        contact.setUrl("https://www.adopet.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Adopet API")
                .version("1.0.0")
                .contact(contact)
                .description("API para sistema de adoção de pets")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}