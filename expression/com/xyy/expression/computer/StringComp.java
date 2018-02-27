package com.xyy.expression.computer;

import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;
/**
 * 字符串
 * @author evan
 *
 */
public class StringComp extends TreatComp {
	public StringComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_STRING) {
			stOutput.push(new OperatorData(String.class, token.getTokenStr()));
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

}
