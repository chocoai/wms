package com.xyy.expression;
import java.util.Stack;
/**
 * 库函数接口
 * @author evan
 *
 */
public interface ILib {
	/**
	 * 查询库数据中方法ID
	 * @param name
	 * @return
	 */
	public int getMethodID(String name);
	/**
	 * 调用库函数
	 * @param context
	 * @param para
	 * @param methodID
	 * @return
	 */
	public Object call(Context context,Stack<OperatorData> para, int methodID);
}
