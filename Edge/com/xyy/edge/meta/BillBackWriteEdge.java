package com.xyy.edge.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.util.xml.annotation.XmlList;

/**
 * 单据回写规则实体
 * @author caofei
 *
 */
@XmlComponent(tag="BillBackWriteEdge",type=BillBackWriteEdge.class)
public class BillBackWriteEdge {
	
	private BillHeadBackWriteEdge billHeadBackWriteEdge=new BillHeadBackWriteEdge();
	
	private BillBodyBackWriteEdge billBodyBackWriteEdge=new BillBodyBackWriteEdge();
	
	public void setBillHeadBackWriteEdge(BillHeadBackWriteEdge billHeadBackWriteEdge) {
		this.billHeadBackWriteEdge = billHeadBackWriteEdge;
	}

	public void setBillBodyBackWriteEdge(BillBodyBackWriteEdge billBodyBackWriteEdge) {
		this.billBodyBackWriteEdge = billBodyBackWriteEdge;
	}

	@XmlComponent(name="billHeadBackWriteEdge",tag="BillHeadBackWriteEdge",type=BillHeadBackWriteEdge.class)
	public BillHeadBackWriteEdge getBillHeadBackWriteEdge() {
		return billHeadBackWriteEdge;
	}

	
	@XmlComponent(name="billBodyBackWriteEdge",tag="BillBodyBackWriteEdge",type=BillBodyBackWriteEdge.class)
	public BillBodyBackWriteEdge getBillBodyBackWriteEdge() {
		return billBodyBackWriteEdge;
	}

	
	@XmlComponent(name = "BillHeadBackWriteEdge", tag = "BillHeadBackWriteEdge", type = BillHeadBackWriteEdge.class)
	public static class BillHeadBackWriteEdge {
		private List<BackWriteRule> backWriteRules=new ArrayList<>();
		@XmlList(name="backWriteRules",tag="",subTag="BackWriteRule",type=BackWriteRule.class)
		public List<BackWriteRule> getBackWriteRules() {
			return backWriteRules;
		}

		public void setBackWriteRules(List<BackWriteRule> backWriteRules) {
			this.backWriteRules = backWriteRules;
		}

	}
	
	@XmlComponent(name = "BillBodyBackWriteEdge", tag = "BillBodyBackWriteEdge", type = BillBodyBackWriteEdge.class)
	public static class BillBodyBackWriteEdge {
		private List<BackWriteRule> backWriteRules=new ArrayList<>();
		@XmlList(name="backWriteRules",tag="",subTag="BackWriteRule",type=BackWriteRule.class)
		public List<BackWriteRule> getBackWriteRules() {
			return backWriteRules;
		}

		public void setBackWriteRules(List<BackWriteRule> backWriteRules) {
			this.backWriteRules = backWriteRules;
		}
		
	}
	
	@XmlComponent(tag="BackWriteRule",type=BackWriteRule.class)
	public static class BackWriteRule {
		
		private Type type;
		//规则
		private String ruleExpr;
		//条件
		private String conditionExp ;
		
		public BackWriteRule() {
			super();
		}

		public BackWriteRule(Type type, String ruleExpr, String conditionExp) {
			super();
			this.type = type;
			this.ruleExpr = ruleExpr;
			this.conditionExp = conditionExp;
		}

		@XmlElement(name="ruleExpr",tag="RuleExpr")
		public String getRuleExpr() {
			return ruleExpr;
		}

		public void setRuleExpr(String ruleExpr) {
			this.ruleExpr = ruleExpr;
		}

		@XmlElement(name="conditionExp",tag="ConditionExp")
		public String getConditionExp() {
			return conditionExp;
		}

		public void setConditionExp(String conditionExp) {
			this.conditionExp = conditionExp;
		}

		@XmlAttribute(name = "type", tag = "Type", type=Type.class)
		public Type getType() {
			return type;
		}

		public void setType(Type Type) {
			this.type = Type;
		}
		
		public static enum Type {
			Rule,//规则反写
			Business,//业务反写
			Delete//删除反反写接口程序
		}
		
	}
}
