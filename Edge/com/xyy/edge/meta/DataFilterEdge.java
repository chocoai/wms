package com.xyy.edge.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

/**
 * 数据过滤规则设置
 * @author caofei
 *
 */
@XmlComponent(tag="DataFilterEdge",type=DataFilterEdge.class)
public class DataFilterEdge {
	
	private SourceBillHeadFilterEdge sourceBillHeadFilterEdge; //单据头数据过滤规则
	private SourceBillBodyFilterEdge sourceBillBodyFilterEdge; //单据体数据过滤规则
	
	public DataFilterEdge() {
		super();
	}
	public DataFilterEdge(SourceBillHeadFilterEdge sourceBillHeadFilterEdge,SourceBillBodyFilterEdge sourceBillBodyFilterEdge){
		super();
		this.sourceBillHeadFilterEdge = sourceBillHeadFilterEdge;
		this.sourceBillBodyFilterEdge = sourceBillBodyFilterEdge;
	}
	
	@XmlComponent(name = "sourceBillHeadFilterEdge", tag = "SourceBillHeadFilterEdge", type = SourceBillHeadFilterEdge.class)
	public SourceBillHeadFilterEdge getSourceBillHeadFilterEdge() {
		return sourceBillHeadFilterEdge;
	}
	public void setSourceBillHeadFilterEdge(SourceBillHeadFilterEdge sourceBillHeadFilterEdge) {
		this.sourceBillHeadFilterEdge = sourceBillHeadFilterEdge;
	}
	
	@XmlComponent(name = "sourceBillBodyFilterEdge", tag = "SourceBillBodyFilterEdge", type = SourceBillBodyFilterEdge.class)
	public SourceBillBodyFilterEdge getSourceBillBodyFilterEdge() {
		return sourceBillBodyFilterEdge;
	}
	public void setSourceBillBodyFilterEdge(SourceBillBodyFilterEdge sourceBillBodyFilterEdge) {
		this.sourceBillBodyFilterEdge = sourceBillBodyFilterEdge;
	}


	@XmlComponent(tag = "SourceBillHeadFilterEdge", type = SourceBillHeadFilterEdge.class)
	public static class SourceBillHeadFilterEdge{
		
		private Filter filter;

		@XmlComponent(name = "filter", tag = "Filter", type = Filter.class)
		public Filter getFilter() {
			return filter;
		}
		public void setFilter(Filter filter) {
			this.filter = filter;
		}

		@XmlComponent(tag = "Filter", type = Filter.class)
		public static class Filter{

			private String formula; //过滤公式
			
			public Filter() {
				super();
			}
			public Filter(String formula) {
				super();
				this.formula = formula;
			}

			@XmlText(name = "formula", type = String.class)
			public String getFormula() {
				return formula;
			}
			public void setFormula(String formula) {
				this.formula = formula;
			}
			
		}
	}
	
	@XmlComponent(tag = "SourceBillBodyFilterEdge", type = SourceBillBodyFilterEdge.class)
	public static class SourceBillBodyFilterEdge{

		private List<Filter> filter=new ArrayList<>();

		@XmlList(name = "filter", subTag = "Filter", type = Filter.class)
		public List<Filter> getFilter() {
			return filter;
		}
		public void setFilter(List<Filter> filter) {
			this.filter = filter;
		}

		@XmlComponent(tag = "Filter", type = Filter.class)
		public static class Filter{

			private String sourceKey; //源单据key
			private String formula; //过滤公式

			public Filter() {
				super();
			}
			public Filter(String sourceKey, String formula) {
				super();
				this.sourceKey = sourceKey;
				this.formula = formula;
			}
			
			@XmlText(name = "formula", type = String.class)
			public String getFormula() {
				return formula;
			}
			public void setFormula(String formula) {
				this.formula = formula;
			}
			
			@XmlAttribute(name = "sourceKey", tag = "SourceKey", type = String.class)
			public String getSourceKey() {
				return sourceKey;
			}

			public void setSourceKey(String sourceKey) {
				this.sourceKey = sourceKey;
			}
			
		}
	}
}

