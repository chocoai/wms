package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillZhiLiangFuJianPostHandler implements PostSaveEventListener {

    private static Logger LOGGER = Logger.getLogger(BillZhiLiangFuJianPostHandler.class);

    @Override
    public void execute(PostSaveEvent event) {
        BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
            return;
        // 单据头数据
        Record head = getHead(dsi);
        // 单据体数据
        List<Record> bodys = getBody(dsi);

        //获取任务是入库验收,养护计划
        int taskType = head.get("taskType");

        // 批量入库验收数据回显
        List<Record> rukuyanshouList = new ArrayList<Record>();
        // 批量入库上架单数据回显
        List<Record> rukushangjiadanList = new ArrayList<Record>();
        // 批量养护计划数据回显
        List<Record> yanghujihuaList = new ArrayList<Record>();
        for (Record body : bodys) {
            //商品编号
            String shangpinbianhao = body.get("shangpinbianhao");
            //获取BillID
            String BillDtlID = body.get("BillDtlID");
            //获取入库验收ID
            String yanshouID = body.get("yanshouID");
            //商品批号
            String pihao=body.get("pihao");
            // 根据状态来判断是,回写到入库验收表,或者养护
            if (taskType == 10) {
                //处理入库验收相应的业务
                Record rukuyanshou = Db.findFirst("select * from xyy_wms_bill_rukuyanshou_details where shangpinbianhao = ? and                     BillDtlID= ?", shangpinbianhao,yanshouID);
                // 处理原因
                int chuliyuanyin = body.get("chuliyuanyin");
                // 复检结论
                int fujianjielun = body.get("fujianjielun");
                rukuyanshou.set("yanshoupingding", fujianjielun);
                rukuyanshou.set("pingdingyuanyin", chuliyuanyin);

                //入库上架单的相应状态
                Record rukushangjiadan = Db.findFirst("select * from xyy_wms_bill_rukushangjiadan_details where shangpinbianhao                 = ? and BillDtlID= ?", shangpinbianhao,BillDtlID);
                //回显状态
                rukushangjiadan.set("yanshoupingding", fujianjielun);
                rukushangjiadan.set("pingdingyuanyin", chuliyuanyin);

                //删除xyy_wms_bill_kucunyuzhanyukou
                String huowei = body.get("huowei");
                String orgId = body.getStr("orgId");
                String danjubianhao = body.getStr("dingdanbianhao");

                //获取商品对象
                String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
                Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
                String goodsId = shangpinObj.getStr("goodsid");

                // 批号对象
                String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId =?";
                Record pihaoObj = Db.findFirst(pihaoSql, pihao,goodsId);
                String pihaoId = "";
                if(pihaoObj!=null){
                    pihaoId = pihaoObj.getStr("pihaoId");
                }

                if(null != huowei && huowei.length()>0) {
                    // 删除预占
                    // 货位对象
                    String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
                    Record huoweiObj = Db.findFirst(huoweiSql, huowei);
                    String huoweiId = huoweiObj.getStr("ID");

                    KuncunParameter paras = new KuncunParameter(orgId, danjubianhao, goodsId, pihaoId, huoweiId, BillDtlID);
                    KucuncalcService.kcCalc.deleteRkKCRecord(paras);
                }


                //清空入库上架单的库区名称,计划货位,实际货位
                rukushangjiadan.set("kuqumingcheng", "");
                rukushangjiadan.set("jihuahuowei", "");
                rukushangjiadan.set("shijihuowei", "");


                rukuyanshouList.add(rukuyanshou);
                rukushangjiadanList.add(rukushangjiadan);
            } else {
                //处理养护执行相应的业务
                Record yanghujihua = Db.findFirst("select * from xyy_wms_bill_yhzx_details where shangpinbianhao = ? and                        pihao =? ", shangpinbianhao,pihao);
                // 复检结论
                int fujianjielun = body.get("fujianjielun");
                yanghujihua.set("fujianjielun", fujianjielun);
                yanghujihuaList.add(yanghujihua);
            }

        }
        // 更新入库验收
        Db.batchUpdate("xyy_wms_bill_rukuyanshou_details", "BillDtlID", rukuyanshouList, rukuyanshouList.size());
        // 更新入库上架单
        Db.batchUpdate("xyy_wms_bill_rukushangjiadan_details", "BillDtlID", rukushangjiadanList, rukushangjiadanList.size());
        // 更新养护计划
        Db.batchUpdate("xyy_wms_bill_yhzx_details", "BillDtlID", yanghujihuaList, yanghujihuaList.size());
    }
}
