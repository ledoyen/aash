package com.ledoyen.aash.junit.runner;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

import com.ledoyen.aash.junit.runner.mockito.MockitoPlugin;
import com.ledoyen.aash.junit.runner.spring.Pojo2;
import com.ledoyen.aash.junit.runner.spring.SpringPlugin;

@RunWith(AashJUnitRunner.class)
@PluginConfiguration({SpringPlugin.class, MockitoPlugin.class})
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringAndMockitoTest {

	@Autowired
	private Pojo2 pojo2;

	@Value("${end.of.sentence}")
	private String endOfSentence;

	@Mock
	private Pojo pojo;

	@Test
	public void hellotest() {
		Mockito.when(pojo.getName()).thenReturn("lo ");
		assertThat(pojo2.getName() + pojo.getName() + endOfSentence, CoreMatchers.equalTo("Hello world."));
	}
}
