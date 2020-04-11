package com.dream.configs;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.Getter;

@Getter
public class MySSEEmitter extends SseEmitter {

	long age;
	
	public MySSEEmitter(Long timeout) {
		super(timeout);
		this.age = System.currentTimeMillis();
	}

	
}
