package com.xyy.erp.platform.system.model;

import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.base.BaseRoleUserRelation;

/**
 * 系统管理-用户角色关联信息实体
 * 
 * @author caofei
 *
 */
@SuppressWarnings("serial")
public class RoleUserRelation extends BaseRoleUserRelation<RoleUserRelation> {

	public RoleUserRelation() {
		this.set("id", UUIDUtil.newUUID());
	}

	public static final RoleUserRelation dao = new RoleUserRelation();

}
