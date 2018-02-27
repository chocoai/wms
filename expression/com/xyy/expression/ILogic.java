package com.xyy.expression;

/**
 * 逻辑运算接口
 * @author evan
 *
 */
public interface ILogic {
	/**
	 * 逻辑与
	 * @param o
	 * @param left
	 * @return
	 */
	boolean and(Object o, boolean left);
	/**
	 * 逻辑或
	 * @param o
	 * @param left
	 * @return
	 */
	boolean or(Object o, boolean left);

	/**
	 * 逻辑非
	 * @return
	 */
	boolean not();
}
