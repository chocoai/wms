package com.xyy.bill.event.listener;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.ParseExcelEvent;
import com.xyy.bill.event.ParseExcelEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.common.tools.UUIDUtil;

/**
 * 采购请货单导入事件
 * 
 * @author SKY
 *
 */
public class CaiGouQingHuoDanParseExcelListener implements ParseExcelEventListener {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(ParseExcelEvent event) {
		BillContext context = event.getContext();
		List<Record> records = (List<Record>) context.get("$records");
		if (records.size() == 0) {
			context.addError("999", "导入的文件为空");
			return;
		}
		for (Record record : records) {
			String shangpinbianhao = record.getStr("shangpinbianhao");
			Record goods = findGoodsByNo(shangpinbianhao);
			if (goods == null) {
				context.addError("999", "商品编号为："+shangpinbianhao+",找不到该商品！");
				return;
			}
			Record goodsPerson = findGoodsPerson(goods.getStr("goodsid"));
			if(goodsPerson==null){
				context.addError("999", "商品编号为："+shangpinbianhao+",找不到该商品的负责人！");
				return;
			}
			record.set("createTime", new Timestamp(System.currentTimeMillis()));
			record.set("updateTime", new Timestamp(System.currentTimeMillis()));
			record.set("caigouyuan", goodsPerson == null || goodsPerson.getStr("caigouyuan")==null?null:goodsPerson.getStr("caigouyuan"));
			record.set("shangpinbianhao", shangpinbianhao);
			record.set("rowID", UUIDUtil.newUUID());
			record.set("shangpinmingcheng", goods.getStr("shangpinmingcheng"));
			record.set("guige", goods.getStr("guige"));
			record.set("danwei", goods.getInt("danwei"));
			record.set("shengchanchangjia", goods.getStr("shengchanchangjia"));
			BigDecimal shuliang= BigDecimal.ZERO;
			Object object1 = record.get("shuliang");
			if (object1 instanceof String) {
				shuliang = new BigDecimal(record.getStr("shuliang"));
			}else if (object1 instanceof BigDecimal) {
				shuliang = record.getBigDecimal("shuliang");
			} 
			BigDecimal hanshuijia= BigDecimal.ZERO;
			Object object2 = record.get("hanshuijia");
			if (object2 instanceof String) {
				hanshuijia = new BigDecimal(record.getStr("hanshuijia"));
			}else if (object2 instanceof BigDecimal) {
				hanshuijia = record.getBigDecimal("hanshuijia");
			} 
			record.set("shuliang", shuliang);
			record.set("hanshuijia", hanshuijia);
			//保留2位小数
			BigDecimal hanshuijine = shuliang.multiply(hanshuijia).setScale(2, BigDecimal.ROUND_HALF_UP);
			record.set("hanshuijine", hanshuijine);
			record.set("shuilv", goods.getBigDecimal("jinxiangshuilv"));
			record.set("pizhunwenhao", goods.getStr("pizhunwenhao"));
			record.set("chandi", goods.getStr("chandi"));
		}
		context.remove("$records");
		context.set("$records", records);
	}

	/**
	 * 商品
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findGoodsByNo(String shangpinbianhao) {
		String sql = "select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = ?";
		Record record = Db.findFirst(sql, shangpinbianhao);
		return record;
	}
	
	/**
	 * 商品负责人
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findGoodsPerson(String goodsId) {
		String sql = "select * from xyy_erp_dic_shangpinfuzeren where goodsid = ?";
		Record record = Db.findFirst(sql, goodsId);
		return record;
	}

}
