package com.xyy.erp.platform.system.model;

import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.base.BaseUser;

/**
 * 系统管理-用户信息实体
 * 
 * @author caofei
 *
 */
@SuppressWarnings("serial")
public class User extends BaseUser<User> {
	
	//角色ID
	private String roleId;
	
	private String orgId;
	
	private String orgName;
	
	private String orgCode;
	
	private String orgCodes;

	public User() {
		this.set("id", UUIDUtil.newUUID());
	}

	public static final User dao = new User();
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgCodes() {
		return orgCodes;
	}

	public void setOrgCodes(String orgCodes) {
		this.orgCodes = orgCodes;
	}
	
}
