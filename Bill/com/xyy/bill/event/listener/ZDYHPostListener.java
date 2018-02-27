package com.xyy.bill.event.listener;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;


/**
 * 重点养护保存后相关操作
 * @author Chen
 *
 */
public class ZDYHPostListener implements PostSaveEventListener {

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		Date endTime =head.getDate("jieshuriqi");
		Date startTime = head.getDate("qishiriqi");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String now = format.format(new Date());
		long times = 0;
		try {
			times = format.parse(now).getTime();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (endTime.getTime()>=times&&startTime.getTime()<=times) {
			List<Record> bodys = dsInst.getBodyDataTableInstance().get(0).getRecords();
			List<Record> updateList = new ArrayList<>();
			for (Record record : bodys) {
				Record zdpz = Db.findFirst("select * from xyy_erp_dic_yanghupinzhong "
						+ "where goodsid ='"+record.getStr("goodsid")+"' limit 1");
				if (zdpz!=null) {
					zdpz.set("yanghuleixing", 1);
				}
				updateList.add(zdpz);
			}
			Db.batchUpdate("xyy_erp_dic_yanghupinzhong", "ID", updateList, 500);
		}
		
	}


}
