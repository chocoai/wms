package com.xyy.edge.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

/**
 * 分组合并规则实体
 * @author caofei
 *
 */
@XmlComponent(tag="BillGroupJoinEdge",type=BillGroupJoinEdge.class)
public class BillGroupJoinEdge {
	
	//单据头分组合并规则
	private BillHeadGroupJoinEdge billHeadGroupJoinEdge;
	//单据体分组合并规则
	private BillBodyGroupJoinEdge billBodyGroupJoinEdge;
	
	
	
	public BillGroupJoinEdge() {
		super();
	}

	public BillGroupJoinEdge(BillHeadGroupJoinEdge billHeadGroupJoinEdge, BillBodyGroupJoinEdge billBodyGroupJoinEdge) {
		super();
		this.billHeadGroupJoinEdge = billHeadGroupJoinEdge;
		this.billBodyGroupJoinEdge = billBodyGroupJoinEdge;
	}

	@XmlComponent(name="billHeadGroupJoinEdge",tag="BillHeadGroupJoinEdge",type=BillHeadGroupJoinEdge.class)
	public BillHeadGroupJoinEdge getBillHeadGroupJoinEdge() {
		return billHeadGroupJoinEdge;
	}

	public void setBillHeadGroupJoinEdge(BillHeadGroupJoinEdge billHeadGroupJoinEdge) {
		this.billHeadGroupJoinEdge = billHeadGroupJoinEdge;
	}
	
	@XmlComponent(name="billBodyGroupJoinEdge",tag="BillBodyGroupJoinEdge",type=BillBodyGroupJoinEdge.class)
	public BillBodyGroupJoinEdge getBillBodyGroupJoinEdge() {
		return billBodyGroupJoinEdge;
	}

	public void setBillBodyGroupJoinEdge(BillBodyGroupJoinEdge billBodyGroupJoinEdge) {
		this.billBodyGroupJoinEdge = billBodyGroupJoinEdge;
	}

	/**
	 * 单据头分组合并规则
	 */
	@XmlComponent(name="billHeadGroupJoinEdge",tag="BillHeadGroupJoinEdge",type=BillHeadGroupJoinEdge.class)
	public static class BillHeadGroupJoinEdge {
		private GroupFileds groupFileds;
		private JoinFileds joinFileds;
		private List<GroupFileds> groupFieldsList = new ArrayList<>();
		private List<JoinFileds> joinFieldsList = new ArrayList<>();
		private String key;
		
		public BillHeadGroupJoinEdge() {
			super();
		}
		
		public BillHeadGroupJoinEdge(GroupFileds groupFileds, JoinFileds joinFileds, List<GroupFileds> groupFieldsList,
				List<JoinFileds> joinFieldsList, String key) {
			super();
			this.groupFileds = groupFileds;
			this.joinFileds = joinFileds;
			this.groupFieldsList = groupFieldsList;
			this.joinFieldsList = joinFieldsList;
			this.key = key;
		}

//		@JsonIgnore
		public String getKey() {
			return key;
		}


		public void setKey(String key) {
			this.key = key;
		}

		@XmlComponent(name = "groupFileds", tag = "GroupFileds", type = GroupFileds.class)
		public GroupFileds getGroupFileds() {
			return groupFileds;
		}

		public void setGroupFileds(GroupFileds groupFileds) {
			this.groupFileds = groupFileds;
		}

		@XmlComponent(name = "joinFileds", tag = "JoinFileds", type = JoinFileds.class)
		public JoinFileds getJoinFileds() {
			return joinFileds;
		}

		public void setJoinFileds(JoinFileds joinFileds) {
			this.joinFileds = joinFileds;
		}

		public List<GroupFileds> getGroupFieldsList() {
			return groupFieldsList;
		}

		public void setGroupFieldsList(List<GroupFileds> groupFieldsList) {
			this.groupFieldsList = groupFieldsList;
		}

		public List<JoinFileds> getJoinFieldsList() {
			return joinFieldsList;
		}

		public void setJoinFieldsList(List<JoinFileds> joinFieldsList) {
			this.joinFieldsList = joinFieldsList;
		}

		
		
	}
	
	/**
	 * 单据体分组合并规则
	 * @author Administrator
	 *
	 */
	@XmlComponent(name="billBodyGroupJoinEdge",tag="BillBodyGroupJoinEdge",type=BillBodyGroupJoinEdge.class)
	public static class BillBodyGroupJoinEdge{
		
		private List<TargetSubBillGroupJoinEdge> targetSubBillGroupJoinEdges=new ArrayList<>();
		
		
		public BillBodyGroupJoinEdge() {
			super();
		}

		public BillBodyGroupJoinEdge(List<TargetSubBillGroupJoinEdge> targetSubBillGroupJoinEdges) {
			super();
			this.targetSubBillGroupJoinEdges = targetSubBillGroupJoinEdges;
		}

		@XmlList(name="targetSubBillGroupJoinEdges",tag="TargetSubBillGroupJoinEdges", subTag = "TargetSubBillGroupJoinEdge",type=TargetSubBillGroupJoinEdge.class)
		public List<TargetSubBillGroupJoinEdge> getTargetSubBillGroupJoinEdges() {
			return targetSubBillGroupJoinEdges;
		}

		public void setTargetSubBillGroupJoinEdges(List<TargetSubBillGroupJoinEdge> targetSubBillGroupJoinEdges) {
			this.targetSubBillGroupJoinEdges = targetSubBillGroupJoinEdges;
		}

	}
	
	
	/**
	 * 目标子单据的分组合并规则
	 * @author Administrator
	 *
	 */
	@XmlComponent(name="targetSubBillGroupJoinEdge",tag="TargetSubBillGroupJoinEdge",type=TargetSubBillGroupJoinEdge.class)
	public static class TargetSubBillGroupJoinEdge{
		private String key;
		private GroupFileds groupFileds;
		private JoinFileds joinFileds;
		private String caption;
		private GroupType groupType=GroupType.field;
		private String formulaExp;
		private Boolean isChecked = Boolean.FALSE;
		
		private List<GroupFileds> groupFiledsList = new ArrayList<>();
		private List<JoinFileds> joinFiledsList = new ArrayList<>();
		
		public TargetSubBillGroupJoinEdge() {
			super();
		}
		
		public TargetSubBillGroupJoinEdge(String key, GroupFileds groupFileds, JoinFileds joinFileds, String caption,
				GroupType groupType, String formulaExp, Boolean isChecked, List<GroupFileds> groupFiledsList,
				List<JoinFileds> joinFiledsList) {
			super();
			this.key = key;
			this.groupFileds = groupFileds;
			this.joinFileds = joinFileds;
			this.caption = caption;
			this.groupType = groupType;
			this.formulaExp = formulaExp;
			this.isChecked = isChecked;
			this.groupFiledsList = groupFiledsList;
			this.joinFiledsList = joinFiledsList;
		}

		@XmlAttribute(name="key",tag="Key",type=String.class)
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}

		@XmlComponent(name = "groupFileds", tag = "GroupFileds", type = GroupFileds.class)
		public GroupFileds getGroupFileds() {
			return groupFileds;
		}

		public void setGroupFileds(GroupFileds groupFileds) {
			this.groupFileds = groupFileds;
		}

		@XmlComponent(name = "joinFileds", tag = "JoinFileds", type = JoinFileds.class)
		public JoinFileds getJoinFileds() {
			return joinFileds;
		}

		public void setJoinFileds(JoinFileds joinFileds) {
			this.joinFileds = joinFileds;
		}

		public List<GroupFileds> getGroupFiledsList() {
			return groupFiledsList;
		}

		public void setGroupFiledsList(List<GroupFileds> groupFiledsList) {
			this.groupFiledsList = groupFiledsList;
		}

		public List<JoinFileds> getJoinFiledsList() {
			return joinFiledsList;
		}

		public void setJoinFiledsList(List<JoinFileds> joinFiledsList) {
			this.joinFiledsList = joinFiledsList;
		}

		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		public GroupType getGroupType() {
			return groupType;
		}

		public void setGroupType(GroupType groupType) {
			this.groupType = groupType;
		}

		public String getFormulaExp() {
			return formulaExp;
		}

		public void setFormulaExp(String formulaExp) {
			this.formulaExp = formulaExp;
		}
		
		public Boolean getChecked() {
			return isChecked;
		}

		public void setChecked(Boolean isChecked) {
			this.isChecked = isChecked;
		}
		
	}
	
	/**
	 * 设置分组字段
	 * @author Administrator
	 *
	 */
	@XmlComponent(name = "groupFileds",tag="GroupFileds",type=GroupFileds.class)
	public static class GroupFileds {
		
		private GroupType groupType=GroupType.field;
		private String keys;
		
		private Boolean isChecked = Boolean.FALSE;
		
		private String formulaExp;
		
		private String fieldName;
		
		private String field;
		
		public GroupFileds() {
			super();
		}

		public GroupFileds(GroupType groupType, String keys, Boolean isChecked, String formulaExp, String fieldName,
				String field) {
			super();
			this.groupType = groupType;
			this.keys = keys;
			this.isChecked = isChecked;
			this.formulaExp = formulaExp;
			this.fieldName = fieldName;
			this.field = field;
		}

		@XmlText(name="keys",type=String.class)
		public String getKeys() {
			return keys;
		}

		public void setKeys(String keys) {
			this.keys = keys;
		}

		@XmlAttribute(name="groupType",tag="GroupType",type=GroupType.class)
		public GroupType getGroupType() {
			return groupType;
		}

		public void setGroupType(GroupType groupType) {
			this.groupType = groupType;
		}
		
		public Boolean getChecked() {
			return isChecked;
		}

		public void setChecked(Boolean isChecked) {
			this.isChecked = isChecked;
		}
		
		public String getFormulaExp() {
			return formulaExp;
		}

		public void setFormulaExp(String formulaExp) {
			this.formulaExp = formulaExp;
		}
		
		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}
		
		
	}
	
	public static enum GroupType{
		field,formula
	}
	
	
	/**
	 * 设置合并规则,对没有设置合并规则的字段，取null或默认值
	 * @author Administrator
	 *
	 */
	@XmlComponent(name="joinFileds",tag="JoinFileds",type=JoinFileds.class)
	public static class JoinFileds{
		private String keys;
		
		private Boolean isChecked = Boolean.FALSE;
		
		private String formulaExp;
		
		private String fieldName;
	
		private String field;
		
		public JoinFileds() {
			super();
		}

		public JoinFileds(String keys, Boolean isChecked, String formulaExp, String fieldName, String field) {
			super();
			this.keys = keys;
			this.isChecked = isChecked;
			this.formulaExp = formulaExp;
			this.fieldName = fieldName;
			this.field = field;
		}

		@XmlText(name="keys",type=String.class)
		public String getKeys() {
			return keys;
		}

		public void setKeys(String keys) {
			this.keys = keys;
		}
		
		public Boolean getChecked() {
			return isChecked;
		}

		public void setChecked(Boolean isChecked) {
			this.isChecked = isChecked;
		}
		public String getFormulaExp() {
			return formulaExp;
		}

		public void setFormulaExp(String formulaExp) {
			this.formulaExp = formulaExp;
		}
		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}
		
	}
	
	
	/**
	 * 把分组合并的XML对象转换为页面对象
	 * @param headTable
	 * @param bodyList
	 */
	public void getBillGroupJoinInfo(DataTableMeta headTable, List<DataTableMeta> bodyList) {
		BillHeadGroupJoinEdge billHeadGroupJoinEdge = new BillHeadGroupJoinEdge();
		//单据头-分组
		List<GroupFileds> headGroupList = new ArrayList<>();
		GroupFileds mGroupFileds = new GroupFileds();
		GroupType groupType = this.billHeadGroupJoinEdge.getGroupFileds().getGroupType();
		for (Field field : headTable.getFields()) {
			GroupFileds groupFileds = new GroupFileds();
			groupFileds.setFieldName(field.getFieldName());//字段名称
			groupFileds.setField(field.getFieldKey());
			String groupKeys = this.billHeadGroupJoinEdge.getGroupFileds().getKeys();
			if (groupKeys.contains(field.getFieldKey())) {
				groupFileds.setChecked(Boolean.TRUE);//字段勾选框
			}
			headGroupList.add(groupFileds);
		}
		if (groupType==GroupType.formula) {
			mGroupFileds.setChecked(Boolean.TRUE);
			mGroupFileds.setFormulaExp(this.billHeadGroupJoinEdge.getGroupFileds().getKeys());
			mGroupFileds.setFieldName(this.billHeadGroupJoinEdge.getGroupFileds().getFieldName());
			mGroupFileds.setGroupType(groupType);
		}
		
		//单据头-合并
		List<JoinFileds> headJoinList = new ArrayList<>();
		for (Field field : headTable.getFields()) {
			JoinFileds jf = new JoinFileds();
			jf.setFieldName(field.getFieldName());//字段名称
			jf.setField(field.getFieldKey());
			String joinKeys = this.billHeadGroupJoinEdge.getJoinFileds().getKeys();
			if (joinKeys.contains(field.getFieldKey())) {
				jf.setChecked(Boolean.TRUE);//勾选框
			}
			String[] keyArray = joinKeys.split("\\|");
			for (String string : keyArray) {
				String[] s = string.split("\\,");
				if (s[0].equals(field.getFieldKey())) {
					if (s.length == 1) {
						jf.setKeys("");//输入框
					}else {
						jf.setKeys(s[1]);//输入框
					}
				}
			}
			headJoinList.add(jf);
		}
		billHeadGroupJoinEdge.setGroupFieldsList(headGroupList);
		billHeadGroupJoinEdge.setGroupFileds(mGroupFileds);
		billHeadGroupJoinEdge.setJoinFieldsList(headJoinList);
		
		//单据体-分组-合并
		BillBodyGroupJoinEdge billBodyGroupJoinEdge = new BillBodyGroupJoinEdge();
		List<TargetSubBillGroupJoinEdge> targetSubBillGroupJoinEdgeList = new ArrayList<>();
		for (DataTableMeta dtm : bodyList) {
			TargetSubBillGroupJoinEdge targetSubBillGroupJoinEdge = new TargetSubBillGroupJoinEdge();
			List<GroupFileds> groupFiledsList = new ArrayList<>();
			List<JoinFileds> joinFiledsList = new ArrayList<>();
			//分组
			for (Field f : dtm.getFields()) {
				GroupFileds gf =  new GroupFileds();
				gf.setFieldName(f.getFieldName());//字段名称
				gf.setField(f.getFieldKey());
				for (TargetSubBillGroupJoinEdge tsgj : this.billBodyGroupJoinEdge.getTargetSubBillGroupJoinEdges()) {
					if (tsgj.getGroupFileds().getGroupType()==GroupType.formula) {
						targetSubBillGroupJoinEdge.setGroupType(GroupType.formula);
						targetSubBillGroupJoinEdge.setFormulaExp(tsgj.getGroupFileds().getKeys());
						targetSubBillGroupJoinEdge.setChecked(Boolean.TRUE);
						continue;
					}
					if (dtm.getKey().equals(tsgj.getKey())) {
						String keys = tsgj.getGroupFileds().getKeys();
						if (keys.contains(f.getFieldKey())) {
							gf.setChecked(Boolean.TRUE);//字段勾选框
						}
					}
				}
				groupFiledsList.add(gf);
			}
			//合并
			for (Field f : dtm.getFields()) {
				JoinFileds jf =  new JoinFileds();
				jf.setFieldName(f.getFieldName());//字段名称
				jf.setField(f.getFieldKey());
				for (TargetSubBillGroupJoinEdge tsgj : this.billBodyGroupJoinEdge.getTargetSubBillGroupJoinEdges()) {
					if (dtm.getKey().equals(tsgj.getKey())) {
						String joinKeys = tsgj.getJoinFileds().getKeys();
						if (joinKeys.contains(f.getFieldKey())) {
							jf.setChecked(Boolean.TRUE);//字段勾选框
						}
						String[] keyArray = joinKeys.split("\\|");
						for (String string : keyArray) {
							String[] s = string.split("\\,");
							if (s[0].equals(f.getFieldKey())) {
								if (s.length == 1) {
									jf.setKeys("");//输入框
								}else {
									jf.setKeys(s[1]);//输入框
								}
							}
						}
					}
				}
				joinFiledsList.add(jf);
			}
			targetSubBillGroupJoinEdge.setCaption(dtm.getCaption());
			targetSubBillGroupJoinEdge.setKey(dtm.getKey());
			targetSubBillGroupJoinEdge.setGroupFiledsList(groupFiledsList);
			targetSubBillGroupJoinEdge.setJoinFiledsList(joinFiledsList);
			targetSubBillGroupJoinEdgeList.add(targetSubBillGroupJoinEdge);
		}
		billBodyGroupJoinEdge.setTargetSubBillGroupJoinEdges(targetSubBillGroupJoinEdgeList);
		
		this.setBillHeadGroupJoinEdge(billHeadGroupJoinEdge);
		this.setBillBodyGroupJoinEdge(billBodyGroupJoinEdge);
		
		
	}

	/**
	 * 把分组合并的页面对象转为XML对象
	 */
	public void setBillGroupJoinInfo() {
		GroupFileds groupFileds = this.getBillHeadGroupJoinEdge().getGroupFileds();
		//单据头-分组
		GroupFileds gFileds = new GroupFileds();
		if (!groupFileds.getChecked()) {
			StringBuffer groupString = new StringBuffer();
			for (GroupFileds gf : this.getBillHeadGroupJoinEdge().getGroupFieldsList()) {
				if (gf.getChecked()) {
					groupString.append(","+gf.getField());
				}
			}
			if (!"".equals(groupString.toString())) {
				gFileds.setKeys(groupString.toString().substring(1));
			}else {
				gFileds.setKeys("");
			}
			gFileds.setChecked(Boolean.TRUE);
		}else if (groupFileds.getChecked()) {
			gFileds.setGroupType(GroupType.formula);
			gFileds.setKeys(groupFileds.getFormulaExp());
		}
		this.getBillHeadGroupJoinEdge().setGroupFileds(gFileds);
		
		//单据头-合并
		JoinFileds jFileds = new JoinFileds();
		StringBuffer joinString = new StringBuffer();
		for (JoinFileds jf : this.getBillHeadGroupJoinEdge().getJoinFieldsList()) {
//			if (!StringUtil.isEmpty(jf.getKeys())) {
				if (jf.getChecked()) {
					joinString.append("|"+jf.getField()+",");
					if (StringUtil.isEmpty(jf.getKeys())) {
						joinString.append("");
					}else {
						joinString.append(jf.getKeys());
					}
//				}
			}
		}
		if (!"".equals(joinString.toString())) {
			jFileds.setKeys(joinString.toString().substring(1));
		}else {
			jFileds.setKeys("");
		}
		this.getBillHeadGroupJoinEdge().setJoinFileds(jFileds);
		
		
		BillBodyGroupJoinEdge billBodyGroupJoinEdge = new BillBodyGroupJoinEdge();
		List<TargetSubBillGroupJoinEdge> targetSubBillGroupJoinEdges = new ArrayList<>();
		for (TargetSubBillGroupJoinEdge tsgje : this.getBillBodyGroupJoinEdge().getTargetSubBillGroupJoinEdges()) {
			TargetSubBillGroupJoinEdge targetSubBillGroupJoinEdge = new TargetSubBillGroupJoinEdge();
			//单据体-分组
			GroupFileds gFiled = new GroupFileds();
			StringBuffer gBuffer = new StringBuffer();
			if (!tsgje.getChecked()) {
				for (GroupFileds gf : tsgje.getGroupFiledsList()) {
					if (gf.getChecked()) {
						gBuffer.append(","+gf.getField());
					}
				}
				gFiled.setGroupType(GroupType.field);
				if (!"".equals(gBuffer.toString())) {
					gFiled.setKeys(gBuffer.toString().substring(1));
				}else {
					gFiled.setKeys("");
				}
			}else if (tsgje.getChecked()) {
				gFiled.setGroupType(GroupType.formula);
				gFiled.setKeys(tsgje.getFormulaExp());
			}
			
			//单据体-合并
			JoinFileds jFiled = new JoinFileds();
			StringBuffer jBuffer = new StringBuffer();
			for (JoinFileds jf : tsgje.getJoinFiledsList()) {
				if (!StringUtil.isEmpty(jf.getKeys())&&jf.getChecked()) {
					jBuffer.append("|"+jf.getField()+","+jf.getKeys());
				}
			}
			if (!"".equals(jBuffer.toString())) {
				jFiled.setKeys(jBuffer.toString().substring(1));
			}else {
				jFiled.setKeys("");
			}
			
			targetSubBillGroupJoinEdge.setGroupFileds(gFiled);
			targetSubBillGroupJoinEdge.setJoinFileds(jFiled);
			targetSubBillGroupJoinEdge.setKey(tsgje.getKey());
			targetSubBillGroupJoinEdges.add(targetSubBillGroupJoinEdge);
		}
		billBodyGroupJoinEdge.setTargetSubBillGroupJoinEdges(targetSubBillGroupJoinEdges);
		this.setBillBodyGroupJoinEdge(billBodyGroupJoinEdge);
		
	}
	
	/**
	 * 将空的分组合并对象转为页面分组合并对象
	 * @param headTable
	 * @param bodyList
	 * @return
	 */
	public BillGroupJoinEdge creatGroupJoinInfo(DataTableMeta headTable, List<DataTableMeta> bodyList) {
		BillGroupJoinEdge billGroupJoinEdge = new BillGroupJoinEdge();
		
		//单据头
		BillHeadGroupJoinEdge billHeadGroupJoinEdge = new BillHeadGroupJoinEdge();
		List<GroupFileds> groupFieldsList = new ArrayList<>();
		List<JoinFileds> joinFieldsList = new ArrayList<>();
		List<Field> fields = headTable.getFields();
		for (Field field : fields) {
			GroupFileds groupFileds = new GroupFileds();
			groupFileds.setFieldName(field.getFieldName());
			groupFileds.setField(field.getFieldKey());
			groupFieldsList.add(groupFileds);
			
			JoinFileds joinFileds = new JoinFileds();
			joinFileds.setFieldName(field.getFieldName());
			joinFileds.setField(field.getFieldKey());
			joinFieldsList.add(joinFileds);
		}
		billHeadGroupJoinEdge.setGroupFieldsList(groupFieldsList);
		billHeadGroupJoinEdge.setJoinFieldsList(joinFieldsList);
		billHeadGroupJoinEdge.setGroupFileds(new GroupFileds());
		billHeadGroupJoinEdge.setJoinFileds(new JoinFileds());
		
		
		//单据体
		BillBodyGroupJoinEdge billBodyGroupJoinEdge = new BillBodyGroupJoinEdge();
		List<TargetSubBillGroupJoinEdge> targetSubBillGroupJoinEdges=new ArrayList<>();
		for (DataTableMeta dtm : bodyList) {
			TargetSubBillGroupJoinEdge targetSubBillGroupJoinEdge = new TargetSubBillGroupJoinEdge();
			List<GroupFileds> bodyGroupList = new ArrayList<>();
			List<JoinFileds> bodyJoinList = new ArrayList<>();
			for (Field field : dtm.getFields()) {
				GroupFileds groupFileds = new GroupFileds();
				groupFileds.setFieldName(field.getFieldName());
				groupFileds.setField(field.getFieldKey());
				bodyGroupList.add(groupFileds);
				
				JoinFileds joinFileds = new JoinFileds();
				joinFileds.setFieldName(field.getFieldName());
				joinFileds.setField(field.getFieldKey());
				bodyJoinList.add(joinFileds);
			}
			targetSubBillGroupJoinEdge.setGroupFiledsList(bodyGroupList);
			targetSubBillGroupJoinEdge.setGroupFileds(new GroupFileds());
			targetSubBillGroupJoinEdge.setJoinFileds(new JoinFileds());
			targetSubBillGroupJoinEdge.setJoinFiledsList(bodyJoinList);
			targetSubBillGroupJoinEdges.add(targetSubBillGroupJoinEdge);
		}
		billBodyGroupJoinEdge.setTargetSubBillGroupJoinEdges(targetSubBillGroupJoinEdges);
		
		billGroupJoinEdge.setBillHeadGroupJoinEdge(billHeadGroupJoinEdge);
		billGroupJoinEdge.setBillBodyGroupJoinEdge(billBodyGroupJoinEdge);
		return billGroupJoinEdge;
	}

	
	
}
