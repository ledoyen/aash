package com.ledoyen.parser.expression;

import com.ledoyen.parser.api.CompositeExpression;
import com.ledoyen.parser.api.Expression;
import com.ledoyen.parser.visitor.Visitor;

public class NotExpression extends CompositeExpression {
	private Expression _expr;

	public NotExpression(Expression expr) {
		_expr = expr;
	}

	public Expression getExpression() {
		return _expr;
	}

	@Override
	public String toString() {
		return "!" + _expr;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
