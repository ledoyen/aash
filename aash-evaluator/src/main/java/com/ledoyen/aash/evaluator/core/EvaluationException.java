package com.ledoyen.aash.evaluator.core;

/**
 * Checked Exception that may occur during evaluation.
 * 
 * @author L.LEDOYEN
 */
public class EvaluationException extends Exception {

	private static final long serialVersionUID = 6103386937681961222L;

	public EvaluationException(String message) {
		super(message);
	}

	public EvaluationException(Exception e) {
		super(e);
	}

	public EvaluationException(String message, Exception e) {
		super(message, e);
	}
}
