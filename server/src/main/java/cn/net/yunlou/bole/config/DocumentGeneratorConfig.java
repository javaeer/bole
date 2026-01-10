package cn.net.yunlou.bole.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * FileName: GeneratorConfig
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 22:41
 * Modified By
 * Modified At
 */
@Data
@Component
@ConfigurationProperties(prefix = "document.generator")
public class DocumentGeneratorConfig {

    /**
     * 临时文件存放位置
     */
    private String tempDir;

    /**
     * 字体存放路径
     */
    private String fontDir;

}
