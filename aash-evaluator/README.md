# Aash Evaluator

__Requires Jdk7__

This is a useful wrapper around Janino compiler, to evaluate java expressions.

## Core API

To evaluate an expression, you must first compile it, then evaluate it (optionally against parameter values).

```java
CompiledExpression<Integer> expression = ExpressionCompiler.build().compile("1 + 3", Integer.class);
Integer value = expression.evaluate(); // returns 4
```

```java
CompiledExpression<Boolean> expression = ExpressionCompiler.build().compile("var1 == 4", new String[]{"var1"}, new Class<?>[]{Integer.class}, Boolean.class);
Boolean value = expression.evaluate(4); // returns true
value = result.evaluate(3); // returns false
```

### Use custom functions

You can use custom static functions :

```java
CompiledExpression<Double> expression = ExpressionCompiler.build(Math.class).compile("abs(-7)", Double.class);
Double value = expression.evaluate(); // returns 7.0
```

## Service API

The service API provide some light eco-system for expression evaluation and chaining.

```java
EvaluatorService service = EvaluatorServiceBuilder.newBuilder()
				.setStore(valueStore)
				.setStaticClasses(StringUtils.class, Math.class)
				.build();
				
service.processEvaluations(
				Evaluation.build("toto", "4", Integer.class),
				Evaluation.build("titi", "toto + 3", new String[] { "toto" }, new Class<?>[] { Long.class }, Long.class),
				// Function from Math.class
				Evaluation.build("tutu", "max(toto, 5)", new String[] { "toto" }, new Class<?>[] { Integer.class }, Double.class),
				// Function from StringUtils.class
				Evaluation.build("text1", "repeat('1', 2)", String.class),
				Evaluation.build("text2", "text1 + \"22\"", new String[] { "text1" }, new Class<?>[] { String.class }, String.class),
				Evaluation.build("textToLong", "text2", new String[] { "text2" }, new Class<?>[] { Long.class }, Long.class)
				);
```

The value store will then contain :
```java
"toto"		-> 4
"titi"		-> 7l
"tutu"		-> 5d
"text1"		-> "11"
"text2"		-> "1122"
textToLong	-> 1122l
```

### Specific ClassLoader

When the Evaluator is used in a complex environment (as in a JEE server), the default ClassLoader (Loading ExpressionCompiler.class) can be different than the one loading custom function Classes.
In that case, it can be necessary to specify the ClassLoader to use for custom function Classes loading.

```java
ExpressionCompiler compiler = ExpressionCompiler.build(StringUtils.class.getClassLoader(), StringUtils.class, Math.class);
compiler.compile(...
```
