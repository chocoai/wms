package com.xyy.erp.platform.system.model;

import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.base.BaseOrgUserRelation;

/**
 * 系统管理-用户机构关联信息实体
 * 
 * @author caofei
 *
 */
@SuppressWarnings("serial")
public class OrgUserRelation extends BaseOrgUserRelation<OrgUserRelation> {

	public OrgUserRelation() {
		this.set("id", UUIDUtil.newUUID());
	}

	public static final OrgUserRelation dao = new OrgUserRelation();

}
