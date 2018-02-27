package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillSpphxqtzPreHandler implements PreSaveEventListener {
    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        BillContext context = event.getContext();
        String orgId=context.getString("orgId");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
        for (Record r : body) {
            String ypihao = r.getStr("ypihao");
            String newpihao = r.getStr("newpihao");
            String pihaoId = r.getStr("pihaoId");
            String newshengchanriqi = r.getStr("newshengchanriqi");
            String newyouxiaoqizhi = r.getStr("newyouxiaoqizhi");
            if (StringUtil.isEmpty(newpihao) || StringUtil.isEmpty(newshengchanriqi) || StringUtil.isEmpty(newyouxiaoqizhi)) {
                event.getContext().addError("损溢确认单", "新批号，新生产日期，新有效期至等数据不能为空");
                return;
            }
            if (StringUtil.isEmpty(ypihao)) {
                event.getContext().addError("损溢确认单", "原批号不能为空");
                return;
            }

            String sql = "SELECT * FROM xyy_wms_bill_kucunyuzhanyukou WHERE pihaoId = ? AND orgId=?";
            Record record = Db.findFirst(sql,pihaoId,orgId);
            if (record != null) {
                event.getContext().addError("商品效期调整", "不能对存在业务操作商品进行调整");
                return;
            }
        }
    }
}
