package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.task.EDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillZDYhHandler implements PostSaveEventListener {
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
                Record zdpz = Db.findFirst("select * from xyy_wms_dic_yanghupinzhong "
                        + "where shangpinbianhao ='"+record.getStr("shangpinbianhao")+"' limit 1");
                if (zdpz!=null) {
                    zdpz.set("yanghuleixing", 1);
                }
                updateList.add(zdpz);
            }
            EDB.batchUpdate("xyy_wms_dic_yanghupinzhong", "ID", updateList);

        }

    }
}
