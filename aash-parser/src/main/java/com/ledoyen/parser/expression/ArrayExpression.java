package com.ledoyen.parser.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ledoyen.parser.api.CompositeExpression;
import com.ledoyen.parser.api.Expression;
import com.ledoyen.parser.visitor.Visitor;

public class ArrayExpression extends CompositeExpression {
	private List<Expression> _items = new ArrayList<Expression>();

	public ArrayExpression() {
	}

	public void addItem(Expression expr) {
		_items.add(expr);
	}

	public List<Expression> getItems() {
		return Collections.unmodifiableList(_items);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		boolean bFirst = true;
		for (Expression item: _items) {
			if (bFirst) {
				bFirst = false;
			} else {
				sb.append(", ");
			}
			sb.append(item.toString());
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
