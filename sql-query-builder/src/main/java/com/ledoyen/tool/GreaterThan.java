package com.ledoyen.tool;

import com.google.common.base.Predicate;

public class GreaterThan implements Predicate<Integer>{

	private int threshold;

	public GreaterThan(int threshold) {
		this.threshold = threshold;
	}

	public boolean apply(Integer input) {
		return input > threshold;
	}
}
