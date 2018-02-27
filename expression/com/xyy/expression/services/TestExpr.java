package com.xyy.expression.services;

import com.xyy.expression.Context;
import com.xyy.expression.OperatorData;
import com.xyy.expression.lib.MathLib;

public class TestExpr {

	public static void main(String[] args) throws Exception{
	//m.if(mobile==null,\"\",\" and mobile='\"+mobile+\"'\")
		String expr="m.if(mobile==null | mobile=='',m.if(),'3+4')";
		ExpService exp=	ExpService.instance();
		
		exp.registerLib("m", new MathLib());
		OperatorData od =exp.calc(expr, new Context(){

			@Override
			public Object getValue(String name) {
				if(name.equals("mobile")){
					return "";
				}
				return null;
			}

			@Override
			public void setVariant(String name, Object value) {
				
			}

			@Override
			public void removeVaraint(String name) {
				
			}
			
		});
		
		System.out.println(od.value);
		

	}

}
