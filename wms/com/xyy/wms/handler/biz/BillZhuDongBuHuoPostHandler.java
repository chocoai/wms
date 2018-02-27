package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.StringUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author lichuang.liao
 * 主动补货单保存后操作
 */
public class BillZhuDongBuHuoPostHandler implements PostSaveEventListener {

    //日志
    private static Logger LOGGER = Logger.getLogger(BillZhuDongBuHuoPostHandler.class);

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

        int status = record.getInt("status");
        if (status != 20) return;
        String orgId=context.getString("orgId");
        String orgCode=context.getString("orgCode");
        String danjubianhao = record.getStr("danjubianhao");
        String cangkubianhao = record.getStr("cangkubianhao");
        String cSql = "select * from xyy_wms_dic_cangkuziliao where cangkubianhao=?";
        Record cangkuObj = Db.findFirst(cSql,cangkubianhao);
        String cangkuId = cangkuObj.getStr("ID");

        for (Record r : body) {
            BigDecimal shuliang = r.getBigDecimal("shuliang");
           // BigDecimal dbzsl = r.getBigDecimal("dbzsl");
            String yichuhuowei = r.getStr("xiajiahuowei");
            String yiruhuowei = r.getStr("shangjiahuowei");
            String shangpinbianhao = r.getStr("shangpinbianhao");
            Date shengchanriqi = r.getDate("shengchanriqi");
            Date youxiaoqizhi = r.getDate("youxiaoqizhi");
            // 商品数据
            Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? AND orgId=?", shangpinbianhao,orgId);
            String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
            // 批号数据
            Record pihaoRecord = Db.findFirst("select * from xyy_wms_dic_shangpinpihao where pihao=? AND goodsid=? AND orgId=?", r.getStr("pihao"), shangpinId,orgId);
            String pihaoId = pihaoRecord == null ? "" : pihaoRecord.getStr("pihaoId");
            //库区数控
            Record kuquRecord = Db.findFirst("select * from xyy_wms_dic_kuqujibenxinxi where kuqubianhao=? AND orgId=?", r.getStr("shangjiakuqu"), orgId);
            String kuquId = kuquRecord == null ? "" : kuquRecord.getStr("ID");

            LOGGER.info("主动补货单 --> 可补货数量: " + shuliang);
            if (shuliang.compareTo(BigDecimal.ZERO) == 0 || shuliang.compareTo(BigDecimal.ZERO) == -1) continue;
            /*
             *  货位资料查询
             */
            // 移出货位
            Record ychwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? AND orgId=?", yichuhuowei,orgId);
            // 移入货位
            Record yrhwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? AND orgId=?", yiruhuowei,orgId);

            String ychwId = ychwRecord.getStr("ID");
            String yrhwId = yrhwRecord.getStr("ID");

            //新货位时需产生一条新库存记录
            String sql ="select * from xyy_wms_bill_shangpinpihaohuoweikucun where shangpinId = ? and pihaoId=? and huoweiId=? AND orgId=? limit 1";
            Record hwccObj =Db.findFirst(sql,shangpinId,pihaoId,yrhwId,orgId);
            if(null==hwccObj) {
                Record record1 = new Record();
                record1.set("ID", com.xyy.util.UUIDUtil.newUUID());
                record1.set("createTime", new Date());
                record1.set("updateTime", new Date());
                record1.set("status", 10);
                record1.set("rowID", com.xyy.util.UUIDUtil.newUUID());
                record1.set("orgId", orgId);
                record1.set("orgCode", orgCode);
                record1.set("shangpinId", shangpinId);
                record1.set("huozhuId", "0001");
                record1.set("cangkuId", cangkuId);
                record1.set("pihaoId", pihaoId);
                record1.set("shengchanriqi", shengchanriqi);
                record1.set("youxiaoqizhi", youxiaoqizhi);
                record1.set("huoweiId", yrhwId);
                record1.set("kucunshuliang", 0);
                record1.set("kuquId", kuquId);
                record1.set("zhiliangzhuangtai", 1);
                Db.save("xyy_wms_bill_shangpinpihaohuoweikucun", record1);
            }


            //补出预扣
            KucuncalcService.kcCalc.insertKCRecord(new KuncunParameter(orgId, "", shangpinId, pihaoId, ychwId, "", danjubianhao, 4, shuliang));
            //补入预占
            KucuncalcService.kcCalc.insertKCRecord(new KuncunParameter(orgId, "", shangpinId, pihaoId, yrhwId, "", danjubianhao, 3, shuliang));

            LOGGER.info("主动补货预占预扣处理完成!!!");
        }
    }
}

