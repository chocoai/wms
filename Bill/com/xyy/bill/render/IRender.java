package com.xyy.bill.render;

/**
 * 渲染器接口
 * 
 * @author evan
 *
 */
public interface IRender {
	/**
	 * 渲染组件
	 * 
	 * @param component
	 */
	public void render(IComponent component);

	public String getFlag();

}
