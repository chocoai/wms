package com.xyy.erp.platform.system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Page;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.Module;
import com.xyy.erp.platform.system.model.Operation;
import com.xyy.erp.platform.system.model.Systems;

/** 
 * 
 * 系统管理-操作服务层
 * @author wsj
 *
 */
public class OperationService {
	
	
	/*
	 * 操作分页查询
	 */
	public Page<Operation> paginate(int pageNumber, int pageSize,String mid) {
		StringBuffer sql=new StringBuffer("from tb_sys_operation");
			sql.append(" where moduleId = '").append(mid+"'  ");
		return Operation.dao.paginate(pageNumber, pageSize, "select *", sql.toString());
	}
	
	/*
	 * 操作分页查询
	 */
	public Page<Operation> paginate(int pageNumber, int pageSize,Operation oper) {
		StringBuffer sql=new StringBuffer("from tb_sys_operation where 1=1 ");
		if(oper!=null){
			if(!StringUtil.isEmpty(oper.getName())){
				sql.append(" and  name like '%").append(oper.getName()+"%'");
			}
			if(!StringUtil.isEmpty(oper.getState())){
				sql.append(" and  state = ").append(oper.getState());
			}
		}
		
		return Operation.dao.paginate(pageNumber, pageSize, "select *", sql.toString());
	}
	
	public List<Operation> findByModuleId(String moduleId) {
		List<Operation> list=Operation.dao.find("select * from tb_sys_operation where moduleId = '"+moduleId+"' ");
		return list;
	}

	/*
	 * 操作通过ID查询
	 */
	public Operation findById(String id){
		return Operation.dao.findById(id);
	}
	
	/*
	 * 保存
	 */
	public String save(String resJson) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> operationMap = (Map<String, Object>) JSON.parse(resJson);
			Operation operation = new Operation();
			operation._setAttrs(operationMap);
			if(operationMap.get("id")==null){
				if(operation.save()){
					return "1";
				}
			}else{
				if(operation.update()){
					return "1";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}
	
	/*
	 * 删除
	 */
	public String deleteById(String id){
		if(Operation.dao.deleteById(id)){
			return "1";
		}
		return "-1";
	}
	
	public String deleteByModuleId(String moduleId){
		List<Operation> list=findByModuleId(moduleId);
		for(Operation t:list){
			deleteById(t.getId());
		}
		return "1";
	}
	
	
	/**
	 * 查询操作列表
	 * @return
	 */
	public List<Operation> queryOperationList(String operIds,int pageIndex, int pageSize){
		if (StringUtils.isEmpty(operIds)) {
			return new ArrayList<Operation>();
		}
		Page<Operation> result = Operation.dao.paginate(pageIndex,pageSize,"select * ","from tb_sys_operation t where t.moduleId in("+ operIds+ ") ");
		return result.getList();
	 }
	
	
	public int getOperationTotal(String operIds) {
		if (StringUtil.isEmpty(operIds)) {
			return 0;
		}
		List<Operation> list = Operation.dao.find("select * from tb_sys_operation t where t.moduleId in("+ operIds+ ") ");
		return list.size();
	}
	
	
	
	
	
	/**
	 * 查询功能树
	 * @return
	 */
	public String queryOpetationTree(String moduleId){
		Module root= new Module();
		root.setId("root");
		root.setName("小药药ERP系统");
		Map<String,Module> cacheMap=new HashMap<String,Module>();
		List<Module> sList = new ArrayList<Module>();
		List<Systems> systems = Systems.dao.find("SELECT s.* FROM tb_sys_systems s ORDER BY s.sortNo");
		List<Operation> oList = Operation.dao.find("SELECT o.* FROM tb_sys_operation o");
		for (Systems s : systems) {//子系统转为模块对象
			Module module = new Module();
			module.setId(s.getId());
			module.setName(s.getName());
			List<Module> mList = Module.dao.find("SELECT m.* FROM tb_sys_module m WHERE m.systemId = '"+s.getId()+"' ORDER BY m.sortNo");
			for (Module m : mList) {
				if (StringUtil.isEmpty(m.getParentId())) {
					m.setParentId(s.getId());
				}
				sList.add(m);//添加组装的模块对象
			}
			sList.add(module);//添加组装的系统对象
		}
		
		for (Operation o : oList) {
			Module module = new Module();
			module.setId("operation_"+o.getId());
			module.setName(o.getName());
			module.setParentId(o.getModuleId());
			sList.add(module);//添加组装的功能对象
		}
		
		for(Module d:sList){
			cacheMap.put(d.getId(), d);
		}
		
		for(Module d:sList){
			if(d.getParentId()==null){
				root.addChild(d);
				d.setParent(root);
			}
			Module parent=cacheMap.get(d.getParentId());
			if(parent!=null){
			   parent.addChild(d);
			   d.setParent(parent);
			}
		}
		
		return "["+iteratorTree(root)+"]";
	 }
	
	public String iteratorTree(Module permission)  {  
        StringBuilder buffer = new StringBuilder();  
        if(permission != null)   
        {     
        	buffer.append("{");  
        	buffer.append(" 'id' : '").append(permission.getId()).append("',");
	        buffer.append(" 'name' : '").append(permission.getName()).append("',");
	        buffer.append(" 'childrens' : [");
	        if(null!=permission.getChildrens() && permission.getChildrens().size()>0){
	    		int i = 0;
	            do {
	            	Module index = permission.getChildrens().get(i);
	            	
	                if (index.getChildrens() != null && index.getChildrens().size() > 0 )   
	                {     
	                    buffer.append(iteratorTree(index));  
	                }else{
	                	buffer.append("{");  
		            	buffer.append(" 'id' : '").append(permission.getChildrens().get(i).getId()).append("',");
				        buffer.append(" 'name' : '").append(permission.getChildrens().get(i).getName()).append("',");
				        buffer.append(" 'childrens' : []}");
	                }
	                
	                if(i < permission.getChildrens().size()-1){
	                	buffer.append(", ");
	    			}
	                i++;
				} while (i<permission.getChildrens().size());
	        }
            buffer.append("]");
            buffer.append("}");
        }  
        return buffer.toString();  
    }
	
	
	
}
