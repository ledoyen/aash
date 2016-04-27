package com.ledoyen.cukesalad.sampleApp.app1.server;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SampleServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleServer.class);

	@Value("${test}")
	private String testValue;

	@Resource(name = "stringBean")
	private String testValue2;

	public SampleServer() {
		LOGGER.info("Initiating web server");
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
		return testValue + testValue2;
	}
}
