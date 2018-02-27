package com.xyy.bill.migration;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

/**
 * 
 * <DataMigration Key="" Caption="" Description="" SrcDataObjectKey="" TgtDataObjectKey=""
 StatusFieldKey="" StatusValue="" Condition="">
</DataMigration>
 * @author 
 *
 */
@XmlComponent(tag = "DataMigration", type = DataMigration.class)
public class DataMigration {
	private String key;
	
	private String caption;
	
	private String description;
	
	private String srcDataObjectKey;
	
	private String tgtDataObjectKey;
	
	private String condition;
	
	private String billStatus;
	
	
	
	@XmlAttribute(name = "billStatus", tag = "BillStatus", type = String.class)
	public String getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
	}

	private List<SourceTable> srcTableCollections = new ArrayList<>();

	@XmlAttribute(name = "key", tag = "Key", type = String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "caption", tag = "Caption", type = String.class)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@XmlAttribute(name = "description", tag = "Description", type = String.class)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlAttribute(name = "srcDataObjectKey", tag = "SrcDataObjectKey", type = String.class)
	public String getSrcDataObjectKey() {
		return srcDataObjectKey;
	}

	public void setSrcDataObjectKey(String srcDataObjectKey) {
		this.srcDataObjectKey = srcDataObjectKey;
	}

	@XmlAttribute(name = "tgtDataObjectKey", tag = "TgtDataObjectKey", type = String.class)
	public String getTgtDataObjectKey() {
		return tgtDataObjectKey;
	}

	public void setTgtDataObjectKey(String tgtDataObjectKey) {
		this.tgtDataObjectKey = tgtDataObjectKey;
	}

	@XmlText(name="condition")
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@XmlList(name="srcTableCollections",tag="SourceTableCollection", subTag="SourceTable",type=SourceTable.class)
	public List<SourceTable> getSrcTableCollections() {
		return srcTableCollections;
	}

	public void setSrcTableCollections(List<SourceTable> srcTableCollections) {
		this.srcTableCollections = srcTableCollections;
	}
	
	
}
