package com.ledoyen.cukesalad.sampleApp.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SampleServer {

	@Value("${test}")
	private String testValue;

	public SampleServer() {
		System.out.println("init");
	}

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	@RequestMapping("/post/{message}")
	String post(@PathVariable String message) {
		return "Message posted : " + message;
	}

	@RequestMapping("/test")
	String test() {
		return testValue;
	}
}
