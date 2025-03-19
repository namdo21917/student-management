package com.study.java.studentmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    
    @Value("${app.swagger.title}")
    private String title;

    @Value("${app.swagger.description}")
    private String description;

    @Value("${app.swagger.version}")
    private String version;

    @Value("${app.swagger.contact.name}")
    private String contactName;

    @Value("${app.swagger.contact.email}")
    private String contactEmail;

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact()
                .name(contactName)
                .email(contactEmail);

        Info info = new Info()
                .title(title)
                .version(version)
                .contact(contact)
                .description(description);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
} 