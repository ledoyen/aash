package com.ledoyen.parser.expression;

import com.ledoyen.parser.api.Expression;

public class BooleanExpression extends BinaryExpression {
	public BooleanExpression(Expression left, char cOp, Expression right) {
		super(left, cOp, right);
	}
}
