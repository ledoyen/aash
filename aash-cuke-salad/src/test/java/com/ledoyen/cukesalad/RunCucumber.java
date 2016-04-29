package com.ledoyen.cukesalad;

import static com.ledoyen.cukesalad.CukeSalad.map;

import java.util.Map;

import org.junit.runner.RunWith;

import com.ledoyen.cukesalad.sampleApp.app2.Application;

import cucumber.api.CucumberOptions;

@RunWith(CukeSalad.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber" }, features = "src/test/resources/features")
@CukeSaladConfiguration(classes = Application.class)
public class RunCucumber {

	@MockProperties
	public static Map<String, String> configuration() {
		return map("test", "43");
	}
}
