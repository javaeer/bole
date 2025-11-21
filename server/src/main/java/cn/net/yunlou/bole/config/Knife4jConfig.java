package cn.net.yunlou.bole.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Value("${app.config.version:1.0.0}")
    private String appVersion;

    @Value("${app.config.environment:dev}")
    private String environment;

    /**
     * 根据@Tag 上的排序，写入x-order
     *
     * @return the global open api customizer
     */
   /* @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getTags()!=null){
                openApi.getTags().forEach(tag -> {
                    Map<String,Object> map=new HashMap<>();
                    map.put("x-order", RandomUtil.randomInt(0,100));
                    tag.setExtensions(map);
                });
            }
            if(openApi.getPaths()!=null){
                openApi.addExtension("x-test123","333");
                openApi.getPaths().addExtension("x-abb",RandomUtil.randomInt(1,100));
            }

        };
    }*/
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("伯乐简历后端服务 API - " + environment.toUpperCase())
                        .description("伯乐简历后端服务接口文档 - " +
                                ("prod".equals(environment) ? "生产环境" : "开发环境"))
                        .version(appVersion)
                        .contact(new Contact()
                                .name("伯乐开发团队")
                                .email("dev@yunlou.net"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                //.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                //.components(new io.swagger.v3.oas.models.Components()
                //        .addSecuritySchemes(securitySchemeName,
                //                new SecurityScheme()
                //                        .name(securitySchemeName)
                //                        .type(SecurityScheme.Type.HTTP)
                //                        .scheme("bearer")
                //                        .bearerFormat("JWT")
                //                        .description("请输入JWT Token，格式: Bearer {token}")))
                ;
    }
}