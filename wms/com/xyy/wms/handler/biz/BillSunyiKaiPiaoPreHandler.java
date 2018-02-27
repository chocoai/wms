package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import com.xyy.bill.event.PreSaveEventListener;

/**
 * @author lichuang.liao
 * 损溢开票单保存前操作
 */
public class BillSunyiKaiPiaoPreHandler implements PreSaveEventListener {
    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        BillContext context = event.getContext();
        String orgId=context.getString("orgId");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
        //String gysbh = head.getStr("gysbh");

        //后台校验 SunyiKaiPiaoValidate
        Date zhidanriqi = head.get("zhidanriqi");
        String zhidanren = head.getStr("zhidanren");
        String kufang = head.getStr("kufang");
        if (zhidanriqi == null || zhidanren == null) {
            event.getContext().addError("损溢开票单", "制单人，制单日期，库房等数据不能为空");
            return;
        }
        //非空校验，小于0校验
        for (Record r : body) {
            if (r.getBigDecimal("sunyishuliang").compareTo(BigDecimal.valueOf(0)) == 0) {
                event.getContext().addError("损溢开票单", "损溢数量为0");
                return;
            }
            if (r.getBigDecimal("shijishuliang").compareTo(BigDecimal.valueOf(0)) < 0) {
                event.getContext().addError("损溢开票单", "实际数量小于0");
                return;
            }
            if(r.getBigDecimal("sjzjs").compareTo(BigDecimal.valueOf(0))<0) {
                event.getContext().addError("损溢开票单", "实际整件数小于0");
                return;
            }
            if(r.getBigDecimal("sjlss").compareTo(BigDecimal.valueOf(0))<0) {
                event.getContext().addError("损溢开票单", "实际零散数小于0");
                return;
            }
            //商品
            String shangpinbianhao = r.getStr("shangpinbianhao");
            Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? AND orgId=?", shangpinbianhao,orgId);
            String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
            //批号
            String pihao = r.getStr("pihao");
            Record pihaoRecord = Db.findFirst("select * from xyy_wms_dic_shangpinpihao where pihao=? AND goodsid=? AND orgId=?", pihao,shangpinId,orgId);
            String pihaoId = pihaoRecord == null ? "" : pihaoRecord.getStr("pihaoId");
            String huowei = r.getStr("huowei");
            //货位
            Record huoweiRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? AND orgId=?", huowei,orgId);
            String huoweiId = huoweiRecord == null ? "" : huoweiRecord.getStr("ID");
            String sql = "SELECT * FROM xyy_wms_bill_kucunyuzhanyukou WHERE huoweiId = ? AND pihaoId=? AND orgId=?";
            Record record = Db.findFirst(sql,huoweiId,pihaoId,orgId);
            if (record != null) {
                event.getContext().addError("损溢开票单", "不能对存在业务操作商品进行操作");
                return;
            }
        }
    }


}
