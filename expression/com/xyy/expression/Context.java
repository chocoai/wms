package com.xyy.expression;
/**
 * 计算上下文接口
 * @author evan
 *
 */
public interface Context {
	public Object getValue(String name);
	//读写变量
	public void setVariant(String name,Object value);
	public void removeVaraint(String name);
}
