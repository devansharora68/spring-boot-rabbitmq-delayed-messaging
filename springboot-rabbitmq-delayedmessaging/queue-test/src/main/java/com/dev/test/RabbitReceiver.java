package com.dev.test;

import org.springframework.stereotype.Component;

@Component
public class RabbitReceiver {
	
	public void receiveMessage(Object message) {
		System.out.println("message received  "+message);
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
