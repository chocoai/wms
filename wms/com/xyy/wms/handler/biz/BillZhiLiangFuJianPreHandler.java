package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.util.StringUtil;
import com.xyy.wms.outbound.biz.ChuKuNeiFuHePreHandler;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author wbc 内复核保存后处理
 */
public class BillZhiLiangFuJianPreHandler implements PreSaveEventListener {

    private static Logger logger = Logger.getLogger(ChuKuNeiFuHePreHandler.class);

    @Override
    public void execute(PreSaveEvent event) {
        BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null) {
            return;
        }
        // 单据头数据
        Record head = getHead(dsi);
        // 单据体数据
        List<Record> bodyList = dsi.getBodyDataTableInstance().get(0).getRecords();

        String dingdanbianhao = head.get("dingdanbianhao");
        Record record = Db.findFirst("select * from  xyy_wms_bill_zhiliangfujian where dingdanbianhao=? ", dingdanbianhao);
        if (record!=null) {
            int status = record.get("status");
            if (status == 40) {
                event.getContext().addError(dingdanbianhao, "不能重复进行质量复检");
                return;
            }
        }

        //修改状态为复核完成
        head.set("status", 40);
    }


}
