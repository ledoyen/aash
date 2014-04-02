package com.ledoyen.parser;

import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.ledoyen.parser.api.Expression;
import com.ledoyen.parser.expression.DottedExpression;
import com.ledoyen.parser.visitor.ToStringVisitor;

public class ExpressionParserTest {

	@Test
	public void testParseDottedExpression() throws IOException {
		String expression = "userTruc.maTable[variable.attribute == false | (toto > 18 & userTruc[*].family in [\"toto\", \"titi\"])].tableau[length(texte)].name";
		DottedExpression parsed = ExpressionParser.parseDotted(expression);
		System.out.println(parsed);
	}

	@Test
	public void testParseArithmeticExpression() throws IOException {
		String expression = "date(variable.attribute) == false | 1 == 0 | false";
		Expression parsed = ExpressionParser.parseArithmetic(expression);
		System.out.println(parsed);
	}

	@Test
	public void testToStringVisitor() throws IOException {
		String expression = "userTruc.maTable[variable.attribute == false | (toto > 18 & userTruc[*].family in [\"toto\", \"titi\"])].tableau[length(texte)].name";
		DottedExpression parsed = ExpressionParser.parseDotted(expression);
		ToStringVisitor v = new ToStringVisitor();
		parsed.accept(v);
		assertThat(v.toString(), CoreMatchers.equalTo(parsed.toString()));
	}
}
