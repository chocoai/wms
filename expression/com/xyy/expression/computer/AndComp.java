package com.xyy.expression.computer;

import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.DataHelper;
import com.xyy.expression.ILogic;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;

/**
 * 逻辑与计算
 * @author evan
 *
 */
public class AndComp extends TreatComp {

	public AndComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals("&")) {
			OperatorData right = stOutput.pop();
			OperatorData left = stOutput.pop();
			OperatorData result = and(left, right);
			if (result != null) {
				stOutput.push(result);
			} else {
				throw new Exception("& operator error.");
			}
		} else {
			this.next.comp(token, ctx, stOutput);
		}

	}

	public static OperatorData and(OperatorData left, OperatorData right) {
		
		if (left.clazz == NullRefObject.class || right.clazz == NullRefObject.class) {
			return new OperatorData(boolean.class,false);
		}
		
		if (DataHelper.isBoolean(left) && DataHelper.isBoolean(right)) {
			return new OperatorData((Boolean) left.value
					&& (Boolean) right.value);
		} else if (left.value instanceof ILogic) {
			return new OperatorData(
					((ILogic) left.value).and(right.value, true));
		} else if (right.value instanceof ILogic) {
			return new OperatorData(((ILogic) right.value).and(left.value,
					false));
		} else {
			return null;
		}
	}
}
