package com.xyy.erp.platform.system.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.DataUserRelation;
import com.xyy.erp.platform.system.model.Organization;

/**
 * 系统管理-机构信息服务层
 * @author 
 *
 */
public class OrganizationService {
	
	/**
	 * 查询机构树
	 * @return
	 */
	public String queryOrgTree(String isDisbled){
		String sql = "select * from tb_sys_organization ";
		if(!StringUtil.isEmpty(isDisbled)){
			sql =sql+" where state = 1";
		}
		sql +=" order by sortNo";
		List<Organization> deptTree = Organization.dao.find(sql);
		Map<String,Organization> cacheMap=new HashMap<String,Organization>();
		Organization root= new Organization();
		if (deptTree.isEmpty()) {
			root.setOrgName("小药药集团");
			root.setRemark("根");
			root.setSortNo(1);
			root.setCreateTime(new Timestamp(System.currentTimeMillis()));
			root.save();
		}
		for(Organization d:deptTree){
			cacheMap.put(d.getId(), d);
		}
		for(Organization d:deptTree){
			if(d.getParentId()==null){
				root=d;
			}
			Organization parent=cacheMap.get(d.getParentId());
			if(parent!=null)
			   parent.addChild(d);
			d.setParent(parent);
			
		}
		return "["+iteratorTree(root)+"]";
	 }
	
	public String iteratorTree(Organization org)  
    {  
        StringBuilder buffer = new StringBuilder();  
        if(org != null)   
        {     
        	buffer.append("{");  
        	buffer.append(" 'id' : '").append(org.getId()).append("',");
	        buffer.append(" 'name' : '").append(org.getOrgName()).append("',");
	        buffer.append(" 'childrens' : [");
	        if(null!=org.getChildrens() && org.getChildrens().size()>0){
	    		int i = 0;
	            do {
	            	Organization index = org.getChildrens().get(i);
	            	
	                if (index.getChildrens() != null && index.getChildrens().size() > 0 )   
	                {     
	                    buffer.append(iteratorTree(index));  
	                }else{
	                	buffer.append("{");  
		            	buffer.append(" 'id' : '").append(org.getChildrens().get(i).getId()).append("',");
				        buffer.append(" 'name' : '").append(org.getChildrens().get(i).getOrgName()).append("',");
				        buffer.append(" 'childrens' : []}");
	                }
	                
	                if(i < org.getChildrens().size()-1){
	                	buffer.append(", ");
	    			}
	                i++;
				} while (i<org.getChildrens().size());
	        }
            buffer.append("]");
            buffer.append("}");
        }  
        return buffer.toString();  
    }
	
	
	/**
	 * 获取所有机构信息
	 */
	public List<Organization> queryOrgList(String id){
		String sql = "";
		if(StringUtil.isEmpty(id)){
			sql = "select * from tb_sys_organization where state = 1 order by sortNo";
		}else{
			sql = "select * from tb_sys_organization where state = 1 and  id != '"+id+"' order by sortNo";
		}
		return Organization.dao.find(sql);
	}
	
	
	/**
	 * 保存机构信息
	 * @param deptJosn
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String saveOrg(String roleJosn) {
		try {
			Map<String, Object> orgMap = (Map<String, Object>) JSON.parse(roleJosn);
			Organization org = new Organization();
			org._setAttrs(orgMap);
			org.setState(1);
			org.setCreateTime(new Timestamp(System.currentTimeMillis()));
			if(org.save()){
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}
	
	
	/**
	 * 查询机构
	 */
	public Organization findOrgById(String id){
		return Organization.dao.findById(id);
	}
	
	/**
	 * 修改机构信息
	 * @param orgJosn
	 * @return
	 */
	public String addOrUpdateOrg(Map<String, Object> orgMap) {
		try {
			Organization org = new Organization();
			org._setAttrs(orgMap);
			String id = (String) orgMap.get("id");
			if(StringUtil.isEmpty(id)){
				org.setCreateTime(new Timestamp(System.currentTimeMillis()));
				if(org.save()){
					return "1";
				}
			}else {
				org.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				if(org.update()){
					return "1";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}
	
	
	public List<String> queryChildrensById(String orgId){
		orgId = "6d8104f9abd748b8906caa19f81ddb8a";
		List<String> idList = new ArrayList<>();
//		StringBuilder buffer = new StringBuilder();
		idList.add(orgId);
		List<String> list = queryOrgById(orgId,idList);
		return list;
	}
	
	public List<String> queryOrgById(String id,List<String> list){
		String sql = "select * from tb_sys_organization where parentId = '"+id+"'";
		List<Organization> orgList = Organization.dao.find(sql);
		for (Organization organization : orgList) {
			list.add(organization.getId());
			queryOrgById(organization.getId(),list);
		}
		return list;
	}
	
	public String queryChildrensByIds(String orgId){
		orgId = "6d8104f9abd748b8906caa19f81ddb8a";
		StringBuilder buffer = new StringBuilder();
		buffer.append(","+orgId);
		buffer = queryOrgByIds(orgId,buffer);
		return buffer.substring(1).toString();
	}
	
	public StringBuilder queryOrgByIds(String id,StringBuilder buffer){
		String sql = "select * from tb_sys_organization where parentId = '"+id+"'";
		List<Organization> orgList = Organization.dao.find(sql);
		for (Organization organization : orgList) {
			buffer.append(","+organization.getId());
			queryOrgByIds(organization.getId(),buffer);
		}
		return buffer;
	}
	
	public String queryOrgCodeByUserId(String userId){
		String sql = "select orgCode from tb_sys_data_user_relation where userId = '"+userId+"'";
		List<DataUserRelation> dataList = DataUserRelation.dao.find(sql);
		StringBuilder buffer = new StringBuilder();
		for (DataUserRelation data : dataList) {
			buffer.append(","+data.getOrgCode());
		}
		return buffer.length()>0?buffer.substring(1).toString():"";
	}
	
	
	public List<Organization> findOrgByOrgCode(String orgCode){
		String sql = "select * from tb_sys_organization where state = 1 and orgCode = "+orgCode ;
		List<Organization> list = Organization.dao.find(sql);
		return list;
	}
	
	
	
	/**
	 * 查询机构数据授权树
	 * @return
	 */
	public String queryDataTree(){
		List<Organization> deptTree = Organization.dao.find("select * from tb_sys_organization where state = 1 order by sortNo");
		Map<String,Organization> cacheMap=new HashMap<String,Organization>();
		Organization root= new Organization();
		if (deptTree.isEmpty()) {
			root.setOrgName("小药药集团");
			root.setRemark("根");
			root.setSortNo(1);
			root.setCreateTime(new Timestamp(System.currentTimeMillis()));
			root.save();
		}
		for(Organization d:deptTree){
			cacheMap.put(d.getId(), d);
		}
		for(Organization d:deptTree){
			if(d.getParentId()==null){
				root=d;
			}
			Organization parent=cacheMap.get(d.getParentId());
			if(parent!=null)
			   parent.addChild(d);
			d.setParent(parent);
			
		}
		return "["+iteratorDataTree(root)+"]";
	 }
	
	public String iteratorDataTree(Organization org)  
    {  
        StringBuilder buffer = new StringBuilder();  
        if(org != null)   
        {     
        	buffer.append("{");  
        	buffer.append(" 'id' : '").append(org.getId()).append("',");
	        buffer.append(" 'name' : '").append(org.getOrgName()).append("',");
	        buffer.append(" 'orgCode' : '").append(org.getOrgCode()).append("',");
	        buffer.append(" 'childrens' : [");
	        if(null!=org.getChildrens() && org.getChildrens().size()>0){
	    		int i = 0;
	            do {
	            	Organization index = org.getChildrens().get(i);
	            	
	                if (index.getChildrens() != null && index.getChildrens().size() > 0 )   
	                {     
	                    buffer.append(iteratorDataTree(index));  
	                }else{
	                	buffer.append("{");  
		            	buffer.append(" 'id' : '").append(org.getChildrens().get(i).getId()).append("',");
		            	buffer.append(" 'name' : '").append(org.getChildrens().get(i).getOrgName()).append("',");
		            	buffer.append(" 'orgCode' : '").append(org.getChildrens().get(i).getOrgCode()).append("',");
				        buffer.append(" 'childrens' : []}");
	                }
	                
	                if(i < org.getChildrens().size()-1){
	                	buffer.append(", ");
	    			}
	                i++;
				} while (i<org.getChildrens().size());
	        }
            buffer.append("]");
            buffer.append("}");
        }  
        return buffer.toString();  
    }
	
	
	

}
