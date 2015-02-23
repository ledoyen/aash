package com.ledoyen.context;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Optional;

/**
 * @author L.LEDOYEN
 */
public class SpringExpressionResolver implements ExpressionResolver {

    private static ExpressionParser parser = new SpelExpressionParser();

    public Expression parse(String expression) {
        return new SpringExpression(expression);
    }

    public Object resolve(Expression expression, AbstractScopedContext context) {
        SpringExpression expr = (SpringExpression) expression;
        EvaluationContext spelContext = new StandardEvaluationContext() {
            public Object lookupVariable(String name) {
                Optional<Object> value = context.get(name);
                if (value.isPresent()) {
                    return value.get();
                } else {
                    return null;
                }
            }
        };
        return expr.expression.getValue(spelContext);
    }

    private static class SpringExpression implements Expression {

        private final org.springframework.expression.Expression expression;

        private SpringExpression(String expr) {
            this.expression = parser.parseExpression(expr);
        }
    }
}
