package com.xyy.wms.outbound.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.util.StringUtil;
import com.xyy.wms.outbound.util.KuncunParameter;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author wbc 内复核保存后处理
 */
public class ChuKuNeiFuHePostHandler implements PostSaveEventListener {


    private static Logger LOGGER = Logger.getLogger(ChuKuNeiFuHePostHandler.class);

    @Override
    public void execute(PostSaveEvent event) {
        BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
            return;
        // 单据头数据
        Record head = getHead(dsi);
        // 单据体数据
        List<Record> bodyList = dsi.getBodyDataTableInstance().get(0).getRecords();

        // 判断当前订单是否已经存在于外复核表中
        String dingdanbianhao = head.getStr("dingdanbianhao");
        String neifuhedanhao = head.getStr("neifuhedanhao");
        StringBuffer sb = new StringBuffer("SELECT * from xyy_wms_bill_chukuwaifuhe WHERE dingdanbianhao = ?");
        Record re = Db.findFirst(sb.toString(), dingdanbianhao);

        if (re != null) {
            // 存在该条订单,就添加数据
            copyBody(re, bodyList, neifuhedanhao);
        } else {
            // 生成出库外复核明细
            copyHead(head);
            copyBody(head, bodyList, neifuhedanhao);
        }

        // 释放容器
        String rongqihao = head.get("rongqihao");
        Object[] params = {0, rongqihao};
        // Db.update("xyy_wms_dic_rqzlwh","update xyy_wms_dic_rqzlwh set shifousuoding=? where rongqibianhao=?", params);
        Db.update("update xyy_wms_dic_rqzlwh set shifousuoding=? where rongqibianhao=?", params);
        LOGGER.info("释放容器");

        // 释放复核台
        // String cangkubianhao = head.getStr("cangkubianhao");
        String fhqbh = head.getStr("fhqbh");
        String fhwbh = head.getStr("fuhetai");
        if (fhqbh != null && fhwbh != null) {
            //Db.update("xyy_wms_dic_fuhequjibenxinxi","update  xyy_wms_dic_fuhequjibenxinxi set shifousuoding=? where fhqbh=? and fhwbh=?", params2);
            Object[] params2 = {fhqbh, fhwbh};
            Db.update("update  xyy_wms_dic_fuhequjibenxinxi set shifousuoding=0 where fhqbh=? AND fhwbh=?", params2);
            LOGGER.info("释放复核台");
        }

        // 修改波次调度的状态
        // Db.update("xyy_wms_bill_bocijihua_details","update xyy_wms_bill_bocijihua_details set dingdanzhuangtai=26 where dingdanbianhao=?", dingdanbianhao);

        List<Record> recordList = Db.find("SELECT * FROM `xyy_wms_bill_dabaorenwu` where dingdanbianhao=?", dingdanbianhao);
        boolean flag = true;
        for (Record record : recordList) {
            if (record.getInt("status") == 36) {
                flag = false;
                break;
            } else {
                flag = true;
            }
        }

        if (flag == false) {
            Db.update("update xyy_wms_bill_bocijihua_details set dingdanzhuangtai=25 where dingdanbianhao=?", dingdanbianhao);
        } else {
            Db.update("update xyy_wms_bill_bocijihua_details set dingdanzhuangtai=26 where dingdanbianhao=?", dingdanbianhao);
        }

    }

    private void copyHead(Record head) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        // 出库外复核汇总
        Record chukuwaifuhe = new Record();
        chukuwaifuhe.set("billID", head.getStr("BillID"));
        chukuwaifuhe.set("orgId", head.get("orgId"));
        chukuwaifuhe.set("orgCode", head.get("orgCode"));
        chukuwaifuhe.set("status", "43");
        chukuwaifuhe.set("rowID", UUIDUtil.newUUID());
        chukuwaifuhe.set("createTime", currentTime);
        chukuwaifuhe.set("dingdanbianhao", head.get("dingdanbianhao"));
        chukuwaifuhe.set("zancunqu", head.get("zancunqu"));
        chukuwaifuhe.set("huozumingcheng", head.get("huozumingcheng"));
        chukuwaifuhe.set("xianlumingcheng", head.get("xianlumingcheng"));
        chukuwaifuhe.set("kehumingcheng", head.get("kehumingcheng"));
        chukuwaifuhe.set("kehubeizhu", head.get("kehubeizhu"));
        chukuwaifuhe.set("caozuoren", head.get("caozuoren"));
        chukuwaifuhe.set("czrmc", head.get("czrmc"));
        if (chukuwaifuhe != null) {
            Db.save("xyy_wms_bill_chukuwaifuhe", chukuwaifuhe);
        }
    }

    private void copyBody(Record head, List<Record> bodyList, String neifuhedanhao) {
        // 拼箱数
        int pinxiangshu = 0;
        // 获取拼箱数
        HashSet<Object> set = new HashSet<>();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        for (Record body : bodyList) {
            // 通过订单编号,商品编号和商品批号来
            String shangpingbianhao = body.getStr("shangpinbianhao");
            String shangpingpihao = body.getStr("shangpingpihao");
            String dingdanbianhao = body.getStr("dingdanbianhao");
            StringBuffer sb = new StringBuffer(
                    "SELECT * from xyy_wms_bill_chukuwaifuhe_details WHERE shangpinbianhao =? AND shangpingpihao = ? AND dingdanbianhao=?");
            Record chukuwaifuheDetails = Db.findFirst(sb.toString(), shangpingbianhao, shangpingpihao, dingdanbianhao);
            // 不存在就添加明细
            if (chukuwaifuheDetails == null) {
                // 出库外复核明细
                chukuwaifuheDetails = new Record();
                chukuwaifuheDetails.set("billDtlID", UUIDUtil.newUUID());
                chukuwaifuheDetails.set("billID", head.getStr("BillID"));
                chukuwaifuheDetails.set("rowID", UUIDUtil.newUUID());
                chukuwaifuheDetails.set("orgId", body.get("orgId"));
                chukuwaifuheDetails.set("orgCode", body.get("orgCode"));
                chukuwaifuheDetails.set("createTime", currentTime);
                chukuwaifuheDetails.set("dingdanbianhao", body.get("dingdanbianhao"));
                chukuwaifuheDetails.set("shuliang", body.get("jihuashuliang"));   //计划数量
                chukuwaifuheDetails.set("lingjianshu", body.get("yifuheshuliang")); //零货数量(ps:已复核数量)
                chukuwaifuheDetails.set("chonghongshuliang", body.get("chonghongshuliang"));//冲红数量
                chukuwaifuheDetails.set("shangpinbianhao", body.get("shangpinbianhao"));
                chukuwaifuheDetails.set("shangpingpihaosn", body.get("shangpingpihaosn"));
                chukuwaifuheDetails.set("shangpinmingcheng", body.get("shangpinmingcheng"));
                chukuwaifuheDetails.set("shengchanchangjia", body.get("shengchanchangjia"));
                chukuwaifuheDetails.set("guige", body.get("guige"));
                chukuwaifuheDetails.set("danwei", body.get("danwei"));
                chukuwaifuheDetails.set("shengchanriqi", body.get("shengchanriqi"));
                chukuwaifuheDetails.set("youxiaoqizhi", body.get("youxiaoqizhi"));
                chukuwaifuheDetails.set("shangpingpihao", body.get("shangpingpihao"));
                chukuwaifuheDetails.set("goodsid", body.get("goodsid"));
                chukuwaifuheDetails.set("xiajiahuoweibianhao", body.get("xiajiahuoweibianhao"));
                chukuwaifuheDetails.set("huoweiID", body.get("huoweiID"));
                chukuwaifuheDetails.set("caozuoren", head.get("czrmc")); // 拣货员
                chukuwaifuheDetails.set("fhymc", head.get("caozuoren")); // 复核员
                chukuwaifuheDetails.set("pinxianghao", body.get("pinxianghao")); // 拼箱号
                set.add(body.get("pinxianghao"));

                //预扣预占表减去冲红数量
                chongHongYuZan(body);

                Db.save("xyy_wms_bill_chukuwaifuhe_details", chukuwaifuheDetails);
            } else {
                // 已复核的零件数量
                BigDecimal yifuheshuliang = body.get("yifuheshuliang");

                //添加箱子数
                set.add(body.get("pinxianghao"));
                // 累加零件总数量
                BigDecimal newshuliang = body.get("yifuheshuliang");
                BigDecimal oldshuliang = chukuwaifuheDetails.get("shuliang");

                chukuwaifuheDetails.set("pinxianghao", body.get("pinxianghao")); // 拼箱号
                // 设置已复核的零件数量
                chukuwaifuheDetails.set("lingjianshu", yifuheshuliang);
                chukuwaifuheDetails.set("shuliang", newshuliang.add(oldshuliang));

                Db.update("xyy_wms_bill_chukuwaifuhe_details", "BillDtlID", chukuwaifuheDetails);
            }
        }


        // 获取set长度
        int length = set.size();
        pinxiangshu = pinxiangshu + length + zongjinshu(bodyList);

        // 更新外复核中的总件数
        StringBuffer sb = new StringBuffer(
                "UPDATE xyy_wms_bill_chukuwaifuhe SET pinxiangshu =pinxiangshu + ?,zongjianshu = zongjianshu + ? ");
        // 修改打包任务中零件的状态为43
        StringBuffer sb1 = new StringBuffer("UPDATE xyy_wms_bill_dabaorenwu ");
        sb1.append("set status = 43 WHERE taskType=10 and dingdanbianhao = ? and taskCode=? ");
        String dingdanbianhao = head.getStr("dingdanbianhao");
        Db.update(sb1.toString(), dingdanbianhao, neifuhedanhao);
        // 如果拣货完成,更新相应的数量,修改回显相应的状态
        if (isCheckCompleted(dingdanbianhao)) {
            // 整件拣货，内复核全部完成 将外复核表的状态改为45
            sb.append(",status = 45 WHERE dingdanbianhao = ? ");
        } else {
            sb.append(" WHERE dingdanbianhao = ?");
        }
        Object[] parms = {pinxiangshu, pinxiangshu, dingdanbianhao};
        Db.update(sb.toString(), parms);
    }

    //拼箱如果不同拣货单下的商品没有装满的逻辑
    private int zongjinshu(List<Record> bodyList) {
        //如果拼箱数 没有满的情况下
        for (Record body1 : bodyList) {
            String dingdanbianhao= body1.get("dingdanbianhao");
            List<Record> list = Db.find("SELECT * FROM `xyy_wms_bill_chukuwaifuhe_details` WHERE dingdanbianhao=?",dingdanbianhao );
            for (Record body2 : list) {
                if (body1.get("pinxianghao") == body2.get("pinxianghao")) {
                        return  -1;
                }

            }
        }
        return 0;
    }


    // 判断是否能进行外复核
    public boolean isCheckCompleted(String dingdanbianhao) {
        List<Record> list = Db.find("select * from xyy_wms_bill_dabaorenwu WHERE dingdanbianhao = ?", dingdanbianhao);
        boolean flag = true;
        for (Record record : list) {
            //还要判断

            // 整件任务，状态为已拣货 或者 零件任务，状态为内复核完成
            if ((record.getInt("taskType") == 20 && record.getInt("status") == 36)
                    || (record.getInt("taskType") == 10 && record.getInt("status") == 43)) {
                flag = true;
            } else {
                flag = false;
                break;
            }
        }
        return flag;
    }

    //预扣预占表减去冲红数量
    private void chongHongYuZan(Record body) {
        KucuncalcService kucuncalcService = new KucuncalcService();
        String orgId = body.get("orgId");
        String shangpinId = body.get("goodsid");
        String pihaoId = body.get("shangpingpihao");
        String huoweiId = body.get("xiajiahuoweibianhao");
        String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao = ?";
        Record huoweiObj = Db.findFirst(huoweiSql, huoweiId);
        String cangkuID = huoweiObj.getStr("cangkuID");

        String dingdanbianhao = body.get("dingdanbianhao");
        //获取业务编号
        Record record = Db.findFirst("select * from xyy_wms_bill_dabaorenwu where dingdanbianhao=?", dingdanbianhao);
        String yewubianhao = record.get("orgId");
        BigDecimal shuliang = body.get("chonghongshuliang");

        KuncunParameter kuncunParameter = new KuncunParameter(orgId, cangkuID, shangpinId, pihaoId, huoweiId, yewubianhao, dingdanbianhao, 2, shuliang);
        Object[] objs = {2};
        kucuncalcService.deleteKCRecord(kuncunParameter, objs);
    }

}
