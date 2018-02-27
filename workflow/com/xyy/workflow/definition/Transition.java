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
import com.xyy.workflow.exception.TransitionTakeException;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 表名:tb_pd_transitions 类名:Transition 包名:com.xyy.workflow.def 主键:id author:evan
 * mail:lm@cdtfax.com
 */
@XmlComponent(tag = "Transition", type = Transition.class)
@Entity(name = "Transition")
@Table(name = "tb_pd_transitions")
public class Transition extends BaseEntity<Transition> {
	private static final long serialVersionUID = -9160982668232144234L;
	public static final String id = "id";
	public static final String adId = "adId";
	public static final String name = "name";
	public static final String labelName = "labelName";
	public static final String to = "to";
	public static final String expr = "expr";
	public static final String shape = "shape";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";
	public static final String cid = "cid";

	public static final Transition dao = new Transition();

	public Transition() {

	}
	
	public Transition(Transition tran) {
		this.setName(tran.getName());
		this.setLabelName(tran.getLabelName());
		this.setTo(tran.getTo());
		this.setExpr(tran.getExpr());
		this.setShape(tran.getShape());
		this.setCid(tran.getCid());
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));
	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public Transition setId(String value) {
		this.set("id", value);
		return this;
	}
	
	
	private ActivityDefinition activityDefinition;

	@ManyToOne
	@JoinColumn(name = "adId")
	public ActivityDefinition getActivityDefinition() {
		this.lazyLoadEntity("activityDefinition");
		return activityDefinition;
	}

	public void setActivityDefinition(ActivityDefinition activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	/**
	 * 指向活动定义id
	 */
	@XmlElement(name = "adId", tag = "adId", type = String.class)
	public String getAdId() {
		return this.getStr("adId");
	}

	public Transition setAdId(String value) {
		this.set("adId", value);
		return this;
	}

	/**
	 * 转移名称
	 */
	@XmlElement(name = "name", tag = "name", type = String.class)
	public String getName() {
		return this.getStr("name");
	}

	public Transition setName(String value) {
		this.set("name", value);
		return this;
	}

	@XmlElement(name = "labelName", tag = "labelName", type = String.class)
	public String getLabelName() {
		return this.getStr("labelName");
	}

	public Transition setLabelName(String value) {
		this.set("labelName", value);
		return this;
	}

	/**
	 * 转移目标
	 */
	@XmlElement(name = "to", tag = "to", type = String.class)
	public String getTo() {
		return this.getStr("to");
	}

	public Transition setTo(String value) {
		this.set("to", value);
		return this;
	}

	/**
	 * 转移表达式
	 */
	@XmlElement(name = "expr", tag = "expr", type = String.class)
	public String getExpr() {
		return this.getStr("expr");
	}

	public Transition setExpr(String value) {
		this.set("expr", value);
		return this;
	}

	/**
	 * 转移线的图形
	 */
	@XmlElement(name = "shape", tag = "shape", type = String.class)
	public String getShape() {
		return this.getStr("shape");
	}

	public Transition setShape(String value) {
		this.set("shape", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public Transition setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public Transition setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}

	@XmlElement(name = "cid", tag = "cid", type = String.class)
	public String getCid() {
		return this.getStr("cid");
	}

	public Transition setCid(String value) {
		this.set("cid", value);
		return this;
	}
	
	/**
	 * 执行转移，并进行相关令牌的交接工作
	 * 
	 * @param ec
	 * @throws Exception
	 */
	public void take(ExecuteContext ec) throws Exception {
		if (this.getTo() == null || "".equals(this.getTo().trim())) {
			throw new TransitionTakeException(ec,this,840);
		}
		ActivityDefinition ad = ec.getProcessInstance().getProcessDefinition()
				.getActivityDefByName(this.getTo().trim());
		if (ad == null) {
			throw new TransitionTakeException(ec,this,841);
		}
		Token token = ec.getToken();
		ActivityInstance curAi=token.getActivityInstance();
		token.setActivityInstance(null);// 进行令牌的交接
		ProcessInstance pi=ec.getProcessInstance();
		ActivityInstance ai = new ActivityInstance(ec.getProcessInstance(), ad);
		ai.setToken(token);
		pi.getActivityInstances().add(ai);
		token.setActivityInstance(ai);
		token.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		ai.saveOrUpdate();
		token.saveOrUpdate();
		saveProcessHistory(ec,pi,curAi, ai);//保存完成流程历史
		//__$dynameicUsrList==>List<Members> dynamicUserList
		ai.getNodeProcessor().enter(new ExecuteContext(token));
	}
	
	private void saveProcessHistory(ExecuteContext ec,ProcessInstance pi,ActivityInstance curAi, ActivityInstance newAi){
		ProcessHistory ph = new ProcessHistory();
		ph.setPiId(pi.getId());
		ph.setTid(ec.getToken().getId());
		ph.setOldAiId(curAi.getId());
		ph.setOldAdId(curAi.getActivityDefinition().getId());
		ph.setNewAiId(newAi.getId());
		ph.setNewAdId(newAi.getActivityDefinition().getId());
		ph.setTime(new Timestamp(System.currentTimeMillis()));
		ph.saveOrUpdate();
	}
	

}
