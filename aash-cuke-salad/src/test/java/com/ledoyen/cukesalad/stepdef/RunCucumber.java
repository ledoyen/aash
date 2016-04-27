package com.ledoyen.cukesalad.stepdef;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.ledoyen.cukesalad.sampleApp.app1.Application;

import cucumber.api.CucumberOptions;
import cucumber.runtime.CukeSalad;

@RunWith(CukeSalad.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber" }, features = "src/test/resources/features")
@ContextConfiguration(classes = Application.class)
public class RunCucumber {

}
