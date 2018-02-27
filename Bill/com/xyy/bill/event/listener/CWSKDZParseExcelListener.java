package com.xyy.bill.event.listener;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.ParseExcelEvent;
import com.xyy.bill.event.ParseExcelEventListener;
import com.xyy.bill.instance.BillContext;

public class CWSKDZParseExcelListener implements ParseExcelEventListener {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(ParseExcelEvent event) {
		BillContext context = event.getContext();
		List<Record> records = (List<Record>) context.get("$records");
		if (records.size() == 0) {
			context.addError("999", "导入的文件为空");
			return;
		}
		Integer pipeifangshi = context.getInteger("pipeifangshi");
		if (pipeifangshi == null) {
			context.addError("999", "匹配方式为空");
			return;
		}
		for (Record record : records) {
			record.set("createTime", new Timestamp(System.currentTimeMillis()));
			record.set("updateTime", new Timestamp(System.currentTimeMillis()));
			record.set("shifoupipei", 0);
			Record r = new Record();
			if (pipeifangshi == 1) {
				//订单号匹配
				String dianshangbianhao = record.getStr("dianshangbianhao");
				//电商订单-->销售订单-->销售出库单-->迁移表
				//1.查询销售订单是否生成
				r = Db.findFirst("select * from xyy_erp_bill_xiaoshoudingdan where dianshangbianhao=?", dianshangbianhao);
				if (r == null) {
					continue;
				}
			}else if (pipeifangshi == 2) {
				String kehumingcheng = record.getStr("kehumingcheng");
				//电商订单-->销售订单-->销售出库单-->迁移表
				//1.查询销售订单是否生成
				r = Db.findFirst("select * from xyy_erp_bill_xiaoshoudingdan where kehumingcheng=?", kehumingcheng);
				if (r == null) {
					continue;
				}
			}
			//2.查询销售出库单是否生成
			Record chuku = Db.findFirst("select * from xyy_erp_bill_xiaoshouchukudan where sjdjbh=?",r.getStr("danjubianhao"));
			if (chuku == null) {
				continue;
			}
			//3.迁移表信息是否生成
			Record migrate = Db.findFirst("select * from xyy_erp_bill_migratexiaoxiangfapiao where danjubianhao=? and jiesuanzhuangtai=0 and sffkyy = 0", chuku.getStr("danjubianhao"));
			if (migrate == null) {
				continue;
			}
			record.set("pipeidianshangbianhao", migrate.get("danjubianhao"));
			record.set("pipeikehumingcheng", migrate.get("kehumingcheng"));
			record.set("hanshuijine", migrate.get("ydzje"));
			record.set("yjsje", migrate.get("yjsje"));
			record.set("ppwjsje", migrate.get("wjsje"));
			record.set("orgId", migrate.get("orgId"));
			record.set("orgCode", migrate.get("orgCode"));
			record.set("kaipiaoriqi", migrate.get("kaipiaoriqi"));
			record.set("kaipiaoyuan", migrate.get("kaipiaoyuan"));
			record.set("kehubianhao", migrate.get("kehubianhao"));
			BigDecimal decimal = new BigDecimal(record.getStr("jiaoyijine"));
			if (decimal.subtract(migrate.getBigDecimal("wjsje")).compareTo(BigDecimal.ZERO)==0) {
				record.set("shifoupipei", 1);
			}
		}
		context.remove("$records");
		context.set("$records", records);
	}

}
