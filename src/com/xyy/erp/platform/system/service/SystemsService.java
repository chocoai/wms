package com.xyy.erp.platform.system.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.model.Module;
import com.xyy.erp.platform.system.model.Systems;

public class SystemsService {

	
	/**
	 * 查询所有系统列表
	 * @return
	 */
	public List<Systems> queryList(){
		
		return Systems.dao.find("select * from tb_sys_systems order by sortNo");
		
	}
	
	/**
	 * 查询除此系统ID以外其他所有系统的列表
	 * @param moduleId
	 * @return
	 */
	public List<Systems> queryList(String sysId){
		
		return Systems.dao.find("select * from tb_sys_systems  where id != '"+sysId+"'");
		
	}
	
	/**
	 * 更新系统信息
	 * @param resJson
	 * @return
	 */
	public String save(String resJson) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> systemMap = (Map<String, Object>) JSON.parse(resJson);
			Systems systems = Systems.dao._setAttrs(systemMap);
			if(systemMap.get("id")==null){
				systems.setId(UUIDUtil.newUUID());
				if(systems.save()){
					Module module = new Module();
					module.setSystemId(systems.getId());
					module.setName(systems.getName()+"模块管理");
					module.setRemark(systems.getName()+"模块管理");
					module.setIsparent("1");
					module.setState(1);
					module.save();
					Menu menu = new Menu();
					menu.setState(1);
					menu.setSystemId(systems.getId());
					menu.setName(systems.getName()+"菜单管理");
					menu.setRemark(systems.getName()+"模块管理");
					menu.setIsparent("1");
					menu.save();
					return "1";
				}
			}else{
				if(systems.update()){
					return "1";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}
	
	
}
