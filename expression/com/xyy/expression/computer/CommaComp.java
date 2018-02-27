package com.xyy.expression.computer;

import java.util.Stack;

import com.xyy.expression.Comma;
import com.xyy.expression.Context;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;
/**
 * 逗号计算符
 * @author evan
 *
 */
public class CommaComp extends TreatComp {
	public CommaComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals(",")) {
			stOutput.push(new OperatorData(Comma.class, token.getTokenStr()));
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

}
