package com.xyy.wms.outbound.biz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;

public class OrderConcelPreHandler implements PreSaveEventListener{
	
	private static Logger logger = Logger.getLogger(PickingPostEvent.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		List<Record> records = dsi.getBodyDataTableInstance().get(0).getRecords();
		//判断当前订单是部分冲红，还是全部冲红
		Map<Object, Object> map = isCancelAll(head,records);
		boolean flag = (boolean) map.get("isConcelAll");
		StringBuffer find_head_sql = new StringBuffer("SELECT * FROM xyy_wms_bill_bocijihua_details WHERE dingdanbianhao = ?");
		Record orderHead = Db.findFirst(find_head_sql.toString(), head.getStr("danjubianhao"));
		StringBuffer find_sql = new StringBuffer("SELECT * FROM xyy_wms_bill_bocijihua_details2 WHERE dingdanbianhao = ?");
		List<Record> goodsList = Db.find(find_sql.toString(), head.getStr("danjubianhao"));
		//全部冲红
		if(flag){
			//更新销售订单头表
			 head.set("shifouchonghong", 1);
		 	 logger.info("销售订单已整单冲红！");
		 	 //更新波次计划详情表1
		 	 if(orderHead == null){
		 		 logger.info("该单尚未计划,计划之前已冲红！");
		 	 }
		 	 if(orderHead != null){
		 		 //已计划，整单冲红
		 		 //更新波次计划头表的状态
				 StringBuffer update_sql = new StringBuffer("UPDATE xyy_wms_bill_bocijihua_details set dingdanzhuangtai = 30 WHERE dingdanbianhao = ?");
				 Db.update(update_sql.toString(),head.getStr("danjubianhao"));
				 for (Record record : goodsList) {
					record.set("chonghongshuliang", record.getBigDecimal("shuliang"));
					 record.set("shangpinzhuangtai", 30);
				}
				 Db.batchUpdate("xyy_wms_bill_bocijihua_details2", "BillDtlID", goodsList, goodsList.size());
				 updateWaveStatus(orderHead);
		 	 	}
		}else{
			 logger.info("销售订单部分冲红！");
			 //订单为空，未计划
			 if(orderHead == null){
		 		 logger.info("该单尚未计划，计划之前已冲红");
		 	 	}
			 //订单不为空,已计划
			 if(orderHead != null){
				 //已计划的订单（客户方取消部分订单，库存充足）
				 Integer dingdanzhuangtai = orderHead.getInt("dingdanzhuangtai");
				 List<Record> redRecords = (List<Record>) map.get("redRecords");
				 if(dingdanzhuangtai == 21){ //已计划
					 for (Record redRecord : redRecords) {
						 for (Record record : goodsList) {
							 if(record.getStr("goodsid").equals(redRecord.getStr("goodsid"))){
								 record.set("chonghongshuliang", redRecord.getBigDecimal("chonghongshuliang"));
								 //设置整件数跟零件数
								 BigDecimal leaveNum = record.getBigDecimal("shuliang").subtract(record.getBigDecimal("chonghongshuliang")); //冲红后的数量
								 BigDecimal[] results = leaveNum.divideAndRemainder(new BigDecimal(record.getInt("dbzsl")));
								 record.set("zhengjianshu", results[0].multiply(new BigDecimal(record.getInt("dbzsl"))));
								 record.set("lingjianshu", results[1]);
								 if(leaveNum.compareTo(BigDecimal.ZERO)==0){
									 record.set("shangpinzhuangtai", 30);//将商品状态改为已冲红
								 }
							 }
						 }
					 }
					 Db.batchUpdate("xyy_wms_bill_bocijihua_details2", "BillDtlID", goodsList, goodsList.size());
				 }
				 //已挂单的订单（已执行）
				 if(dingdanzhuangtai == 29){
					 for (Record redRecord : redRecords) {
						 for (Record record : goodsList) {
							 if(record.getStr("goodsid").equals(redRecord.getStr("goodsid"))){
								 record.set("chonghongshuliang", redRecord.getBigDecimal("chonghongshuliang"));
								 //设置整件数跟零件数
								 BigDecimal leaveNum = record.getBigDecimal("shuliang").subtract(record.getBigDecimal("chonghongshuliang")); //冲红后的数量
								 BigDecimal[] results = leaveNum.divideAndRemainder(new BigDecimal(record.getInt("dbzsl")));
								 record.set("zhengjianshu", results[0].multiply(new BigDecimal(record.getInt("dbzsl"))));
								 record.set("lingjianshu", results[1]);
								 record.set("shangpinzhuangtai", 20);
								 if(record.getBigDecimal("shuliang").compareTo(redRecord.getBigDecimal("chonghongshuliang")) == 0){
									 //更新波次计划头表的状态
									 record.set("shangpinzhuangtai", 30);
								 }else{
									 record.set("shangpinzhuangtai", 20);//将状态改为已计划
								 }
							 }
						 }
						 Db.batchUpdate("xyy_wms_bill_bocijihua_details2", "BillDtlID", goodsList, goodsList.size());
					 }
					 boolean isPlan = true;
					 for (Record record : goodsList) {
						if(record.getInt("shangpinzhuangtai")== 29){
							isPlan = false;
						}else{
							continue;
						}
					}
					 if(isPlan&&goodsList.size()>0){
						StringBuffer update_sql = new StringBuffer("UPDATE xyy_wms_bill_bocijihua_details set dingdanzhuangtai = 20 WHERE dingdanbianhao = ?");
						Db.update(update_sql.toString(),head.getStr("danjubianhao"));
						updateWaveStatus(orderHead);
					 }
				 }	
			 }
		}
	}
	
	/**
	 * @param goodsList
	 * @param head
	 * 更新波次订单状态
	 */
	private void updateWaveStatus(Record head){
	 //更新波次计划头表的状态
		StringBuffer select_sql = new StringBuffer("select * from xyy_wms_bill_bocijihua_details where danjubianhao=? and dingdanzhuangtai not in(20,29,30)");
		List<Record> reList=Db.find(select_sql.toString(),head.getStr("danjubianhao"));
		if(reList.size()>0){
			Db.update("update xyy_wms_bill_bocijihua set status=23 where danjubianhao=?",head.getStr("danjubianhao"));
		}else{
			Db.update("update xyy_wms_bill_bocijihua set status=20 where danjubianhao=?",head.getStr("danjubianhao"));
		}
	}

	/**
	 * @param head
	 * 判断是部分冲红还是全部冲红
	 * @param records
	 */
	private Map<Object,Object> isCancelAll(Record head, List<Record> records) {
		boolean flag = true;
		HashMap<Object, Object> map = new HashMap<Object,Object>();
		List<Record> redRecords = new ArrayList<Record>();
		//遍历所有的订单明细
		for (Record record : records) {
			BigDecimal chonghongshuliang = record.getBigDecimal("chonghongshuliang");
			if(chonghongshuliang.compareTo(BigDecimal.ZERO) == 0){
				//不进行冲红
				continue;
			}else{
				//需要冲红
				flag = false;
				redRecords.add(record);
			}
		}
		if(flag){
			//当前订单所有明细冲红数量都为0，即表示整单冲红
			for (Record record : records) {
				record.set("chonghongshuliang",record.getBigDecimal("shuliang") );
			}
			map.put("isConcelAll", true);
			map.put("head", head);
			map.put("records", records);
		}else{
			for (Record record : redRecords) {
				record.set("chonghongshuliang",record.getBigDecimal("chonghongshuliang") );
			}
			//当前订单部分冲红，返回需要冲红的明细
			map.put("isConcelAll", false);
			map.put("head", head);
			map.put("redRecords", redRecords);
		}
		return map;
	}
	
}
