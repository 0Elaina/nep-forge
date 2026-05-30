package com.nep.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NepForge 接口文档")
                        .version("v0.0.1")
                        .description("NepForge 项目测试接口文档")
                        .contact(new Contact()
                                .name("Neptune")
                        )
                );
    }
}
