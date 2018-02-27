package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;

import static com.xyy.bill.util.RecordDataUtil.getHead;

public class YjbgdPreHandler implements PreSaveEventListener {
    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        String shangpinbianhao = head.getStr("shangpinbianhao");
        if (shangpinbianhao == null || shangpinbianhao.equals("")) {
            event.getContext().addError("药检报告单", "药检报告单数据为空");
            return;
        }
    }
}
