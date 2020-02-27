package com.dev.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.BindingBuilder.GenericArgumentsConfigurer;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	@Value("${rabbitmq.queue:delayedqueue}")
	String queueName;

	@Value("${rabbitmq.exchange:exchange.directdelayed}")
	String exchange;

	@Value("${rabbitmq.routingkey:delayedqueue}")
	private String routingkey;

	@Bean
	Queue queue() {
		Map<String, Object> args = new HashMap<>();
		args.put("x-single-active-consumer", true);

		return new Queue(queueName, true, false, false, args);
	}

	/*@Bean
	DirectExchange exchange() {
		Map<String, Object> args = new HashMap<>();
		args.put("x-delayed-type", "direct");
		args.put("exchangeType","x-delayed-message");
		return new DirectExchange(exchange,true,false,args);
	}*/

	
	@Bean
	CustomExchange delayedMessageExchange() {
	    Map<String, Object> args = new HashMap<String, Object>();
	    args.put("x-delayed-type", "direct");
	    return new CustomExchange(exchange, "x-delayed-message", true, false, args);
	}
	
	@Bean
	GenericArgumentsConfigurer binding(Queue queue, CustomExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingkey);
	}

	@Bean
	Binding binding(Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingkey);
	}
	
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setPrefetchCount(1);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(RabbitReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
}
