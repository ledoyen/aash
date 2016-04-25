package com.ledoyen.cukesalad.demo;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ledoyen.cukesalad.CukeSaladAnnotationContext;
import com.ledoyen.cukesalad.sampleApp.Application;
import com.ledoyen.cukesalad.tools.StringMap;

public class DemoTest {

	public static void main(String[] args) throws BeansException, Exception {
		Map<String, String> props = StringMap.of("test", "43");
		try (ConfigurableApplicationContext context = new CukeSaladAnnotationContext(props, Application.class)) {
			context.start();

			context.getBean(MockMvc.class).perform(MockMvcRequestBuilders.get("/post/message1"))
					.andExpect(MockMvcResultMatchers.status().isOk());

			context.getBean(MockMvc.class).perform(MockMvcRequestBuilders.get("/test"))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().string("43"));
		}
	}
}
