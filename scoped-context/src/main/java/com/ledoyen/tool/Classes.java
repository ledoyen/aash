package com.ledoyen.tool;

/**
 * @author L.LEDOYEN
 */
public final class Classes {

    private Classes() {
    }

    public static boolean isClassPresent(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
