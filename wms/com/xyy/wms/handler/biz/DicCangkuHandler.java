package com.xyy.wms.handler.biz;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;

public class DicCangkuHandler implements PreSaveEventListener {
	

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

	
	
	@Override
	public void execute(PreSaveEvent event) {
		
		// TODO Auto-generated method stub
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		Record head = getHead(dsi);
		
		Integer status = head.getInt("status");
		if(status==40){
				String cangkumingchen = head.getStr("cangkumingchen");
				if(cangkumingchen!=null){//只有保存和提交需要拦截
					System.out.println("保存成功");
				}else {
					event.getContext().addError("999", "商品名称不可以为空");
				}
		}
	}




}
