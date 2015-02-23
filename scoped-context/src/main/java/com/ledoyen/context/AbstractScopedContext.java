package com.ledoyen.context;

import com.ledoyen.tool.Either;
import com.ledoyen.tool.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author L.LEDOYEN
 */
abstract class AbstractScopedContext implements ScopedContext {

    private static ExpressionResolver expressionResolver;

    private final boolean threadSafe;
    protected final Map<String, Either<Object, Expression>> values;

    AbstractScopedContext(boolean threadSafe) {
        this.threadSafe = threadSafe;
        if (threadSafe) {
            values = new ConcurrentHashMap<>();
        } else {
            values = new HashMap<>();
        }
    }

    public ScopedContext createChild() {
        return new NodeScopedContext(this);
    }

    public ScopedContext shield() {
        return new ShieldScopedContext(this);
    }

    public boolean isThreadSafe() {
        return threadSafe;
    }

    public ScopedContext put(String key, Object value) {
        Preconditions.checkArgument(key != null && key.length() > 0, "key must not be null or empty");
        Preconditions.checkArgument(value != null, "value must not be null");
        values.put(key, Either.left(value));
        return this;
    }

    public ScopedContext putExpression(String key, String expression) {
        if (expressionResolver == null) {
            expressionResolver = ExpressionResolvers.getAvailableResolver();
        }
        Preconditions.checkArgument(key != null && key.length() > 0, "key must not be null or empty");
        Preconditions.checkArgument(expression != null && expression.length() > 0, "expression must not be null or empty");
        values.put(key, Either.right(expressionResolver.parse(expression)));
        return this;
    }

    public Optional<Object> get(String key) {
        Optional<Either<Object, Expression>> value = innerGet(key);
        if (value.isPresent()) {
            if (value.get().isLeft()) {
                return Optional.of(value.get().left());
            } else {
                return Optional.ofNullable(resolve(value.get().right()));
            }
        } else {
            return Optional.empty();
        }
    }

    private Object resolve(Expression exp) {
        return expressionResolver.resolve(exp, this);
    }

    protected abstract Optional<Either<Object, Expression>> innerGet(String key);
}
