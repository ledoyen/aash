package com.ledoyen.parser.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class ParsingTools {
	private BufferedReader _stream;
	private int _iLineNumber = 1;
	private int _iColNumber = 1;
	private int _iLastChar = -1;
	private StringBuffer _sImplicitCopy = null;
	
	public ParsingTools(BufferedReader stream) {
		_stream = stream;
	}

	public ParsingTools(String sText) {
		_stream = new BufferedReader(new StringReader(sText));
	}

	public int getLineNumber() { return _iLineNumber; }
	public int getColNumber() { return _iColNumber; }

	public int readChar() throws IOException {
		_iLastChar = _stream.read();
		if (_iLastChar == '\n') {
			_iLineNumber++;
			_iColNumber = 1;
		} else {
			_iColNumber++;
		}
		if (_sImplicitCopy != null && _iLastChar > 0) {
			_sImplicitCopy.append((char) _iLastChar);
		}
		return _iLastChar;
	}

	public void goBack() throws IOException {
		_stream.reset();
		if (_iLastChar == '\n') {
			_iLineNumber--;
		} else {
			_iColNumber--;
		}
		_iLastChar = -1;
		if (_sImplicitCopy != null) {
			_sImplicitCopy.deleteCharAt(_sImplicitCopy.length() - 1);
		}
	}

	public String getImplicitCopy() {
		if (_sImplicitCopy == null) {
			return null;
		}
		return _sImplicitCopy.toString();
	}

	public void setImplicitCopyMode(boolean b) {
		if (b) {
			_sImplicitCopy = new StringBuffer();
		} else {
			_sImplicitCopy = null;
		}
	}

	public boolean ignoreBlanks() throws IOException {
		_stream.mark(1);
		int iChar = readChar();
		while (iChar == ' ' || iChar == '\t' || iChar == '\r' || iChar == '\n') {
			_stream.mark(1);
			iChar = readChar();
		}
		goBack();
		return (iChar != -1);
	}

	public String readNextChars(int iSize) throws IOException {
		StringBuilder sb = new StringBuilder();
		while (iSize != 0) {
			int c = readChar();
			if (c <= 0) {
				break;
			}
			sb.append((char) c);
			iSize--;
		}
		return sb.toString();
	}

	public String readIdentifier() throws IOException {
		String sIdentifier = null;
		_stream.mark(1);
		int iChar = readChar();
		if ((iChar < 'a' || iChar > 'z') && (iChar < 'A' || iChar > 'Z') && iChar != '_') {
			goBack();
		} else {
			sIdentifier = "";
			do {
				sIdentifier += String.valueOf((char) iChar);
				_stream.mark(1);
				iChar = readChar();
			} while ((iChar >= 'a' && iChar <= 'z') || (iChar >= 'A' && iChar <= 'Z') || (iChar >= '0' && iChar <= '9') || iChar == '_');
			goBack();
		}
		String text = ParsingToolsLight.convertLatin1ToUTF16(sIdentifier);
		return text;
	}

	public Double readDouble() throws IOException {
		String sNumber = null;
		_stream.mark(1);
		int iChar = readChar();
		if ((iChar < '0' || iChar > '9') && (iChar != '.') && (iChar != '-')) {
			goBack();
			return null;
		}
		sNumber = "";
		do {
			sNumber += String.valueOf((char) iChar);
			_stream.mark(1);
			iChar = readChar();
		} while ((iChar >= '0' && iChar <= '9') || (iChar == '.'));
		goBack();
		try {
			return new Double(sNumber);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public String readSimpleQuotedString() throws IOException {
		String sString = null;
		_stream.mark(1);
		int iChar = readChar();
		if (iChar != '\'') {
			goBack();
		} else {
			iChar = readChar();
			sString = "";
			while (iChar != '\'') {
				if (iChar == '\\') {
					iChar = readChar();
					if (iChar == 'n') iChar = '\n';
					else if (iChar == 'r') iChar = '\r';
					else if (iChar == 't') iChar = '\t';
					else if (iChar == '\'') iChar = '\'';
					else if (iChar == '"') iChar = '"';
				}
				sString += String.valueOf((char) iChar);
				iChar = readChar();
			}
		}
		String text = ParsingToolsLight.convertLatin1ToUTF16(sString);
		return text;
	}

	public String readString() throws IOException {
		String sString = null;
		_stream.mark(1);
		int iChar = readChar();
		if (iChar != '"') {
			goBack();
			if (iChar == '\'') {
				return readSimpleQuotedString();
			}
		} else {
			iChar = readChar();
			if (iChar == '"') {
				_stream.mark(1);
				iChar = readChar();
				if (iChar == '"') {
					// triple-quoted string
					return readRemainingTripleQuotedString();
				} else {
					// empty string
					goBack();
					return "";
				}
			}
			sString = "";
			while (iChar != '"') {
				if (iChar == '\\') {
					iChar = readChar();
					if (iChar == 'n') iChar = '\n';
					else if (iChar == 'r') iChar = '\r';
					else if (iChar == 't') iChar = '\t';
					else if (iChar == '\'') iChar = '\'';
					else if (iChar == '"') iChar = '"';
				}
				sString += String.valueOf((char) iChar);
				iChar = readChar();
			}
		}
		String text = ParsingToolsLight.convertLatin1ToUTF16(sString);
		return text;
	}

	public String readRemainingTripleQuotedString() throws IOException {
		String sString = "";
		boolean bContinue = true;
		int iChar = readChar();
		do {
			while (iChar != '"') {
				if (iChar == '\\') {
					iChar = readChar();
					if (iChar == 'n') iChar = '\n';
					else if (iChar == 'r') iChar = '\r';
					else if (iChar == 't') iChar = '\t';
					else if (iChar == '\'') iChar = '\'';
					else if (iChar == '"') iChar = '"';
				}
				sString += String.valueOf((char) iChar);
				iChar = readChar();
			}
			iChar = readChar();
			if (iChar == '"') {
				iChar = readChar();
				if (iChar == '"') {
					bContinue = false;
				} else {
					sString += "\"\"";
				}
			} else {
				sString += "\"";
			}
		} while (bContinue);
		_stream.mark(1);
		iChar = readChar();
		if (iChar == '"') {
			sString += "\"";
		} else {
			goBack();
		}
		String text = ParsingToolsLight.convertLatin1ToUTF16(sString);
		return text;
	}

	public boolean isEqualTo(char c) throws IOException {
		_stream.mark(1);
		int iChar = readChar();
		if (iChar != c) {
			goBack();
			return false;
		}
		return true;
	}

	public boolean isEqualTo(String sText) throws IOException {
		_stream.mark(sText.length());
		for (int i = 0; i < sText.length(); i++) {
			int iChar = readChar();
			if (iChar != sText.charAt(i)) {
				goBack();
				return false;
			}
		}
		return true;
	}

	public boolean isEqualToIdentifier(String sText) throws IOException {
		_stream.mark(sText.length()+1);
		for (int i = 0; i < sText.length(); i++) {
			int iChar = readChar();
			if (iChar != sText.charAt(i)) {
				goBack();
				return false;
			}
		}
		int iLastChar = readChar();
		if ((iLastChar >= '0' && iLastChar <= '9') || (iLastChar >= 'a' && iLastChar <= 'z') || (iLastChar >= 'A' && iLastChar <= 'Z') || iLastChar == '_') {
			goBack();
			return false;
		}
		return true;
	}

	public boolean lookAhead(char c) throws IOException {
		_stream.mark(1);
		int iChar = readChar();
		goBack();
		return (iChar == c);
	}

	public boolean lookAheadSequence(String sText) throws IOException {
		_stream.mark(sText.length());
		for (int i = 0; i < sText.length(); i++) {
			int iChar = readChar();
			if (iChar != sText.charAt(i)) {
				goBack();
				return false;
			}
		}
		goBack();
		return true;
	}

	public boolean lookAheadIdentifier(String sText) throws IOException {
		_stream.mark(sText.length()+1);
		for (int i = 0; i < sText.length(); i++) {
			int iChar = readChar();
			if (iChar != sText.charAt(i)) {
				goBack();
				return false;
			}
		}
		int iLastChar = readChar();
		goBack();
		if ((iLastChar >= '0' && iLastChar <= '9') || (iLastChar >= 'a' && iLastChar <= 'z') || (iLastChar >= 'A' && iLastChar <= 'Z') || iLastChar == '_') {
			return false;
		}
		return true;
	}

	public String readUptoChar(char c) throws IOException {
		String sText = "";
		_stream.mark(1);
		int iChar = readChar();
		while (iChar != c) {
			if (iChar == -1) return null;
			sText += String.valueOf((char) iChar);
			_stream.mark(1);
			iChar = readChar();
		}
		goBack();
		String text = ParsingToolsLight.convertLatin1ToUTF16(sText);
		return text;
	}

	public String syntaxError(String sErrorMessage) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(getLineNumber());
		sb.append(":");
		sb.append(getColNumber());
		sb.append("]");
		sb.append(sErrorMessage);
		return sb.toString();
	}
}
