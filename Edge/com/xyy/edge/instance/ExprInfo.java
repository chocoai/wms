package com.xyy.edge.instance;

import com.xyy.util.StringUtil;

public class ExprInfo {
	private String expr;
	private String alias;
	private String property;



	/**
	 * 解析源表达式： expr [as prop in alias ] 正则匹配：
	 * 
	 * @param source
	 * @return
	 */
	public static ExprInfo parse(String source) {
		ExprInfo result = new ExprInfo();
		if (source.lastIndexOf(" as ") > 0 && source.lastIndexOf(" in ") > 0) {
			result.expr = source.substring(0, source.lastIndexOf(" as ")).trim();
			result.property = source.substring(source.lastIndexOf(" as ") + 4, source.lastIndexOf(" in ")).trim();
			result.alias = source.substring(source.lastIndexOf(" in ") + 4).trim();
		} else {
			result.expr = source;
		}
		return result;
	}

	public ExprInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExprInfo(String expr, String alias, String property) {
		super();
		this.expr = expr;
		this.alias = alias;
		this.property = property;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean containerAs() {
		return !(StringUtil.isEmpty(this.alias) || StringUtil.isEmpty(this.property));

	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.expr != null) {
			sb.append("expr=").append(this.expr).append(" | ");
		}

		if (this.alias != null) {
			sb.append("alias=").append(this.alias).append(" | ");
		}

		if (this.property != null) {
			sb.append("property=").append(this.property);
		}
		return sb.toString();
	}

}
