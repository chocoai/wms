package com.xyy.erp.platform.system.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseRole<M extends BaseRole<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}

	public java.lang.String getId() {
		return get("id");
	}

	public void setParentId(java.lang.String parentId) {
		set("parentId", parentId);
	}

	public java.lang.String getParentId() {
		return get("parentId");
	}

	public void setRoleName(java.lang.String roleName) {
		set("roleName", roleName);
	}

	public java.lang.String getRoleName() {
		return get("roleName");
	}

	public void setSortNo(java.lang.Integer sortNo) {
		set("sortNo", sortNo);
	}

	public java.lang.Integer getSortNo() {
		return get("sortNo");
	}

	public void setState(java.lang.Integer state) {
		set("state", state);
	}

	public java.lang.Integer getState() {
		return get("state");
	}

	public void setInheritable(java.lang.Integer inheritable) {
		set("inheritable", inheritable);
	}

	public java.lang.Integer getInheritable() {
		return get("inheritable");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("createTime", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("createTime");
	}

	public void setCreator(java.lang.String creator) {
		set("creator", creator);
	}

	public java.lang.String getCreator() {
		return get("creator");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("updateTime", updateTime);
	}

	public java.util.Date getUpdateTime() {
		return get("updateTime");
	}

	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}

	public java.lang.String getRemark() {
		return get("remark");
	}

}
