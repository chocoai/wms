package com.xyy.edge.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.edge.meta.BillTransformEdge.TargetBillField.CategoryType;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

/**
 * 单据转换规则实体
 * @author caofei
 *
 */
@XmlComponent(tag="BillTransformEdge",type=BillTransformEdge.class)
public class BillTransformEdge {
	//单据头转换规则
	private List<TargetBillField> billHeadTransformEdge = new ArrayList<>();
	//单据体转换规则
	private List<BillBodyEdge> billBodyTransformEdge = new ArrayList<>();

	@XmlList(name="billHeadTransformEdge",tag="BillHeadTransformEdge",subTag="TargetBillField",type=TargetBillField.class)
	public List<TargetBillField> getBillHeadTransformEdge() {
		return billHeadTransformEdge;
	}

	public void setBillHeadTransformEdge(List<TargetBillField> billHeadTransformEdge) {
		this.billHeadTransformEdge = billHeadTransformEdge;
	}

	@XmlList(name="billBodyTransformEdge",tag="BillBodyTransformEdge",subTag="BillBodyEdge",type=BillBodyEdge.class)
	public List<BillBodyEdge> getBillBodyTransformEdge() {
		return billBodyTransformEdge;
	}

	public void setBillBodyTransformEdge(List<BillBodyEdge> billBodyTransformEdge) {
		this.billBodyTransformEdge = billBodyTransformEdge;
	}
	
	
	/**
	 * 目标分录转换规则
	 * @author Chen
	 *
	 */
	@XmlComponent(tag="BillBodyEdge",type=BillBodyEdge.class)
	public static class BillBodyEdge {
		//在一个目标分录上可以施加多个明细转换规则
		private List<BillDtlEdge> billDtlEdges;
		//本次明细转化规则针对的源单明细
		private String targetDltKey;

		@XmlList(name="billDtlEdges",tag="",subTag="BillDtlEdge",type=BillDtlEdge.class)
		public List<BillDtlEdge> getBillDtlEdges() {
			return billDtlEdges;
		}

		public void setBillDtlEdges(List<BillDtlEdge> billDtlEdges) {
			this.billDtlEdges = billDtlEdges;
		}
		
		@XmlAttribute(name="targetDltKey",tag="targetDltKey",type=String.class)
		public String getTargetDltKey() {
			return targetDltKey;
		}

		public void setTargetDltKey(String targetDltKey) {
			this.targetDltKey = targetDltKey;
		}

		public void setfieldName(DataTableMeta dataTableMeta) {
			for (BillDtlEdge billDtlEdge : this.getBillDtlEdges()) {
				List<TargetBillField> newList = new ArrayList<>();
				for (Field table : dataTableMeta.getFields()) {
					TargetBillField newField = new TargetBillField();
					newField.setCategory(CategoryType.property);
					newField.setFieldName(table.getFieldName());
					newField.setKey(table.getFieldKey());
					for (TargetBillField bodyfield : billDtlEdge.getTargetBillFields()) {
						if (bodyfield.getKey().equals(table.getFieldKey())) {
							newField.setFieldName(table.getFieldName());
							newField.setData(bodyfield.getData());
						}
					}
					newList.add(newField);
				}
				billDtlEdge.getTargetBillFields().clear();
				billDtlEdge.getTargetBillFields().addAll(newList);
			}
			
		}

		public void setFieldContent(DataTableMeta dataTableMeta) {
			this.setTargetDltKey(dataTableMeta.getKey());
			List<BillDtlEdge> newList = new ArrayList<>();
			newList.add(dataTableMeta.getTargetFileds());
			this.setBillDtlEdges(newList);
			
		}

	}
	

	@XmlComponent(tag="BillDtlEdge",type=BillDtlEdge.class)
	public static class BillDtlEdge {
		private String sourceDltKey;
		
		private List<TargetBillField> targetBillFields;
	
		
		@XmlAttribute(name="sourceDltKey",tag="sourceDltKey",type=String.class)
		public String getSourceDltKey() {
			return sourceDltKey;
		}
	
		public void setSourceDltKey(String sourceDltKey) {
			this.sourceDltKey = sourceDltKey;
		}
	
		@XmlList(name="targetBillFields",tag="",subTag="TargetBillField",type=TargetBillField.class)
		public List<TargetBillField> getTargetBillFields() {
			return targetBillFields;
		}
	
		public void setTargetBillFields(List<TargetBillField> targetBillFields) {
			this.targetBillFields = targetBillFields;
		}

		public String parseAndGetSourceDltKey() {
			for(TargetBillField tbf:this.targetBillFields){
				String expr=tbf.getData();
				if(tbf.getCategory()==CategoryType.property && !StringUtil.isEmpty(expr) && expr.startsWith("=")){
					if(expr.indexOf(".")>-1){
						return expr.substring(1, expr.indexOf("."));
					}
				}
			}
			return null;
		}
	}
	

	public static class TargetBillField {

		private String key;
		
		private CategoryType category=CategoryType.property;
		
		private String data;
		
		private String targetDltKey;
		
		private String fieldName;

		@XmlAttribute(name="Key",tag="Key",type=String.class)
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		@XmlAttribute(name="Category",tag="Category",type=CategoryType.class)
		public CategoryType getCategory() {
			return category;
		}

		public void setCategory(CategoryType category) {
			this.category = category;
		}
		
		@XmlText(name = "data")
		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
		
		@XmlAttribute(name="targetDltKey",tag="targetDltKey",type=String.class)
		public String getTargetDltKey() {
			return targetDltKey;
		}

		public void setTargetDltKey(String targetDltKey) {
			this.targetDltKey = targetDltKey;
		}

		public static enum CategoryType {
			property, group,formulaGroup
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		
	
	}

	public void setfieldName(DataTableMeta headTable, List<DataTableMeta> bodyList) {
		List<TargetBillField> newList = new ArrayList<>();
		for (Field field : headTable.getFields()) {
			TargetBillField newField = new TargetBillField();
			newField.setCategory(CategoryType.property);
			newField.setFieldName(field.getFieldName());
			newField.setKey(field.getFieldKey());
			for (TargetBillField headField : this.getBillHeadTransformEdge()) {
				if (field.getFieldKey().equals(headField.getKey())) {
					newField.setData(headField.getData());
					newField.setCategory(headField.getCategory());
				}
			}
			newList.add(newField);
		}
		for (TargetBillField headField : this.getBillHeadTransformEdge()) {
			if (headField.getCategory().equals(CategoryType.group)||headField.getCategory().equals(CategoryType.formulaGroup)) {
				TargetBillField newField = new TargetBillField();
				newField.setCategory(CategoryType.property);
				for (DataTableMeta dataTableMeta : bodyList) {
					if (dataTableMeta.getKey().equals(headField.getKey())) {
						newField.setFieldName(dataTableMeta.getCaption());
					}
				}
				newField.setKey(headField.getKey());
				newField.setData(headField.getData());
				newField.setCategory(headField.getCategory());
				newList.add(newField);
			}
		}
		this.getBillHeadTransformEdge().clear();
		this.getBillHeadTransformEdge().addAll(newList);
		
		List<BillBodyEdge> newBodyList = new ArrayList<>();
		for (DataTableMeta dataTableMeta : bodyList) {
			BillBodyEdge newBody = new BillBodyEdge();
			newBody.setFieldContent(dataTableMeta);
			for (BillBodyEdge body : this.getBillBodyTransformEdge()) {
				if (body.getTargetDltKey().equals(dataTableMeta.getKey())) {
					body.setfieldName(dataTableMeta);
					newBody.setBillDtlEdges(body.getBillDtlEdges());
					newBody.setTargetDltKey(body.getTargetDltKey());
				}
			}
			newBodyList.add(newBody);
		}
		this.getBillBodyTransformEdge().clear();
		this.getBillBodyTransformEdge().addAll(newBodyList);
	}

}
