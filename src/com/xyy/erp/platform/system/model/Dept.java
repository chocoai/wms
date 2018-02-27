package com.xyy.erp.platform.system.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.base.BaseDept;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Dept extends BaseDept<Dept> {
	public static final Dept dao = new Dept();

	public Dept(){
		set("id", UUIDUtil.newUUID());
	}
	
	/**
	 * 分页查询组织信息
	 * 
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @return 分页数据
	 */
	public Page<Dept> paginate(int pageNumber, int pageSize) {
		return paginate(pageNumber, pageSize, "SELECT b.*,a.deptName AS parentName", "FROM tb_sys_dept a JOIN tb_sys_dept b WHERE a.id = b.parentId ORDER BY id ASC");
	}

	/**
	 * 添加组织信息
	 * @param group 
	 * @return 
	 */
	public boolean add(Dept group) {
		Record record = new Record().set("id", group.getId()).set("parentId", group.getParentId())
				.set("deptName", group.getDeptName()).set("sortNo", group.getSortNo())
				.set("state", group.getState()).set("createTime", new Date())
				.set("creator", group.getCreator()).set("updateTime", new Date())
				.set("remark", group.getRemark());
		return Db.save("tb_sys_dept", record);
	}
	
	/**
	 * 根据 ID查询组织信息
	 * @param id 
	 * @return 
	 */
	public Dept getDeptById(String id){
		return dao.findById(id);
	}
	
	/**
	 * 根据名称模糊查询组织信息
	 * @param groupName 
	 * @return
	 */
	public List<Dept> getDeptByName(String groupName){
		List<Dept> groups = dao.find("select * from tb_sys_dept where deptName like " +"'%"+groupName+"%'");
		return groups;
	}
	
	
	private Dept parent;

	private List<Dept> chidrens=new ArrayList<Dept>();
	
	public void addChild(Dept child){
		this.chidrens.add(child);
	}
	
	public Dept getParent() {
		return parent;
	}

	public void setParent(Dept parent) {
		this.parent = parent;
	}

	public List<Dept> getChidrens() {
		return chidrens;
	}

	public void setChidrens(List<Dept> chidrens) {
		this.chidrens = chidrens;
	}
	
	
}
