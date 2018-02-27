package com.xyy.edge.meta;

import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlText;

/**
 * 规则过滤实体
 * @author caofei
 *
 */
@XmlComponent(tag="RuleFilterEdge",type=RuleFilterEdge.class)
public class RuleFilterEdge {

	private RuleHeadFilterEdge ruleHeadFilterEdge; //单据头过滤规则
	private RuleBodyFilterEdge ruleBodyFilterEdge; //单据体过滤规则
	
	public RuleFilterEdge() {
		super();
	}
	public RuleFilterEdge(RuleHeadFilterEdge ruleHeadFilterEdge,RuleBodyFilterEdge ruleBodyFilterEdge){
		super();
		this.ruleHeadFilterEdge = ruleHeadFilterEdge;
		this.ruleBodyFilterEdge = ruleBodyFilterEdge;
	}
	
	
	@XmlComponent(name = "ruleHeadFilterEdge", tag = "RuleHeadFilterEdge", type = RuleHeadFilterEdge.class)
	public RuleHeadFilterEdge getRuleHeadFilterEdge() {
		return ruleHeadFilterEdge;
	}
	public void setRuleHeadFilterEdge(RuleHeadFilterEdge ruleHeadFilterEdge) {
		this.ruleHeadFilterEdge = ruleHeadFilterEdge;
	}
	@XmlComponent(name = "ruleBodyFilterEdge", tag = "RuleBodyFilterEdge", type = RuleBodyFilterEdge.class)
	public RuleBodyFilterEdge getRuleBodyFilterEdge() {
		return ruleBodyFilterEdge;
	}
	public void setRuleBodyFilterEdge(RuleBodyFilterEdge ruleBodyFilterEdge) {
		this.ruleBodyFilterEdge = ruleBodyFilterEdge;
	}


	@XmlComponent(tag = "RuleHeadFilterEdge", type = RuleHeadFilterEdge.class)
	public static class RuleHeadFilterEdge{
		
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
			
			@XmlText(name = "formula", type = String.class)
			public String getFormula() {
				return formula;
			}
			public void setFormula(String formula) {
				this.formula = formula;
			}

			public Filter() {
				super();
			}
			public Filter(String formula){
				super();
				this.formula = formula;
			}
		}
	}
	
	@XmlComponent(tag = "RuleBodyFilterEdge", type = RuleBodyFilterEdge.class)
	public static class RuleBodyFilterEdge{

/*		private List<Filter> filter;

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

		}*/
	}
}
