package com.flowiee.dms.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI myOpenAPI() {
        //Server devServer = new Server();
        //devServer.setUrl("http://localhost:8085");
        //devServer.setDescription("Server URL in Development environment");

//        Server prodServer = new Server();
//        prodServer.setUrl(serverUrl);
//        prodServer.setDescription("Server URL in Production environment");

//        Contact contact = new Contact();
//        contact.setEmail("nguyenducviet0684@gmail.com");
//        contact.setName("Nguyen Duc Viet");
//        contact.setUrl("http://localhost:8085");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Flowiee API")
                .version("1.0");
                //.contact(contact)
                //.description("This API exposes endpoints to manage tutorials.").termsOfService("http://localhost:8085/swagger-ui/index.html")
                //.license(mitLicense);

        //return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
        return new OpenAPI().info(info);
    }
}