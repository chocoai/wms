package com.xyy.wms.outbound.biz;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;

public class WavePlanPostEvent implements PostSaveEventListener{

	@Override	
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		String orgId=(String) context.get("orgId");
		String danjubianhao=head.getStr("danjubianhao") ;//波次编号   
		StringBuffer sb=new StringBuffer("select sum((t1.shuliang/t2.dbzsl)*t2.dbzzl) kg,sum((t1.shuliang/t2.dbzsl)*t2.dbztj) cm ");
		sb.append(" from xyy_wms_bill_bocijihua_details2 t1,xyy_wms_dic_shangpinziliao t2 ");
		sb.append(" where 1=1 and t1.shangpinbianhao = t2.shangpinbianhao and t1.danjubianhao=? and t1.orgId=?");
		List<Record> listRecord=Db.find(sb.toString(),danjubianhao,orgId);
		if(listRecord!=null&&listRecord.size()>0){
			Record record=listRecord.get(0);
			head.set("zongzhongliang", record.getBigDecimal("kg"));//设置波次总重量
			head.set("zongtiji", record.getBigDecimal("cm"));//设置波次总体积
			//Db.update("xyy_wms_bill_bocijihua","BillID",head);
		}
		
		//计算订单总重量和总体积
		List<Record> recoredList=dsi.getBodyDataTableInstance().get(0).getRecords();
		StringBuffer buff=new StringBuffer("select sum((t1.shuliang/t2.dbzsl)*t2.dbzzl) kg,sum((t1.shuliang/t2.dbzsl)*t2.dbztj) cm,");
		buff.append(" t1.dingdanbianhao ");
		buff.append(" from xyy_wms_bill_bocijihua_details2 t1,xyy_wms_dic_shangpinziliao t2 ");
		buff.append(" where 1=1 and t1.shangpinbianhao = t2.shangpinbianhao and t1.danjubianhao=? and t1.orgId=? group by t1.dingdanbianhao");
		List<Record> listOrderRecord=Db.find(buff.toString(),danjubianhao,orgId);
		if(listOrderRecord!=null&&listOrderRecord.size()>0){ 
			for(Record orderRecord:listOrderRecord){
				for(Record orderRecord2:recoredList){
					if(orderRecord.getStr("dingdanbianhao").equals(orderRecord2.getStr("dingdanbianhao"))){
						orderRecord2.set("zongzhongliang", orderRecord.getBigDecimal("kg"));//设置订单总重量
						orderRecord2.set("zongtiji", orderRecord.getBigDecimal("cm"));//设置订单总体积
						break;
					}
				}
				
				//Db.batchUpdate("xyy_wms_bill_bocijihua_details","BillDtlID",order);
			}
		}
		//计算每个商品整件和零件数量
		List<Record> shangPinList=dsi.getBodyDataTableInstance().get(1).getRecords();
		List<Record> list=Db.find("select floor((t1.shuliang-t1.chonghongshuliang)/t2.dbzsl)*t2.dbzsl zjs,(t1.shuliang-t1.chonghongshuliang-floor((t1.shuliang-t1.chonghongshuliang)/t2.dbzsl)*t2.dbzsl) ljs,"
				+ " t1.goodsid,t2.dbzsl,t2.dbzzl,t2.dbztj,t1.dingdanbianhao"
				+ " from xyy_wms_bill_bocijihua_details2 t1,xyy_wms_dic_shangpinziliao t2 "
				+ "	WHERE t1.goodsid = t2.goodsid and shangpinzhuangtai=20 and danjubianhao=? and t1.orgId=?", danjubianhao,orgId);
		if(list!=null&&list.size()>0){
			for(Record record:list){
				for(Record record2:shangPinList){
					if(record.getStr("goodsid").equals(record2.getStr("goodsid"))
							&&record.getStr("dingdanbianhao").equals(record2.getStr("dingdanbianhao"))){
						record2.set("zhengjianshu", record.getBigDecimal("zjs"));//整件数
						record2.set("lingjianshu", record.getBigDecimal("ljs"));//零件数
						record2.set("dbzsl", record.getBigDecimal("dbzsl"));//大包装数量
						record2.set("dbzzl", record.getBigDecimal("dbzzl"));//大包装重量
						record2.set("dbztj", record.getBigDecimal("dbztj"));//大包装体积
						break;
					}
				}
			}
		}
		for(Record order:recoredList){
			for(Record shangpin:shangPinList){
				if(shangpin.getStr("dingdanbianhao").equals(order.getStr("dingdanbianhao"))){
					BigDecimal js=(shangpin.getBigDecimal("shuliang")
							.subtract(shangpin.getBigDecimal("chonghongshuliang")))
							.divide(shangpin.getBigDecimal("dbzsl"), 2, RoundingMode.HALF_UP)
							.add(order.getBigDecimal("zhehejianshu"));
					if(js.doubleValue()<=1){//小客户
						order.set("kehuleixing",4);
					}else if(js.doubleValue()>1&&js.doubleValue()<=5){//普通客户
						order.set("kehuleixing",3);
					}else if(js.doubleValue()>5&&js.doubleValue()<=10){//中客户
						order.set("kehuleixing",1);
					}else{//大客户
						order.set("kehuleixing",2);
					}
					order.set("zhehejianshu",js);
				}
			}
		}
	}

}
