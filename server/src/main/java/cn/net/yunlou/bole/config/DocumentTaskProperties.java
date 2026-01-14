package cn.net.yunlou.bole.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "document.task")
public class DocumentTaskProperties {
    private String queueName;
    private String exchangeName;
    private String routingKey;
}
