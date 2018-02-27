package com.xyy.workflow.definition;

import java.sql.Timestamp;

import com.jfinal.plugin.activerecord.entity.BaseEntity;
import com.jfinal.plugin.activerecord.entity.Entity;
import com.jfinal.plugin.activerecord.entity.GeneratedValue;
import com.jfinal.plugin.activerecord.entity.GenericGenerator;
import com.jfinal.plugin.activerecord.entity.Id;
import com.jfinal.plugin.activerecord.entity.JoinColumn;
import com.jfinal.plugin.activerecord.entity.ManyToOne;
import com.jfinal.plugin.activerecord.entity.Table;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;

/**
 * 表名:tb_pd_processform 类名:ProcessForm 包名:com.xyy.workflow.def 主键:id author:evan
 * mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ProcessForm", type = ProcessForm.class)
@Entity(name = "ProcessForm")
@Table(name = "tb_pd_processform")
public class ProcessForm extends BaseEntity<ProcessForm> {
	private static final long serialVersionUID = 8913360076061106720L;
	public static final String id = "id";
	public static final String pdId = "pdId";
	public static final String formId = "formId";
	public static final String formName = "formName";
	public static final String isMainForm = "isMainForm";
	public static final String isSingle = "isSingle";
	public static final String permisisionType = "permisisionType";
	public static final String excludeFields = "excludeFields";
	public static final String createTime = "createTime";
	public static final String sortNumber = "sortNumber";
	public static final String requireField = "requireField";
	public static final String showType = "showType";
	
	//无数据显示方式：0：不显示，1：创建
	public final static int SHOW_TYPE_NO = 0;
	public final static int SHOW_TYPE_CREATE = 1;

	public static final ProcessForm dao = new ProcessForm();

	public ProcessForm() {
			
	}
	
	/**
	 * 复制一份af的副本
	 * @param af，需要复制的对象
	 */
	public ProcessForm(ProcessForm af) {
		this.setFormId(af.getFormId());
		this.setFormName(af.getFormName());
		this.setIsMainForm(af.getIsMainForm());
		this.setIsSingle(af.getIsSingle());		
		this.setPermisisionType(af.getPermisisionType());
		this.setExcludeFields(af.getExcludeFields());
		this.setSortNumber(af.getSortNumber());
		this.setRequireField(af.getRequireField());
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));	
		this.setShowType(af.getShowType());
	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ProcessForm setId(String value) {
		this.set("id", value);
		return this;
	}

	private ProcessDefinition processDefinition;

	// 活动定义:private String pdId;
	@ManyToOne
	@JoinColumn(name = "pdId")
	public ProcessDefinition getProcessDefinition() {
		return this.processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
	
	
	/**
	 * 外键，流程定义id
	 */
	@XmlElement(name = "pdId", tag = "pdId", type = String.class)
	public String getPdId() {
		return this.getStr("pdId");
	}

	public ProcessForm setPdId(String value) {
		this.set("pdId", value);
		return this;
	}

	/**
	 * 外键，表单引用id
	 */
	@XmlElement(name = "formId", tag = "formId", type = String.class)
	public String getFormId() {
		return this.getStr("formId");
	}

	public ProcessForm setFormId(String value) {
		this.set("formId", value);
		return this;
	}

	/**
	 * 表单引用name
	 */
	@XmlElement(name = "formName", tag = "formName", type = String.class)
	public String getFormName() {
		return this.getStr("formName");
	}

	public ProcessForm setFormName(String value) {
		this.set("formName", value);
		return this;
	}

	/**
	 * 是否为主表单：0：否；1：是，流程表单一定为主表单，且为单态，忽略这两个参数
	 */
	@XmlElement(name = "isMainForm", tag = "isMainForm", type = Integer.class)
	public Integer getIsMainForm() {
		return this.getInt("isMainForm");
	}

	public ProcessForm setIsMainForm(Integer value) {
		this.set("isMainForm", value);
		return this;
	}

	/**
	 * 是否为单态表单：0：否；1：是
	 */
	@XmlElement(name = "isSingle", tag = "isSingle", type = Integer.class)
	public Integer getIsSingle() {
		return this.getInt("isSingle");
	}

	public ProcessForm setIsSingle(Integer value) {
		this.set("isSingle", value);
		return this;
	}

	/**
	 * 许可类型：0：乐观许可；1：悲观许可
	 */
	@XmlElement(name = "permisisionType", tag = "permisisionType", type = Integer.class)
	public Integer getPermisisionType() {
		return this.getInt("permisisionType");
	}

	public ProcessForm setPermisisionType(Integer value) {
		this.set("permisisionType", value);
		return this;
	}

	@XmlElement(name = "excludeFields", tag = "excludeFields", type = String.class)
	public String getExcludeFields() {
		return this.getStr("excludeFields");
	}

	public ProcessForm setExcludeFields(String value) {
		this.set("excludeFields", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public ProcessForm setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	/**
	 * 排序号
	 */
	@XmlElement(name = "sortNumber", tag = "sortNumber", type = Integer.class)
	public Integer getSortNumber() {
		return this.getInt("sortNumber");
	}

	public ProcessForm setSortNumber(Integer value) {
		this.set("sortNumber", value);
		return this;
	}

	@XmlElement(name = "requireField", tag = "requireField", type = String.class)
	public String getRequireField() {
		return this.getStr("requireField");
	}

	public ProcessForm setRequireField(String value) {
		this.set("requireField", value);
		return this;
	}
	
	/**
	 * 无数据是否显示：0：不显示；1：新建
	 */
	@XmlElement(name = "showType", tag = "showType", type = Integer.class)
	public Integer getShowType() {
		return this.getInt("showType");
	}
	
	public ProcessForm setShowType(Integer value) {
		this.set("showType", value);
		return this;
	}

}
