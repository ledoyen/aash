package com.ledoyen.parser.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ledoyen.parser.api.CompositeExpression;
import com.ledoyen.parser.api.Expression;
import com.ledoyen.parser.visitor.Visitor;

public class FunctionCallExpression extends CompositeExpression {
	private String _sFunctionName;
	private List<Expression> _parameters = new ArrayList<Expression>();

	public FunctionCallExpression(String sFunctionName) {
		_sFunctionName = sFunctionName;
	}

	public void addParameter(Expression expr) {
		_parameters.add(expr);
	}

	public String getFunctionName() {
		return _sFunctionName;
	}

	public List<Expression> getParameters() {
		return Collections.unmodifiableList(_parameters);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(_sFunctionName);
		sb.append('(');
		boolean bFirst = true;
		for (Expression item: _parameters) {
			if (bFirst) {
				bFirst = false;
			} else {
				sb.append(", ");
			}
			sb.append(item.toString());
		}
		sb.append(')');
		return sb.toString();
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
