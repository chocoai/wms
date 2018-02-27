package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.mail.imap.protocol.ID;
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
 * 损溢开票单保存后操作
 */
public class BillSunyiKaiPiaoPostHandler implements PostSaveEventListener {

    //日志
    private static Logger LOGGER = Logger.getLogger(BillSunyiKaiPiaoPostHandler.class);

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
        String danjubianhao = record.getStr("danjubianhao");
        for (Record r : body) {
            BigDecimal kucunshuliang = r.getBigDecimal("kucunshuliang");
            BigDecimal sunyishuliang = r.getBigDecimal("sunyishuliang");
            String shangpinbianhao = r.getStr("shangpinbianhao");

            String huowei = r.getStr("huowei");
            //商品数据
            Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? AND orgId=?", shangpinbianhao,orgId);
            String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
            //批号数据
            String pihao = r.getStr("pihao");
            String pihaosql ="select * from xyy_wms_dic_shangpinpihao where pihao=? AND goodsid=? AND orgId=?";
            Record pihaoObj =Db.findFirst(pihaosql,pihao,shangpinId,orgId);
            //日志打印
            LOGGER.info("损溢开票单-->库存数量：" + kucunshuliang + "损溢数量:" + sunyishuliang);

            //货位资料查询
            Record Rhuowei = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? AND orgId=?", huowei,orgId);
            String huoweiId = Rhuowei.getStr("ID");
            //溢
            if(sunyishuliang.compareTo(BigDecimal.ZERO)<0) {
                BigDecimal syshuliang = new BigDecimal(0).subtract(sunyishuliang);
                KucuncalcService.kcCalc.insertKCRecord(new KuncunParameter(orgId, "", shangpinId, pihaoObj.getStr("pihaoId"), huoweiId, "", danjubianhao, 8, syshuliang));
            }
            //损
            if(sunyishuliang.compareTo(BigDecimal.ZERO)>0) {

                KucuncalcService.kcCalc.insertKCRecord(new KuncunParameter(orgId, "", shangpinId, pihaoObj.getStr("pihaoId"), huoweiId, "", danjubianhao, 7, sunyishuliang));
            }

            LOGGER.info("损溢开票生成");
        }
    }
}
