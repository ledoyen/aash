package com.ledoyen.parser.visitor;

import com.ledoyen.parser.expression.ArrayExpression;
import com.ledoyen.parser.expression.BinaryExpression;
import com.ledoyen.parser.expression.BooleanLiteral;
import com.ledoyen.parser.expression.DottedExpression;
import com.ledoyen.parser.expression.DoubleLiteral;
import com.ledoyen.parser.expression.FunctionCallExpression;
import com.ledoyen.parser.expression.NotExpression;
import com.ledoyen.parser.expression.StarLiteral;
import com.ledoyen.parser.expression.StringLiteral;

public interface Visitor {

	void visit(BooleanLiteral booleanLiteral);
	void visit(DoubleLiteral doubleLiteral);
	void visit(StarLiteral starLiteral);
	void visit(StringLiteral stringLiteral);

	void visit(DottedExpression e);
	void visit(NotExpression notExpression);

	void visit(ArrayExpression arrayExpression);
	void visit(BinaryExpression binaryExpression);
	void visit(FunctionCallExpression functionCallExpression);
}
