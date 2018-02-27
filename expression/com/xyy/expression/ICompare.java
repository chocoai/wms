package com.xyy.expression;

/**
 * 比较运算接口
 * @author evan
 *
 */
public interface ICompare {
	/**
	 * 大于
	 * @param obj
	 * @param left
	 * @return
	 */
	boolean gt(Object obj, boolean left);
	
	/**
	 * 大于等于
	 * @param obj
	 * @param left
	 * @return
	 */
	boolean ge(Object obj, boolean left);
	
	/**
	 * 小于
	 * @param obj
	 * @param left
	 * @return
	 */
	boolean lt(Object obj, boolean left);

	/**
	 * 小于等于
	 * @param obj
	 * @param left
	 * @return
	 */
	boolean le(Object obj, boolean left);

	/**
	 * 等于
	 * @param obj
	 * @param left
	 * @return
	 */
	boolean eq(Object obj, boolean left);

	/**
	 * 不等于
	 * @param obj
	 * @param left
	 * @return
	 */
	boolean neq(Object obj, boolean left);
}
