package com.ledoyen.aash.tool;

public class EitherLeftMapper<U, V> implements Function<U, Either<U, V>>{

	public Either<U, V> apply(U input) {
		return Either.Left(input);
	}
}
