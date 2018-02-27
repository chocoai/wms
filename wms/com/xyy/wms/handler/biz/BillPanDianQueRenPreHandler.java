package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;

import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;

public class BillPanDianQueRenPreHandler implements PreSaveEventListener {

    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
        // 后台校验 YiKuKaiPiaoValidate
        String zhidanren = head.getStr("zhidanren");
        Date zhidanriqi = head.get("zhidanriqi");
        if (zhidanriqi == null || zhidanren == null) {
            event.getContext().addError("盘点确认单", "制单人，制单日期，库房等数据不能为空");
            return;
        }

    }

}
