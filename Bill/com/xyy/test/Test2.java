package com.xyy.test;

import org.apache.camel.component.dataset.DataSet;

import com.xyy.bill.event.PreLoadEvent;
import com.xyy.bill.event.PreLoadEventListener;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;

/**
 * 	   <Event Type="PreSave" Processor="com.xyy.test.Test" />
		<Event Type="StatusChanged" Processor="com.xyy.test.Test2" />
 * @author evan
 *
 */
public class Test2 implements StatusChangedEventListener,PreSaveEventListener {

	@Override
	public void execute(StatusChangedEvent event) {
		System.out.println(event.getOldStatus());
		System.out.println(event.getNewStatus());
	}

	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi=(DataSetInstance)event.getContext().get("$model");
		dsi.getHeadDataTableInstance().getRecords().get(0).set("status", 8);
		
	}

	

	

}
