package com.ledoyen.aash.junit.runner.mockito;

import org.junit.runner.RunWith;

import com.ledoyen.aash.junit.runner.AashCoreRunner;
import com.ledoyen.aash.junit.runner.PluginConfiguration;

@RunWith(AashCoreRunner.class)
@PluginConfiguration(MockitoPlugin.class)
public class MockitoPluginTest extends AbstractMockitoTest {

}
