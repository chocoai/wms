package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;

import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author by lc.liao
 * */
public class DickehudizhiPreHandler implements PreSaveEventListener {
    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        String lianxiren = head.getStr("lianxiren");
        String dianhua = head.getStr("dianhua");
        String dizhi = head.getStr("dizhi");
        if (StringUtil.isEmpty(lianxiren) || StringUtil.isEmpty(dianhua) || StringUtil.isEmpty(dizhi)) {
            event.getContext().addError("客户地址", "联系人，电话，地址数据不能为空");
            return;
        }

    }
}
