package com.ledoyen.tool;

/**
 * @author L.LEDOYEN
 */
public final class Preconditions {

    private Preconditions() {
    }

    public static void checkArgument(boolean exp, String message) {
        if (!exp) {
            throw new IllegalArgumentException(message);
        }
    }
}
