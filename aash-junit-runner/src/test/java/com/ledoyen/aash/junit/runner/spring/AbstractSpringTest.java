package com.ledoyen.aash.junit.runner.spring;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ledoyen.aash.junit.runner.Pojo;

public abstract class AbstractSpringTest {

	@Autowired
	private Pojo2 pojo2;

	@Value("${end.of.sentence}")
	private String endOfSentence;

	@Autowired
	private Pojo pojo;

	@Test
	public void hellotest() {
		Mockito.when(pojo.getName()).thenReturn("lo ");
		assertThat(pojo2.getName() + pojo.getName() + endOfSentence, CoreMatchers.equalTo("Hello world."));
	}
}
