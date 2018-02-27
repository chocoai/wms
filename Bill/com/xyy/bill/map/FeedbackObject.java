package com.xyy.bill.map;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlList2;

@XmlComponent(tag = "FeedbackObject", type = FeedbackObject.class)
public class FeedbackObject {
	private String tableKey;
	private String billStatus;
	private String condition;
	private List<FeedbackTable> feedbackTables = new ArrayList<>();

	@XmlAttribute(name = "billStatus", tag = "BillStatus", type = String.class)
	public String getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
	}
	
	

	@XmlList(name = "feedbackTables", tag = "FeedbackTableCollection", subTag = "FeedbackTable", type = FeedbackTable.class)
	public List<FeedbackTable> getFeedbackTables() {
		return feedbackTables;
	}

	public void setFeedbackTables(List<FeedbackTable> feedbackTables) {
		this.feedbackTables = feedbackTables;
	}

	@XmlAttribute(name = "tableKey", tag = "TableKey", type = String.class)
	public String getTableKey() {
		return tableKey;
	}

	public void setTableKey(String tableKey) {
		this.tableKey = tableKey;
	}

	@XmlElement(name = "condition", tag = "Condition")
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
