package com.xyy.bill.spread;

import com.xyy.bill.render.IComponent;

public abstract class AbstractComponent implements IComponent {
	private IComponent parent;
	private boolean visible=true;
	
	public AbstractComponent() {
		super();
	}
	

	@Override
	public String getKey() {
		return null;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible=visible;
	}


	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public IComponent getParent() {
		return this.parent;
	}

	@Override
	public void setParent(IComponent parent) {
		this.parent=parent;
	}
	
	protected abstract void initialize();

}
