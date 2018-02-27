package com.xyy.expression.computer;

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
 *  不等于计算
 * @author evan
 *
 */
public class NotEqualComp extends TreatComp {

	public NotEqualComp(Computer context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals("!=")) {
			OperatorData right = stOutput.pop();
			OperatorData left = stOutput.pop();
			OperatorData result = notEqual(left, right);
			if (result != null) {
				stOutput.push(result);
			} else {
				throw new Exception("!= operator error.");
			}
		} else {
			this.next.comp(token, ctx, stOutput);
		}

	}

	public static OperatorData notEqual(OperatorData left, OperatorData right) {
		
		if(left.clazz == NullRefObject.class && right.clazz == NullRefObject.class){
			return new OperatorData(boolean.class, false);
		}
		if(left.clazz==NullRefObject.class && right.clazz != NullRefObject.class){
			return new OperatorData(boolean.class, true);
		}
		
		if(right.clazz==NullRefObject.class && left.clazz != NullRefObject.class){
			return new OperatorData(boolean.class, true);
		}
		//数字比较
		if (DataHelper.isNumber(left) && DataHelper.isNumber(right)) {
			return new OperatorData(boolean.class,
					Double.parseDouble(left.value.toString()) != Double
							.parseDouble(right.value.toString()));
		} else if (DataHelper.isBoolean(left) && DataHelper.isBoolean(right)) {//boolean值比较
			return new OperatorData(boolean.class,
					(Boolean) left.value != (Boolean) right.value);
		} else if (!left.value.equals(right.value)) {
			return new OperatorData(boolean.class, true);
		} else if (left.value instanceof ICompare) {
			Boolean o = ((ICompare) left.value).neq(right.value, true);
			return new OperatorData(boolean.class, o);
		} else if (right.value instanceof ICompare) {
			Boolean o = ((ICompare) right.value).neq(left.value, false);
			return new OperatorData(boolean.class, o);
		} else {
			//部署与上面情况的为true,即判定为!=
			return null;
		}

	}

}
