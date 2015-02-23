package com.ledoyen.context;

import com.ledoyen.tool.Either;

import java.util.Optional;

/**
 * Context shielding parent from retrieval.<br>
 * Variables and expressions are available, but accessing the parent is not.
 *
 * @author L.LEDOYEN
 */
public class ShieldScopedContext extends AbstractScopedContext {

    final AbstractScopedContext parent;

    public ShieldScopedContext(AbstractScopedContext parent) {
        super(parent.isThreadSafe());
        this.parent = parent;
    }

    public Optional<ScopedContext> getParent() {
        return Optional.empty();
    }

    protected Optional<Either<Object, Expression>> innerGet(String key) {
        return parent.innerGet(key);
    }
}
