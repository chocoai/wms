package com.xyy.erp.platform.system.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.Module;
import com.xyy.erp.platform.system.model.Operation;

/**
 * 系统管理-资源管理服务层
 * @author wsj
 *
 */
public class ModuleService {
	
	private OperationService operationService = Enhancer.enhance(OperationService.class);
	
	/**
	 * 根据系统ID查询已关联模块列表
	 * @param systemId
	 * @return
	 */
	public String getSelected(String systemId) {
		List<Module> list = getListByIdAndState(systemId);
		StringBuffer buffer = new StringBuffer();
		for (Module module : list) {
			buffer.append(",{");
			buffer.append(" 'id' : '").append(module.getId()).append("',");
			buffer.append(" 'name' : '").append(module.getName()).append("',");
			buffer.append(" 'childrens' : []}");
		}
		return buffer.length()>0?"["+buffer.toString().substring(1)+"]":"[]";
	}
	
	/**
	 * 根据系统ID和父模块ID查询模块树（最终子节点为功能）
	 * @param systemId
	 * @param parentId
	 * @param buffer
	 * @return
	 */
	public String queryModuleAndOpt(String systemId, String parentId, StringBuffer buffer) {
		List<Module> list = getListByIdAndState(systemId,parentId);
		List<Operation> list2 = operationService.findByModuleId(parentId);
		int i = 0;
		for (Module module : list) {
			if (i!=0) {
				buffer.append(",");
			}else if (list2!=null&&list2.size()>0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append(" 'id' : '").append(module.getId()).append("',");
			buffer.append(" 'type' : '").append("0',");
			buffer.append(" 'name' : '").append(module.getName()).append("',");
			buffer.append(" 'childrens' : [");
			buffer.append(" "+getOptTree( module.getId())+"");
			queryModuleAndOpt(systemId, module.getId(), buffer);
			buffer.append("]}");
			i++;
		}
		
		
		return "["+buffer.toString()+"]";
	}
	
	
	/**
	 * 根据父模块ID查询模块树
	 * @param parentId
	 * @param buffer
	 * @return
	 */
	public String queryModuleTree(String parentId, StringBuffer buffer) {
		List<Module> list = getListByIdAndState(parentId);
		int i = 0;
		for (Module module : list) {
			if (i!=0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append(" 'id' : '").append(module.getId()).append("',");
			buffer.append(" 'name' : '").append(module.getName()).append("',");
			buffer.append(" 'childrens' : [");
			queryModuleTree(module.getId(), buffer);
			buffer.append("]}");
			i++;
		}
		
		
		return "["+buffer.toString()+"]";
	}
	
	private String getOptTree(String moduleId) {
		StringBuffer buffer = new StringBuffer();
		List<Operation> list = operationService.findByModuleId(moduleId);
		int i = 0;
		for (Operation operation : list) {
			if (i!=0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append(" 'id' : '").append(operation.getId()).append("',");
			buffer.append(" 'type' : '").append("1',");
			buffer.append(" 'name' : '").append(operation.getName()).append("',");
			buffer.append(" 'childrens' : []}");
			i++;
		}
		
		
		return buffer.toString();
	}
	
	/**
	 * 更新模块
	 * @param moduleIds
	 * @param systemId
	 * @return
	 */
	public String updateState(String moduleIds, String systemId) {
		List<Module> list = getListById(systemId);
		if (list!=null&&list.size()>0) {
			for (Module module : list) {
				module.setState(0);
				module.update();
			}
		}
		if (moduleIds.length()<=0) {
			return "1";
		}
		String[] idList =  moduleIds.substring(1).split(",");
		for (String id : idList) {
			Module module = Module.dao.findById(id);
			module.setState(1);
			module.update();
		}
		
		return "1";
	}
	
	/*
	 * 保存
	 */
	public String save(String resJson) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> moduleMap = (Map<String, Object>) JSON.parse(resJson);
			Module module = new Module();
			module._setAttrs(moduleMap);
			if(moduleMap.get("id")==null){
				module.setId(UUIDUtil.newUUID());
				if(module.save()){
					return "1";
				}
			}else{
				if(module.update()){
					return "1";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}
	
	/**
	 * 递归删除模块
	 * @param moduleId
	 * @return
	 */
	public String del(String moduleId) {
		List<Module> list = Module.dao.find("select * from tb_sys_module where parentId = '"+moduleId+"' order by sortNo ");
		for (Module module : list) {
			del(module.getId());
			delModuleToOpt(module);
			Module.dao.deleteById(module.getId());
		}
		
		Module.dao.deleteById(moduleId);
		
		return "1";
	}

	/**
	 * 删除模块时级联更新功能表
	 * @param module
	 */
	private void delModuleToOpt(Module module) {
		List<Operation> list = Operation.dao.find("select * from tb_sys_operation where moduleId = '"+module.getId()+"' order by sortNo");
		for (Operation operation : list) {
			operation.setModuleId(null);
			operation.setModuleName(null);
			operation.update();
		}
		
	}
	
	/**
	 * 根据系统ID查询模块树 
	 * @param systemId
	 * @return
	 */
	public String queryResTree(String systemId){
		List<Module> deptTree = Module.dao.find("select * from tb_sys_module where systemId = '"+systemId+"' order by sortNo ");
		Map<String,Module> cacheMap=new HashMap<String,Module>();
		Module root=null;
		for(Module d:deptTree){
			cacheMap.put(d.getId(), d);
		}
		for(Module d:deptTree){
			if(d.getParentId()==null){
				root=d;
			}
			Module parent=cacheMap.get(d.getParentId());
			if(parent!=null)
			   parent.addChild(d);
			d.setParent(parent);;
			
		}
		return "["+iteratorTree(root)+"]";
	 }
	
	
	private String iteratorTree(Module module)  
    {  
        StringBuilder buffer = new StringBuilder();  
        if(module != null)   
        {     
        	buffer.append("{");  
        	buffer.append(" 'id' : '").append(module.getId()).append("',");
	        buffer.append(" 'name' : '").append(module.getName()).append("',");
	        buffer.append(" 'childrens' : [");
	        if(null!=module.getChildrens() && module.getChildrens().size()>0){
	    		int i = 0;
	            do {
	            	Module index = module.getChildrens().get(i);
	            	
	                if (index.getChildrens() != null && index.getChildrens().size() > 0 )   
	                {     
	                    buffer.append(iteratorTree(index));
	                }else{
	                	buffer.append("{");  
		            	buffer.append(" 'id' : '").append(module.getChildrens().get(i).getId()).append("',");
				        buffer.append(" 'name' : '").append(module.getChildrens().get(i).getName()).append("',");
				        buffer.append(" 'childrens' : []}");
	                }
	                
	                if(i < module.getChildrens().size()-1){
	                	buffer.append(", ");
	    			}
	                i++;
				} while (i<module.getChildrens().size());
	        }
            buffer.append("]");
            buffer.append("}");
        }  
        return buffer.toString();  
    }
	
	/*
	 * 通过ID查询资源
	 */
	public Module findById(String id){
		return Module.dao.findById(id);
	}
	
	/*
	 * 删除
	 */
	public String deleteById(String id){
		if(Module.dao.deleteById(id)){
			operationService.deleteByModuleId(id);
			return "1";
		}
		return "-1";
	}
	
	/**
	 * 根据系统ID查询除此模块ID以外的其他模块列表
	 * @param moduleId
	 * @param systemId
	 * @return
	 */
	public List<Module> getListById(String moduleId,String systemId) {
		if (StringUtil.isEmpty(moduleId)||moduleId==null) {
			return Module.dao.find("select * from tb_sys_module where systemId = '"+systemId+"' order by sortNo ");
		}
		return Module.dao.find("select * from tb_sys_module where id != '"+moduleId+"' and systemId = '"+systemId+"' order by sortNo ");
	}
	
	/**
	 * 根据系统ID查询模块列表
	 * @param systemId
	 * @return
	 */
	public List<Module> getListById(String systemId) {
		
		return Module.dao.find("select * from tb_sys_module where systemId = '"+systemId+"' order by sortNo ");
	}
	
	/**
	 * 根据父模块ID查询状态为1的模块列表
	 * 状态为1
	 * @param systemId
	 * @return
	 */
	public List<Module> getListByIdAndState(String parentId) {
		if (parentId == null) {
			return Module.dao.find("select * from tb_sys_module where parentId is null and state = 1 order by sortNo ");
		}
		return Module.dao.find("select * from tb_sys_module where parentId = '"+parentId+"' and state = 1 order by sortNo ");
	}
	
	/**
	 * 根据父模块ID的模块列表
	 * @param parentId
	 * @return
	 */
	public List<Module> getListByParentId(String parentId) {
		if (parentId == null) {
			return Module.dao.find("select * from tb_sys_module where parentId is null  order by sortNo ");
		}
		return Module.dao.find("select * from tb_sys_module where parentId = '"+parentId+"' order by sortNo ");
	}
	
	/**
	 * 根据系统ID查询状态为1的模块列表
	 * @param systemId
	 * @return
	 */
	public List<Module> getListByIdAndState(String systemId, String parentId) {
		if (parentId==null) {
			return Module.dao.find("select * from tb_sys_module where systemId = '"+systemId+"' and parentId is null and state = 1 order by sortNo ");
		}
		return Module.dao.find("select * from tb_sys_module where systemId = '"+systemId+"' and parentId = '"+parentId+"' and state = 1 order by sortNo ");
	}
	
	
	
	/**
	 * 查询权限树
	 * @return
	 */
	public String queryModuleTree(){
		List<Module> deptTree = Module.dao.find("select * from tb_sys_module order by sortNo");
		Map<String,Module> cacheMap=new HashMap<String,Module>();
		Module root= new Module();
		if (deptTree.isEmpty()) {
			root.setName("小药药");
			root.setRemark("根");
			root.setSortNo(new Long(1));
			root.save();
		}
		for(Module d:deptTree){
			cacheMap.put(d.getId(), d);
		}
		for(Module d:deptTree){
			if(d.getParentId()==null){
				root=d;
			}
			Module parent=cacheMap.get(d.getParentId());
			if(parent!=null)
			   parent.addChild(d);
			d.setParent(parent);
			
		}
		return "["+iteratorTree(root)+"]";
	 }
	
	public Page<Module> getPage(String id,String s,int pageIndex,int pageSize) {
		if(StringUtil.isEmpty(id)){
			return Module.dao.paginate(pageIndex, pageSize, "select * ", "from tb_sys_module where parentId is not null  order by id "); 
		}else{
			if(!"0".equals(s)){
				return Module.dao.paginate(pageIndex, pageSize, "select * ", "from tb_sys_module where parentId = '"+id+"' or id= '"+id+"' order by id ");
			}
			return Module.dao.paginate(pageIndex, pageSize, "select * ", "from tb_sys_module where id = '"+id+"'  order by id ");
		}
		
	}
}

