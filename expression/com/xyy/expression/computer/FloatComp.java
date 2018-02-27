package com.xyy.expression.computer;

import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;
/**
 * 浮点数计算
 * @author evan
 *
 */
public class FloatComp extends TreatComp {

	public FloatComp(Computer context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_FLOAT) {
			stOutput.push(new OperatorData(double.class, Double
					.parseDouble(token.getTokenStr())));
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

}
