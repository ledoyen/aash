package com.ledoyen.context;

import com.ledoyen.tool.Classes;

/**
 * @author L.LEDOYEN
 */
public final class ExpressionResolvers {

    private static final boolean springExpressionAvailable = Classes.isClassPresent("org.springframework.expression.Expression");

    private ExpressionResolvers() {
    }

    static ExpressionResolver getAvailableResolver() {
        if (springExpressionAvailable) {
            return new SpringExpressionResolver();
        } else {
            throw new IllegalStateException("No Expression resolver detected [spring-expression]");
        }
    }
}
