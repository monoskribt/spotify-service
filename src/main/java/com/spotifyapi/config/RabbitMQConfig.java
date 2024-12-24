package com.spotifyapi.config;

import com.spotifyapi.props.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(rabbitMQProperties.getExchange());
    }

    @Bean
    public Queue queue() {
        return new Queue(rabbitMQProperties.getQueue(), true);
    }

    @Bean
    public Binding binding(DirectExchange directExchange, Queue queue) {
        return BindingBuilder.bind(queue)
                .to(directExchange).with(rabbitMQProperties.getRoutingKey());
    }
}
