package com.ledoyen.cukesalad.stepdef;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.ledoyen.cukesalad.core.CukeSaladContext;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SpringStepDef {

	@Autowired
	private CukeSaladContext cukeContext;

	@Autowired
	private ApplicationContext applicationContext;

	private Object lastMethodCallResult;

	@When("^a call on service (.+) method (.+) is made$")
	public void a_call_on_service_method_is_made(String serviceClass, String methodName) throws Exception {
		Object bean = applicationContext.getBean(cukeContext.getClassesBySimpleName().get(serviceClass));

		lastMethodCallResult = bean.getClass().getDeclaredMethod(methodName).invoke(bean);
	}

	@Then("^the method response should be of type (\\S+)$")
	public void the_methode_response_should_be_of_type(String className) {
		assertThat(lastMethodCallResult).isInstanceOf(cukeContext.getClassesBySimpleName().get(className));
	}

	@Then("^the method response should be (\\S+)$")
	public void the_method_reponse_should_be(Object o) {
		assertThat(lastMethodCallResult).isEqualTo(o);
	}
}
