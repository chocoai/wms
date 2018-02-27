package com.xyy.erp.platform.system.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.CommonTools;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.RandomUtil;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.erp.platform.system.model.Dept;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.StringUtil;

/**
 * 组织信息服务层
 * @author sl
 *
 */
public class DeptService {

	/**
	 * 返回分页数据
	 */
	public Page<Dept> paginate(int pageNumber, int pageSize) {
		return Dept.dao.paginate(pageNumber, pageSize);
	}
	
	/**
	 * 添加组织信息
	 */
	public boolean save(String deptJosn) {
		@SuppressWarnings("unchecked")
		Map<String, Object> groupMap = (Map<String, Object>) JSON.parse(deptJosn);
		Dept dept = new Dept();
		dept._setAttrs(groupMap);
		dept.setZhujima(CommonTools.getPinYinChar(dept.getDeptName()));
		if((groupMap.get("id")==null)){
			String head=CommonTools.getPinYinHeadChar(dept.getDeptName());
			dept.setDeptId(head+TimeUtil.format(new Date(), "MMddHH")+RandomUtil.getRandomCode(3));
			dept.setCode(head+TimeUtil.format(new Date(), "MMddHH")+RandomUtil.getRandomCode(3));
			boolean result=dept.save();
			if(result){
				doWork(dept.getDeptId(),true);
			}
			return result;
		}else{
			dept.setUpdateTime(new Date());
			boolean result=dept.update();
			if(result){
				doWork(dept.getDeptId(),true);
			}
			return result;
		}
	}
	
	public void doWork(String DeptId,boolean add){
		Record res=Db.findFirst("select * from tb_sys_dept where deptId=?",DeptId);
		Record wms=new Record();
		wms.set("bmid", res.get("deptId"));
		wms.set("bmbh",res.get("code"));//目前没有
		wms.set("bm", res.get("deptName"));
		wms.set("zjm", res.get("zhujima"));//没有
		wms.set("denglrq", res.get("createTime"));
		
		wms.set("delrq", res.get("updateTime"));
		wms.set("beactive", res.getInt("state")==1?"Y":"N");
		wms.set("dmrq", res.get("updateTime"));
		
		wms.set("WMFLG", "N");
		wms.set("lastmodifytime", res.get("updateTime"));
		if(add){
			Db.use(DictKeys.db_dataSource_wms_mid).save("int_inf_bmdoc", wms);
		}else{
			Db.use(DictKeys.db_dataSource_wms_mid).update("int_inf_bmdoc","bmid", wms);
		}
	}
	
	/**
	 * 更新组织信息
	 */
//	public String update(String deptJosn) {
//		try {
//			@SuppressWarnings("unchecked")
//			Map<String, Object> groupMap = (Map<String, Object>) JSON.parse(deptJosn);
//			Dept group = new Dept();
//			group._setAttrs(groupMap);
//			group.setUpdateTime(new Date());
//			if(group.update()){
//				return "1";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "-1";
//		}
//		return "-1";
//	}

	/**
	 * 删除组织
	 */
	public boolean delete(String id){
		return Dept.dao.deleteById(id);
		
	}
	//查询指定parentId的组织信息
	public void deleteByParentId(String id){
		List<Dept> listDept = Dept.dao.find("select * from tb_sys_dept where parentId = '" + id + "' order by sortNo");
		for(Dept g : listDept){
			Dept.dao.deleteById(g.getId());
		}
		
	}
	
	/**
	 * 查询组织数量
	 */
	public List<Dept> findAllDept(){
		return Dept.dao.find("select * from tb_sys_dept order by sortNo");
	}
	
	/**
	 * 根据 ID查询组织信息
	 */
	public Dept getDeptById(String id){
		Dept dept = Dept.dao.getDeptById(id);
		if (null != dept ) {
			return dept;
		}else{
			return null;
		}
	}
	
	/**
	 * 查询组织树
	 * @return 组织信息
	 */
	public String queryDeptTree(){
		List<Dept> groupTree = Dept.dao.find("select * from tb_sys_dept order by sortNo");
		Map<String,Dept> groupMap=new HashMap<String,Dept>();
		Dept root=null;
		for(Dept d:groupTree){
			groupMap.put(d.getId(), d);
		}
		for(Dept d:groupTree){
			if(d.getParentId()==null){
				root=d;
			}
			Dept parent=groupMap.get(d.getParentId());
			if(parent!=null)
			   parent.addChild(d);
			d.setParent(parent);
			
		}
		return "["+iteratorTree(root)+"]";
	 }
	//遍历树
	public String iteratorTree(Dept dept)  
    {  
        StringBuilder buffer = new StringBuilder();  
        if(dept != null)   
        {     
        	buffer.append("{");  
        	buffer.append(" 'id' : '").append(dept.getId()).append("',");
        	buffer.append(" 'parentId' : '").append(dept.getParentId()).append("',");
	        buffer.append(" 'name' : '").append(dept.getDeptName()).append("',");
	        buffer.append(" 'children' : [");
	        if(null!=dept.getChidrens() && dept.getChidrens().size()>0){
	    		int i = 0;
	            do {
	            	Dept index = dept.getChidrens().get(i);
	            	
	                if (index.getChidrens() != null && index.getChidrens().size() > 0 )   
	                {     
	                    buffer.append(iteratorTree(index));  
	                }else{
	                	buffer.append("{");  
		            	buffer.append(" 'id' : '").append(dept.getChidrens().get(i).getId()).append("',");
				        buffer.append(" 'name' : '").append(dept.getChidrens().get(i).getDeptName()).append("',");
				        buffer.append(" 'children' : []}");
	                }
	                
	                if(i < dept.getChidrens().size()-1){
	                	buffer.append(", ");
	    			}
	                i++;
				} while (i<dept.getChidrens().size());
	        }
            buffer.append("]");
            buffer.append("}");
        }  
        return buffer.toString();  
    }

	/**
	 * 查询所有用户名
	 */
	public List<User> findUserName() {
		return User.dao.find("select id,loginName from tb_sys_user order by sortNo");
	}

	/**
	 * 根据ID查询组织下的用户信息
	 * @param groupId
	 * @return 用户信息
	 */
	public Page<User> queryDeptUser(String groupId, int pageIndex, int pageSize) {
//		String sql = "select u.* from `tb_sys_user` u where u.id in "
//				+ "(select t.userId from `tb_sys_dept_user_relation` t "
//				+ "where t.groupId = '"+groupId+"') limit "+pageIndex+","+pageSize;
		return User.dao.paginate(pageIndex, pageSize, "select u.*", "from `tb_sys_user` u where u.id in (select t.userId from `tb_sys_dept_user_relation` t where t.deptId = '"+groupId+"') ORDER BY u.id ASC ");
	}
	//根据ID查询组织下的用户信息ID
	public List<User> queryGroupUserCount(String deptId) {
		List<User> users = User.dao.find("select u.id from `tb_sys_user` u where u.id in (select t.userId from `tb_sys_dept_user_relation` t where t.deptId = '"+deptId+"') order by u.sortNo");
		return users;
	}
	
	//根据ID查询组织下的用户信息
		public List<User> queryGroupUser(String deptId) {
			List<User> users = User.dao.find("select * from `tb_sys_user` u where u.id in (select t.userId from `tb_sys_dept_user_relation` t where t.deptId = '"+deptId+"') order by u.sortNo");
			return users;
		}

	//查询指定parentId的组织信息
	public Page<Dept> queryByParentId(String id,int pageIndex,int pageSize){
		if(!StringUtil.isEmpty(id)){
			return Dept.dao.paginate(pageIndex, pageSize,"select *","from tb_sys_dept where id= '" + id + "' or parentId = '" + id + "' order by id");
		}else{
			return Dept.dao.paginate(pageIndex, pageSize,"select *","from tb_sys_dept  order by id");
		}
	}
}
