package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;

import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author lichuang.liao
 * 补货上架单保存前操作
 */
public class BillBuHuoShangJiaPreHandler implements PreSaveEventListener {
    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
        //String gysbh = head.getStr("gysbh");

        // TODO 后台校验 YiKuKaiPiaoValidate
        Date zhidanriqi = head.get("zhidanriqi");
        String zhidanren = head.getStr("zhidanren");
        String kufang = head.getStr("kufang");
        if (zhidanriqi == null || zhidanren == null) {
            event.getContext().addError("补货上架单", "制单人，制单日期，库房等数据不能为空");
            return;
        }
    }
}
