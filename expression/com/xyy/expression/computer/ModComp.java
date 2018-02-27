package com.xyy.expression.computer;

import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.DataHelper;
import com.xyy.expression.IArithmetical;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;

/**
 * 求模计算
 * @author evan
 *
 */
public class ModComp extends TreatComp {

	public ModComp(Computer context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals("%")) {

			OperatorData right = stOutput.pop();
			OperatorData left = stOutput.pop();
			OperatorData resultData = mod(left, right);
			if (resultData != null) {
				stOutput.push(resultData);
			} else {
				throw new Exception("mode operator error.");
			}
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

	public static OperatorData mod(OperatorData left, OperatorData right) {
		if(left.clazz == NullRefObject.class || right.clazz==NullRefObject.class){
			return null;
		}
		if (DataHelper.isInt(left) && DataHelper.isInt(right)) {
			return new OperatorData(long.class, Long.parseLong(left.value
					.toString()) % Long.parseLong(right.value.toString()));
		} else if (left.value instanceof IArithmetical) {
			Object o = ((IArithmetical) left.value).mod(right.value, true);
			return DataHelper.createOperatorData(o);
		} else if (right.value instanceof IArithmetical) {
			Object o = ((IArithmetical) right.value).mod(left.value, false);
			return DataHelper.createOperatorData(o);
		} else {
			return null;
		}
	}

}
