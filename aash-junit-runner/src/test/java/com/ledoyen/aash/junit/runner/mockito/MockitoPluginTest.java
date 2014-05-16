package com.ledoyen.aash.junit.runner.mockito;

import org.junit.runner.RunWith;

import com.ledoyen.aash.junit.runner.AashJUnitRunner;
import com.ledoyen.aash.junit.runner.PluginConfiguration;

@RunWith(AashJUnitRunner.class)
@PluginConfiguration(MockitoPlugin.class)
public class MockitoPluginTest extends AbstractMockitoTest {

}
