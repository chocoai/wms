package com.xyy.bill.map;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.handler.AbstractHandler;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.meta.DataSetMeta;

public class MapMany2OneHandler extends AbstractHandler {

	public MapMany2OneHandler(String scope) {
		super(scope);
	}

	@Override
	public void init(BillContext context) {
		// TODO Auto-generated method stub
		super.init(context);
	}

	@Override
	public void preHandle(BillContext context) {
		// TODO Auto-generated method stub
		super.preHandle(context);
	}

	@Override
	public void postHandle(BillContext context) {
		// TODO Auto-generated method stub
		super.postHandle(context);
	}
	
	/**
	 * 			map("mapKey",[billids],"targetFormKey");	
			map("mapkey",{
				mapType:2,
				data:[billids]
			
			},"targetFormKey");	
	 */
	@Override
	public void handle(BillContext context) {
		super.handle(context);
		String mapKey=context.getString("mapKey");
		String bills=context.getString("data");
		JSONArray aBills=JSON.parseArray(bills);
		//获取BillMap对象
		BillMap billMap=BillPlugin.engine.getBillMap(mapKey);
		List<DataSetInstance> srcDataSetInstances=new ArrayList<>();
		for(Object bill:aBills){
			srcDataSetInstances.add(this.loadDataSetInstanceViaBillID(billMap.getSrcDataObjectKey(),bill.toString()));
		}
	
		if(srcDataSetInstances.size()==1){
			DataSetInstance result=DataMap.dataMap(context,srcDataSetInstances.get(0), billMap);
			if(result!=null)
				context.set("$result", result);
			else
				context.addError("888", "转换错误");
		}else{//多个原单转化
			List<DataSetInstance> dsis=new ArrayList<>();
			for(DataSetInstance src:srcDataSetInstances){
				DataSetInstance result=DataMap.dataMap(context,src, billMap);
				if(result==null){
					context.addError("888", "转换错误");
					break;
				}
				dsis.add(result);
			}
			context.set("$result_type","DataSetInstanceList");
			context.set("$result", dsis);//数据集实例数组
		}
	}

	
	/**
	 * 加载DataSetInstance
	 * @param bill
	 * @return
	 */
	private DataSetInstance loadDataSetInstanceViaBillID(String billKey,String bill) {
		BillDef billDef=BillPlugin.engine.getBillDef(billKey);
		DataSetMeta dataSetMeta=billDef.getDataSet();
		DataSetInstance result=new DataSetInstance(dataSetMeta);
		result.loadBillData(bill);
		return result;
	}
	
	
	
	

}
