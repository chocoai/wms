package com.xyy.erp.platform.system.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.DeptUserRelation;

/**
 * 组织 - 用户关系 服务
 * @author sl
 *
 */
public class DeptUserService {

	public String saveOrUpdate(String deptid, String userId) {
		DeptUserRelation relation = DeptUserRelation.dao.findFirst("select * from tb_sys_dept_user_relation where "
				+ "userId = '"+userId+"' ");
		
		if (relation == null) {
			relation = new DeptUserRelation();
			relation.setDeptId(deptid);
			relation.setUserId(userId);
			relation.setId(System.currentTimeMillis()+"");
			relation.save();
		}else {
			relation.setDeptId(deptid);
			relation.update();
		}
		
		return "1";
	}

	/**
	 * 更新组织用户关系
	 */
	public String update(String groupUserJosn) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> groupMap = (Map<String, Object>) JSON.parse(groupUserJosn);
			DeptUserRelation groupUserRelation = new DeptUserRelation();
			groupUserRelation._setAttrs(groupMap);
			if(groupUserRelation.update()){
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}

	/**
	 * 删除
	 * @param groupDataJosn
	 */
	public boolean delete(String groupDataJosn) {
		return DeptUserRelation.dao.deleteById(groupDataJosn);
	}

	/**
	 * 添加
	 * @param groupUserRelation
	 */
	public String add(DeptUserRelation groupUserRelation) {
		boolean falg = DeptUserRelation.dao.add(groupUserRelation);
		if (falg) {
			return "1"; //表示添加成功
		}
		return "0";
	}
	
	/**
	 * 查询所有数量（id数量）
	 * @return
	 */
	public List<DeptUserRelation> queryAll(){
		return DeptUserRelation.dao.find
				("select * from tb_sys_dept_user_relation gu join tb_sys_dept g on gu.deptid = g.id");
	}
	
	public DeptUserRelation findByUserId(String userId) {
		if (StringUtil.isEmpty(userId)) {
			return null;
		}
		return DeptUserRelation.dao.findFirst("select * from tb_sys_dept_user_relation where "
				+ "userId = '"+userId+"' ");
	}
}
