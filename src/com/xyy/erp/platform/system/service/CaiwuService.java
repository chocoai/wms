package com.xyy.erp.platform.system.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.system.model.User;

public class CaiwuService {

	//财务作废操作回写
	public boolean cancel(BillContext context) {
		JSONArray model = context.getJSONArray("model");
		String billKey = context.getString("billKey");
		User user = (User) context.get("user");
		int type = 0;
		if (billKey.indexOf("jinxiang")>0) {
			type=1;
		}else {
			type=2;
		}
		if (model.size() <0) {
			return false;
		}
		for (Object object : model) {
			JSONObject json = JSONObject.parseObject(object.toString());
			List<Record> records = new ArrayList<>();
			List<Record> list = new ArrayList<>();
			if (type == 1) {
				records = Db.find("select * from xyy_erp_bill_jinxiangfapiaoguanli_details1 where BillID=?", json.get("BillID"));
			}else {
				records = Db.find("select * from xyy_erp_bill_xiaoxiangfapiaoguanli_details1 where BillID=?", json.get("BillID"));
			}
			for (Record record : records) {
				Record r = new Record();
				String tableName = "";
				if (type == 1) {
					list = Db.find("select * from xyy_erp_bill_jinxiangfapiaoguanli_details where BillID=? and danjubianhao=?", json.get("BillID"),record.get("danjubianhao"));
					if (record.getInt("type") == 1) {
						//采购入库单
						tableName = "xyy_erp_bill_caigourukudan";
						r = Db.findFirst("select * from xyy_erp_bill_caigourukudan where danjubianhao=?", record.get("danjubianhao"));
					}else if (record.getInt("type") == 2){
						//采购退出出库单
						tableName = "xyy_erp_bill_caigoutuichuchukudan";
						r = Db.findFirst("select * from xyy_erp_bill_caigoutuichuchukudan where danjubianhao=?", record.get("danjubianhao"));
					}else {
						//采购退补价执行单
						tableName = "xyy_erp_bill_caigoutuibujiazhixingdan";
						r = Db.findFirst("select * from xyy_erp_bill_caigoutuibujiazhixingdan where danjubianhao=?", record.get("danjubianhao"));
					}
				}else{
					list = Db.find("select * from xyy_erp_bill_xiaoxiangfapiaoguanli_details where BillID=? and danjubianhao=?", json.get("BillID"),record.get("danjubianhao"));
					if (record.getInt("type") == 1) {
						//销售出库单
						tableName = "xyy_erp_bill_xiaoshouchukudan";
						r = Db.findFirst("select * from xyy_erp_bill_xiaoshouchukudan where danjubianhao=?", record.get("danjubianhao"));
					}else if (record.getInt("type") == 2){
						//销售退回入库单
						tableName = "xyy_erp_bill_xiaoshoutuihuirukudan";
						r = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuihuirukudan where danjubianhao=?", record.get("danjubianhao"));
					}else {
						//销售退补价执行单
						tableName = "xyy_erp_bill_xiaoshoutuibujiazhixingdan";
						r = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuibujiazhixingdan where danjubianhao=?", record.get("danjubianhao"));
					}
				}
				if (r == null) {
					continue;
				}
				r.set("updateTime", new Timestamp(System.currentTimeMillis()));
				r.set("updator", user.getId());
				r.set("updatorName", user.getRealName());
				r.set("updatorTel", user.getMobile());
				r.set("fapiaozhuangtai", 0);
				r.set("jiesuanzhuangtai", 0);
				r.set("yjsje", r.getBigDecimal("yjsje").subtract(record.getBigDecimal("hanshuijine")));
				//处理头部信息
				Db.update(tableName,"BillID",r);
				//处理单据体信息
				String table = tableName+"_details";
				for (Record detail : list) {
					Record d = Db.findFirst("select * from "+table + " where BillID=? and shangpinbianhao=?",json.get("BillID"),detail.get("shangpinbianhao"));
					if (d == null) {
						continue;
					}
					d.set("updateTime", new Timestamp(System.currentTimeMillis()));
					d.set("updator", user.getId());
					d.set("updatorName", user.getRealName());
					d.set("updatorTel", user.getMobile());
					d.set("ykpsl", d.getBigDecimal("ykpsl").subtract(detail.getBigDecimal("shuliang")));
					d.set("ykpje", d.getBigDecimal("ykpje").subtract(detail.getBigDecimal("hanshuijine")));
					Db.update(table,"BillDtlID",d);
				}
			}
		}
		return true;
	}

	//提交单据回写信息
	public boolean submitBill(BillContext context, Record mainRecord){
		String billKey = context.getString("billKey");
		User user = (User) context.get("user");
		int type = 0;
		if (billKey.indexOf("jinxiang")>=0) {
			type=1;
		}else {
			type=2;
		}
		String BillID = mainRecord.getStr("BillID");
		List<Record> records = new ArrayList<>();
		List<Record> list = new ArrayList<>();
		//
		if (type == 1) {
			records = Db.find("select * from xyy_erp_bill_jinxiangfapiaoguanli_details1 where BillID=?", BillID);
		}else {
			records = Db.find("select * from xyy_erp_bill_xiaoxiangfapiaoguanli_details1 where BillID=?", BillID);
		}
		for (Record record : records) {
			boolean flag = true;
			Record r = new Record();
			String tableName = "";
			if (type == 1) {
				list = Db.find("select * from xyy_erp_bill_jinxiangfapiaoguanli_details where BillID=? and danjubianhao=?", BillID,record.getStr("danjubianhao"));
				if (record.getInt("type") == 1) {
					//采购入库单
					tableName = "xyy_erp_bill_caigourukudan";
					r = Db.findFirst("select * from xyy_erp_bill_caigourukudan where danjubianhao=?", record.getStr("danjubianhao"));
				}else if (record.getInt("type") == 2){
					//采购退出出库单
					tableName = "xyy_erp_bill_caigoutuichuchukudan";
					r = Db.findFirst("select * from xyy_erp_bill_caigoutuichuchukudan where danjubianhao=?", record.getStr("danjubianhao"));
				}else {
					//采购退补价执行单
					tableName = "xyy_erp_bill_caigoutuibujiazhixingdan";
					r = Db.findFirst("select * from xyy_erp_bill_caigoutuibujiazhixingdan where danjubianhao=?", record.getStr("danjubianhao"));
				}
			}else{
				list = Db.find("select * from xyy_erp_bill_xiaoxiangfapiaoguanli_details where BillID=? and danjubianhao=?", BillID,record.getStr("danjubianhao"));
				if (record.getInt("type") == 1) {
					//销售出库单
					tableName = "xyy_erp_bill_xiaoshouchukudan";
					r = Db.findFirst("select * from xyy_erp_bill_xiaoshouchukudan where danjubianhao=?", record.getStr("danjubianhao"));
				}else if (record.getInt("type") == 2){
					//销售退回入库单
					tableName = "xyy_erp_bill_xiaoshoutuihuirukudan";
					r = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuihuirukudan where danjubianhao=?", record.getStr("danjubianhao"));
				}else {
					//销售退补价执行单
					tableName = "xyy_erp_bill_xiaoshoutuibujiazhixingdan";
					r = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuibujiazhixingdan where danjubianhao=?", record.getStr("danjubianhao"));
				}
			}
			//处理单据体信息
			String table = tableName+"_details";
			for (Record detail : list) {
				Record d = Db.findFirst("select * from "+table + " where BillID=? and shangpinbianhao=?",BillID,detail.getStr("shangpinbianhao"));
				d.set("updateTime", new Timestamp(System.currentTimeMillis()));
				d.set("updator", user.getId());
				d.set("updatorName", user.getRealName());
				d.set("updatorTel", user.getMobile());
				d.set("ykpsl", d.getBigDecimal("ykpsl").add(detail.getBigDecimal("shuliang")));
				d.set("ykpje", d.getBigDecimal("ykpje").add(detail.getBigDecimal("hanshuijine")));
				Db.update(table,"BillDtlID",d);
				if (!d.getBigDecimal("ykpsl").equals(d.getBigDecimal("shuliang"))) {
					flag = false;
				}
			}
			if (r == null) {
				continue;
			}
			r.set("updateTime", new Timestamp(System.currentTimeMillis()));
			r.set("updator", user.getId());
			r.set("updatorName", user.getRealName());
			r.set("updatorTel", user.getMobile());
			r.set("fapiaozhuangtai", 0);
			r.set("jiesuanzhuangtai", 0);
			r.set("yjsje", r.getBigDecimal("yjsje").subtract(record.getBigDecimal("hanshuijine")));
			if (flag) {
				r.set("fapiaozhuangtai", 1);
				r.set("jiesuanzhuangtai", 1);
			}
			//处理头部信息
			Db.update(tableName,"BillID",r);
		}
		return true;
	}

	public boolean blend(BillContext context, Record mainRecord) {
		User user = (User) context.get("user");
		String billKey = context.getString("billKey");
		String code = context.getString("code");
		int type = 0;
		if (billKey.indexOf("fukuan")>=0) {
			type=1;
		}else {
			type=2;
		}
		//冲红分支
		if (code.equals("blood")) {
			String tableName = "";
			if (type == 1) {
				tableName = "xyy_erp_bill_caigoufukuandan";
				mainRecord = Db.findFirst("select * from xyy_erp_bill_caigoufukuandan where danjubianhao=?", mainRecord.getStr("yuandanbianhao"));
			}else{
				tableName = "xyy_erp_bill_xiaoshoushoukuandan";
				mainRecord = Db.findFirst("select * from xyy_erp_bill_xiaoshoushoukuandan where danjubianhao=?", mainRecord.getStr("yuandanbianhao"));
			}
			mainRecord.set("updateTime", new Timestamp(System.currentTimeMillis()));
			mainRecord.set("updator", user.getId());
			mainRecord.set("updatorName", user.getRealName());
			mainRecord.set("updatorTel", user.getMobile());
			mainRecord.set("shifouchonghong", 1);
			Db.update(tableName, "BillID", mainRecord);
			tableName = tableName + "_details2";
			List<Record> records = Db.find("select * from "+tableName+" where BillID=?",mainRecord.getStr("BillID"));
			for (Record record : records) {
				Record r = new Record();
				String table = "";
				if (type == 1) {
					if (record.getInt("type") == 1) {
						table = "xyy_erp_bill_caigourukudan";
						r = Db.findFirst("select * from xyy_erp_bill_caigourukudan where danjubianhao=?", record.getStr("danjubianhao"));
					}else if (record.getInt("type") == 2) {
						table = "xyy_erp_bill_caigoutuichuchukudan";
						r = Db.findFirst("select * from xyy_erp_bill_caigoutuichuchukudan where danjubianhao=?", record.getStr("danjubianhao"));
					}else {
						table = "xyy_erp_bill_caigoutuibujiazhixingdan";
						r = Db.findFirst("select * from xyy_erp_bill_caigoutuibujiazhixingdan where danjubianhao=?", record.getStr("danjubianhao"));
					}
				}else{
					if (record.getInt("type") == 1) {
						table = "xyy_erp_bill_xiaoshouchukudan";
						r = Db.findFirst("select * from xyy_erp_bill_xiaoshouchukudan where danjubianhao=?", record.getStr("danjubianhao"));
					}else if (record.getInt("type") == 2) {
						table = "xyy_erp_bill_xiaoshoutuihuirukudan";
						r = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuihuirukudan where danjubianhao=?", record.getStr("danjubianhao"));
					}else {
						table = "xyy_erp_bill_xiaoshoutuibujiazhixingdan";
						r = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuibujiazhixingdan where danjubianhao=?", record.getStr("danjubianhao"));
					}
				}
				if (r == null) {
					continue;
				}
				r.set("updateTime", new Timestamp(System.currentTimeMillis()));
				r.set("updator", user.getId());
				r.set("updatorName", user.getRealName());
				r.set("updatorTel", user.getMobile());
				r.set("yjsje", 0);
				r.set("jiesuanzhuangtai", 0);
				Db.update(table, "BillID", r);
			}
			return true;
		}
		List<Record> records = new ArrayList<>();
		//勾兑方式
		int blendType = mainRecord.getInt("gouduifangshi");
		if (blendType == 1) {
			//按单价勾兑
			if (type == 1) {
				//采购收款
				records = Db.find("select * from xyy_erp_bill_caigoufukuandan_details2 where BillID=?", mainRecord.get("BillID"));
			}else{
				//销售付款
				records = Db.find("select * from xyy_erp_bill_xiaoshoushoukuandan_details2 where BillID=?", mainRecord.get("BillID"));
			}
			for (Record record : records) {
				Record r = new Record();
				String tableName = "";
				if (type == 1) {
					if (record.getInt("type") == 1) {
						tableName = "xyy_erp_bill_caigourukudan";
						r = Db.findFirst("select * from xyy_erp_bill_caigourukudan where danjubianhao=?", record.get("danjubianhao"));
					}else if (record.getInt("type") == 2) {
						tableName = "xyy_erp_bill_caigoutuichuchukudan";
						r = Db.findFirst("select * from xyy_erp_bill_caigoutuichuchukudan where danjubianhao=?", record.get("danjubianhao"));
					}else {
						tableName = "xyy_erp_bill_caigoutuibujiazhixingdan";
						r = Db.findFirst("select * from xyy_erp_bill_caigoutuibujiazhixingdan where danjubianhao=?", record.get("danjubianhao"));
					}
				}else{
					if (record.getInt("type") == 1) {
						tableName = "xyy_erp_bill_xiaoshouchukudan";
						r = Db.findFirst("select * from xyy_erp_bill_xiaoshouchukudan where danjubianhao=?", record.get("danjubianhao"));
					}else if (record.getInt("type") == 2) {
						tableName = "xyy_erp_bill_xiaoshoutuihuirukudan";
						r = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuihuirukudan where danjubianhao=?", record.get("danjubianhao"));
					}else {
						tableName = "xyy_erp_bill_xiaoshoutuibujiazhixingdan";
						r = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuibujiazhixingdan where danjubianhao=?", record.get("danjubianhao"));
					}
				}
				if (r == null) {
					continue;
				}
				r.set("updateTime", new Timestamp(System.currentTimeMillis()));
				r.set("updator", user.getId());
				r.set("updatorName", user.getRealName());
				r.set("updatorTel", user.getMobile());
				r.set("yjsje", r.getBigDecimal("yjsje").add(record.getBigDecimal("jsje")));
				if (record.getBigDecimal("jsje").add(record.getBigDecimal("yjsje")).equals(record.getBigDecimal("hanshuijine"))) {
					r.set("jiesuanzhuangtai", 1);
				}
				Db.update(tableName, "BillID", r);
			}
			return true;
		}else if (blendType == 2) {
			//自动勾兑
			return autoBlend(mainRecord,type);
		}
		return true;
	}

	private boolean autoBlend(Record mainRecord, int type) {
		
		if (type == 1) {
			//采购付款单
			List<Record> records = Db.find("");
		}
		
		return true;
	}
	

	
}
