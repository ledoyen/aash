package com.ledoyen.parser.expression;

import com.ledoyen.parser.api.CompositeExpression;
import com.ledoyen.parser.api.Expression;
import com.ledoyen.parser.visitor.Visitor;

public class DottedExpression extends CompositeExpression {
	private String _sVarName;
	private Expression _indexExpr;
	private DottedExpression _nextExpr;

	public DottedExpression(String sVarName) {
		_sVarName = sVarName;
	}

	public String getVariableName() {
		return _sVarName;
	}

	public Expression getIndex() {
		return _indexExpr;
	}

	public void setIndex(Expression indexExpr) {
		_indexExpr = indexExpr;
	}

	public Expression getNext() {
		return _nextExpr;
	}

	public void setNext(DottedExpression nextExpr) {
		_nextExpr = nextExpr;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(_sVarName);
		if (_indexExpr != null) {
			sb.append('[');
			sb.append(_indexExpr.toString());
			sb.append(']');
		}
		if (_nextExpr != null) {
			sb.append('.');
			sb.append(_nextExpr.toString());
		}
		return sb.toString();
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
