package com.ledoyen.aash.junit.runner.spring;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.ledoyen.aash.junit.runner.AashCoreRunner;
import com.ledoyen.aash.junit.runner.PluginConfiguration;

@RunWith(AashCoreRunner.class)
@PluginConfiguration(SpringPlugin.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringPluginTest extends AbstractSpringTest {

}
