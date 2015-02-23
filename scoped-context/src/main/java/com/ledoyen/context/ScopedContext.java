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

    /**
     * @return a new {@link ScopedContext}, root of the context tree. This root is not thread safe.
     * @see {@link #createThreadSafe}
     */
    static ScopedContext create() {
        return new RootScopedContext(false);
    }

    /**
     * @return a new <b>Thread Safe</b> {@link ScopedContext}, root of the context tree.
     * @see {@link #create}
     */
    static ScopedContext createThreadSafe() {
        return new RootScopedContext(true);
    }

    /**
     * @return a new {@link ScopedContext} extending the current one.
     */
    ScopedContext createChild();

    /**
     * @return a new {@link ScopedContext} extending the current one and shielded from Parent retrieval.
     */
    ScopedContext shield();

    /**
     * @return the parent if any, empty() otherwise
     */
    Optional<ScopedContext> getParent();
}
