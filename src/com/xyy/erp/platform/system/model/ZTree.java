package com.xyy.erp.platform.system.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ZTree模型
 * @author caofei
 *
 */
public class ZTree {

	public String id;
	public String pId;
	public String name;
	private Object attributes;
	public boolean open;
	private boolean isParent;
	public String url;
	public String icon;
	public String title;
	public List<ZTree> children;
	boolean checked;
	boolean checkDisable;

	public ZTree() {
	}

	public ZTree(String id, String name, boolean open, boolean isParent) {
		this.id = id;
		this.name = name;
		this.open = open;
		this.isParent = isParent;
		this.children = new ArrayList<>();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return this.pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOpen() {
		return this.open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ZTree> getChildren() {
		return this.children;
	}

	public void setChildren(List<ZTree> children) {
		this.children = children;
	}

	public boolean isIsParent() {
		return this.isParent;
	}

	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}

	public Object getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}

	public boolean isCheckDisable() {
		return this.checkDisable;
	}

	public void setCheckDisable(boolean checkDisable) {
		this.checkDisable = checkDisable;
	}

	public boolean isChecked() {
		return this.checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
