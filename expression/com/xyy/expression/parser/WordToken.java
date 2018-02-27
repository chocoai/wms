package com.xyy.expression.parser;

import java.util.Stack;

import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatToken;
/**
 * 字符Token
 * @author evan
 *
 */
public class WordToken extends TreatToken {
	public WordToken(Parser context) {
		super(context);
	}

	@Override
	public void treat(Token token, Stack<Token> stTemp, Stack<Token> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_CHAR
				|| token.getTokenType() == TokenType.TOKEN_TYPE_EXPR
				|| token.getTokenType() == TokenType.TOKEN_TYPE_FLOAT
				|| token.getTokenType() == TokenType.TOKEN_TYPE_INT
				|| token.getTokenType() == TokenType.TOKEN_TYPE_OTHER
				|| token.getTokenType() == TokenType.TOKEN_TYPE_STRING
				|| token.getTokenType() == TokenType.TOKEN_TYPE_VAR
				|| token.getTokenType()==TokenType.TOKEN_TYPE_CELL_REF) {
			stOutput.push(token);
		} else {
			this.next.treat(token, stTemp, stOutput);
		}

	}

}
