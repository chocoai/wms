package com.xyy.expression;

import java.util.Map;

import com.xyy.expression.parser.ParserHandler;

/**
 * Token对象
 * 
 * @author Evan
 * 
 */
public class Token {
	private String tokenStr;
	private TokenType tokenType;

	public Token(String tokenStr, TokenType tokenType) {
		super();
		this.tokenStr = tokenStr;
		this.tokenType = tokenType;

	}

	public Operator getOperator() {
		Map<String, Operator> maps = new ParserHandler().buildTokenProcessor();
		if (maps.containsKey(this.tokenStr)) {
			return maps.get(this.tokenStr);
		}
		return null;
	}

	public String getTokenStr() {
		return tokenStr;
	}

	public void setTokenStr(String tokenStr) {
		this.tokenStr = tokenStr;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

}
