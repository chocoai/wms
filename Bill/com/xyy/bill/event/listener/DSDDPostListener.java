package com.xyy.bill.event.listener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;


/**
 * 电商订单保存后相关操作
 * @author Chen
 *
 */
public class DSDDPostListener implements PostSaveEventListener {
	
	private static final Log LOG = Log.getLog(DSDDPostListener.class);

	//拉取电商订单时，明细的商品未查到商品基本信息或商品总库存信息，先做异常保存，在取消异常后再次查询相关信息进行保存
	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		if (head.getInt("status")!=20) {
			return;
		}
		List<Record> details = dsInst.getBodyDataTableInstance().get(0).getRecords();
		StringBuffer errorBuffer = new StringBuffer();
		for (Record record : details) {
			if (StringUtil.isEmpty(record.getStr("shangpinmingcheng"))) {
				Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = '"+record.getStr("CODE")+"' limit 1");
				if (sp==null) {
					LOG.error("商品基本信息不存在【"+record.getStr("CODE")+"】");
					head.set("status", 21);
					errorBuffer.append("商品【"+record.getStr("CODE")+"】基本信息不存在|");
					continue;
				}
				record.set("goodsid", sp.getStr("goodsid"));
				record.set("shangpinmingcheng", sp.getStr("shangpinmingcheng"));
				record.set("guige", sp.getStr("guige"));
				record.set("danwei", sp.getInt("danwei"));
				record.set("shengchanchangjia", sp.getStr("shengchanchangjia"));
				record.set("shuilv", sp.getBigDecimal("xiaoxiangshuilv"));
				record.set("pizhunwenhao", sp.getStr("pizhunwenhao"));
				record.set("chandi", sp.getStr("chandi"));
				record.set("danjia", record.getBigDecimal("hanshuijia")
						.divide(new BigDecimal(1).add(record.getBigDecimal("shuilv").divide(new BigDecimal(100))),6,RoundingMode.HALF_UP));
				record.set("jine", record.getBigDecimal("danjia").multiply(record.getBigDecimal("shuliang")).setScale(2, RoundingMode.HALF_UP));
				record.set("shuie", record.getBigDecimal("hanshuijine").subtract(record.getBigDecimal("jine")));
				if (record.get("kucunshuliang")==null) {
					Record spKC = Db.findFirst("select * from xyy_erp_bill_shangpinzongkucun where shangpinId = '"+sp.getStr("goodsid")+"' limit 1");
					if (spKC==null) {
						LOG.error("商品总库存不存在【"+record.getStr("CODE")+"】");
						head.set("status", 21);
						errorBuffer.append("商品【"+record.getStr("CODE")+"】总库存不存在|");
						continue;
					}
					record.set("kekaishuliang", spKC.getBigDecimal("kucunshuliang"));
				}
			}
			
			
		}
		Db.update("xyy_erp_bill_dianshangdingdan", "BillID", head);
		Db.batchUpdate("xyy_erp_bill_dianshangdingdan_details", "BillDtlID", details, 500);
		
	}

}
