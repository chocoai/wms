package com.xyy.expression.computer;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.DataHelper;
import com.xyy.expression.ICompare;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;

/**
 * 大于等于计算
 * @author evan
 *
 */
public class GreaterEqualComp extends TreatComp {

	public GreaterEqualComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals(">=")) {
			OperatorData right = stOutput.pop();
			OperatorData left = stOutput.pop();
			OperatorData result = greaterEqual(left, right);
			if (result != null) {
				stOutput.push(result);
			} else {
				throw new Exception(">= operator error.");
			}
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

	public static OperatorData greaterEqual(OperatorData left,
			OperatorData right) {
		if (left.clazz == NullRefObject.class && right.clazz==NullRefObject.class) {
			return new OperatorData(boolean.class, true);
		}else if(left.clazz == NullRefObject.class || right.clazz==NullRefObject.class){
			return null;
		}
	
		if (DataHelper.isNumber(left) && DataHelper.isNumber(right)) {
			return new OperatorData(Boolean.class,
					Double.parseDouble(left.value.toString()) >= Double
							.parseDouble(right.value.toString()));
		} else if (left.value instanceof ICompare) {
			Boolean o = ((ICompare) left.value).ge(right.value, true);
			return new OperatorData(Boolean.class, o);
		} else if (right.value instanceof ICompare) {
			Boolean o = ((ICompare) right.value).ge(left.value, false);
			return new OperatorData(Boolean.class, o);
		} else if (DataHelper.isDate(left) && DataHelper.isDate(right)) {
			return new OperatorData(Boolean.class,
					((Date) left.value).getTime() >= ((Date) right.value)
							.getTime());
		} else if (DataHelper.isDate(left) && DataHelper.isString(right)) {
			Date t_date = null;
			try {
				t_date = DateFormat.getInstance().parse(
						right.value.toString());
				return new OperatorData(Boolean.class,
						((Date) left.value).getTime() >= t_date.getTime());
			} catch (ParseException e) {
				return new OperatorData(boolean.class, false);
			}
		} 
		else {
			return null;
		}
	}

}
