package com.ledoyen.aash.junit.runner.parameters;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public abstract class AbstractParametersTest {

	private final String phoneNumber;
	private final boolean isValidPhoneNumber;

	public AbstractParametersTest(final String phoneNumber, final boolean isValidPhoneNumber) {
	    this.phoneNumber = phoneNumber;
	    this.isValidPhoneNumber = isValidPhoneNumber;
	}

	@Parameters
	public static Collection<Object[]> params() {
		return Arrays.asList( //
				new Object[] { "1", false }, //
				new Object[] { "0123456789", true } //
				);
	}

	@Test
	public void isValidPhoneNumberNumberTest() {
		Pattern phonePattern = Pattern.compile("^0[1-6]{1}(([0-9]{2}){4})|((\\s[0-9]{2}){4})|((-[0-9]{2}){4})$");
	    final boolean result = phonePattern.matcher(phoneNumber).matches();
	    assertThat(result, CoreMatchers.equalTo(isValidPhoneNumber));
	}
}
