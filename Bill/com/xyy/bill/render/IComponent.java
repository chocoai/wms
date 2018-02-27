package com.xyy.bill.render;
/**
 * 通用报表组件接口
 * @author evan
 *
 */
public interface IComponent {	
	public String getKey();
	
	/**
	 * 组件是否可见
	 * @return
	 */
	public boolean isVisible();
	
	/**
	 * 设置组件的可见性
	 * @param visible
	 */
	public void setVisible(boolean visible);
	
	/**
	 * 获取父组件
	 * @return
	 */
	public IComponent getParent();
	/**
	 * 设置父组件
	 * @param parent
	 */
	public void setParent(IComponent parent);  
	
	/**
	 * 获取组件的标志
	 * @return
	 */
	public String getFlag();

}
