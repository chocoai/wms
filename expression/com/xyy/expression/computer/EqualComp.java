package com.xyy.expression.computer;

import java.sql.Time;
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
 * 相等计算
 * 
 * @author evan
 * 
 */
public class EqualComp extends TreatComp {

	public EqualComp(Computer context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals("==")) {
			OperatorData right = stOutput.pop();
			OperatorData left = stOutput.pop();
			if (left.clazz==String.class&&left.value.equals("null")) {
				left = new OperatorData(NullRefObject.class, null);
			}
			stOutput.push(equal(left, right));
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

	public static OperatorData equal(OperatorData left, OperatorData right) {
		if (right.clazz == NullRefObject.class
				&& left.clazz == NullRefObject.class) {
			return new OperatorData(boolean.class, true);
		} else if (right.clazz == NullRefObject.class
				|| left.clazz == NullRefObject.class) {
			return new OperatorData(boolean.class, false);
		} else {
			if (DataHelper.isNumber(left) && DataHelper.isNumber(right)) {
				return new OperatorData(boolean.class,
						Double.parseDouble(left.value.toString()) == Double
								.parseDouble(right.value.toString()));
			} else if (DataHelper.isBoolean(left)
					&& DataHelper.isBoolean(right)) {
				return new OperatorData(boolean.class,
						(Boolean) left.value == (Boolean) right.value);
			} else if (left.value.equals(right.value)) {
				return new OperatorData(boolean.class, true);
			} else if (left.value instanceof ICompare) {
				Boolean o = ((ICompare) left.value).eq(right.value, true);
				return new OperatorData(boolean.class, o);
			} else if (right.value instanceof ICompare) {
				Boolean o = ((ICompare) right.value).eq(left.value, false);
				return new OperatorData(boolean.class, o);
			} else if (DataHelper.isDate(left) && DataHelper.isDate(right)) {
				return new OperatorData(Boolean.class,
						((Date) left.value).getTime() == ((Date) right.value)
								.getTime());
			} else if (DataHelper.isDate(left) && DataHelper.isString(right)) {
				Date t_date = null;
				try {
					t_date = DateFormat.getInstance().parse(
							right.value.toString());
					return new OperatorData(Boolean.class,
							((Date) left.value).getTime() == t_date.getTime());
				} catch (ParseException e) {
					return new OperatorData(boolean.class, false);
				}
			} else {
				return new OperatorData(boolean.class,
						left.value.equals(right.value));
			}
		}
	}
}
