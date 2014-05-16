package com.ledoyen.aash.tool;

public class EitherRightMapper<U, V> implements Function<V, Either<U, V>>{

	public Either<U, V> apply(V input) {
		return Either.Right(input);
	}
}
