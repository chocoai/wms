package com.xyy.erp.platform.system.service;

import java.util.List;

import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.DataUserRelation;
import com.xyy.erp.platform.system.model.OrgUserRelation;
import com.xyy.erp.platform.system.model.Organization;

/**
 * 机构用户关联服务层
 * @author caofei
 *
 */
public class OrgUserService {
	
	
	public List<OrgUserRelation> findByUserId(String userId) {
		if (StringUtil.isEmpty(userId)) {
			return null;
		}
		return OrgUserRelation.dao.find("select * from tb_sys_org_user_relation where "
				+ "userId = '"+userId+"' ");
	}
	
	public boolean saveOrUpdate(String userId, String orgIds){
		List<OrgUserRelation> list = findByUserId(userId);
		if (list!=null&&list.size()>0) {
			for (OrgUserRelation relation : list) {
				relation.delete();
			}
		}
		if (orgIds.length()<=0) {
			return true;
		}
		String[] roleIdsArr =  orgIds.substring(1).split(",");
		for (String roleId : roleIdsArr) {
			OrgUserRelation relation = new OrgUserRelation();
			relation.setId(UUIDUtil.newUUID());
			relation.setOrgId(roleId);
			relation.setUserId(userId);
			relation.save();
		}
		return true;
	}
	
	//----------------------------【数据授权】start----------------------------------
	
	public List<DataUserRelation> findDataByUserId(String userId) {
		if (StringUtil.isEmpty(userId)) {
			return null;
		}
		return DataUserRelation.dao.find("select * from tb_sys_data_user_relation where "
				+ "userId = '"+userId+"' ");
	}
	
	public boolean saveOrUpdateData(String userId, String orgIds){
		List<DataUserRelation> list = findDataByUserId(userId);
		if (list!=null&&list.size()>0) {
			for (DataUserRelation relation : list) {
				relation.delete();
			}
		}
		if (orgIds.length()<=0) {
			return true;
		}
		String[] orgIdsArr =  orgIds.substring(1).split(",");
		for (String orgId : orgIdsArr) {
			DataUserRelation relation = new DataUserRelation();
			relation.setId(UUIDUtil.newUUID());
			Organization orgObject = Organization.dao.findById(orgId);
			relation.setOrgId(orgId);
			relation.setOrgCode(orgObject.getOrgCode());
			relation.setUserId(userId);
			relation.save();
		}
		return true;
	}
	
	
	//----------------------------【数据授权】end----------------------------------
	 
	

}
