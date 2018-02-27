package com.xyy.edge.listener;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.edge.event.BillBodyConvertEvent;
import com.xyy.edge.event.BillBodyConvertListener;
import com.xyy.edge.instance.BillDtlEdgeEntity;
import com.xyy.edge.instance.BillDtlItemEntity;

public class WJDDListener implements BillBodyConvertListener {

	@Override
	public void before(BillBodyConvertEvent event) {
		// TODO Auto-generated method stub
		BillDtlEdgeEntity entity=(BillDtlEdgeEntity) event.getContext().get("sourceBody");
		List<BillDtlItemEntity> list = entity.getBillDtlItemEntities();
		for (BillDtlItemEntity item : list) {
			Record record = item.getRecord();
			
		}
	}

	@Override
	public void after(BillBodyConvertEvent event) {
		// TODO Auto-generated method stub

	}

}
