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
 * 减法计算
 * @author evan
 *
 */
public class SubtractComp extends TreatComp {

	public SubtractComp(Computer context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals("-")) {
			OperatorData right = stOutput.pop();
			OperatorData left = stOutput.pop();
			OperatorData result = subtract(left, right);
			if (result != null) {
				stOutput.push(result);
			} else {
				throw new Exception("- operator error");
			}

		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

	public static OperatorData subtract(OperatorData left, OperatorData right) {
		if (left.clazz == NullRefObject.class || right.clazz == NullRefObject.class) {
			return null;
		}

		if (DataHelper.isInt(left) && DataHelper.isInt(right)) {
			return new OperatorData(long.class, Long.parseLong(left.value
					.toString()) - Long.parseLong(right.value.toString()));

		} else if (DataHelper.isNumber(left) && DataHelper.isNumber(right)) {
			return new OperatorData(double.class, Double.parseDouble(left.value
					.toString()) - Double.parseDouble(right.value.toString()));
		} else if (left.value instanceof IArithmetical) {
			Object o = ((IArithmetical) left.value).sub(right.value, true);
			return DataHelper.createOperatorData(o);
		} else if (right.value instanceof IArithmetical) {
			Object o = ((IArithmetical) right.value).sub(left.value, false);
			return DataHelper.createOperatorData(o);
		} else {
			return null;
		}
	}

}
