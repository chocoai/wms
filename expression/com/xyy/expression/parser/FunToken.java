package com.xyy.expression.parser;

import java.util.Stack;

import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatToken;

/**
 * 函数解析器
 * @author evan
 *
 */
public class FunToken extends TreatToken {

	public FunToken(Parser context) {
		super(context);
	}

	@Override
	public void treat(Token token, Stack<Token> stTemp, Stack<Token> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_FUN) {
			stTemp.push(token);
			stOutput.push((new Token("rem begin_args", TokenType.TOKEN_TYPE_REM)));
		} else {
			this.next.treat(token, stTemp, stOutput);
		}
	}

}
