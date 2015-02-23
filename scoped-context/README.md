# Scoped Context

__Requires Java 8__

This is a __tree structured context__ supporting storage of values or expressions (currently only [spring-expression](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html) is supported).

Use this when you need scopes for your variables.

Parents context are immutable from child scope.

```java
ScopedContext root = ScopedContext.create().put("key", "value");
ScopedContext firstLevel = root.createChild();

firstLevel.put("key", "overrided");

firstLevel.get("key"); // evaluates to "overrided"
root.get("key"); // evaluates to "value"
```

## Expression resolution

Expressions are stored compiled, but evaluated only when get.

```java
ScopedContext root = ScopedContext.create().put("key", "value").putExpression("myExpr", "#key");
ScopedContext firstLevel = root.createChild().put("key", overrided");

firstLevel.get("myExpr"); // evaluates to "overrided"
root.get("myExpr"); // evaluates to "value"
```

## Navigation from child to parent and shielding

Navigation from child to parent is possible in order to be able to put data to a more global scope.

```java
ScopedContext root = ScopedContext.create().put("key", "value");
ScopedContext firstLevel = root.createChild().put("key", overrided");

firstLevel.getParent().get().get("key"); // evaluates to "value"
```

However in certain cases, global scopes needs to be shielded from this kind of navigation (environmental scope for example).

```java
ScopedContext root = ScopedContext.create().put("key", "value");
ScopedContext firstLevel = root.shield().put("key", overrided");

firstLevel.getParent(); // evaluates to Optional.empty()
```
