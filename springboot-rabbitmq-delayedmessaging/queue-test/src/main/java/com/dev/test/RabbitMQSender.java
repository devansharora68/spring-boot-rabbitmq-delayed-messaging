package com.dev.test;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {
	
	@Autowired
	private AmqpTemplate rabbitTemplate;
	
	@Value("${rabbitmq.exchange:exchange.directdelayed}")
	private String exchange;
	
	@Value("${rabbitmq.routingkey:delayedqueue}")
	private String routingkey;	
	
	public void send(Employee company) {
		System.out.println("Sending message = " + company);
		rabbitTemplate.convertAndSend(exchange,routingkey,company,
	            message -> {
	                message.getMessageProperties().setDelay(10000);//sets delay of 10000 miliseconds
	                return message;
	            });
	}
}
