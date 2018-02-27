package com.xyy.expression.parser;

import java.util.Stack;

import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatToken;
/**
 * 其他Token解析器
 * @author evan
 *
 */
public class OtherToken extends TreatToken {

	public OtherToken(Parser context) {
		super(context);
	}
   
	@Override
	public void treat(Token token, Stack<Token> stTemp, Stack<Token> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OTHER) {
			throw new Exception("Token error.");
		} else {
			this.next.treat(token, stTemp, stOutput);
		}
	}

}
