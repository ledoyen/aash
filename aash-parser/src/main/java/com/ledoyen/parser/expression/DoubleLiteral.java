package com.ledoyen.parser.expression;

import com.ledoyen.parser.api.LeafExpression;
import com.ledoyen.parser.visitor.Visitor;

public class DoubleLiteral extends LeafExpression {
	private double _dValue;

	public DoubleLiteral(double dValue) {
		_dValue = dValue;
	}

	public double getValue() {
		return _dValue;
	}

	@Override
	public String toString() {
		return String.valueOf(_dValue);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
