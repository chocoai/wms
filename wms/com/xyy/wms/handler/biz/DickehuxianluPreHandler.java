package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;

import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author by lc.liao
 */

public class DickehuxianluPreHandler implements PreSaveEventListener {

    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
        String xianluId = head.getStr("xianluID");
        String Id = head.getStr("ID");
        if (StringUtil.isEmpty(Id)) {
            for (Record r : body) {
                String kehubianhao = r.getStr("kehubianhao");
                String sql = "SELECT * FROM xyy_wms_dic_kehuxianlu a INNER JOIN xyy_wms_dic_kehuxianlu_list b ON a.id = b.id WHERE kehubianhao = ? AND xianluID=? AND shifouhuodong=1 LIMIT 1";
                Record record = Db.findFirst(sql, kehubianhao, xianluId);
                if (record != null) {
                    event.getContext().addError("客户线路", "该客户已经绑定过线路");
                    return;
                }
            }
        }
    }
}
