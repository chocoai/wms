package com.xyy.bill.map.trigger;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.map.IMapTriiger;
import com.xyy.bill.services.util.CommonService;
import com.xyy.erp.platform.common.tools.DictKeys;

/**
 * 电商订单下推销售订单前筛选方法
 * @author Chen
 *
 */
public class DSPreTrigger implements IMapTriiger{

	@Override
	public void execute(BillContext context) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		List<DataTableInstance> dtis = (List<DataTableInstance>)context.get("$srcdatadtis");
		Record kehu = null;
		List<Record> detailList = null;
		Record head = new Record();
		for (DataTableInstance table : dtis) {
			if (table.getDataTableMeta().getHead()) {
				head = table.getRecords().get(0);
				String kehubianhao = table.getRecords().get(0).get("kehubianhao");
				kehu = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where kehubianhao = '"+kehubianhao+"'");
			}else {
				detailList = table.getRecords();
			}
			
		}
		if (kehu!=null && detailList!=null && detailList.size()>0) {
			CommonService service = new CommonService();
			JSONObject result1 = service.checkJYFW(detailList, kehu);
			JSONObject result2 = service.checkKehuZizhi(kehu);
			JSONObject result3 = service.checkKucun(detailList);
			StringBuffer eBuffer = new StringBuffer();
			StringBuffer errorBuffer = new StringBuffer();
			boolean flag = true;
			boolean flag2 = true;
			if (!result1.getBooleanValue("flag")) {
				flag =  false;
				eBuffer.append("|"+result1.getJSONObject("errorMsg").getString("异常原因"));
				errorBuffer.append("|商品经营范围不符");
			}
				
			if (!result2.getBooleanValue("flag")) {
				flag =  false;
				eBuffer.append("|"+result2.getJSONObject("errorMsg").getString("异常原因"));
				errorBuffer.append("|客户资质不符");
			}
					
			if (!result3.getBooleanValue("flag")) {
				flag =  false;
				eBuffer.append("|"+result3.getJSONObject("errorMsg").getString("异常原因"));
				errorBuffer.append("|商品库存不足");
			}
			if (head.getInt("kefuyichang")==1 && flag==true) {
				Db.update("update xyy_erp_bill_dianshangdingdan set status = 22,yichangyuanyin = null "
						+ " where danjubianhao = '"+head.getStr("danjubianhao")+"'");
//				Db.use(DictKeys.db_dataSource_wms_mid).update("update tb_order_tmp set STATE = 2 ,KFSTATE = 1"
//						+ "where ORDERNO = '"+head.getStr("danjubianhao")+"'");
				return;
			}else {
				JSONObject result4 = service.checkKehuBeizhu(head);
				if (!result4.getBooleanValue("flag")) {
					flag = false;
					flag2 = false;
					eBuffer.append("|"+result4.getJSONObject("errorMsg").getString("异常原因"));
					errorBuffer.append("|客服有特殊备注");
					
				}
			}
			
			if (flag) {
				Db.update("update xyy_erp_bill_dianshangdingdan set status = 22 "
								+ " where danjubianhao = '"+head.getStr("danjubianhao")+"'");
//				Db.use(DictKeys.db_dataSource_wms_mid).update("update tb_order_tmp set STATE = 2 ,KFSTATE = 1"
//						+ "where ORDERNO = '"+head.getStr("danjubianhao")+"'");
			}else {
				StringBuffer sql  =new StringBuffer("update xyy_erp_bill_dianshangdingdan set status = 21,");
				if (!flag2) {
					sql.append("kefuyichang = 1,");
				}
				sql.append("yichangyuanyin = '"+eBuffer.toString()+"' where danjubianhao = '"+head.getStr("danjubianhao")+"'");
				Db.update(sql.toString());
				Db.use(DictKeys.db_dataSource_wms_mid).update("update tb_order_tmp set  STATE = 1,KFSTATE = 1  "
						
						+ "where ORDERNO = '"+head.getStr("danjubianhao")+"'");
				context.addError("888", errorBuffer.toString());
			}
					
		}
	}

}
