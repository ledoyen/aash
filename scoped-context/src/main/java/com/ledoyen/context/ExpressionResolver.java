package com.ledoyen.context;

/**
 * @author L.LEDOYEN
 */
public interface ExpressionResolver {

    Expression parse(String expression);

    Object resolve(Expression expresion, AbstractScopedContext context);
}
