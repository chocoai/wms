package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author lichuang.liao
 * 商品批号调整保存后操作
 * */
public class BillSpphxqtzPostHandler implements PostSaveEventListener {

    //日志
    private static Logger LOGGER = Logger.getLogger(BillSpphxqtzPostHandler.class);

    @Override
    public void execute(PostSaveEvent event) {
        BillContext context = event.getContext();
        String orgId=context.getString("orgId");
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null || StringUtil.isEmpty(dsi.getFullKey())) return;
        // 单据头数据
        Record record = getHead(dsi);
        // 单据体数据
        List<Record> body = getBody(dsi);
        //User user = (User)context.get("user");

        int status = record.getInt("status");
        if (status != 20) return;

        for (Record r : body) {
            //商品信息
           String shangpinbianhao = r.getStr("shangpinbianhao");
           Record shangpinObj = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? AND orgId=?",shangpinbianhao,orgId);
           String shangpinId = shangpinObj.getStr("goodsid");


            //原批号信息
            String pihaoId = r.getStr("pihaoId");

            //新批号信息
            String newpihao=r.getStr("newpihao");
            String newshengchanriqi=r.getStr("newshengchanriqi");
            String newyouxiaoqizhi=r.getStr("newyouxiaoqizhi");
            //Date date = new Date();

            //更新新批号到批号表

            Db.update("update xyy_wms_dic_shangpinpihao set pihao = ?,shengchanriqi = ?,youxiaoqizhi=? where pihaoId = ? AND goodsid=? AND orgId=?",newpihao,newshengchanriqi,newyouxiaoqizhi,pihaoId,shangpinId,orgId);
            //EDB.update("xyy_wms_dic_shangpinpihao","update xyy_wms_dic_shangpinpihao set pihao = ?,shengchanriqi = ?,youxiaoqizhi=? where pihaoId = ? AND goodsid=?",newpihao,newshengchanriqi,newyouxiaoqizhi,pihaoId,shangpinId);
            //日志打印
            LOGGER.info("批号效期调整完成！！" );
        }
    }
}
