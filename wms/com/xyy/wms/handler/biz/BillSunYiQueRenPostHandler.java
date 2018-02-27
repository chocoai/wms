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
import com.xyy.wms.outbound.biz.ZhangYeInsertService;
import com.xyy.wms.outbound.util.KuncunParameter;
import com.xyy.wms.outbound.util.ZhangYeParameter;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;


/**
 * @author lichuang.liao
 * 损溢确认单保存后操作
 */

public class BillSunYiQueRenPostHandler implements PostSaveEventListener {

    //日志
    private static Logger LOGGER = Logger.getLogger(BillSunYiQueRenPostHandler.class);

    //账页record集合




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
        String orgId=context.getString("orgId");
        String orgCode=context.getString("orgCode");
        String danjubianhao = record.getStr("danjubianhao");
        String cangkubianhao = record.getStr("cangkubianhao");
        String cSql = "select * from xyy_wms_dic_cangkuziliao where cangkubianhao='"+cangkubianhao+"'AND orgId ='"+orgId+"'";
        Record cangkuObj = Db.findFirst(cSql);
        String cangkuId= cangkuObj.getStr("ID");
        List<Record> yzyList = new ArrayList<>();

        int status = record.getInt("status");
        if (status != 47) return;
        for (Record r : body) {
            BigDecimal sunyishuliang = r.getBigDecimal("sunyishuliang");
            BigDecimal kucunshuliang = r.getBigDecimal("kucunshuliang");
            //损溢数量为0时跳出
            String shangpinbianhao = r.getStr("shangpinbianhao");
            //商品数据
            Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? AND orgId=?", shangpinbianhao,orgId);
            String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
            String huowei = r.getStr("huowei");
            String pihao = r.getStr("pihao");
            String pihaosql ="select * from xyy_wms_dic_shangpinpihao where pihao=? AND goodsid=? AND orgId=?";
            Record pihaoObj =Db.findFirst(pihaosql,pihao,shangpinId,orgId);
            String pihaoId = pihaoObj.getStr("pihaoId");
            //货位资料查询
            Record Rhuowei = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? AND orgId=?", huowei,orgId);
            String huoweiId = Rhuowei.getStr("ID");
            //日志相关
            LOGGER.info("损溢开票单-->库存数量：" + kucunshuliang + "损溢数量:" + sunyishuliang);
            if(sunyishuliang.compareTo(BigDecimal.ZERO)<0) {
                BigDecimal syshuliang = new BigDecimal(0).subtract(sunyishuliang);
                KucuncalcService.kcCalc.deleteKCRecord(new KuncunParameter(orgId,"",shangpinId, pihaoId, huoweiId,"",danjubianhao,8,BigDecimal.ZERO),new Object[]{8});
                //账页相关
                Record yrRecord=new Record();
                yrRecord.set("orgId",orgId);
                yrRecord.set("orgCode",orgCode);
                yrRecord.set("danjubianhao",danjubianhao);
                yrRecord.set("rukushuliang",syshuliang);
                yrRecord.set("pihaoId",pihaoId);
                yrRecord.set("shangpinId",shangpinId);
                yrRecord.set("huoweiId",huoweiId);
                yrRecord.set("cangkuId",cangkuId);
                yrRecord.set("huozhuId","0001");
                yrRecord.set("zhaiyao",6);
                yrRecord.set("zhiliangzhuangtai",0);
                yrRecord.set("createTime",new Date());
                yrRecord.set("updateTime",new Date());
                yrRecord.set("zhidanren",record.getStr("zhidanren"));
                yrRecord.set("zhidanriqi",record.getDate("zhidanriqi"));
                yzyList.add(yrRecord);
            }
            if(sunyishuliang.compareTo(BigDecimal.ZERO)>0) {
                KucuncalcService.kcCalc.deleteKCRecord(new KuncunParameter(orgId,"",shangpinId, pihaoId, huoweiId,"",danjubianhao,7,BigDecimal.ZERO),new Object[]{7});
                Record ycRecord=new Record();
                ycRecord.set("orgId",orgId);
                ycRecord.set("orgCode",orgCode);
                ycRecord.set("danjubianhao",danjubianhao);
                ycRecord.set("chukushuliang",sunyishuliang);
                ycRecord.set("pihaoId",pihaoId);
                ycRecord.set("shangpinId",shangpinId);
                ycRecord.set("huoweiId",huoweiId);
                ycRecord.set("cangkuId",cangkuId);
                ycRecord.set("huozhuId","0001");
                ycRecord.set("zhaiyao",6);
                ycRecord.set("zhiliangzhuangtai",0);
                ycRecord.set("createTime",new Date());
                ycRecord.set("updateTime",new Date());
                ycRecord.set("zhidanren",record.getStr("zhidanren"));
                ycRecord.set("zhidanriqi",record.getDate("zhidanriqi"));
                yzyList.add(ycRecord);
            }

            LOGGER.info("损溢完成 ！！！");

        }
        if (yzyList.size() > 0) {
            //生成账页数据
            EDB.batchSave("xyy_wms_bill_shangpinzhangye",yzyList);
        }

    }
}
