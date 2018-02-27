package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.ext.directive.Str;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.StringUtil;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillYiKuKaiPiaoPostHandler implements PostSaveEventListener {

    private static Logger LOGGER = Logger.getLogger(BillYiKuKaiPiaoPostHandler.class);

    @Override
    public void execute(PostSaveEvent event) {
        BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null || StringUtil.isEmpty(dsi.getFullKey())) return;
        // 单据头数据
        Record record = getHead(dsi);
        //获取组织机构id
        String orgId=context.getString("orgId");
        // 单据体数据
        List<Record> body = getBody(dsi);
        String danjubianhao=record.getStr("danjubianhao");
        int status = record.getInt("status");
        if (status != 20) return;
        String cangkumingcheng=record.getStr("cangkumingcheng");
        Record cangku=Db.findFirst("select * from xyy_wms_dic_cangkuziliao where cangkumingchen=?",cangkumingcheng);
        String cangkuID=cangku==null?"":cangku.getStr("ID");
        //移出货位数据集合
        List<Record> ycUpdateList = new ArrayList<>();
        //移入货位数据集合
        List<Record> yrUpdateList = new ArrayList<>();
        for (Record r : body) {
            String yichuhuowei = r.getStr("yichuhuowei");
            BigDecimal shuliang = r.getBigDecimal("shuliang");
            String yiruhuowei = r.getStr("yiruhuowei");
            String shangpinbianhao = r.getStr("shangpinbianhao");
            // 商品数据
            Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? and orgId=?", shangpinbianhao,orgId);
            String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
            // 批号数据
            String pihaoId = r.getStr("pihaoId");
            LOGGER.info("移库开票单 --> 移出货位编号: " + yichuhuowei + " 移入货位编号: " + yiruhuowei + " 数量: " + shuliang);
            if (StringUtil.isEmpty(yichuhuowei) || StringUtil.isEmpty(yiruhuowei)) continue;
            // 移出货位
            Record ychwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? and orgId=?", yichuhuowei,orgId);
            // 移入货位
            Record yrhwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? and orgId=?", yiruhuowei,orgId);
            if (ychwRecord != null && yrhwRecord != null) {
                String ychwId = ychwRecord.getStr("ID");
                String yrhwId = yrhwRecord.getStr("ID");
                String[] ycParams = {shangpinId, pihaoId, ychwId};
                String[] yrParams = {shangpinId, pihaoId, yrhwId,orgId};
                //移出预扣
                Record yichuRecord = new Record();
                yichuRecord.set("danjubianhao", danjubianhao);
                yichuRecord.set("BillID", UUIDUtil.newUUID());
                yichuRecord.set("shuliang", shuliang);
                yichuRecord.set("huoweiId", ychwId);
                yichuRecord.set("pihaoId", pihaoId);
                yichuRecord.set("goodsid", shangpinId);
                yichuRecord.set("createTime", new Date());
                yichuRecord.set("updateTime", new Date());
                yichuRecord.set("orgId", yrhwRecord.getStr("orgId"));
                yichuRecord.set("orgCode", yrhwRecord.getStr("orgCode"));
                yichuRecord.set("zuoyeleixing", 6);
                ycUpdateList.add(yichuRecord);
                Record yrkucun = Db.findFirst("select * from xyy_wms_bill_shangpinpihaohuoweikucun where shangpinId = ? and pihaoId = ? and huoweiId = ? and orgId=? order by createTime desc limit 1", yrParams);
                if(yrkucun==null){
                    Date shengchanriqi = r.getDate("shengchanriqi");
                    Date youxiaoqizhi = r.getDate("youxiaoqizhi");
                    Record yiru=new Record();
                    yiru.set("ID",UUIDUtil.newUUID());
                    yiru.set("cangkuId",cangkuID);
                    yiru.set("orgId",yrhwRecord.getStr("orgId"));
                    yiru.set("orgCode",yrhwRecord.getStr("orgCode"));
                    yiru.set("shangpinId", shangpinId);
                    yiru.set("pihaoId", pihaoId);
                    yiru.set("kucunshuliang", BigDecimal.ZERO);
                    yiru.set("shengchanriqi", shengchanriqi);
                    yiru.set("youxiaoqizhi", youxiaoqizhi);
                    yiru.set("huoweiId", yrhwId);
                    yiru.set("createTime", new Date());
                    yiru.set("beizhu", "");
                    Db.save("xyy_wms_bill_shangpinpihaohuoweikucun",yiru);
                }
                //移入预占
                Record yiruRecord = new Record();
                yiruRecord.set("danjubianhao", danjubianhao);
                yiruRecord.set("BillID", UUIDUtil.newUUID());
                yiruRecord.set("shuliang", shuliang);
                yiruRecord.set("huoweiId", yrhwId);
                yiruRecord.set("pihaoId", pihaoId);
                yiruRecord.set("goodsid", shangpinId);
                yiruRecord.set("createTime", new Date());
                yiruRecord.set("updateTime", new Date());
                yiruRecord.set("orgId", yrhwRecord.getStr("orgId"));
                yiruRecord.set("orgCode", yrhwRecord.getStr("orgCode"));
                yiruRecord.set("zuoyeleixing", 5);
                yrUpdateList.add(yiruRecord);
            }
        }
        KucuncalcService.kcCalc.insertBatchKCRecord(ycUpdateList);
        KucuncalcService.kcCalc.insertBatchKCRecord(yrUpdateList);
        LOGGER.info("移库开票单预占预扣处理完成");
    }
}
