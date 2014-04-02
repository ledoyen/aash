# Aash Parser

An expression parser, for simple stuff without using ANTLR.

## Bean Path expressions

To parse a bean path expression, use the following code :

```java
String expression = "userTruc.maTable[variable.attribute == false | (toto > 18 & userTruc[*].family in [\"toto\", \"titi\"])].tableau[length(texte)].name";
DottedExpression parsedExpression = ExpressionParser.parseDotted(expression);
System.out.println(parsedExpression);
```

```bash
userTruc.maTable[((variable.attribute = false) | ((toto > 18.0) & (userTruc[*].family E ["toto", "titi"])))].tableau[length(texte)].name
```

## Arithmetic expressions

For arithmetic expression, use the following code :

```java
String expression = "date(variable.attribute) == false | 1 == 0 | false";
DottedExpression parsedExpression = ExpressionParser.parseArithmetic(expression);
System.out.println(parsedExpression);
```

```bash
(((date(variable.attribute) = false) | (1.0 = 0.0)) | false)
```

## Visit the expression tree

The parsing resulting tree can be browsed using some implementation of `com.ledoyen.parser.visitor.Visitor` as shown by the following example :

```java
ToStringVisitor v = new ToStringVisitor();
parsedExpression.accept(v);
System.out.println(v.toString());
```

The Visitor implementation do have the responsability to call `accept` on children where there is.

This way you can do stuff before and after this call, for example :

```java
public void visit(BinaryExpression binaryExpression) {
	sb.append('(');
	binaryExpression.getLeft().accept(this);
	sb.append(' ');
	sb.append(binaryExpression.getOperator());
	sb.append(' ');
	binaryExpression.getRight().accept(this);
	sb.append(')');
}
```
