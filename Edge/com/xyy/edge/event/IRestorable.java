package com.xyy.edge.event;

public interface IRestorable {
	/**
	 * 保存当前对象"现场"
	 */
	public void save();
	
	/**
	 * 恢复对象上一次保存的“现场”
	 * 
	 */
	public void restore();
}
