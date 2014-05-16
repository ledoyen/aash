package com.ledoyen.aash.tool;

public class Either<U, V> {

	private U left;

	private V right;

	private Either(U left, V right) {
		this.left = left;
		this.right = right;
	}

	public static <U, V> Either<U, V> Left(U left) {
		return new Either<U, V>(left, null);
	}

	public static <U, V> Either<U, V> Right(V right) {
		return new Either<U, V>(null, right);
	}

	public U left() {
		return left;
	}

	public V right() {
		return right;
	}

	public boolean isLeft() {
		return left != null;
	}
}
