package com.xyy.erp.platform.dataDockingSE.manager;
/**
*
* @auther : zhanzm
*/

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.dataDockingSE.ann.Xml;

public class TableManager {
	/**
	 * 时间戳一（同步固定条件）
	 */
	private Timestamp startTime;
	/**
	 * 时间戳二（同步固定条件）
	 */
	private Timestamp endTime;
	/**
	 * 拉取数据的源
	 */
	private String source;
	private String sourceTableName;
	/**
	 * 推送数据的目标
	 */
	private String target;
	private String targetTableName;
	/**
	 * 主键
	 */
	private String targetPrimayKey;
	/**
	 * 是否全字段同步
	 */
	private String coloums;
	/**
	 * 同步数据的筛选条件
	 */
	private String condition;
	/**
	 * 需同步的所有字段集合
	 */
	private List<String> coloum = new ArrayList<String>();
	/**
	 * 需同步的数据集合
	 */
	private List<Record> records = Collections.synchronizedList(new ArrayList<>());
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public List<Record> getRecords() {
		return records;
	}
	public void setRecords(List<Record> records) {
		this.records.addAll(records);
	}
	/**
	 * 重试机制中去重的算法
	 */
	public void removal() {
		records = Collections.synchronizedList(new ArrayList<>(new HashSet<>(records)));
	}
	public void setRecords(Record records) {
		this.records.add(records);
	}
	public void remove(List<Record> records) {
		this.records.removeAll(records);
	}
	public void clear() {
		this.records.clear();;
	}
	public String getSource() {
		return source;
	}
	@Xml(type="attr")
	public void setSource(String source) {
		this.source = source;
	}
	public String getSourceTableName() {
		return sourceTableName;
	}
	@Xml(type="attr")
	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}
	public String getTarget() {
		return target;
	}
	@Xml(type="attr")
	public void setTarget(String target) {
		this.target = target;
	}
	public String getTargetTableName() {
		return targetTableName;
	}
	@Xml(type="attr")
	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}
	public String getTargetPrimayKey() {
		return targetPrimayKey;
	}
	@Xml(type="attr")
	public void setTargetPrimayKey(String targetPrimayKey) {
		this.targetPrimayKey = targetPrimayKey;
	}
	public String getColoums() {
		return coloums;
	}
	@Xml(type="attr")
	public void setColoums(String coloums) {
		this.coloums = coloums;
	}
	public List<String> getColoum() {
		return coloum;
	}
	@Xml(type="node")
	public void setColoum(List<String> coloum) {
		this.coloum = coloum;
	}
	public String getCondition() {
		return condition;
	}
	@Xml(type="attr")
	public void setCondition(String condition) {
		this.condition = condition;
	}
	@Override
	public String toString() {
		return "DataManager [source=" + source + ", sourceTableName=" + sourceTableName + ", target=" + target
				+ ", targetTableName=" + targetTableName + ", targetPrimayKey=" + targetPrimayKey + ", coloums="
				+ coloums + ", coloum=" + coloum + "]";
	} 
}
