package com.xyy.expression.computer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatComp;

import oracle.net.aso.a;
/**
 * 变量计算
 * @author evan
 *
 */
public class VariantComp extends TreatComp {
	public VariantComp(Computer context) {
		super(context);
	}

	@Override
	public void comp(Token token, Context ctx, Stack<OperatorData> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_VAR) {
			Object result = getVariantValue(token.getTokenStr(), ctx);
			if (result == null) {
				stOutput.push(new OperatorData(NullRefObject.class,
						new NullRefObject()));
			} else {
				stOutput.push(new OperatorData(result.getClass(), result));
			}
		} else {
			this.next.comp(token, ctx, stOutput);
		}
	}

	private Object getVariantValue(String varName, Context curCtx)
			throws Exception {
		if ("true".equals(varName.toLowerCase())) {
			return true;
		} else if ("false".equals(varName.toLowerCase())) {
			return false;
		} else if ("null".equals(varName)) {
			return null;
		}
		Object result = null;
		if (!varName.contains(".")) {
			result = curCtx.getValue(varName);
		} else {
			//[a,b,c]==>a.b.c
			String[] objs = varName.split("\\.");
			assert objs.length > 0;
			Object ctx = curCtx.getValue(objs[0]);
			if (ctx == null) {
				return null;
			}
			for (int i = 1; i < objs.length; i++) {
				ctx = getPropertyValue(ctx, objs[i]);
				if (ctx == null) {
					return null;
					
				}
			}
			result = ctx;
		}
		return result;
	}

	private Object getPropertyValue(Object ctx, String property) {
		if(ctx instanceof Map){
			return ((Map)ctx).get(property);
		}else{
			String m_p = "get" + property.substring(0, 1).toUpperCase()
					+ property.substring(1);
			try {
				Method p_m = ctx.getClass().getDeclaredMethod(m_p, new Class[] {});
				return p_m.invoke(ctx, new Object[] {});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		return null;
	}

}
