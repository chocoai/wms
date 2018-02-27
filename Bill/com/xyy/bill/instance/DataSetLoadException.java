package com.xyy.bill.instance;

/**
 * 数据集加载异常类
 * @author evan
 *
 */
public class DataSetLoadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7627258002927056774L;

	public DataSetLoadException() {
		super();
	}

	public DataSetLoadException(String message) {
		super(message);
	}

	public DataSetLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataSetLoadException(Throwable cause) {
		super(cause);	
	}
	
	
	

}
