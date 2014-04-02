package com.ledoyen.parser.expression;

import com.ledoyen.parser.api.LeafExpression;
import com.ledoyen.parser.visitor.Visitor;

public class BooleanLiteral extends LeafExpression {
	private boolean _bValue;

	public BooleanLiteral(String sValue) {
		_bValue = sValue.equals("true");
	}

	public boolean getValue() {
		return _bValue;
	}

	@Override
	public String toString() {
		return (_bValue) ? "true" : "false";
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
