package com.ledoyen.parser.api;

import com.ledoyen.parser.visitor.Visitor;

public abstract class Expression {
	public abstract String toString();

	public abstract void accept(Visitor v);
}
