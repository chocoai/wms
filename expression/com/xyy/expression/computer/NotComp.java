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
 * 逻辑非计算
 * @author evan
 *
 */
public class NotComp extends TreatComp {

	public NotComp(Computer context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void comp(Token token,Context ctx,Stack<OperatorData> stOutput) throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE
				&& token.getTokenStr().equals("!")) {
			OperatorData right = stOutput.pop();
			OperatorData result = not(right);
			if(result!=null){
				stOutput.push(result);
			} else {
				throw new Exception("! operator error.");
			}
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}
	
	public static OperatorData not(OperatorData right){
		if (right.clazz==NullRefObject.class) {
			return new OperatorData(boolean.class, true);
		}
		if (DataHelper.isBoolean(right)) {
			return new OperatorData(boolean.class,
					!(Boolean) right.value);
		} else if (right.value instanceof ILogic) {
			Boolean o = ((ILogic) right.value).not();
			return new OperatorData(boolean.class, o);
		} else {
			return null;
		}
	}

}
