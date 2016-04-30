package com.ledoyen.cukesalad.core;

import java.util.Map;

public interface CukeSaladContext {

	Map<String, Class<?>> getClassesBySimpleName();
}
