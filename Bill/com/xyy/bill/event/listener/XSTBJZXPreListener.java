package com.xyy.bill.event.listener;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;


/**
 * 销售退补价执行保存前相关操作
 * @author Chen
 *
 */
public class XSTBJZXPreListener implements PreSaveEventListener {

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		if (head!=null&&head.getInt("status")==20) {
			List<Record> details = dsInst.getBodyDataTableInstance().get(0).getRecords();
			BigDecimal hanshuijine = new BigDecimal(0);
			BigDecimal jine = new BigDecimal(0);
			BigDecimal shuie = new BigDecimal(0);
			BigDecimal maoli = new BigDecimal(0);
			for (Record record : details) {
				record.set("danjubianhao", head.getStr("danjubianhao"));
				record.set("danjia",record.getBigDecimal("hanshuijine").divide(record.getBigDecimal("shuliang"),6,RoundingMode.HALF_UP).divide(
						new BigDecimal(1+record.getBigDecimal("shuilv").intValue()/(double)100),6,RoundingMode.HALF_UP));
				record.set("jine", record.getBigDecimal("danjia").multiply(record.getBigDecimal("shuliang")).setScale(2, RoundingMode.HALF_UP));
				record.set("shuie", record.getBigDecimal("hanshuijine").subtract(record.getBigDecimal("jine")));
				hanshuijine = hanshuijine.add(record.getBigDecimal("hanshuijine"));
				jine = jine.add(record.getBigDecimal("jine"));
				shuie = shuie.add(record.getBigDecimal("shuie"));
				maoli = maoli.add(record.getBigDecimal("maoli"));
			}
			head.set("hanshuijine", hanshuijine);
			head.set("jine", jine);
			head.set("shuie", shuie);
			head.set("chengbenjine", 0);
			head.set("maoli", maoli);
			Record order = Db.findFirst("select * from xyy_erp_bill_xiaoshoudingdan "
					+ "where danjubianhao ='"+head.getStr("yxsdh")+"' limit 1");
			if (order!=null) {
				head.set("jiesuanfangshi", order.getInt("jiesuanfangshi"));
				head.set("yxsdh", order.getStr("danjubianhao"));
				head.set("ydsdh", order.getStr("dianshangbianhao"));
			}
		}
		context.set("$model", dsInst);
	}
}
