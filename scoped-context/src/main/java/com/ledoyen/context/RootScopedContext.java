package com.ledoyen.context;

import com.ledoyen.tool.Either;

import java.util.Optional;

/**
 * @author L.LEDOYEN
 */
class RootScopedContext extends AbstractScopedContext {

    RootScopedContext(boolean threadSafe) {
        super(threadSafe);
    }

    public Optional<ScopedContext> getParent() {
        return Optional.empty();
    }

    protected Optional<Either<Object, Expression>> innerGet(String key) {
        if (values.containsKey(key)) {
            return Optional.of(values.get(key));
        } else {
            return Optional.empty();
        }
    }
}
