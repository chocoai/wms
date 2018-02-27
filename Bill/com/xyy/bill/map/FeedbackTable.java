package com.xyy.bill.map;

import java.util.ArrayList;
import java.util.List;

import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

@XmlComponent(tag = "FeedbackTable", type = FeedbackTable.class)
public class FeedbackTable {
	private String key;

	private String tableKey;

	private List<FeedbackField> feedbackFields = new ArrayList<>();

	@Override
	public int hashCode() {
		if (StringUtil.isEmpty(key))
			return super.hashCode();
		else
			return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != FeedbackTable.class)
			return false;
		FeedbackTable t = (FeedbackTable) obj;
		return t.key.equals(this.key);
	}

	@XmlAttribute(name = "key", tag = "Key", type = String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "tableKey", tag = "TableKey", type = String.class)
	public String getTableKey() {
		return tableKey;
	}

	public void setTableKey(String tableKey) {
		this.tableKey = tableKey;
	}

	@XmlList(name = "feedbackFields", subTag = "FeedbackField", type = FeedbackField.class)
	public List<FeedbackField> getFeedbackFields() {
		return feedbackFields;
	}

	public void setFeedbackFields(List<FeedbackField> feedbackFields) {
		this.feedbackFields = feedbackFields;
	}

	@XmlComponent(tag = "FeedbackField", type = FeedbackField.class)
	public static class FeedbackField {
		private String fieldKey;
		private OpSign opSign;
		private String feedFieldKey;
		private String expr;
		
		@XmlText(name="expr")
		public String getExpr() {
			return expr;
		}

		public void setExpr(String expr) {
			this.expr = expr;
		}

		@XmlAttribute(name = "fieldKey", tag = "FieldKey", type = String.class)
		public String getFieldKey() {
			return fieldKey;
		}

		public void setFieldKey(String fieldKey) {
			this.fieldKey = fieldKey;
		}

		@XmlAttribute(name = "opSign", tag = "OpSign", type = OpSign.class)
		public OpSign getOpSign() {
			return opSign;
		}

		public void setOpSign(OpSign opSign) {
			this.opSign = opSign;
		}

		@XmlAttribute(name = "feedFieldKey", tag = "FeedFieldKey", type = String.class)
		public String getFeedFieldKey() {
			return feedFieldKey;
		}

		public void setFeedFieldKey(String feedFieldKey) {
			this.feedFieldKey = feedFieldKey;
		}

	}

}
