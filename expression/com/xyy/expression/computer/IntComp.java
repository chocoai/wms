package com.xyy.expression.computer;

import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;

/**
 * 整形计算
 * @author evan
 *
 */
public class IntComp extends TreatComp {
	public IntComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_INT) {
			stOutput.push(new OperatorData(Integer.class, Integer.parseInt(token
					.getTokenStr())));
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

}
