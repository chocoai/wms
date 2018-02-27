package com.xyy.bill.render;

import java.util.List;
/**
 * 定义基础的容器接口
 * @author evan
 *
 */
public interface IContainer extends IComponent {
	/**
	 * 添加组件到容器中
	 * @param component
	 */
	public void addComponent(IComponent component);
	
	/**
	 * 删除组件
	 * @param component
	 */
	public void removeComponent(IComponent component);

	/**
	 * 获取容器中的所有子组件
	 * @return
	 */
	public List<IComponent> getChildren();
	
	
}
