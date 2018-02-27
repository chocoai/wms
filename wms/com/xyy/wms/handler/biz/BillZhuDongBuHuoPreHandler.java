package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author lichuang.liao
 * 主动补货单保存前操作
 */
public class BillZhuDongBuHuoPreHandler implements PreSaveEventListener {
    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
        //String gysbh = head.getStr("gysbh");

        //后台校验 ZhuDongBuHuoValidate
        Date zhidanriqi = head.get("zhidanriqi");
        String zhidanren = head.getStr("zhidanren");
        String kufang = head.getStr("kufang");

        if (zhidanriqi == null || zhidanren == null || kufang == null) {
            event.getContext().addError("主动补货单", "制单人，制单日期，库房等数据不能为空");
            return;
        }
    /*    for(Record r:body) {
            BigDecimal ybhsl = r.getBigDecimal("ybhsl");
            BigDecimal kbhsl =  r.getBigDecimal("kbhsl");
            if(kbhsl.compareTo(ybhsl)==-1) {
                event.getContext().addError("主动补货单","应补数量不能大于补货数量");
                return;
            }
        }*/
    }
}
