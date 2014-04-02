package com.ledoyen.parser.expression;

import com.ledoyen.parser.api.Expression;

public class ArithmeticExpression extends BinaryExpression {
	public ArithmeticExpression(Expression left, char cOp, Expression right) {
		super(left, cOp, right);
	}
}
