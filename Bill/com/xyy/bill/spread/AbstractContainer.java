package com.xyy.bill.spread;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;

public abstract class AbstractContainer extends AbstractComponent implements IContainer {
	private List<IComponent> childrens = new ArrayList<>();

	public AbstractContainer() {
		super();
	}

	@Override
	public void addComponent(IComponent component) {
		this.childrens.add(component);
		component.setParent(this);
	}

	@Override
	public void removeComponent(IComponent component) {
		this.childrens.remove(component);
		component.setParent(null);

	}

	@Override
	public List<IComponent> getChildren() {
		return this.childrens;
	}

}
