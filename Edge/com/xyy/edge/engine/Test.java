package com.xyy.edge.engine;

import org.jdom.Element;

import com.xyy.edge.meta.BillEdge;
import com.xyy.edge.meta.BillEdgeController;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentWriter;
import com.xyy.util.xml.NotWritableException;

public class Test {

	public static void main(String[] args) {
		BillEdge billEdge = new BillEdge();
		billEdge.setKey("Bill001_Bill100_Edge");
		billEdge.setCaption("业务订单转换拣货单规则");
		billEdge.setName("业务订单_拣货单_Edge");
		billEdge.setSourceBillKey("Bill001");
		billEdge.setTargetBillKey("Bill100");
		billEdge.setVersion("1.0.0");
		
		billEdge.setDescription(new BillEdge.Description("业务订单转换拣货单规则说明"));

		BillEdgeController billEdgeController = new BillEdgeController();
		billEdgeController.setAutoSaveController(new BillEdgeController.AutoSaveController(BillEdgeController.AutoSaveController.CtryType.AutoSave));
		billEdgeController.setBackWriteController(new BillEdgeController.BackWriteController(BillEdgeController.BackWriteController.CtryType.Save));
		billEdgeController.setDisplayController(new BillEdgeController.DisplayController(BillEdgeController.DisplayController.CtryType.GoEditor));
		billEdgeController.setMultiConvertCtrl(new BillEdgeController.MultiConvertCtrl(BillEdgeController.MultiConvertCtrl.CtryType.AllowAndNotWarning));

		billEdge.setBillEdgeController(billEdgeController);

		ComponentWriter cw = new ComponentWriter(BillEdge.class);
		try {
			Element root = cw.writeXML(billEdge);
			XMLUtil.save(root, "e:/temp/billEdge.xml");
		} catch (NotWritableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
