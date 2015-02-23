package com.ledoyen.context;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertThat;

/**
 * @author L.LEDOYEN
 */
public class ScopedContextTest {

    @Test
    public void scoped_context_creation_should_return_valid_mutable_context() {
        ScopedContext context = ScopedContext.create();

        assertThat(context, Matchers.notNullValue());
        context.put("key", "value");
        assertThat(context.get("key"), Matchers.equalTo(Optional.of("value")));

        context.put("key", "value2");
        assertThat(context.get("key"), Matchers.equalTo(Optional.of("value2")));
    }

    @Test
    public void get_absent_key_should_return_empty() {
        assertThat(ScopedContext.create().get("key"), Matchers.equalTo(Optional.empty()));
    }

    @Test
    public void expressions_should_resolve_when_get() {
        ScopedContext context = ScopedContext.create();

        context.put("key1", "value1");
        context.putExpression("key2", "#key1");
        assertThat(context.get("key2"), Matchers.equalTo(Optional.of("value1")));

        context.put("key1", "value2");
        assertThat(context.get("key2"), Matchers.equalTo(Optional.of("value2")));
    }

    @Test
    public void child_put_should_not_affect_parent() {
        ScopedContext root = ScopedContext.create();
        root.put("key", "value");
        root.putExpression("expr", "#key");

        ScopedContext child = root.createChild();

        child.put("key", "overrided");

        assertThat(root.get("key"), Matchers.equalTo(Optional.of("value")));
        assertThat(root.get("expr"), Matchers.equalTo(Optional.of("value")));

        assertThat(child.get("key"), Matchers.equalTo(Optional.of("overrided")));
        assertThat(child.get("expr"), Matchers.equalTo(Optional.of("overrided")));
    }

    @Test
    public void immutable_child_should_have_same_read_only_operations() {
        ScopedContext context = ScopedContext.create().put("key", "value").putExpression("expr", "#key").shield();

        assertThat(context.get("expr"), Matchers.equalTo(Optional.of("value")));
    }

    @Test
    public void root_should_not_have_parent() {
        assertThat(ScopedContext.create().getParent(), Matchers.equalTo(Optional.empty()));
    }

    @Test
    public void shield_child_should_have_parent_values_but_do_not_permit_parent_retrieval() {
        ScopedContext immutable = ScopedContext.create().put("key", "value").shield();

        assertThat(immutable.get("key"), Matchers.equalTo(Optional.of("value")));
        assertThat(immutable.getParent(), Matchers.equalTo(Optional.empty()));
    }

    @Test
    public void normal_child_should_have_parent_values_and_permit_parent_retrieval() {
        ScopedContext root = ScopedContext.create().put("key", "value");
        ScopedContext child = root.createChild();

        assertThat(child.get("key"), Matchers.equalTo(Optional.of("value")));
        assertThat(child.getParent(), Matchers.equalTo(Optional.of(root)));
    }

    @Test
    public void expression_referencing_absent_key_should_return_null() {
        assertThat(ScopedContext.create().putExpression("expr", "#key").get("expr"), Matchers.equalTo(Optional.empty()));
    }
}
