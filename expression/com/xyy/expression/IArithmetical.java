package com.xyy.expression;

/**
 * 算术运算接口
 * @author evan
 *
 */
public interface IArithmetical {
	/**
	 * 加法运算
	 * @param obj
	 * @param left
	 * @return
	 */
	Object add(Object obj,boolean left);
	/**
	 * 减法运算
	 * @param obj
	 * @param left
	 * @return
	 */
	Object sub(Object obj,boolean left);
	/**
	 * 乘法运算
	 * @param obj
	 * @param left
	 * @return
	 */
	Object mul(Object obj,boolean left);
	/**
	 * 除法运算
	 * @param obj
	 * @param left
	 * @return
	 */
	Object div(Object obj,boolean left);
	/**
	 * 求模运算
	 * @param obj
	 * @param left
	 * @return
	 */
	Object mod(Object obj,boolean left);
}
