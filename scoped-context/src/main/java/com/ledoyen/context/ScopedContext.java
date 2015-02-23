package com.ledoyen.context;

import java.util.Optional;

/**
 * Representation of a Node in the tree Context.
 *
 * @author L.LEDOYEN
 */
public interface ScopedContext {

    ScopedContext put(String key, Object value);

    ScopedContext putExpression(String key, String expression);

    Optional<Object> get(String key);

    boolean isThreadSafe();

    static ScopedContext create() {
        return new RootScopedContext(false);
    }

    ScopedContext createChild();

    ScopedContext shield();

    Optional<ScopedContext> getParent();
}
