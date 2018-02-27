package com.xyy.wms.outbound.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;
import com.xyy.wms.outbound.util.KuncunParameter;
import com.xyy.wms.outbound.util.ZhangYeParameter;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author wbc 外复核保存后处理
 */
public class ChuKuWaiFuHePostHandler implements PostSaveEventListener {
    private static Logger LOGGER = Logger.getLogger(ChuKuWaiFuHePostHandler.class);

    @Override
    public void execute(PostSaveEvent event) {
        BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
            return;
        // 单据头数据
        Record record = getHead(dsi);
        // 单据体数据
        List<Record> bodys = getBody(dsi);
        // User user = (User)context.get("user");
        List<Record> shangpinzongkucunList = new ArrayList<Record>();
        List<Record> shangpinpihaokucunList = new ArrayList<Record>();
        List<Record> spphhwList = new ArrayList<Record>();
        for (Record body : bodys) {
            // 订单实际出库数量
            BigDecimal shuliang = body.getBigDecimal("shuliang");
            // 出库零散数量
            //BigDecimal shuliang = body.getBigDecimal("lingjianshu");
            //获取订单编号
            String dingdanbianhao = body.getStr("dingdanbianhao");
            // 获取商品id
            String shangpinId = body.getStr("goodsid");

            //通过整件数来判断是否用整件数,如果有的话,去xyy_wms_bill_dabaorenwu_details
            BigDecimal zhengjianshu = body.getBigDecimal("zhengjianshu");
            BigDecimal a=new BigDecimal(0);
            if (zhengjianshu.compareTo(a)!=0){
                List<Record> recordList = Db.find("select * from xyy_wms_bill_dabaorenwu_details where taskType in(10,20) and  dingdanbianhao=?",dingdanbianhao);

                for (Record body1:recordList){
                    //获取批号id;
                    String pihaoId = body1.getStr("shangpingpihao");
                    //获取获取货位id;
                    String huoweiId = body1.getStr("xiajiahuowei");
                    //获取orgId
                    String orgId = body1.getStr("orgId");
                    //获取cangkuID
                    // 货位对象
                    String huoweibianhao =body1.getStr("xiajiahuoweibianhao");
                    String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao = ?";
                    Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhao);
                    String kuquId = huoweiObj.getStr("kuquId");
                    String cangkuID = huoweiObj.getStr("cangkuID");

                    //获取业务编号
                    Record record1=Db.findFirst("select * from xyy_wms_bill_dabaorenwu where dingdanbianhao=?",dingdanbianhao);
                    String yewubianhao=record1.getStr("bocibianhao");

                    //通过预扣预占表
                    KucuncalcService kucuncalcService = new KucuncalcService();
                    KuncunParameter kuncunParameter = new KuncunParameter(orgId, cangkuID, shangpinId, pihaoId, huoweiId, yewubianhao, dingdanbianhao, 2, shuliang);
                    Object[] objs={2};
                    kucuncalcService.deleteKCRecord(kuncunParameter,objs);
                }
            }else {

                //获取批号id;
                String pihaoId = body.getStr("shangpingpihao");
                //获取获取货位id;
                String huoweiId = body.getStr("huoweiID");
                //获取orgId
                String orgId = body.getStr("orgId");
                //获取cangkuID
                // 货位对象
                String huoweibianhao = body.getStr("xiajiahuoweibianhao");
                String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao = ?";
                Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhao);
                String kuquId = huoweiObj.getStr("kuquId");
                String cangkuID = huoweiObj.getStr("cangkuID");

                //获取业务编号
                Record record1 = Db.findFirst("select * from xyy_wms_bill_dabaorenwu where dingdanbianhao=?", dingdanbianhao);
                String yewubianhao = record1.getStr("bocibianhao");

                //通过预扣预占表
                KucuncalcService kucuncalcService = new KucuncalcService();
                KuncunParameter kuncunParameter = new KuncunParameter(orgId, cangkuID, shangpinId, pihaoId, huoweiId, yewubianhao, dingdanbianhao, 2, shuliang);
                Object[] objs = {2};
                kucuncalcService.deleteKCRecord(kuncunParameter, objs);

            }
            // 5,释放集货位
            openJiHuoWei(record);

            // 6,状态回显,外复核完成
            waiFuHeWanChen(record);

            // 7,修改波次调度的状态
            Db.update("update xyy_wms_bill_bocijihua_details set dingdanzhuangtai=28 where dingdanbianhao=?",
                    dingdanbianhao);

            //8,销售订单零散冲红
            String sql = "select * from xyy_wms_bill_xiaoshoudingdan_details  where  goodsid=? and danjubianhao=?  ";
            Record xiaoshoudiandan = Db.findFirst(sql, shangpinId, dingdanbianhao);
            //冲红数量
            BigDecimal chonghongshuliang = xiaoshoudiandan.getBigDecimal("chonghongshuliang");
            //出库冲红数量
            BigDecimal chukuchonghongshuliang = body.getBigDecimal("chonghongshuliang");
            chonghongshuliang.add(chukuchonghongshuliang);

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            //回写销售订单汇总表
            Object[] paras = {currentTime, dingdanbianhao};
            Db.update("update xyy_wms_bill_xiaoshoudingdan set shifouchonghong='1' ,  updateTime=? where danjubianhao=?", paras);
            //回写销售订单明细表
            Object[] paras1 = {chonghongshuliang, currentTime, dingdanbianhao, shangpinId};
            Db.update("update xyy_wms_bill_xiaoshoudingdan_details set chonghongshuliang=?  ,  updateTime=?   where danjubianhao=? and  goodsid=?", paras1);


            //9,销售订单整单冲红
            String zhengjiansql = "select * from xyy_wms_bill_xiaoshoudingdan  where  shifouchonghong=1 and  shifoutongbu=0 ";
            List<Record> recordList = Db.find(zhengjiansql);
            for (Record records : recordList) {
                records.set("updateTime", currentTime);
                records.set("shifoutongbu", "1");
                Db.update("xyy_wms_bill_xiaoshoudingdan", "BillID", records);
            }
        }

        // 4,更新商品账页表
        insertZhangye(record, bodys);


    }

    // 4,更新商品账页表
    //更新【商品帐页表】，insert处理
    public static void insertZhangye(Record record, List<Record> body) {
        //String huozhubianhao = record.getStr("huozhubianhao");
        String dingdanbianhao = record.getStr("dingdanbianhao");
        List<ZhangYeParameter> list = new ArrayList();
        for (Record r : body) {
            String shangpinbianhao = r.getStr("shangpinbianhao");

            //通过整件数来判断是否用整件数,如果有的话,去xyy_wms_bill_dabaorenwu_details
            BigDecimal zhengjianshu = r.getBigDecimal("zhengjianshu");
            BigDecimal a=new BigDecimal(0);
            if (zhengjianshu.compareTo(a)!=0) {
                List<Record> recordList = Db.find("select * from xyy_wms_bill_dabaorenwu_details where taskType in(10,20) and  dingdanbianhao=?", dingdanbianhao);
                for (Record body1 : recordList) {
                    // 商品对象
                    String spSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao = ?";
                    Record spObj = Db.findFirst(spSql, shangpinbianhao);
                    String goodsId = spObj.getStr("goodsid");
                    // 批号Id
                    String pihaoId = body1.getStr("shangpingpihao");
                    // 实际出库的数量
                    int shuliang1 = body1.getInt("shuliang");
                    BigDecimal shuliang = new BigDecimal(shuliang1);
                    // 货位对象
                    String huoweibianhao = body1.getStr("xiajiahuoweibianhao");
                    String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao = ?";
                    Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhao);
                    String kuquId = huoweiObj.getStr("kuquId");
                    String cangkuID = huoweiObj.getStr("cangkuID");

                    String huoweiId = huoweiObj.getStr("ID");
                    //制单人
                    String zhidanren = body1.getStr("caozuoren");
                    //制单时间
                    Date zhidanriqi = body1.getDate("createTime");

                    // 出库数量 rukushuliang
                    BigDecimal rukushuliang = new BigDecimal("0.00");
                    // 账页对象
                    ZhangYeParameter pams = new ZhangYeParameter();
                    pams.setOrgId(body1.getStr("orgId"));
                    pams.setOrgCode(body1.getStr("orgCode"));
                    pams.setDanjubianhao(dingdanbianhao);
                    pams.setCangkuId(cangkuID);
                    pams.setHuoweiId(huoweiId);
                    pams.setPihaoId(pihaoId);
                    pams.setHuozhuId("0001");
                    pams.setChukushuliang(shuliang);
                    pams.setRukushuliang(rukushuliang);
                    pams.setShangpinId(goodsId);
                    pams.setZhidanren(zhidanren);
                    pams.setZhidanriqi(zhidanriqi);
                    list.add(pams);
                }
                ZhangYeInsertService.zhangyeInsert.updateZhangye(list, 3);
                return;
            } else {
                // 商品对象
                String spSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao = ?";
                Record spObj = Db.findFirst(spSql, shangpinbianhao);
                String goodsId = spObj.getStr("goodsid");
                // 批号Id
                String pihaoId = r.getStr("shangpingpihao");
                // 实际出库的数量
                BigDecimal shuliang = r.getBigDecimal("shuliang");
                // 货位对象
                String huoweibianhao = r.getStr("xiajiahuoweibianhao");
                String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao = ?";
                Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhao);
                String kuquId = huoweiObj.getStr("kuquId");
                String cangkuID = huoweiObj.getStr("cangkuID");

                String huoweiId = huoweiObj.getStr("ID");
                //制单人
                String zhidanren = r.getStr("caozuoren");
                //制单时间
                Date zhidanriqi = r.getDate("createTime");

                // 出库数量 rukushuliang
                BigDecimal rukushuliang = new BigDecimal("0.00");
                // 账页对象
                ZhangYeParameter pams = new ZhangYeParameter();
                pams.setOrgId(r.getStr("orgId"));
                pams.setOrgCode(r.getStr("orgCode"));
                pams.setDanjubianhao(dingdanbianhao);
                pams.setCangkuId(cangkuID);
                pams.setHuoweiId(huoweiId);
                pams.setPihaoId(pihaoId);
                pams.setHuozhuId("0001");
                pams.setChukushuliang(shuliang);
                pams.setRukushuliang(rukushuliang);
                pams.setShangpinId(goodsId);
                pams.setZhidanren(zhidanren);
                pams.setZhidanriqi(zhidanriqi);
                list.add(pams);
            }
        }
        ZhangYeInsertService.zhangyeInsert.updateZhangye(list, 3);
    }

    // 5,释放集货位
    private void openJiHuoWei(Record record) {
        String jihuoweibianhao = record.get("zancunqu");
        String[] split = jihuoweibianhao.split("/");
        if (split[0].equals(split[1])) {
            Db.update("update xyy_wms_dic_jihuowei set shifousuoding=0 where jihuoweibianhao=?", split[1]);
            LOGGER.info("释放集货位");
        }else {

        }
    }

    // 6,状态回显,外复核完成
    private void waiFuHeWanChen(Record record) {
        String dingdanbianhao = record.get("dingdanbianhao");
        Db.update("UPDATE xyy_wms_bill_chukuwaifuhe set status = 47 WHERE dingdanbianhao = ? ", dingdanbianhao);
        LOGGER.info("状态回显,外复核完成");
    }

}
