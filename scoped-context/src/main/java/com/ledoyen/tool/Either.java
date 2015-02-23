package com.ledoyen.tool;

/**
 * @author L.LEDOYEN
 */
public class Either<U, V> {
    private final U left;
    private final V right;

    private Either(U left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <U, V> Either<U, V> left(U left) {
        Preconditions.checkArgument(left != null, "left must not be null");
        return new Either<>(left, null);
    }

    public static <U, V> Either<U, V> right(V right) {
        Preconditions.checkArgument(right != null, "right must not be null");
        return new Either<>(null, right);
    }

    public boolean isLeft() {
        return left != null;
    }

    public U left() {
        return left;
    }

    public V right() {
        return right;
    }
}
