package com.ledoyen.parser;

import java.io.IOException;

import com.ledoyen.parser.api.Expression;
import com.ledoyen.parser.expression.ArithmeticExpression;
import com.ledoyen.parser.expression.ArrayExpression;
import com.ledoyen.parser.expression.BooleanExpression;
import com.ledoyen.parser.expression.BooleanLiteral;
import com.ledoyen.parser.expression.DottedExpression;
import com.ledoyen.parser.expression.DoubleLiteral;
import com.ledoyen.parser.expression.FunctionCallExpression;
import com.ledoyen.parser.expression.NotExpression;
import com.ledoyen.parser.expression.StarLiteral;
import com.ledoyen.parser.expression.StringLiteral;
import com.ledoyen.parser.tool.ParsingTools;

public class ExpressionParser {

	private static DottedExpression parseDottedExpression(ParsingTools stream) throws IOException {
		stream.ignoreBlanks();
		String sVarName = stream.readIdentifier();
		if (sVarName == null) {
			throw new IOException(stream.syntaxError("dotted expression expected"));
		}
		DottedExpression expr = new DottedExpression(sVarName);
		stream.ignoreBlanks();
		if (stream.isEqualTo('[')) {
			Expression indexExpr = parseExpression(stream);
			stream.ignoreBlanks();
			if (!stream.isEqualTo(']')) {
				throw new IOException(stream.syntaxError("']' expected"));
			}
			expr.setIndex(indexExpr);
		}
		stream.ignoreBlanks();
		if (stream.isEqualTo('.')) {
			DottedExpression nextExpr = parseDottedExpression(stream);
			expr.setNext(nextExpr);
		}
		return expr;
	}

	private static Expression parseExpression(ParsingTools stream) throws IOException {
		return parseBooleanExpression(stream);
	}

	private static Expression parseBooleanExpression(ParsingTools stream) throws IOException {
		Expression expr = parseComparisonExpression(stream);
		stream.ignoreBlanks();
		while (stream.lookAhead('&') || stream.lookAhead('|')) {
			char cOp = (char) stream.readChar();
			Expression right = parseComparisonExpression(stream);
			expr = new BooleanExpression(expr, cOp, right);
		}
		return expr;
	}

	private static Expression parseComparisonExpression(ParsingTools stream) throws IOException {
		Expression expr = parseArithmeticExpression(stream);
		stream.ignoreBlanks();
		char cOp = '\0';
		if (stream.isEqualTo("<=")) {
			cOp = 'i';
		} else if (stream.isEqualTo(">=")) {
			cOp = 's';
		} else if (stream.isEqualTo("==")) {
			cOp = '=';
		} else if (stream.isEqualTo("!=")) {
			cOp = '!';
		} else if (stream.isEqualTo("<")) {
			cOp = '<';
		} else if (stream.isEqualTo(">")) {
			cOp = '>';
		} else if (stream.isEqualToIdentifier("in")) {
			cOp = 'E';
		}
		if (cOp != '\0') {
			Expression right = parseArithmeticExpression(stream);
			expr = new ArithmeticExpression(expr, cOp, right);
		}
		return expr;
	}

	private static Expression parseArithmeticExpression(ParsingTools stream) throws IOException {
		return parseSumExpression(stream);
	}

	private static Expression parseSumExpression(ParsingTools stream) throws IOException {
		Expression expr = parseMultExpression(stream);
		stream.ignoreBlanks();
		while (stream.lookAhead('+') || stream.lookAhead('-')) {
			char cOp = (char) stream.readChar();
			Expression right = parseMultExpression(stream);
			expr = new ArithmeticExpression(expr, cOp, right);
		}
		return expr;
	}

	private static Expression parseMultExpression(ParsingTools stream) throws IOException {
		Expression expr = parseLiteralExpression(stream);
		stream.ignoreBlanks();
		while (stream.lookAhead('*') || stream.lookAhead('/')) {
			char cOp = (char) stream.readChar();
			Expression right = parseLiteralExpression(stream);
			expr = new ArithmeticExpression(expr, cOp, right);
		}
		return expr;
	}

	private static Expression parseLiteralExpression(ParsingTools stream) throws IOException {
		stream.ignoreBlanks();
		if (stream.isEqualTo('(')) {
			Expression expr = parseExpression(stream);
			stream.ignoreBlanks();
			if (!stream.isEqualTo(')')) {
				throw new IOException(stream.syntaxError("')' expected"));
			}
			return expr;
		} else if (stream.isEqualTo('!')) {
			Expression expr = parseLiteralExpression(stream);
			return new NotExpression(expr);
		} else if (stream.isEqualTo('*')) {
			return new StarLiteral();
		} else if (stream.isEqualTo('[')) {
			ArrayExpression expr = new ArrayExpression();
			stream.ignoreBlanks();
			if (!stream.isEqualTo(']')) {
				Expression item = parseExpression(stream);
				expr.addItem(item);
				stream.ignoreBlanks();
				while (stream.isEqualTo(',')) {
					item = parseExpression(stream);
					expr.addItem(item);
					stream.ignoreBlanks();
				}
				if (!stream.isEqualTo(']')) {
					throw new IOException(stream.syntaxError("']' expected"));
				}
			}
			return expr;
		} else {
			String sText = stream.readString();
			if (sText != null) {
				return new StringLiteral(sText);
			} else {
				Double dValue = stream.readDouble();
				if (dValue != null) {
					return new DoubleLiteral(dValue);
				} else {
					DottedExpression dotted = parseDottedExpression(stream);
					if (dotted.getIndex() == null && dotted.getNext() == null) {
						if (dotted.getVariableName().equals("true") || dotted.getVariableName().equals("false")) {
							return new BooleanLiteral(dotted.getVariableName());
						}
					}
					if (stream.isEqualTo('(')) {
						return parseIntermediateFunctionCall(stream, dotted.getVariableName());
					}
					return dotted;
				}
			}
		}
	}

	private static Expression parseIntermediateFunctionCall(ParsingTools stream, String sFunctionName) throws IOException {
		FunctionCallExpression expr = new FunctionCallExpression(sFunctionName);
		stream.ignoreBlanks();
		if (!stream.isEqualTo(')')) {
			Expression param = parseExpression(stream);
			expr.addParameter(param);
			stream.ignoreBlanks();
			while (stream.isEqualTo(',')) {
				param = parseExpression(stream);
				expr.addParameter(param);
				stream.ignoreBlanks();
			}
			if (!stream.isEqualTo(')')) {
				throw new IOException(stream.syntaxError("')' expected"));
			}
		}
		return expr;
	}

	public static DottedExpression parseDotted(String expression) throws IOException {
		return parseDottedExpression(new ParsingTools(expression));
	}

	public static Expression parseArithmetic(String expression) throws IOException {
		return parseExpression(new ParsingTools(expression));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String sText = "userTruc.maTable[variable.attribute == false | (toto > 18 & userTruc[*].family in [\"toto\", \"titi\"])].tableau[length(texte)].name";
		ParsingTools stream = new ParsingTools(sText);
		try {
			System.out.println(sText);
			DottedExpression expr = parseDottedExpression(stream);
			System.out.println(expr.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
