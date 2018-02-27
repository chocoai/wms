package com.xyy.bill.instance;

import java.util.ArrayList;
import java.util.List;

import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;
/**
 * 
 * 参数化查询方法
 * @author evan
 *
 */
public class ParamSQL {
	private String sql;
	private List<String> paramExpr = new ArrayList<>();

	public ParamSQL() {
		super();
	}

	public ParamSQL(String sql, List<String> paramExpr) {
		super();
		this.sql = sql;
		this.paramExpr = paramExpr;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<String> getParamExpr() {
		return paramExpr;
	}

	public void setParamExpr(List<String> paramExpr) {
		this.paramExpr = paramExpr;
	}

	public Object[] calParamExpr(DataTableInstance dti) {
		Object[] result = new Object[this.paramExpr.size()];
		int index = 0;
		for (String expr : this.paramExpr) {
			Object value;
			try {
				value = ExpService.instance().calc(expr, dti.getDataTableRunContext()).value;
				result[index] = value;
			} catch (ExpressionCalException e) {
				e.printStackTrace();
			}
			
			index += 1;
		}
		return result;
	}
	
	
	public Object[] calRefreshParamExpr(DataTableInstance dti) {
		Object[] result = new Object[this.paramExpr.size()];
		int index = 0;
		for (String expr : this.paramExpr) {
			Object value;
			try {
				value = ExpService.instance().calc(expr,dti.getDataTableRunContext()).value;
				result[index] = value;
			} catch (ExpressionCalException e) {
				e.printStackTrace();
			}
			
		
			index += 1;
		}
		return result;
	}

}
