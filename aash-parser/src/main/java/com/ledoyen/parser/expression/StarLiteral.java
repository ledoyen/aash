package com.ledoyen.parser.expression;

import com.ledoyen.parser.api.LeafExpression;
import com.ledoyen.parser.visitor.Visitor;

public class StarLiteral extends LeafExpression {

	public StarLiteral() {
	}

	@Override
	public String toString() {
		return "*";
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
