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
 * 或计算
 * @author evan
 *
 */
public class OrComp extends TreatComp {

	public OrComp(Computer context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals("|")) {

			OperatorData right = stOutput.pop();
			OperatorData left = stOutput.pop();
			OperatorData result=or(left, right);
			if(result!=null){
				stOutput.push(result);
			} else {
				throw new Exception("| operator error.");
			}
		} else {
			this.next.comp(token, ctx, stOutput);
		}

	}

	public static OperatorData or(OperatorData left, OperatorData right) {
		if(left.clazz==NullRefObject.class){
			left=new OperatorData(boolean.class,false);
		}
		if(right.clazz==NullRefObject.class){
			right=new OperatorData(boolean.class,false);
		}
		
		if (DataHelper.isBoolean(left) && DataHelper.isBoolean(right)) {
			return new OperatorData(boolean.class, (Boolean) left.value
					|| (Boolean) right.value);
		} else if (left.value instanceof ILogic) {
			Boolean o = ((ILogic) left.value).or(right.value, true);
			return new OperatorData(boolean.class, o);
		} else if (right.value instanceof ILogic) {
			Boolean o = ((ILogic) right.value).or(left.value, false);
			return new OperatorData(boolean.class, o);
		} else {
			return null;
		}

	}

}
