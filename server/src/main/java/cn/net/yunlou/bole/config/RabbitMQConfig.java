package cn.net.yunlou.bole.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(RabbitAutoConfiguration.class)
@EnableConfigurationProperties(DocumentTaskProperties.class)
public class RabbitMQConfig {

    /**
     * 消息消费方 对象序列化配置
     *
     * @param connectionFactory
     * @return
     */
    @Bean("rabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * 消息发送方 序列化配置
     *
     * <p>被序列化对象应该提供一个无参构造 否则会报错
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());

        // 设置并发消费者数量
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(1);

        return factory;
    }

    @Bean("jackson2JsonMessageConverter")
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue documentQueue(DocumentTaskProperties properties) {
        return QueueBuilder.durable(properties.getQueueName()).build();
    }

    @Bean
    public TopicExchange documentExchange(DocumentTaskProperties properties) {
        return ExchangeBuilder.topicExchange(properties.getExchangeName()).durable(true).build();
    }

    @Bean
    public Binding documentBinding(
            Queue documentQueue,
            TopicExchange documentExchange,
            DocumentTaskProperties properties) {
        return BindingBuilder.bind(documentQueue)
                .to(documentExchange)
                .with(properties.getRoutingKey());
    }
}
