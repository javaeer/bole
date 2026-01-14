package cn.net.yunlou.bole.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AppConfigProperties.class})
@RequiredArgsConstructor
public class Knife4jConfig {

    private final AppConfigProperties appConfigProperties;

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title(
                                        appConfigProperties.getName()
                                                + " - "
                                                + appConfigProperties
                                                        .getEnvironment()
                                                        .toUpperCase())
                                .description(
                                        "接口文档 - "
                                                + ("prod"
                                                                .equals(
                                                                        appConfigProperties
                                                                                .getEnvironment())
                                                        ? "生产环境"
                                                        : "开发环境"))
                                .version(appConfigProperties.getVersion())
                                .contact(
                                        new Contact()
                                                .name(appConfigProperties.getTeam().getName())
                                                .email(appConfigProperties.getTeam().getEmail()))
                                .license(
                                        new License()
                                                .name("Apache 2.0")
                                                .url(
                                                        "http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
