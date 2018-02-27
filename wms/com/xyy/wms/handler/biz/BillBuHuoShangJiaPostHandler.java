package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.StringUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;


/**
 * @author lichuang.liao
 * 补货上架单保存后操作
 */
public class BillBuHuoShangJiaPostHandler implements PostSaveEventListener {

    //日志
    private static Logger LOGGER = Logger.getLogger(BillBuHuoShangJiaPostHandler.class);


    @Override
    public void execute(PostSaveEvent event) {
        BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null || StringUtil.isEmpty(dsi.getFullKey())) return;
        // 单据头数据
        Record record = getHead(dsi);
        // 单据体数据
        List<Record> body = getBody(dsi);
        //User user = (User)context.get("user");
        String orgId = context.getString("orgId");
        String orgCode = context.getString("orgCode");
        String danjubianhao = record.getStr("danjubianhao");
        String cangkubianhao = record.getStr("cangkubianhao");
        String billID = record.getStr("SourceID");
        String cSql = "select * from xyy_wms_dic_cangkuziliao where cangkubianhao=?";
        Record cangkuObj = Db.findFirst(cSql,cangkubianhao);
        String cangkuId = cangkuObj.getStr("ID");
        int status = record.getInt("status");

        //账页record集合
        List<Record> czyList = new ArrayList<>();

        if (status != 20) return;
        for (Record r : body) {
            String billDtID = r.getStr("SourceID");
            BigDecimal shuliang = r.getBigDecimal("shijishuliang");
            String yichuhuowei = r.getStr("xiajiahuowei");
            String yiruhuowei = r.getStr("shangjiahuowei");
            String shangpinbianhao = r.getStr("shangpinbianhao");
            // 商品数据
            Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? AND orgId=?", shangpinbianhao, orgId);
            String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");

            // 批号数据
            Record pihaoRecord = Db.findFirst("select * from xyy_wms_dic_shangpinpihao where pihao=? AND orgId=?", r.getStr("pihao"), orgId);
            String pihaoId = pihaoRecord == null ? "" : pihaoRecord.getStr("pihaoId");

            LOGGER.info("主动补货单 --> 可补货数量: " + shuliang);
            if (shuliang.compareTo(BigDecimal.ZERO) == 0 || shuliang.compareTo(BigDecimal.ZERO) == -1) continue;
            /*
             *  货位资料查询
             */
            // 移出货位
            Record ychwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? AND orgId=?", yichuhuowei, orgId);
            // 移入货位
            Record yrhwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? AND orgId=?", yiruhuowei, orgId);

            String ychwId = ychwRecord.getStr("ID");
            String yrhwId = yrhwRecord.getStr("ID");


            //补出预扣
            KucuncalcService.kcCalc.deleteKCRecord(new KuncunParameter(orgId, "", shangpinId, pihaoId, ychwId, "", danjubianhao, 4, BigDecimal.ZERO));
            //账页相关
            Record ycRecord = new Record();
            ycRecord.set("orgId", orgId);
            ycRecord.set("orgCode", orgCode);
            ycRecord.set("danjubianhao", danjubianhao);
            ycRecord.set("chukushuliang", shuliang);
            ycRecord.set("pihaoId", pihaoId);
            ycRecord.set("shangpinId", shangpinId);
            ycRecord.set("huoweiId", ychwId);
            ycRecord.set("cangkuId", cangkuId);
            ycRecord.set("huozhuId", "0001");
            ycRecord.set("zhaiyao", 7);
            ycRecord.set("zhiliangzhuangtai", 0);
            ycRecord.set("createTime", new Date());
            ycRecord.set("updateTime", new Date());
            ycRecord.set("zhidanren", record.getStr("zhidanren"));
            ycRecord.set("zhidanriqi", record.getDate("zhidanriqi"));
            czyList.add(ycRecord);

            //补入预占
            KucuncalcService.kcCalc.deleteKCRecord(new KuncunParameter(orgId, "", shangpinId, pihaoId, yrhwId, "", danjubianhao, 3, BigDecimal.ZERO));
            //账页相关
            Record yrRecord = new Record();
            yrRecord.set("orgId", orgId);
            yrRecord.set("orgCode", orgCode);
            yrRecord.set("danjubianhao", danjubianhao);
            yrRecord.set("rukushuliang", shuliang);
            yrRecord.set("pihaoId", pihaoId);
            yrRecord.set("shangpinId", shangpinId);
            yrRecord.set("huoweiId", yrhwId);
            yrRecord.set("cangkuId", cangkuId);
            yrRecord.set("huozhuId", "0001");
            yrRecord.set("zhaiyao", 7);
            yrRecord.set("zhiliangzhuangtai", 0);
            yrRecord.set("createTime", new Date());
            yrRecord.set("updateTime", new Date());
            yrRecord.set("zhidanren", record.getStr("zhidanren"));
            yrRecord.set("zhidanriqi", record.getDate("zhidanriqi"));
            czyList.add(yrRecord);

            updateDtStatus(billDtID);

            LOGGER.info("补货上架预占预扣处理完成:");
        }

        updateStatus(billID);

        if (czyList.size() > 0) {
            //生成账页数据
            EDB.batchSave("xyy_wms_bill_shangpinzhangye", czyList);
        }

    }

    //修改体的状态
    private static void updateDtStatus(String billDtID) {
        Db.update("update xyy_wms_bill_zhudongbuhuo_details set zhuangtai=26 where BillDtlID=? ", billDtID);
    }

    //修改头的状态
    private static void updateStatus(String billID) {
        Record r = Db.findFirst("select count(*) countSum from xyy_wms_bill_zhudongbuhuo_details WHERE BillID=?", billID);
        Record r2 = Db.findFirst("select count(*) countStatuns from xyy_wms_bill_zhudongbuhuo_details WHERE BillID=? and zhuangtai=26", billID);
        if (r.get("countSum") == r2.get("countStatuns")) {
            Db.update("update xyy_wms_bill_zhudongbuhuo set zhuangtai=26 WHERE BillID=? ", billID);
        }
    }
}
