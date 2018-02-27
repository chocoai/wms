package com.xyy.bill.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.def.MultiBillDef;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

/**
 * 
 * 引擎配置文件 <Config></Config>
 * 
 *
 */

@XmlComponent(name = "config", tag = "Config", type = BillConfig.class)
public class BillConfig {
	private Map<String, Bill> maps = new ConcurrentHashMap<>();
	private List<Bill> bills = new ArrayList<>();
	private Map<String, MultiBill> mutiMap = new ConcurrentHashMap<>();
	private List<MultiBill> multiBills = new ArrayList<>();
	private Map<String, Dics> dicMap = new ConcurrentHashMap<>();
	private List<Dics> dics = new ArrayList<>();
	@XmlList(name = "bills", tag = "Bills", subTag = "Bill", type = Bill.class)
	public List<Bill> getBills() {
		return bills;
	}

	public void setBills(List<Bill> bills) {
		
		if (this.maps == null) {
			this.maps = new ConcurrentHashMap<>();
		}

		for (Bill bill : this.bills) {
			if (!this.maps.containsKey(bill.getKey())) {
				this.maps.put(bill.getKey(), bill);
			}
		}
		this.bills.clear();
		this.bills.addAll(this.maps.values());
	}

	public List<Dics> getDics() {
		return dics;
	}

	public void setDics(List<Dics> dics) {
		if (this.dicMap == null) {
			this.dicMap = new ConcurrentHashMap<>();
		}

		for (Dics dic : this.dics) {
			if (!this.dicMap.containsKey(dic.getKey())) {
				this.dicMap.put(dic.getKey(), dic);
			}
		}
		this.dics.clear();
		this.dics.addAll(this.dicMap.values());
	}

	// @XmlList(name = "multiBills", tag = "MultiBills", subTag = "MultiBill",
	// type = MultiBill.class)
	public List<MultiBill> getMultiBills() {
		return multiBills;
	}

	public void setMultiBills(List<MultiBill> multiBills) {
		if (this.mutiMap == null) {
			this.mutiMap = new ConcurrentHashMap<>();
		}

		for (MultiBill multiBill : this.multiBills) {
			if (!this.mutiMap.containsKey(multiBill.getKey())) {
				this.mutiMap.put(multiBill.getKey(), multiBill);
			}
		}
		this.multiBills.clear();
		this.multiBills.addAll(this.mutiMap.values());
	}

	@XmlComponent(tag = "Bill", type = Bill.class)
	public static class Bill {
		private String key;// 单据key
		private String name;// 单据标题
		private String file;// 数据文件
		private String defFile;// 单据定义文件

		@XmlAttribute(name = "key", tag = "Key", type = String.class)
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		@XmlAttribute(name = "name", tag = "Name", type = String.class)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@XmlAttribute(name = "file", tag = "File", type = String.class)
		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}

		@XmlAttribute(name = "defFile", tag = "DefFile", type = String.class)
		public String getDefFile() {
			return defFile;
		}

		public void setDefFile(String defFile) {
			this.defFile = defFile;
		}

	}

	@XmlComponent(tag = "MultiBill", type = MultiBill.class)
	public static class MultiBill {

		private String key;

		private String name;

		private String file;

		private String defFile;// 单据定义文件
		
		@XmlAttribute(name = "key", tag = "Key", type = String.class)
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		@XmlAttribute(name = "name", tag = "Name", type = String.class)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@XmlAttribute(name = "file", tag = "File", type = String.class)
		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}

		@XmlAttribute(name = "defFile", tag = "DefFile", type = String.class)
		public String getDefFile() {
			return defFile;
		}

		public void setDefFile(String defFile) {
			this.defFile = defFile;
		}
	}

	@XmlComponent(tag = "Dictionary", type = MultiBill.class)
	public static class Dics{
		private String key;

		private String name;

		private String file;
		
		private String defFile;// 单据定义文件

		@XmlAttribute(name = "key", tag = "Key", type = String.class)
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		@XmlAttribute(name = "name", tag = "Name", type = String.class)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@XmlAttribute(name = "file", tag = "File", type = String.class)
		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}
		
		@XmlAttribute(name = "defFile", tag = "DefFile", type = String.class)
		public String getDefFile() {
			return defFile;
		}

		public void setDefFile(String defFile) {
			this.defFile = defFile;
		}
	}
	/**
	 * 添加配置文件 <Bill Key="testcaigoudingdan" Name="采购订单" File=
	 * "config/Bill/Bill_testcaigoudingdan_DS.xml" />
	 * 
	 * @param billDef
	 */
	public void addBillDef(BillDef billDef) {
		if (this.maps.containsKey(billDef.getKey()) || billDef.getView() == null || billDef.getDataSet() == null) {
			return;
		}
		
		
		Bill bill = new Bill();
		bill.setKey(billDef.getKey());
		bill.setFile("config/Bill/" + billDef.getFullKey() + "_DS.xml");

		String name = billDef.getView().getCaption();
		if (StringUtil.isEmpty(name)) {
			name = UUIDUtil.newUUID();
		}
		bill.setName(name);
		bill.setDefFile("config/Bill/" + billDef.getFullKey() + ".xml");
		this.maps.put(billDef.getKey(), bill);
		this.bills.clear();
		this.bills.addAll(this.maps.values());
	}

	public void addmultiBills(MultiBillDef multiBillDef){
		if (this.mutiMap.containsKey(multiBillDef.getKey()) || multiBillDef.getView() == null || multiBillDef.getDataSet() == null) {
			return;
		}
		
		MultiBill multiBill = new MultiBill();
		multiBill.setKey(multiBillDef.getKey());
		multiBill.setFile("config/Bill/" + multiBillDef.getFullKey() + "_DS.xml");
		
		String name = multiBillDef.getView().getCaption();
		if (StringUtil.isEmpty(name)) {
			name = UUIDUtil.newUUID();
		}
		multiBill.setName(name);
		multiBill.setDefFile("config/Bill/" + multiBillDef.getFullKey() + ".xml");
		this.mutiMap.put(multiBillDef.getKey(), multiBill);
		this.multiBills.clear();
		this.multiBills.addAll(this.mutiMap.values());
	}
	
	public void addDics (DicDef dicDef){
		if (this.dicMap.containsKey(dicDef.getKey()) || dicDef.getView() == null || dicDef.getDataSet() == null) {
			return;
		}
		
		Dics dic = new Dics();
		dic.setKey(dicDef.getKey());
		dic.setFile("config/Bill/" + dicDef.getFullKey() + "_DS.xml");
		
		String name = dicDef.getView().getCaption();
		if (StringUtil.isEmpty(name)) {
			name = UUIDUtil.newUUID();
		}
		dic.setName(name);
		dic.setDefFile("config/Bill/" + dicDef.getFullKey() + ".xml");
		this.dicMap.put(dicDef.getKey(), dic);
		this.dics.clear();
		this.dics.addAll(this.dicMap.values());
	}
}
