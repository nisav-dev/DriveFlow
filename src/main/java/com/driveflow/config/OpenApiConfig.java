package com.driveflow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI driveFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DriveFlow REST API")
                        .version("1.0.0")
                        .description("""
                                ## ממשק API עבור מערכת השכרת רכבים DriveFlow

                                ה-API מספק גישה לנתונים בזמן אמת:
                                - **רכבים** — חיפוש, זמינות, סטטוס
                                - **הזמנות** — פרטים וסטטיסטיקות (ADMIN/AGENT בלבד)
                                - **סניפים** — כולל קואורדינטות GPS למפה

                                כניסה נדרשת לחלק מהנקודות — השתמש בפרטי ה-Demo:
                                `admin@driveflow.co.il / Admin1234`
                                """)
                        .contact(new Contact()
                                .name("DriveFlow Dev Team")
                                .email("dev@driveflow.co.il"))
                        .license(new License()
                                .name("Academic Project — All Rights Reserved")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development")))
                .components(new Components()
                        .addSecuritySchemes("cookieAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")
                                .description("Spring Session cookie — התחבר דרך /login")));
    }
}
