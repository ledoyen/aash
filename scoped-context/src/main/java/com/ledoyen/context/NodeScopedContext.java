package com.ledoyen.context;

import com.ledoyen.tool.Either;

import java.util.Optional;

/**
 * @author L.LEDOYEN
 */
public class NodeScopedContext extends AbstractScopedContext {

    private final AbstractScopedContext parent;

    NodeScopedContext(AbstractScopedContext parent) {
        super(parent.isThreadSafe());
        this.parent = parent;
    }

    public Optional<ScopedContext> getParent() {
        return Optional.of(parent);
    }

    protected Optional<Either<Object, Expression>> innerGet(String key) {
        if (values.containsKey(key)) {
            return Optional.of(values.get(key));
        } else {
            return parent.innerGet(key);
        }
    }
}
