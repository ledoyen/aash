package com.ledoyen.aash.junit.runner.mockito;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.ledoyen.aash.junit.runner.Pojo;

public abstract class AbstractMockitoTest {

	@Mock
    Pojo pojo;

	@Test
	public void hellotest() {
		Mockito.when(pojo.getName()).thenReturn("Hello world.");
		assertThat(pojo.getName(), CoreMatchers.equalTo("Hello world."));
	}
}
