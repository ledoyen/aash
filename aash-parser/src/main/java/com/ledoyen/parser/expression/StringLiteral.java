package com.ledoyen.parser.expression;

import com.ledoyen.parser.api.LeafExpression;
import com.ledoyen.parser.visitor.Visitor;

public class StringLiteral extends LeafExpression {
	private String _sText;

	public StringLiteral(String sText) {
		_sText = sText;
	}

	public String getValue() {
		return _sText;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		for(char c: _sText.toCharArray()) {
			if (c == '\\') {
				sb.append("\\\\");
			} else if (c == '\r') {
				sb.append("\\r");
			} else if (c == '\n') {
				sb.append("\\n");
			} else if (c == '\t') {
				sb.append("\\t");
			} else {
				sb.append(c);
			}
		}
		sb.append('"');
		return sb.toString();
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
