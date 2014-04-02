package com.ledoyen.parser.visitor;

import com.ledoyen.parser.api.Expression;
import com.ledoyen.parser.expression.ArrayExpression;
import com.ledoyen.parser.expression.BinaryExpression;
import com.ledoyen.parser.expression.BooleanLiteral;
import com.ledoyen.parser.expression.DottedExpression;
import com.ledoyen.parser.expression.DoubleLiteral;
import com.ledoyen.parser.expression.FunctionCallExpression;
import com.ledoyen.parser.expression.NotExpression;
import com.ledoyen.parser.expression.StarLiteral;
import com.ledoyen.parser.expression.StringLiteral;

public class ToStringVisitor implements Visitor {

	private StringBuilder sb = new StringBuilder();

	public void visit(BooleanLiteral booleanLiteral) {
		sb.append((booleanLiteral.getValue()) ? "true" : "false");
	}

	public void visit(DoubleLiteral doubleLiteral) {
		sb.append(String.valueOf(doubleLiteral.getValue()));
	}

	public void visit(StarLiteral starLiteral) {
		sb.append("*");
	}

	public void visit(StringLiteral stringLiteral) {
		sb.append('"');
		for(char c: stringLiteral.getValue().toCharArray()) {
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
	}

	public void visit(DottedExpression e) {
		sb.append(e.getVariableName());
		if (e.getIndex() != null) {
			sb.append('[');
			e.getIndex().accept(this);
			sb.append(']');
		}
		if (e.getNext() != null) {
			sb.append('.');
			e.getNext().accept(this);
		}
	}

	public void visit(NotExpression notExpression) {
		sb.append("!");
		notExpression.getExpression().accept(this);
	}

	public void visit(ArrayExpression arrayExpression) {
		sb.append('[');
		boolean bFirst = true;
		for (Expression item: arrayExpression.getItems()) {
			if (bFirst) {
				bFirst = false;
			} else {
				sb.append(", ");
			}
			item.accept(this);
		}
		sb.append(']');
	}

	public void visit(BinaryExpression binaryExpression) {
		sb.append('(');
		binaryExpression.getLeft().accept(this);
		sb.append(' ');
		sb.append(binaryExpression.getOperator());
		sb.append(' ');
		binaryExpression.getRight().accept(this);
		sb.append(')');
	}

	public void visit(FunctionCallExpression functionCallExpression) {
		sb.append(functionCallExpression.getFunctionName());
		sb.append('(');
		boolean bFirst = true;
		for (Expression item: functionCallExpression.getParameters()) {
			if (bFirst) {
				bFirst = false;
			} else {
				sb.append(", ");
			}
			item.accept(this);
		}
		sb.append(')');
	}

	public void reset() {
		sb = new StringBuilder();
	}

	public String toString() {
		return sb.toString();
	}
}
