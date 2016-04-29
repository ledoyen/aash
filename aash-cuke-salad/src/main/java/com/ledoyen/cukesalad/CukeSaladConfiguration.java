package com.ledoyen.cukesalad;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CukeSaladConfiguration {

	/**
	 * The <em>annotated classes</em> to use for loading an {@link org.springframework.context.ApplicationContext ApplicationContext}.
	 * @see org.springframework.context.annotation.Configuration
	 */
	Class<?>[] classes();

	boolean reloadBeforeTest() default false;
}
