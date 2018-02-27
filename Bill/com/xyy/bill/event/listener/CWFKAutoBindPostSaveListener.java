package com.xyy.bill.event.listener;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.UUIDUtil;

/**
 * 财务收/付款自动勾兑
 * @author 
 *
 */
public class CWFKAutoBindPostSaveListener implements PostSaveEventListener {

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		User user = (User)context.get("user");
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		int status = head.getInt("status");
		if (status != 20) {
			return;
		}
		Integer bindType = head.get("gouduifangshi");
		if (bindType==0) {
			return;
		}
		List<Record> records = new ArrayList<>();
		List<Record> updateList = new ArrayList<>();//需要更新的列
		StringBuffer sql = new StringBuffer();
		sql.append("select * from");
		String tableName = "";
		String backTable="";
		BigDecimal jihuafukuan;
		if (bindType==2) {
			//自动勾兑
			if(dsi.getHeadDataTableInstance().getKey().contains("caigoufukuanjihuadan")){
				//采购付款计划
				tableName = "xyy_erp_bill_migratejinxiangkaipiao";
				backTable = "xyy_erp_bill_caigoufukuanjihuadan_details";
				sql.append(" xyy_erp_bill_migratejinxiangkaipiao").append(" where gysbh =? and gysmc =? and jiesuanzhuangtai=0  and sffkyy=0")
				.append(" order by createTime asc");
				records = Db.find(sql.toString(), head.get("gysbh"),head.get("gysmc"));
				jihuafukuan = head.getBigDecimal("jihuajine");
			}else{
				//销售收款
				tableName = "xyy_erp_bill_migratexiaoxiangfapiao";
				backTable = "xyy_erp_bill_xiaoshoushoukuandan_details2";
				sql.append(" xyy_erp_bill_migratexiaoxiangfapiao").append(" where kehubianhao =? and kehumingcheng =? and jiesuanzhuangtai=0 and sffkyy=0")
				.append(" order by createTime asc");
				records = Db.find(sql.toString(), head.get("kehubianhao"),head.get("kehumingcheng"));
				jihuafukuan = head.getBigDecimal("jszje");
			}
			
			for (Record record : records) {
				jihuafukuan = jihuafukuan.subtract(record.getBigDecimal("wjsje"));
				if (jihuafukuan.compareTo(BigDecimal.ZERO)>=0) {
					record.set("yjsje", record.getBigDecimal("ydzje"));
					record.set("wjsje", 0);
					record.set("jiesuanzhuangtai", 1);
					record.set("sffkyy", 1);
				}else{
					//还原最后一次
					jihuafukuan = jihuafukuan.add(record.getBigDecimal("wjsje"));
					record.set("yjsje", record.getBigDecimal("yjsje").add(jihuafukuan));
					record.set("wjsje", record.getBigDecimal("wjsje").subtract(jihuafukuan));
					record.set("updateTime", new Timestamp(System.currentTimeMillis()));
					if (user != null) {
						record.set("updator", user.getId());
						record.set("updatorTel", user.getMobile());
					}
					record.set("updateTime", new Timestamp(System.currentTimeMillis()));
					if (user != null) {
						record.set("updator", user.getId());
						record.set("updatorTel", user.getMobile());
					}
					updateList.add(record);
					break;
				}
				record.set("updateTime", new Timestamp(System.currentTimeMillis()));
				if (user != null) {
					record.set("updator", user.getId());
					record.set("updatorTel", user.getMobile());
				}
				updateList.add(record);
				
			}
			//回写 付款/收款 单据明细
			List<Record> insertList = new ArrayList<>();
			for (Record record : updateList) {
				Record r = new Record();
				r.set("BillDtlID", UUIDUtil.newUUID());
				r.set("BillID", head.get("BillID"));
				r.set("orgId", head.get("orgId"));
				r.set("orgCode", head.get("orgCode"));
				r.set("rowID", UUIDUtil.newUUID());
				r.set("createTime", new Timestamp(System.currentTimeMillis()));
				r.set("updateTime", new Timestamp(System.currentTimeMillis()));

				r.set("zhidanriqi", record.get("kaipiaoriqi"));
				r.set("zhidanren", record.get("kaipiaoyuan"));
				r.set("SourceID", record.get("OID"));
				r.set("SrcKey", "OID");
				r.set("danjubianhao", record.get("danjubianhao"));
				r.set("hanshuijine", record.get("ydzje"));
				r.set("yjsje", record.get("yjsje"));
				r.set("jsje", record.get("wjsje"));
				r.set("zhaiyao", record.get("zhaiyao"));
				r.set("SrcTableName", tableName);
				insertList.add(r);
			}
			
			Db.batchSave(backTable, insertList, 50);
			Db.batchUpdate(tableName,"OID", updateList, 50);
		}
	}

	
}
