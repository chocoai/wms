package com.xyy.bill.handler.biz;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;

/**
 * 采购退补价执行单
 */
public class CaiGouTuiBuJiaZhiXingDanEvent implements StatusChangedEventListener {

	@Override
	public void execute(StatusChangedEvent event) {
		//此段逻辑全由采购退补价开票单审核通过后执行
//		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		System.out.println(event.getOldStatus());
		System.out.println(event.getNewStatus());
//		if (event.getNewStatus() == 20) {
//			Record head = getHead(dsi);
//			List<Record> body = getBody(dsi);
			
			/*//回写采购退补价开票单
			String danjubianhao = head.getStr("danjubianhao");
			backWriteCaiGouTuiBuJiaKaiPiaoDan(danjubianhao);
			
			//商品总库存处理
			RecordDataUtil.operateTotalStock(head,body,4);
			
			//商品账页处理
			RecordDataUtil.addGoodsFolio(head,body,4);
			
			//往来账处理
			RecordDataUtil.addDealingFolio(head,body,4);*/
			
//		}
	}
	
	/**
	 * 回写采购退补价开票单
	 * @param danjubianhao
	 */
	/*private static void backWriteCaiGouTuiBuJiaKaiPiaoDan(String danjubianhao) {
		String sql="update xyy_erp_bill_caigoutuibujiakaipiaodan set shifoutiqu = 1 where danjubianhao = ?";
		int update = Db.update(sql,danjubianhao);
		System.out.println("【回写采购退补价开票单】是否成功："+update);
		
	}*/
	
	

	/**
	 * 获取头部数据
	 * 
	 * @param dsi
	 *            数据集实例
	 * @return
	 */
	public static Record getHead(DataSetInstance dsi) {
		DataTableInstance headDti = dsi.getHeadDataTableInstance();
		if (headDti.getRecords().size() != 1) {
			return null;
		}
		Record record = headDti.getRecords().get(0);
		return record;
	}

	/**
	 * 获取体部数据
	 * 
	 * @param dsi
	 *            数据集实例
	 * @return
	 */
	public static List<Record> getBody(DataSetInstance dsi) {
		List<DataTableInstance> bodyDataTableInstance = dsi.getBodyDataTableInstance();
		if (bodyDataTableInstance.size() != 1) {
			return null;
		}
		List<Record> records = bodyDataTableInstance.get(0).getRecords();
		return records;
	}

	
	

}
