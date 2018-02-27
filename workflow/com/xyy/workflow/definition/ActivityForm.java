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
 * 表名:tb_pd_activityform 类名:ActivityForm 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ActivityForm", type = ActivityForm.class)
@Entity(name = "ActivityForm")
@Table(name = "tb_pd_activityform")
public class ActivityForm extends BaseEntity<ActivityForm> {
	private static final long serialVersionUID = 231998336585494503L;
	// canMainForm,是否引用主表单，主表单即流程表单，后续节点可以多次引用主表单
	public final static int ACTIVTY_MAIN_FORM_TASK = 0;// 该表单是任务表单，不是主表单，每个任务实例对应新生成一个任务表单；
	public final static int ACTIVTY_MAIN_FORM_PROCESS = 1;// 主表单，即流程表单，该表单只能流程关联

	// canSingle:单态表单，在整个流程实例中仅有有一份，请表单本身就是一种单态表单，单态表单在任务节点时，其他任务节点可以引用，但不可以再次创建，目前版本没有实现扣续版本实现这个概念
	public final static int ACTIVTY_SINGLE_FORM_NO = 0;
	public final static int ACTIVTY_SINGLE_FORM_YES = 1;
	
	//无数据显示方式：0：不显示，1：隐藏
	public final static int SHOW_TYPE_NO = 0;
	public final static int SHOW_TYPE_CREATE = 1;
	
	public static final String id = "id";
	public static final String adId = "adId";
	public static final String formId = "formId";
	public static final String formName = "formName";
	public static final String canMainForm = "canMainForm";
	public static final String canSingle = "canSingle";
	public static final String permisisionType = "permisisionType";
	public static final String excludeFields = "excludeFields";
	public static final String createTime = "createTime";
	public static final String sortNumber = "sortNumber";
	public static final String requireField = "requireField";
	public static final String showType = "showType";

	public static final ActivityForm dao = new ActivityForm();

	public ActivityForm() {

	}

	/**
	 * 复制一份af的副本
	 * 
	 * @param af，需要复制的对象
	 */
	public ActivityForm(ActivityForm af) {
		this.setFormId(af.getFormId());
		this.setFormName(af.getFormName());
		this.setCanMainForm(af.getCanMainForm());
		this.setCanSingle(af.getCanSingle());
		this.setPermisisionType(af.getPermisisionType());
		this.setExcludeFields(af.getExcludeFields());
		this.setRequireField(af.getRequireField());
		this.setSortNumber(af.getSortNumber());
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

	public ActivityForm setId(String value) {
		this.set("id", value);
		return this;
	}

	private ActivityDefinition activityDefinition;

	// 活动定义:private String adId;
	@ManyToOne
	@JoinColumn(name = "adId")
	public ActivityDefinition getActivityDefinition() {
		return activityDefinition;
	}

	public void setActivityDefinition(ActivityDefinition activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	/**
	 * 外键，活动定义id
	 */
	@XmlElement(name = "adId", tag = "adId", type = String.class)
	public String getAdId() {
		return this.getStr("adId");
	}

	public ActivityForm setAdId(String value) {
		this.set("adId", value);
		return this;
	}

	/**
	 * 外键，表单引用id
	 */
	@XmlElement(name = "formId", tag = "formId", type = String.class)
	public String getFormId() {
		return this.getStr("formId");
	}

	public ActivityForm setFormId(String value) {
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

	public ActivityForm setFormName(String value) {
		this.set("formName", value);
		return this;
	}

	/**
	 * 是否为主表单：0：否；1：是
	 */
	@XmlElement(name = "canMainForm", tag = "canMainForm", type = Integer.class)
	public Integer getCanMainForm() {
		return this.getInt("canMainForm");
	}

	public ActivityForm setCanMainForm(Integer value) {
		this.set("canMainForm", value);
		return this;
	}

	/**
	 * 是否为单态表单：0：否；1：是
	 */
	@XmlElement(name = "canSingle", tag = "canSingle", type = Integer.class)
	public Integer getCanSingle() {
		return this.getInt("canSingle");
	}

	public ActivityForm setCanSingle(Integer value) {
		this.set("canSingle", value);
		return this;
	}

	/**
	 * 许可类型：0：乐观许可；1：悲观许可
	 */
	@XmlElement(name = "permisisionType", tag = "permisisionType", type = Integer.class)
	public Integer getPermisisionType() {
		return this.getInt("permisisionType");
	}

	public ActivityForm setPermisisionType(Integer value) {
		this.set("permisisionType", value);
		return this;
	}

	/**
	 * 无数据是否显示：0：不显示；1：新建
	 */
	@XmlElement(name = "showType", tag = "showType", type = Integer.class)
	public Integer getShowType() {
		return this.getInt("showType");
	}
	
	public ActivityForm setShowType(Integer value) {
		this.set("showType", value);
		return this;
	}
	@XmlElement(name = "excludeFields", tag = "excludeFields", type = String.class)
	public String getExcludeFields() {
		return this.getStr("excludeFields");
	}

	public ActivityForm setExcludeFields(String value) {
		this.set("excludeFields", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public ActivityForm setCreateTime(java.sql.Timestamp value) {
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

	public ActivityForm setSortNumber(Integer value) {
		this.set("sortNumber", value);
		return this;
	}

	@XmlElement(name = "requireField", tag = "requireField", type = String.class)
	public String getRequireField() {
		return this.getStr("requireField");
	}

	public ActivityForm setRequireField(String value) {
		this.set("requireField", value);
		return this;
	}

}
