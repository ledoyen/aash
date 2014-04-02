package com.ledoyen.parser.expression;

import com.ledoyen.parser.api.CompositeExpression;
import com.ledoyen.parser.api.Expression;
import com.ledoyen.parser.visitor.Visitor;

public class BinaryExpression extends CompositeExpression {
	private char _cOp;
	private Expression _left;
	private Expression _right;

	public BinaryExpression(Expression left, char cOp, Expression right) {
		_left = left;
		_cOp = cOp;
		_right = right;
	}

	public char getOperator() { return _cOp; }
	public Expression getLeft() { return _left; }
	public Expression getRight() { return _right; }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(_left.toString());
		sb.append(' ');
		sb.append(_cOp);
		sb.append(' ');
		sb.append(_right.toString());
		sb.append(')');
		return sb.toString();
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
