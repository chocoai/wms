package com.xyy.bill.handler.biz;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.util.PullWMSDataUtil;
import com.xyy.bill.util.RecordDataUtil;

/**
 *	采购入库单的帐页，库存等记录
 */
public class CaiGouRuKuDanEvent implements StatusChangedEventListener {

	@Override
	public void execute(StatusChangedEvent event) {
		DataSetInstance dsi=(DataSetInstance)event.getContext().get("$model");
		
		System.out.println(event.getOldStatus());
		System.out.println(event.getNewStatus());
		//
		if(event.getNewStatus() == 20){
			
			//以下逻辑被调整到拉取数据时就处理
			
			Record head = getHead(dsi);
			List<Record> bodyList = getBody(dsi);
			
			// 回写采购订单明细项数据
			PullWMSDataUtil.backWriteCaiGouDingDanDetails(head, bodyList);
			// 商品批号
			RecordDataUtil.operteBatchGoods(head, bodyList, 1);
			// 商品批号库存处理
			RecordDataUtil.operateBatchStock(head, bodyList, 1);
			// 商品总库存处理
			RecordDataUtil.operateTotalStock(head, bodyList, 1);
			// 商品账页处理
			RecordDataUtil.addGoodsFolio(head, bodyList, 1);
			// 往来账处理
			RecordDataUtil.addDealingFolio(head, bodyList, 1);
		}
		
		

		
		
		
	}
	
	
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
