package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;

public class BillPanDianJiHuaPreHandler  implements PreSaveEventListener{

	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
	        Record head = getHead(dsi);
			//获取组织机构id
			String orgId=event.getContext().getString("orgId");
	        List<Record> body = getBody(dsi);
	        //后台校验 YiKuKaiPiaoValidate
	        Date zhidanriqi=head.get("zhidanriqi");
	        String zhidanren=head.getStr("zhidanren");
	        if(zhidanriqi==null||zhidanren==null){
	        	event.getContext().addError("盘点计划单","制单人，制单日期等数据不能为空");
	        	return;
	        }
	        for (Record r:body){
				String shangpinbianhao=r.getStr("shangpinbianhao");
				String huoweibianhao=r.getStr("huoweibianhao");
				Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? and orgId=?", shangpinbianhao,orgId);
				String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
				// 批号数据
				String pihaoId = r.getStr("pihaoId");
				Record yrhwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? and orgId=?", huoweibianhao,orgId);
				String huoweiId = yrhwRecord.getStr("ID");
				String[] hwParams = {shangpinId, pihaoId, huoweiId,orgId};
				Record record=Db.findFirst("select * from xyy_wms_bill_kucunyuzhanyukou where goodsid = ? and pihaoId = ? and huoweiId = ? and orgId=? order by createTime desc limit 1",hwParams);
				if (record!=null){
					event.getContext().addError("盘点计划单","当前商品存在其他业务操作不能进行操作");
					return;
				}

			}
		
	}

}
