package com.xyy.workflow.definition;

import java.util.List;

import com.jfinal.plugin.activerecord.entity.BaseEntity;
import com.jfinal.plugin.activerecord.entity.Entity;
import com.jfinal.plugin.activerecord.entity.GeneratedValue;
import com.jfinal.plugin.activerecord.entity.GenericGenerator;
import com.jfinal.plugin.activerecord.entity.Id;
import com.jfinal.plugin.activerecord.entity.JoinColumn;
import com.jfinal.plugin.activerecord.entity.ManyToOne;
import com.jfinal.plugin.activerecord.entity.OneToMany;
import com.jfinal.plugin.activerecord.entity.Table;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;

/**
 * 表名:tb_pd_processtypedefinition 类名:ProcessTypeDefinition
 * 包名:com.xyy.workflow.def2 主键:id author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ProcessTypeDefinition", type = ProcessTypeDefinition.class)
@Entity(name = "ProcessTypeDefinition")
@Table(name = "tb_pd_processtypedefinition")
public class ProcessTypeDefinition extends BaseEntity<ProcessTypeDefinition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3572035231233800608L;
	public static final String id = "id";
	public static final String name = "name";
	public static final String parentId = "parentId";
	public static final String parentName = "parentName";
	public static final String desc = "desc";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";

	public static final ProcessTypeDefinition dao = new ProcessTypeDefinition();

	public ProcessTypeDefinition() {

	}

	
	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ProcessTypeDefinition setId(String value) {
		this.set("id", value);
		return this;
	}

	/**
	 * 类别名称
	 */
	@XmlElement(name = "name", tag = "name", type = String.class)
	public String getName() {
		return this.getStr("name");
	}

	public ProcessTypeDefinition setName(String value) {
		this.set("name", value);
		return this;
	}

	
	
	
	/**
	 * 父节点ID
	 */
	@XmlElement(name = "parentId", tag = "parentId", type = String.class)
	public String getParentId() {
		return this.getStr("parentId");
	}

	public ProcessTypeDefinition setParentId(String value) {
		this.set("parentId", value);
		return this;
	}
	
	private ProcessTypeDefinition parent;
	@ManyToOne
	@JoinColumn(name="parentId")
	public ProcessTypeDefinition getParent() {
		this.lazyLoadEntity("parent");
		return parent;
	}

	public void setParent(ProcessTypeDefinition parent) {
		this.parent = parent;
	}
	
	

	private List<ProcessTypeDefinition> chidrens;
	@OneToMany(mappedBy="parent")
	public List<ProcessTypeDefinition> getChidrens() {
		this.lazyLoadEntity("chidrens");
		return chidrens;
	}
	public void setChidrens(List<ProcessTypeDefinition> chidrens) {
		this.chidrens = chidrens;
	}
	

	/**
	 * 父节点名称
	 */
	@XmlElement(name = "parentName", tag = "parentName", type = String.class)
	public String getParentName() {
		return this.getStr("parentName");
	}

	public ProcessTypeDefinition setParentName(String value) {
		this.set("parentName", value);
		return this;
	}

	/**
	 * 描述
	 */
	@XmlElement(name = "desc", tag = "desc", type = String.class)
	public String getDesc() {
		return this.getStr("desc");
	}

	public ProcessTypeDefinition setDesc(String value) {
		this.set("desc", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public ProcessTypeDefinition setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public ProcessTypeDefinition setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}
	
	
	
}
