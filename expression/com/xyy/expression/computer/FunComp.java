package com.xyy.expression.computer;

import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;
/**
 * 函数计算
 * @author evan
 *
 */
public class FunComp extends TreatComp {

	public FunComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_FUN) {
			String funName = token.getTokenStr();
			Object result = this.context.callFuntion(funName, ctx, stOutput);
			if (result == null) {
				stOutput.push(new OperatorData(NullRefObject.class,
						new NullRefObject()));
			} else {
				stOutput.push(new OperatorData(result.getClass(), result));
			}

		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}
}
