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
 * 加法计算
 * @author evan
 *
 */
public class AddComp extends TreatComp {
	public AddComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals("+")) {
			OperatorData right = stOutput.pop();
			OperatorData left = stOutput.pop();
			OperatorData resultData = add(left, right);
			if (resultData != null) {
				stOutput.push(resultData);
			} else {
				throw new Exception("+ operator error");
			}
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

	public static OperatorData add(OperatorData left, OperatorData right) {
		if (left.clazz == NullRefObject.class ) {
			left=new OperatorData(String.class,"");
		}
		if (right.clazz == NullRefObject.class) {
			right=new OperatorData(String.class,"");
		}
		
		
		if (right.clazz == String.class || left.clazz == String.class) {
			return new OperatorData(String.class, left.value.toString()
					+ right.value.toString());
		} else if (DataHelper.isInt(left) && DataHelper.isInt(right)) {
			return new OperatorData(long.class, Long.parseLong(left.value
					.toString()) + Long.parseLong(right.value.toString()));
		} else if (DataHelper.isNumber(left) && DataHelper.isNumber(right)) {
			return new OperatorData(double.class, Double.parseDouble(left.value
					.toString()) + Double.parseDouble(right.value.toString()));
		} else if (left.value instanceof IArithmetical) {
			Object o = ((IArithmetical) left.value).add(right.value, true);
			return DataHelper.createOperatorData(o);
		} else if (right.value instanceof IArithmetical) {
			Object o = ((IArithmetical) right.value).add(left.value, false);
			return DataHelper.createOperatorData(o);
		} else {
			return null;
		}
	}
}
