package cn.net.yunlou.bole.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FileName: AppProperties Description: Created By laughtiger Created At 2025/12/4 14:48 Modified By
 * Modified At
 */
@Data
@ConfigurationProperties(prefix = "app.config")
public class AppConfigProperties {

    private String name;

    private String version;

    private String environment;

    private Team team = new Team();

    private Security security = new Security();

    private Cors cors = new Cors();

    @Data
    public static class Team {
        private String name;
        private String email;
    }

    @Data
    public static class Security {
        private String jwtSecret;
        private String jwtAccessExpiration;
        private String jwtRefreshExpiration;
        private String tokenPrefix;
        private String header;
        private List<String> whiteList;
    }

    @Data
    public static class Cors {
        private String allowedOrigins;
        private String allowedMethods;
        private String allowedHeaders;
        private String exposedHeaders;
        private Boolean allowCredentials;
    }
}
