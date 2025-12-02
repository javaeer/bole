package cn.net.yunlou.bole.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * FileName: SucurityWaitlistConfig Description: Created By MR. WANG Created At 2025/11/21 17:13
 * Modified By Modified At
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityWhitelistConfig {

    private List<String> whiteList;
}
