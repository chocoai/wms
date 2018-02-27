package com.xyy.bill.migration;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.migration.SourceField.GroupingPolicy;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlList;

/**
 * <SourceTable TableKey="" IsPrimary="">
		<SourceField Type="" Definition="" OpSign="" IsNegtive="" GroupingPolicy=""
			PeriodGranularity="" PeriodValue="" MapFormula="" TargetTableKey="" 
			TargetFieldKey="" RefFieldKey=""/>
			......
	</SourceTable>
 * @author 
 *
 */
public class SourceTable {

	private String tableKey;
	
	private String targetTableKey;
	
	private String key;
	
	private boolean isPrimary=false;
	
	private List<SourceField> srcFileds = new ArrayList<>();
	
	
	@Override
	public int hashCode() {	
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null || obj.getClass()!=SourceTable.class){
			return false;
		}
		SourceTable t=(SourceTable)obj;
		return t.key.equals(key);
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

	@XmlAttribute(name = "isPrimary", tag = "IsPrimary", type = boolean.class)
	public boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	@XmlList(name="srcFileds", subTag="SourceField",type=SourceField.class)
	public List<SourceField> getSrcFileds() {
		return srcFileds;
	}

	public void setSrcFileds(List<SourceField> srcFileds) {
		this.srcFileds = srcFileds;
	}

	@XmlAttribute(name = "targetTableKey", tag = "TargetTableKey", type = String.class)
	public String getTargetTableKey() {
		return targetTableKey;
	}

	public void setTargetTableKey(String targetTableKey) {
		this.targetTableKey = targetTableKey;
	}

	public List<SourceField> findGroupSrcFileds() {
		List<SourceField> result=new ArrayList<>();
		for(SourceField field:this.srcFileds){
			if(field.getGroupingPolicy()==GroupingPolicy.Discrete){
				result.add(field);
			}
		}
		
		return result;
	}
	
	
   public List<SourceField> findGroupCalFileds() {
		List<SourceField> result=new ArrayList<>();
		for(SourceField field:this.srcFileds){
			if(field.getGroupingPolicy()==GroupingPolicy.None){
				result.add(field);
			}
		}
		return result;
	}
}
