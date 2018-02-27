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
 * 除法计算
 * @author evan
 *
 */
public class DivisionComp extends TreatComp {

	public DivisionComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals("/")) {
			OperatorData right = stOutput.pop();
			OperatorData left = stOutput.pop();
			OperatorData result = division(left, right);
			stOutput.push(this.division(left, right));
		} else {
			this.next.comp(token, ctx, stOutput);
		}

	}

	public static OperatorData division(OperatorData left, OperatorData right) {
		if (left.clazz == NullRefObject.class
				|| right.clazz == NullRefObject.class) {
			return new OperatorData(long.class, 0);
		} else {
			if (DataHelper.isInt(left) && DataHelper.isInt(right)) {
				if (Long.parseLong(right.value.toString()) != 0) {
					if (Long.parseLong(left.value.toString())
							% Long.parseLong(right.value.toString()) == 0) {
						return new OperatorData(
								long.class,
								Long.parseLong(left.value.toString())
										/ Long.parseLong(right.value.toString()));
					} else {
						return new OperatorData(double.class,
								Double.parseDouble(left.value.toString())
										/ Double.parseDouble(right.value
												.toString()));
					}
				} else {
					return new OperatorData(long.class, 0);
				}

			} else if (DataHelper.isNumber(right) && DataHelper.isNumber(left)) {
				if (Double.parseDouble(right.value.toString()) != 0) {
					return new OperatorData(
							double.class,
							Double.parseDouble(left.value.toString())
									/ Double.parseDouble(right.value.toString()));
				} else {
					return new OperatorData(long.class, 0);
				}

			} else if (left.value instanceof IArithmetical) {
				Object o = ((IArithmetical) left.value).div(right.value, true);
				if (o != null) {
					return new OperatorData(o.getClass(),
							DataHelper.createOperatorData(o));
				} else {
					return new OperatorData(NullRefObject.class, null);
				}

			} else if (right.value instanceof IArithmetical) {
				Object o = ((IArithmetical) right.value).div(left.value, false);
				return new OperatorData(o.getClass(),
						DataHelper.createOperatorData(o));

			} else {
				return new OperatorData(long.class, 0);
			}
		}
	}

}
