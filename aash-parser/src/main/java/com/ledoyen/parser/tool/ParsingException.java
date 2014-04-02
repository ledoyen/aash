package com.ledoyen.parser.tool;

import java.io.IOException;

public class ParsingException extends IOException {
	private static final long serialVersionUID = 1L;

	public ParsingException(ParsingTools stream, String sMessage) {
		super(stream.getLineNumber() + "," + stream.getColNumber() + ": " + sMessage);
	}
}
