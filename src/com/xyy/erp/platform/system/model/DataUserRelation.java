package com.xyy.erp.platform.system.model;

import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.base.BaseDataUserRelation;

/**
 * 系统管理-用户数据权限关联信息实体
 * 
 * @author caofei
 *
 */
@SuppressWarnings("serial")
public class DataUserRelation extends BaseDataUserRelation<DataUserRelation> {

	public DataUserRelation() {
		this.set("id", UUIDUtil.newUUID());
	}

	public static final DataUserRelation dao = new DataUserRelation();

}
