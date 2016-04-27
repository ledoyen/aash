package com.ledoyen.cukesalad.demo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ledoyen.cukesalad.automocker.AutoMockerContextBuilder;
import com.ledoyen.cukesalad.sampleApp.app1.Application;
import com.ledoyen.cukesalad.tools.StringMap;

public class DemoTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoTest.class);

	@Test
	public void testDemoProject() throws Exception {
		try (ConfigurableApplicationContext context = AutoMockerContextBuilder.newBuilder()
				.mockPropertySources(StringMap.of("test", "43")).buildWithJavaConfig(Application.class)) {
			context.start();

			context.getBean(MockMvc.class).perform(MockMvcRequestBuilders.get("/post/message1"))
					.andExpect(MockMvcResultMatchers.status().isOk());

			context.getBean(MockMvc.class).perform(MockMvcRequestBuilders.get("/test"))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().string("4343"));

			LOGGER.info("Test OK !");
		}
	}
}
