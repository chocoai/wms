package com.xyy.expression.computer;

import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Rem;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;
/**
 * 注解信息
 * @author evan
 *
 */
public class RemComp extends TreatComp {
	public RemComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_REM
				&& token.getTokenStr().startsWith("rem")) {
			stOutput.push(new OperatorData(Rem.class, token.getTokenStr()));
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}
}
