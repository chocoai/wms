package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;

import static com.xyy.bill.util.RecordDataUtil.getHead;

public class DicLjqzlwhPreHandler implements PreSaveEventListener {
    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        //获取组织机构id
        String orgId=event.getContext().getString("orgId");
        Record head = getHead(dsi);
        String ljqmc = head.getStr("ljqmc");
        String ljqbh = head.getStr("ljqbh");
        String id = head.getStr("ID");
        String[] ljParams = {ljqmc, ljqbh,orgId};
        if (StringUtil.isEmpty(id)) {
            Record record = Db.findFirst("select * from xyy_wms_dic_ljqzlwh l where l.ljqmc=? and l.ljqbh=? and orgId=?", ljParams);
            if (record != null) {
                event.getContext().addError("逻辑区资料维护", "逻辑区已经存在");
                return;
            }
        }
    }

}
