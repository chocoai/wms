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
public class DicpeisongxianluPreHandler implements PreSaveEventListener {
    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        String id = head.getStr("ID");
        //新增校验
        if(StringUtil.isEmpty(id)) {
            String xianlubianhao=head.getStr("xianlubianhao");
            String sql="select * from xyy_wms_dic_peisongxianlu  where xianlubianhao=? AND shifouhuodong=1";
            Record record= Db.findFirst(sql,xianlubianhao);
            if (record!=null){
                event.getContext().addError("配送线路","已经存在相同线路编号");
                return;
            }
        }
    }
}
