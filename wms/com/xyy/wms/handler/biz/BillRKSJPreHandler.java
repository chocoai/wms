package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

public class BillRKSJPreHandler implements PreSaveEventListener {


	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey())) {
			return;

		}
		// 单据头数据
		Record record = getHead(dsi);
		// 单据体数据
		List<Record> body = getBody(dsi);
		for (Record r : body) {
			String shijihuowei =  r.get("shijihuowei");
			if(shijihuowei.equals("")){
				event.getContext().addError("实际货位不能为空", "实际货位不能为空");
				return;
			}
			String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
			Record huoweiObj = Db.findFirst(huoweiSql, shijihuowei);
			if(huoweiObj==null){
				event.getContext().addError("实际货位不存在", "实际货位不存在");
				return;
			}

		}

		
	}
	
}
