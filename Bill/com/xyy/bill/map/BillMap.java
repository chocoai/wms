package com.xyy.bill.map;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.util.xml.annotation.XmlList;

/**
 *
 * 
 * @author evan
 *
 */
@XmlComponent(tag = "Map", type = BillMap.class)
public class BillMap {
	/**
	 * 多次生成选项：
	 * 		AllowMulti:可以多次生成
	 *  	Forbid:禁止多次
	 * 	@author evan	 
	 * 
	 */
	public static enum AllowMultiCreate{
		AllowMulti,Forbid
	}

	private String key;
	private String caption;
	private String srcDataObjectKey;
	private String tgtDataObjectKey;
	private String maxPushValue;
	private String minPushValue;
	private String condition;//执行规则的前置条件
	
	private List<TargetTable> targetTables = new ArrayList<>();
	
	private  MapDataType srcDataType;//元数据类型
	private  MapDataType tgtDataType;//目标数据类型
	
	private  boolean tgtSave=false;//转换完成后的保存选项
	private AllowMultiCreate allowMultiCreate=AllowMultiCreate.Forbid;
	
	
	private List<Trigger> triggers=new ArrayList<>();
	@XmlList(name="triggers",tag="TriggerCollection", subTag="Trigger",type=Trigger.class)
	public List<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}

	@XmlAttribute(name="tgtSave",tag="TgtSave",type=boolean.class)
	public boolean getTgtSave() {
		return tgtSave;
	}

	public void setTgtSave(boolean tgtSave) {
		this.tgtSave = tgtSave;
	}

	@XmlAttribute(name="allowMultiCreate",tag="AllowMultiCreate",type=AllowMultiCreate.class)
	public AllowMultiCreate getAllowMultiCreate() {
		return allowMultiCreate;
	}

	public void setAllowMultiCreate(AllowMultiCreate allowMultiCreate) {
		this.allowMultiCreate = allowMultiCreate;
	}

	@XmlAttribute(name="srcDataType",tag="SrcDataType",type=MapDataType.class)
	public MapDataType getSrcDataType() {
		return srcDataType;
	}

	public void setSrcDataType(MapDataType srcDataType) {
		this.srcDataType = srcDataType;
	}

	@XmlAttribute(name="tgtDataType",tag="TgtDataType",type=MapDataType.class)
	public MapDataType getTgtDataType() {
		return tgtDataType;
	}

	public void setTgtDataType(MapDataType tgtDataType) {
		this.tgtDataType = tgtDataType;
	}

	@XmlAttribute(name="key",tag="Key",type=String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name="caption",tag="Caption",type=String.class)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@XmlAttribute(name="srcDataObjectKey",tag="SrcDataObjectKey",type=String.class)
	public String getSrcDataObjectKey() {
		return srcDataObjectKey;
	}

	public void setSrcDataObjectKey(String srcDataObjectKey) {
		this.srcDataObjectKey = srcDataObjectKey;
	}

	@XmlAttribute(name="tgtDataObjectKey",tag="TgtDataObjectKey",type=String.class)
	public String getTgtDataObjectKey() {
		return tgtDataObjectKey;
	}

	public void setTgtDataObjectKey(String tgtDataObjectKey) {
		this.tgtDataObjectKey = tgtDataObjectKey;
	}

	@XmlAttribute(name="maxPushValue",tag="MaxPushValue",type=String.class)
	public String getMaxPushValue() {
		return maxPushValue;
	}

	public void setMaxPushValue(String maxPushValue) {
		this.maxPushValue = maxPushValue;
	}

	@XmlAttribute(name="minPushValue",tag="MinPushValue",type=String.class)
	public String getMinPushValue() {
		return minPushValue;
	}

	public void setMinPushValue(String minPushValue) {
		this.minPushValue = minPushValue;
	}

	@XmlElement(tag="Condition",name="condition")
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	private List<FeedbackObject> feedbackObjects  = new ArrayList<>();
	
	@XmlList(name="feedbackObjects",tag="FeedBackCollection", subTag="FeedbackObject",type=FeedbackObject.class)
	public List<FeedbackObject> getFeedbackObjects() {
		return feedbackObjects;
	}

	public void setFeedbackObjects(List<FeedbackObject> feedbackObjects) {
		this.feedbackObjects = feedbackObjects;
	}

	@XmlList(name="targetTables",tag="TargetTableCollection", subTag="TargetTable",type=TargetTable.class)
	public List<TargetTable> getTargetTables() {
		return targetTables;
	}

	

	public void setTargetTables(List<TargetTable> targetTables) {
		this.targetTables = targetTables;
	}

	public TargetTable getTargetTable(String key) {
		for(TargetTable table:this.targetTables){
			if(table.getKey().equals(key)){
				return table;
			}
		}
		return null;
	}
	

}
