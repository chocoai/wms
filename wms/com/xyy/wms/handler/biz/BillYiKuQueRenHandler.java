package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xyy.erp.platform.system.task.EDB;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.biz.ZhangYeInsertService;
import com.xyy.wms.outbound.util.KuncunParameter;
import com.xyy.wms.outbound.util.ZhangYeParameter;
import org.apache.log4j.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

public class BillYiKuQueRenHandler implements PostSaveEventListener {
    private static Logger LOGGER = Logger.getLogger(BillYiKuQueRenHandler.class);



    @Override
    public void execute(PostSaveEvent event) {
        BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
            return;
        //账页record集合
        List<Record> zyList = new ArrayList<>();
        // 单据头数据
        Record record = getHead(dsi);
        String danjubianhao=record.getStr("danjubianhao");
        //获取仓库基本信息
        String cangkubianhao=record.getStr("cangkubianhao");
        String ckSql="SELECT * from xyy_wms_dic_cangkuziliao c where c.cangkubianhao='"+cangkubianhao+"'";
        Record ckRecord=Db.findFirst(ckSql);
        String cangkuID=ckRecord==null?"":ckRecord.getStr("ID");
        // 单据体数据
        List<Record> body = getBody(dsi);
        //获取组织机构id
        String orgId=context.getString("orgId");
        int status = record.getInt("status");
        if (status != 47)
            return;
        for (Record r : body) {
            String yichuhuowei = r.getStr("yichuhuowei");
            BigDecimal shuliang = r.getBigDecimal("shijishuliang");
            String yiruhuowei = r.getStr("yiruhuowei");
            String shangpinbianhao = r.getStr("shangpinbianhao");
            // 商品数据
            Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? and orgId=?", shangpinbianhao,orgId);
            String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
            // 批号数据
            Record pihaoRecord = Db.findFirst("select * from xyy_wms_dic_shangpinpihao where pihaoId=? and orgId=?", r.getStr("pihaoId"),orgId);
            String pihaoId = pihaoRecord == null ? "" : pihaoRecord.getStr("pihaoId");
            LOGGER.info("移库确认单 --> 移出货位编号: " + yichuhuowei + " 移入货位编号: " + yiruhuowei + " 数量: " + shuliang);
            if (StringUtil.isEmpty(yichuhuowei) || StringUtil.isEmpty(yiruhuowei)) continue;
            // 移出货位
            Record ychwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? and orgId=?", yichuhuowei,orgId);
            // 移入货位
            Record yrhwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? and orgId=?", yiruhuowei,orgId);
            if (ychwRecord != null && yrhwRecord != null) {
                String ychwId = ychwRecord.getStr("ID");
                String yrhwId = yrhwRecord.getStr("ID");
                //移出业务类型
                Object[] ycObjs={6};
                //更新移出货位库存
                KucuncalcService.kcCalc.deleteKCRecord(new KuncunParameter(orgId,null,shangpinId,pihaoId,ychwId,null,danjubianhao,6,BigDecimal.ZERO));
                //生成账页
                Record ycRecord=new Record();
                ycRecord.set("orgId",ychwRecord.getStr("orgId"));
                ycRecord.set("danjubianhao",danjubianhao);
                ycRecord.set("chukushuliang",shuliang);
                ycRecord.set("pihaoId",pihaoId);
                ycRecord.set("shangpinId",shangpinId);
                ycRecord.set("huoweiId",ychwId);
                ycRecord.set("cangkuId",cangkuID);
                ycRecord.set("huozhuId","0001");
                ycRecord.set("zhaiyao",5);
                ycRecord.set("zhiliangzhuangtai",0);
                ycRecord.set("createTime",new Date());
                ycRecord.set("updateTime",new Date());
                ycRecord.set("zhidanren",record.getStr("zhidanren"));
                ycRecord.set("zhidanriqi",record.getDate("zhidanriqi"));
                zyList.add(ycRecord);
                //移入货位业务类型
                Object[] yrObjs={5};
                //更新库存
                KucuncalcService.kcCalc.deleteKCRecord(new KuncunParameter(orgId,null,shangpinId,pihaoId,yrhwId,null,danjubianhao,5,BigDecimal.ZERO));
                //生成账页
                Record yrRecord=new Record();
                yrRecord.set("orgId",ychwRecord.getStr("orgId"));
                yrRecord.set("danjubianhao",danjubianhao);
                yrRecord.set("rukushuliang",shuliang);
                yrRecord.set("pihaoId",pihaoId);
                yrRecord.set("shangpinId",shangpinId);
                yrRecord.set("huoweiId",yrhwId);
                yrRecord.set("zhaiyao",5);
                yrRecord.set("huozhuId","0001");
                yrRecord.set("cangkuId",cangkuID);
                yrRecord.set("zhiliangzhuangtai",0);
                yrRecord.set("createTime",new Date());
                yrRecord.set("updateTime",new Date());
                yrRecord.set("zhidanren",record.getStr("zhidanren"));
                yrRecord.set("zhidanriqi",record.getDate("zhidanriqi"));
                zyList.add(yrRecord);
            }
        }
        if (zyList.size() > 0) {
            //生成账页数据
            EDB.batchSave("xyy_wms_bill_shangpinzhangye",zyList);
        }
    }
}
