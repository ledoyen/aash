package com.ledoyen.tool;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

/**
 * @author L.LEDOYEN
 */
public class ClassesTest {

    @Test
    public void self_class_should_be_found_in_cp() {
        assertThat(Classes.isClassPresent(this.getClass().getName()), Matchers.equalTo(true));
    }

    @Test
    public void absent_class_should_not_be_found_in_cp() {
        assertThat(Classes.isClassPresent("some.AbsentClass"), Matchers.equalTo(false));
    }
}
