package com.ledoyen.tool;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

/**
 * @author L.LEDOYEN
 */
public class PreconditionsTest {

    @Test
    public void false_condition_should_raise_exception() {
        Exception e = null;
        try {
            Preconditions.checkArgument(false, "message");
        } catch (Exception ex) {
            e = ex;
        }

        assertThat(e, Matchers.notNullValue());
        assertThat(e, Matchers.instanceOf(IllegalArgumentException.class));
        assertThat(e.getMessage(), Matchers.equalTo("message"));
    }

    @Test
    public void true_condition_should_do_nothing() {
        Preconditions.checkArgument(true, "message");
    }
}
