package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.util.PullWMSDataUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.util.StringUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillRuKuYanShouPostHandler implements PostSaveEventListener {

    private static Logger LOGGER = Logger
            .getLogger(BillRuKuYanShouPostHandler.class);

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
        // 判断状态，1-录入，20-正式，40-审核通过
        /*
         * int status = head.getInt("status"); if (status != 20) { return; }
		 */
        HashMap<String, Integer> count = new HashMap<String, Integer>();
        count.put("yanshou", 0); // 验收明细计数
        count.put("jushou", 0); // 拒收明细计数
        count.put("buhege", 0); // 不合格明细计数
        count.put("shangjia", 0); // 上架明细计数
        count.put("fujian", 0); // 质量复检明细计数
        String billID1 = UUIDUtil.newUUID(); // 拒收
        String billID2 = UUIDUtil.newUUID(); // 不合格
        String billID3 = UUIDUtil.newUUID(); // 上架单
        String billID4 = UUIDUtil.newUUID(); // 复检单
        String dingdanbianhao = head.getStr("dingdanbianhao"); // 订单编号
        String rukudanID = head.getStr("rukudanID"); // 入库单ID

        // 生成上架单汇总
        newshangjiadan(head, billID3);

        for (Record body : bodys) {
            BigDecimal shuliang = body.get("shuliang"); // 商品总数
            String shangpinbianhao = body.getStr("shangpinbianhao"); // 商品编号
            String pihao = body.getStr("pihao"); // 商品批号
            String rukuID = body.getStr("rukuID"); // 关联入库收货明细单
            int yanshoupingding = body.getInt("yanshoupingding"); // 验收评定（0-待处理，1-合格，2-不合格，3-拒收）

            // 商品对象
            String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
            Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
            String goodsId = shangpinObj.getStr("goodsid");
            // 如果商品为新批号时，更新商品批号表xyy_wms_dic_shangpinpihao
            boolean falg = getShangPinPHById(pihao, shangpinbianhao);
            if (!falg) {
                Record sppihao = new Record();

                String newpihaoId = "PH" + PullWMSDataUtil.orderno2();// 批号id
                sppihao.set("ID", UUIDUtil.newUUID());
                sppihao.set("shengchanriqi", body.get("shengchanriqi")); // 生产日期
                sppihao.set("youxiaoqizhi", body.get("youxiaoqizhi")); // 有效期至
                sppihao.set("pihao", pihao); // 批号
                sppihao.set("goodsId", goodsId); // 商品id
                sppihao.set("pihaoId", newpihaoId);
                Db.save("xyy_wms_dic_shangpinpihao", sppihao);
                LOGGER.info("更新商品批号：" + pihao);

                // 之前的入库单商品批号
				/*String prePihao = Db
						.queryStr(
								"select pihao from xyy_wms_bill_rukushouhuo_details where shangpinbianhao = ? and BillDtlID = ?",
								shangpinbianhao, rukuID);
				String prePihaoID = Db
						.queryStr(
								"SELECT pihaoId FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId=? limit 1",
								prePihao, goodsId);

				// 更新商品批号库存的入库预占xyy_wms_bill_shangpinpihaokucun
				updatePihaoKuCun(goodsId, newpihaoId, shuliang, prePihaoID);*/
            }

            // 生成据收单明细
            if (yanshoupingding == 3) {
                jushoudanDetails(head, body, billID1, count);
            }

            // 生成不合格品单明细
            if (yanshoupingding == 2) {
                buhegeDetails(head, body, billID2, shuliang, count);
            }

            // 生成上架单明细
            shangjiadanDetails(head, body, billID1, billID2, billID3, billID4,
                    count);

            // 回写入库收货明细单的验收数量
            updateRukushuliang(dingdanbianhao, shangpinbianhao, shuliang,
                    rukuID, rukudanID);

            // 回写收货明细的验收状态（0未验收，1-已验收）
            Record shuliangCompare = Db
                    .findFirst(
                            "select shuliang,yanshoushuliang from xyy_wms_bill_rukushouhuo_details where shangpinbianhao = ? and BillDtlID =?",
                            shangpinbianhao, rukuID);
            if (shuliangCompare != null) {
                BigDecimal shouhuoshuliang = shuliangCompare.get("shuliang");
                BigDecimal shouhuoyanshoushuliang = shuliangCompare
                        .get("yanshoushuliang");
                if (shouhuoshuliang.compareTo(shouhuoyanshoushuliang) == 0) {
                    Db.update(
                            "update xyy_wms_bill_rukushouhuo_details set yanshouzhuangtai = ? where shangpinbianhao = ? and BillDtlID =?",
                            1, shangpinbianhao, rukuID);
                }
            }

            Integer countYanshou = (Integer) count.get("yanshou") + 1;
            count.put("yanshou", countYanshou);// 验收明细计数

            // 更新商品总库存的入库预占
            //updateZongKuCun(goodsId, head, body);

            // 更新商品批号库存的入库预占
            //updateKuCun(goodsId, head, body);
        }

        // 生成据收单汇总
        if (count.get("jushou") > 0) {
            jushoudandan(head, billID1);
        }

        // 生成不合格品单汇总
        if (count.get("buhege") > 0) {
            buhegepin(head, billID2);
        }

        // 生成质量复检单汇总
        if (count.get("fujian") > 0) {
            zhiliangfujian(head, billID4);
        }

        // 回写入库收货单的验收状态，0-未验收，1-部分验收，2-全部验收
        long queryShuohuoDetails = Db
                .queryLong(
                        // 每单明细数
                        "select count(*) from xyy_wms_bill_rukushouhuo a ,"
                                + "xyy_wms_bill_rukushouhuo_details b "
                                + "where a.billID =b.billID and a.dingdanbianhao = ? and a.billid = ?",
                        dingdanbianhao, rukudanID);
        long queryShuohuoIsYanshou = Db
                .queryLong(
                        // 已经验收的单数
                        "select count(*) from xyy_wms_bill_rukushouhuo a ,"
                                + "xyy_wms_bill_rukushouhuo_details b "
                                + "where a.billID =b.billID and a.dingdanbianhao = ? and a.billid = ? and b.yanshouzhuangtai=1",
                        dingdanbianhao, rukudanID);
        if (queryShuohuoIsYanshou == queryShuohuoDetails) {
            updateRukuZhuangtai(dingdanbianhao, 2, rukudanID);
        } else if (queryShuohuoIsYanshou < queryShuohuoDetails
                && queryShuohuoIsYanshou > 0) {
            updateRukuZhuangtai(dingdanbianhao, 1, rukudanID);
        } else if (queryShuohuoIsYanshou == 0) {
            updateRukuZhuangtai(dingdanbianhao, 0, rukudanID);
        } else {
            event.getContext().addError(null, "验收单数量异常");
            return;
        }
    }

    /**
     * 更新商品总库存预占
     *
     * @param danjubianhao
     * @param shangpinbianhao
     * @param rukushuliang
     */
    public static void updateZongKuCun(String goodsid, Record head, Record body) {
        String cangkuId = "";
        String huoweiId = "";
        String pihaoId = "";
        int zuoyeleixing = 1;
        String orgId = head.getStr("orgId");
        String yewubianhao = body.getStr("BillDtlID");
        String danjubianhao = head.getStr("dingdanbianhao");
        BigDecimal shuliang = body.getBigDecimal("shuliang");
        KuncunParameter paras = new KuncunParameter(orgId, cangkuId, goodsid,
                pihaoId, huoweiId, yewubianhao, danjubianhao, zuoyeleixing,
                shuliang);
        KucuncalcService.kcCalc.insertKCRecord(paras);
        LOGGER.info("商品总库存库存更新成功，商品编号：" + yewubianhao + "数量:" + shuliang);
    }

    /**
     * 更新商品批号库存
     *
     * @param danjubianhao
     * @param shangpinbianhao
     * @param rukushuliang
     */
    public static void updatePihaoKuCun(String shangpinId, String pihaoId,
                                        BigDecimal shuliang, String prePihaoID) {
        String sql = "select * from xyy_wms_bill_shangpinpihaokucun where shangpinId = ? and pihaoId = ?";
        List<Record> list = Db.find(sql, shangpinId, pihaoId);
        Record r = new Record();

        // 更新商品批号库存的老批号的库存预占
        Db.update(
                "xyy_wms_bill_shangpinpihaokucun",
                "update xyy_wms_bill_shangpinpihaokucun set rukuyuzhan = rukuyuzhan - ? where shangpinId = ? and pihaoId = ?",
                shuliang, shangpinId, prePihaoID);

        if (list.size() > 0) {
            // 库存中已经有的数量
            BigDecimal sum = list.get(0).getBigDecimal("rukuyuzhan");
            // 更新之后的数量
            BigDecimal rukuyuzhan = sum.add(shuliang);
            String sql2 = "update xyy_wms_bill_shangpinpihaokucun set rukuyuzhan = ? where shangpinId = ? and pihaoId = ?";
            // Db.update(sql2,rukuyuzhan,shangpinId,pihaoId);
            Db.update("xyy_wms_bill_shangpinpihaokucun", sql2, rukuyuzhan,
                    shangpinId, pihaoId);
        } else {
            r.set("ID", UUIDUtil.newUUID());
            r.set("shangpinId", shangpinId);
            r.set("huozhuId", "0001");
            r.set("cangkuId", "394b2a54e5e04c098a117550d249a267");// 东西湖仓库id，以后再改
            r.set("pihaoId", pihaoId);
            r.set("rukuyuzhan", shuliang);
            Db.save("xyy_wms_bill_shangpinpihaokucun", r);

        }
        LOGGER.info("商品批号库存更新成功，商品编号：" + shangpinId + "批号:" + pihaoId);
    }

    /**
     * 更新商品批号库存预占
     *
     * @param danjubianhao
     * @param shangpinbianhao
     * @param rukushuliang
     */
    public static void updateKuCun(String yewubianhao, Record head, Record body) {
        // 商品对象
        String shangpinbianhao = body.getStr("shangpinbianhao");
        String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
        Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
        String goodsId = shangpinObj.getStr("goodsid");
        String Pihao = body.getStr("pihao");
        String PihaoID = Db
                .queryStr(
                        "SELECT pihaoId FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId=? limit 1",
                        Pihao, goodsId);
        String orgId = head.getStr("orgId");
        String cangkumingchen = head.getStr("kufang");
        String cangkuId = Db
                .queryStr(
                        "select ID from xyy_wms_dic_cangkuziliao where cangkumingchen=?",
                        cangkumingchen);
        String danjubianhao = head.getStr("dingdanbianhao");
        BigDecimal shuliang = body.getBigDecimal("shuliang");
        KucuncalcService kucuncalcService2 = new KucuncalcService();
        KuncunParameter kuncunParameter2 = new KuncunParameter(orgId, cangkuId,
                goodsId, PihaoID, "", yewubianhao, danjubianhao, 1, shuliang);
        kucuncalcService2.insertKCRecord(kuncunParameter2);

        LOGGER.info("商品批号库存更新成功，商品编号：" + goodsId + "批号:" + PihaoID);
    }

    /**
     * 查询同一BillID的明细集合
     *
     * @param BillID
     * @param tableName
     * @return
     */
    public static List<Record> getDetailsById(String BillID, String tableName) {
        String sql = "select * from " + tableName + " where BillID = ?";
        List<Record> list = Db.find(sql, BillID);
        return list;
    }

    /**
     * 生成据收单汇总
     */
    public void jushoudandan(Record head, String billID) {
        Record jushoudan = new Record();
        jushoudan.set("billID", billID);
        jushoudan.set("dingdanbianhao", head.getStr("dingdanbianhao"));
        jushoudan.set("dingdanriqi", head.getDate("dingdanriqi"));
        jushoudan.set("dingdanleixing", head.getInt("dingdanleixing"));
        jushoudan.set("danweibianhao", head.getStr("danweibianhao"));
        jushoudan.set("danweimingcheng", head.getStr("danweimingcheng"));
        jushoudan.set("huozhubianhao", head.getStr("huozhubianhao"));
        jushoudan.set("huozhumingcheng", head.getStr("huozhumingcheng"));
        jushoudan.set("kufang", head.getStr("kufang"));
        jushoudan.set("shouhuoyuan", head.getStr("shouhuoyuan"));
        jushoudan.set("zhijianyuan", head.getStr("zhijianyuan"));
        jushoudan.set("bumenmingcheng", head.getStr("bumenmingcheng"));
        jushoudan.set("yewulaiyuan", 20); // 业务来源（10-入库收货，20-入库验收，30-销退收货，40-销退验收）

        jushoudan.set("orgId", head.getStr("orgId"));
        jushoudan.set("orgCode", head.getStr("orgCode"));
        jushoudan.set("rowID", UUIDUtil.newUUID());
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        jushoudan.set("createTime", currentTime);
        jushoudan.set("updateTime", currentTime);
        jushoudan.set("updateTime", currentTime);
        jushoudan.set("cangkubianhao", head.getStr("cangkubianhao"));

        if (jushoudan != null) {
            Db.save("xyy_wms_bill_rukujushou", jushoudan);
            LOGGER.info("拒收单据生成成功，编号：" + head.getStr("dingdanbianhao"));
        }
    }

    /**
     * 生成上架单汇总
     *
     * @param head
     * @param shangjiadan
     * @param billID
     */
    public void newshangjiadan(Record head, String billID) {
        Record shangjiadan = new Record();
        shangjiadan.set("billID", billID);
        shangjiadan.set("dingdanbianhao", head.getStr("dingdanbianhao"));
        shangjiadan.set("dingdanriqi", head.getDate("dingdanriqi"));
        shangjiadan.set("dingdanleixing", head.getInt("dingdanleixing"));
        shangjiadan.set("danweibianhao", head.getStr("danweibianhao"));
        shangjiadan.set("danweimingcheng", head.getStr("danweimingcheng"));
        shangjiadan.set("huozhubianhao", head.getStr("huozhubianhao"));
        shangjiadan.set("huozhumingcheng", head.getStr("huozhumingcheng"));
        shangjiadan.set("kufang", head.getStr("kufang"));
        shangjiadan.set("shouhuoyuan", head.getStr("shouhuoyuan"));
        shangjiadan.set("yanshouyuan", head.getStr("zhijianyuan"));
        shangjiadan.set("caigouyuan", head.get("caigouyuan"));
        shangjiadan.set("beizhu", head.get("beizhu"));
        shangjiadan.set("orgId", head.getStr("orgId"));
        shangjiadan.set("orgCode", head.getStr("orgCode"));
        shangjiadan.set("rowID", UUIDUtil.newUUID());
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        shangjiadan.set("createTime", currentTime);
        shangjiadan.set("updateTime", currentTime);
        shangjiadan.set("cangkubianhao", head.getStr("cangkubianhao"));

        if (shangjiadan != null) {
            Db.save("xyy_wms_bill_rukushangjiadan", shangjiadan);
            LOGGER.info("入库上架单生成成功，编号：" + head.getStr("dingdanbianhao"));
        }
    }

    /**
     * 生成不合格品单汇总
     *
     * @param head
     * @param buhegepin
     * @param billID
     */
    public void buhegepin(Record head, String billID) {
        Record buhegepin = new Record();
        buhegepin.set("billID", billID);
        buhegepin.set("dingdanbianhao", head.getStr("dingdanbianhao"));
        buhegepin.set("dingdanriqi", head.getDate("dingdanriqi"));
        buhegepin.set("dingdanleixing", head.getInt("dingdanleixing"));
        buhegepin.set("danweibianhao", head.getStr("danweibianhao"));
        buhegepin.set("danweimingcheng", head.getStr("danweimingcheng"));
        buhegepin.set("huozhubianhao", head.getStr("huozhubianhao"));
        buhegepin.set("huozhumingcheng", head.getStr("huozhumingcheng"));
        buhegepin.set("kufang", head.getStr("kufang"));
        buhegepin.set("shouhuoyuan", head.getStr("shouhuoyuan"));
        buhegepin.set("zhijianyuan", head.getStr("zhijianyuan"));
        buhegepin.set("bumenmingcheng", head.getStr("bumenmingcheng"));
        buhegepin.set("beizhu", head.get("beizhu"));
        buhegepin.set("bumenmingcheng", head.get("bumenmingcheng"));
        buhegepin.set("orgId", head.getStr("orgId"));
        buhegepin.set("orgCode", head.getStr("orgCode"));
        buhegepin.set("rowID", UUIDUtil.newUUID());
        buhegepin.set("status", 1); // 设置为可编辑状态
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        buhegepin.set("createTime", currentTime);
        buhegepin.set("updateTime", currentTime);
        buhegepin.set("cangkubianhao", head.getStr("cangkubianhao"));
        if (buhegepin != null) {
            Db.save("xyy_wms_bill_buhegepin", buhegepin);
            LOGGER.info("不合格品单生成成功，编号：" + head.getStr("dingdanbianhao"));
        }
    }

    /**
     * 生成质量复检单汇总
     *
     * @param head
     * @param fujian
     * @param billID
     */
    public void zhiliangfujian(Record head, String billID) {
        Record fujian = new Record();
        fujian.set("billID", billID);
        fujian.set("dingdanbianhao", head.getStr("dingdanbianhao"));
        fujian.set("dingdanriqi", head.getDate("dingdanriqi"));
        fujian.set("dingdanleixing", head.getInt("dingdanleixing"));
        fujian.set("danweibianhao", head.getStr("danweibianhao"));
        fujian.set("danweimingcheng", head.getStr("danweimingcheng"));
        fujian.set("huozhubianhao", head.getStr("huozhubianhao"));
        fujian.set("huozhumingcheng", head.getStr("huozhumingcheng"));
        fujian.set("bumenmingcheng", head.getStr("bumenmingcheng"));
        fujian.set("orgId", head.getStr("orgId"));
        fujian.set("orgCode", head.getStr("orgCode"));
        fujian.set("rowID", UUIDUtil.newUUID());
        fujian.set("status", 1); // 设置为可编辑状态
        fujian.set("taskType", 10); // 作业类型（10-入库验收，20-养护计划）
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        fujian.set("createTime", currentTime);
        fujian.set("updateTime", currentTime);
        fujian.set("cangkubianhao", head.getStr("cangkubianhao"));
        if (fujian != null) {
            Db.save("xyy_wms_bill_zhiliangfujian", fujian);
            LOGGER.info("质量复检单生成成功，编号：" + head.getStr("dingdanbianhao"));
        }
    }

    /**
     * 生成不合格品明细单
     *
     * @param head
     * @param body
     * @param buhegeDetail
     * @param billID
     * @param buhegeshuliang
     * @param countBuhege
     */
    public void buhegeDetails(Record head, Record body, String billID,
                              BigDecimal buhegeshuliang, Map<String, Integer> count) {
        Record buhegeDetail = new Record();
        String shangpinbianhao = body.getStr("shangpinbianhao"); // 商品编号
        String shangpinmingcheng = body.getStr("shangpinmingcheng"); // 商品名称
        String shangpinguige = body.getStr("shangpinguige"); // 商品规格
        int baozhuangdanwei = body.getInt("baozhuangdanwei"); // 包装单位
        String pizhunwenhao = body.getStr("pizhunwenhao"); // 批准文号
        String shengchanchangjia = body.getStr("shengchanchangjia"); // 生产厂家
        String pihao = body.getStr("pihao"); // 商品批号
        Date shengchanriqi = body.get("shengchanriqi"); // 生产日期
        Date youxiaoqizhi = body.get("youxiaoqizhi"); // 有效期至
        int pingdingyuanyin = body.getInt("pingdingyuanyin"); // 评定原因
        buhegeDetail.set("billDtlID", UUIDUtil.newUUID());
        buhegeDetail.set("billID", billID);
        buhegeDetail.set("orgId", body.getStr("orgId"));
        buhegeDetail.set("orgCode", body.getStr("orgCode"));
        buhegeDetail.set("rowID", UUIDUtil.newUUID());

        // buhegeDetail.set("dingdanbianhao", head.getStr("dingdanbianhao"));
        buhegeDetail.set("shangpinbianhao", shangpinbianhao);
        buhegeDetail.set("shangpinmingcheng", shangpinmingcheng);
        buhegeDetail.set("shangpinguige", shangpinguige);
        buhegeDetail.set("baozhuangshuliang",
                body.getBigDecimal("baozhuangshuliang"));
        buhegeDetail.set("baozhuangdanwei", baozhuangdanwei);
        buhegeDetail.set("pizhunwenhao", pizhunwenhao);
        buhegeDetail.set("shengchanchangjia", shengchanchangjia);
        buhegeDetail.set("pihao", pihao);
        buhegeDetail.set("shengchanriqi", shengchanriqi);
        buhegeDetail.set("youxiaoqizhi", youxiaoqizhi);
        buhegeDetail.set("pingdingyuanyin", pingdingyuanyin);
        buhegeDetail.set("yanshoupingding", body.getInt("yanshoupingding"));

        buhegeDetail.set("hanshuijia", body.get("hanshuijia"));
        buhegeDetail.set("hanshuijine", body.get("hanshuijine"));
        buhegeDetail.set("choujianshuliang",
                body.getBigDecimal("choujianshuliang"));
        buhegeDetail.set("shuliang", buhegeshuliang);
        buhegeDetail.set("beizhu", body.get("beizhu"));
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        buhegeDetail.set("createTime", currentTime);
        buhegeDetail.set("updateTime", currentTime);
        // 保存不合格品单
        if (buhegeDetail != null) {
            Db.save("xyy_wms_bill_buhegepin_details", buhegeDetail);
            Integer countBubuhge = (Integer) count.get("buhege") + 1;
            count.put("buhege", countBubuhge);
            LOGGER.info("不合格品单生成成功，商品编号：" + body.getStr("shangpinbianhao"));
        }
    }

    /**
     * 生成据收单明细
     *
     * @param head
     * @param body
     * @param jushoudanDetail
     * @param billID
     * @param countJushou
     */
    public void jushoudanDetails(Record head, Record body, String billID,
                                 Map<String, Integer> count) {
        Record jushoudanDetail = new Record();
        String shangpinbianhao = body.getStr("shangpinbianhao"); // 商品编号
        String shangpinmingcheng = body.getStr("shangpinmingcheng"); // 商品名称
        String shangpinguige = body.getStr("shangpinguige"); // 商品规格
        int baozhuangdanwei = body.getInt("baozhuangdanwei"); // 包装单位
        String pizhunwenhao = body.getStr("pizhunwenhao"); // 批准文号
        String shengchanchangjia = body.getStr("shengchanchangjia"); // 生产厂家
        String pihao = body.getStr("pihao"); // 商品批号
        Date shengchanriqi = body.get("shengchanriqi"); // 生产日期
        Date youxiaoqizhi = body.get("youxiaoqizhi"); // 有效期至

        int pingdingyuanyin = body.getInt("pingdingyuanyin"); // 评定原因
        String rongqibianhao = body.getStr("rongqibianhao"); // 容器编号

        BigDecimal hanshuijia = body.getBigDecimal("hanshuijia"); // 含税价

        jushoudanDetail.set("billDtlID", UUIDUtil.newUUID());
        jushoudanDetail.set("billID", billID);
        jushoudanDetail.set("orgId", body.getStr("orgId"));
        jushoudanDetail.set("orgCode", body.getStr("orgCode"));
        jushoudanDetail.set("rowID", UUIDUtil.newUUID());

        jushoudanDetail.set("dingdanbianhao", head.get("dingdanbianhao"));
        jushoudanDetail.set("shangpinbianhao", shangpinbianhao);
        jushoudanDetail.set("shangpinmingcheng", shangpinmingcheng);
        jushoudanDetail.set("shangpinguige", shangpinguige);
        jushoudanDetail.set("baozhuangshuliang",
                body.getBigDecimal("baozhuangshuliang"));
        jushoudanDetail.set("baozhuangdanwei", baozhuangdanwei);
        jushoudanDetail.set("pizhunwenhao", pizhunwenhao);
        jushoudanDetail.set("shengchanchangjia", shengchanchangjia);
        jushoudanDetail.set("pihao", pihao);
        jushoudanDetail.set("shengchanriqi", shengchanriqi);
        jushoudanDetail.set("youxiaoqizhi", youxiaoqizhi);
        jushoudanDetail.set("pingdingyuanyin", pingdingyuanyin);
        jushoudanDetail.set("rongqibianhao", rongqibianhao);

        jushoudanDetail.set("hanshuijia", hanshuijia);
        jushoudanDetail.set("jushoushuliang", body.getBigDecimal("shuliang"));
        jushoudanDetail.set("choujianshuliang", body.getBigDecimal("choujianshuliang"));
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        jushoudanDetail.set("createTime", currentTime);
        jushoudanDetail.set("updateTime", currentTime);

        if (jushoudanDetail != null) {
            Db.save("xyy_wms_bill_rukujushou_details", jushoudanDetail);
            Integer countJushou = (Integer) count.get("jushou") + 1;
            count.put("jushou", countJushou);
            LOGGER.info("拒收单明细生成成功，拒收商品编号：" + shangpinbianhao);
        }
    }

    /**
     * 生成据收单明细
     *
     * @param head
     * @param body
     * @param jushoudanDetail
     * @param billID
     * @param countJushou
     */
    public void zhiliangfujiandetail(Record head, Record body, String billID4, String sjmxUUID, String huoweibianhao,
                                     Map<String, Integer> count) {
        String shangpinbianhao = body.getStr("shangpinbianhao"); // 商品编号
        String shangpinmingcheng = body.getStr("shangpinmingcheng"); // 商品名称
        String shangpinguige = body.getStr("shangpinguige"); // 商品规格
        int baozhuangdanwei = body.getInt("baozhuangdanwei"); // 包装单位
        String pizhunwenhao = body.getStr("pizhunwenhao"); // 批准文号
        String shengchanchangjia = body.getStr("shengchanchangjia"); // 生产厂家
        String pihao = body.getStr("pihao"); // 商品批号
        //Date shengchanriqi = body.get("shengchanriqi"); // 生产日期
        //Date youxiaoqizhi = body.get("youxiaoqizhi"); // 有效期至
        //int pingdingyuanyin = body.getInt("pingdingyuanyin"); // 评定原因
        //String rongqibianhao = body.getStr("rongqibianhao"); // 容器编号
        //BigDecimal hanshuijia = body.getBigDecimal("hanshuijia"); // 含税价
        BigDecimal baozhuangshuliang = body.getBigDecimal("baozhuangshuliang"); // 包装数量
        BigDecimal shuliang = body.getBigDecimal("shuliang"); // 数量

        Record zhiliangfujiandetail = new Record();
        zhiliangfujiandetail.set("shuliang", shuliang);
        zhiliangfujiandetail.set("billID", billID4);
        zhiliangfujiandetail.set("dingdanbianhao",
                head.getStr("dingdanbianhao"));
        zhiliangfujiandetail.set("shangpinbianhao", shangpinbianhao);
        zhiliangfujiandetail.set("shangpinmingcheng", shangpinmingcheng);
        zhiliangfujiandetail.set("shangpinguige", shangpinguige);
        zhiliangfujiandetail.set("baozhuangshuliang", baozhuangshuliang);
        zhiliangfujiandetail.set("baozhuangdanwei", baozhuangdanwei);
        zhiliangfujiandetail.set("pizhunwenhao", pizhunwenhao);
        zhiliangfujiandetail.set("shengchanchangjia", shengchanchangjia);
        zhiliangfujiandetail.set("pihao", pihao);
        zhiliangfujiandetail.set("yanshouID", body.getStr("BillDtlID"));
        zhiliangfujiandetail.set("billDtlID", sjmxUUID);
        zhiliangfujiandetail.set("orgId", body.getStr("orgId"));
        zhiliangfujiandetail.set("rowID", UUIDUtil.newUUID());
        zhiliangfujiandetail.set("huowei", huoweibianhao);

        zhiliangfujiandetail.set("orgCode", body.getStr("orgCode"));

        // zhiliangfujiandetail.set("shengchanriqi", shengchanriqi);
        // zhiliangfujiandetail.set("youxiaoqizhi", youxiaoqizhi);
        // zhiliangfujiandetail.set("rongqibianhao", rongqibianhao);
        // zhiliangfujiandetail.set("fujianjielun", yanshoupingding);
        // zhiliangfujiandetail.set("chuliyuanyin", pingdingyuanyin);
        // shangjiadanDetail.set("zhengjianshu", zhengjianshu);
        // shangjiadanDetail.set("lingsanshu", lingsanshu);
        // zhiliangfujiandetail.set("choujianshuliang", choujianshuliang);
        if (zhiliangfujiandetail != null) {
            Db.save("xyy_wms_bill_zhiliangfujian_details",
                    zhiliangfujiandetail);
            Integer countFujian = (Integer) count.get("fujian") + 1;
            count.put("fujian", countFujian);
            LOGGER.info("质量复检单明细生成成功，商品编号：" + shangpinbianhao);
        }
    }

    /**
     * 生成上架单明细
     *
     * @param head
     * @param body
     * @param shangjiadanDetails
     * @param jushoushuliang
     * @param shuliang
     */
    public void shangjiadanDetails(Record head, Record body, String billID1,
                                   String billID2, String billID3, String billID4,
                                   Map<String, Integer> count) {
        Record shangjiadanDetail = new Record();
        BigDecimal zhengjianshu = body.getBigDecimal("zhengjianshu");
        BigDecimal lingsanshu = body.getBigDecimal("lingsanshu");
        BigDecimal shuliang = body.getBigDecimal("shuliang");
        BigDecimal choujianshuliang = body.getBigDecimal("choujianshuliang");
        BigDecimal baozhuangshuliang = body.getBigDecimal("baozhuangshuliang");
        String shangpinbianhao = body.getStr("shangpinbianhao"); // 商品编号
        String shangpinmingcheng = body.getStr("shangpinmingcheng"); // 商品名称
        String shangpinguige = body.getStr("shangpinguige"); // 商品规格
        int baozhuangdanwei = body.getInt("baozhuangdanwei"); // 包装单位
        String pizhunwenhao = body.getStr("pizhunwenhao"); // 批准文号
        String shengchanchangjia = body.getStr("shengchanchangjia"); // 生产厂家
        String pihao = body.getStr("pihao"); // 商品批号
        Date shengchanriqi = body.get("shengchanriqi"); // 生产日期
        Date youxiaoqizhi = body.get("youxiaoqizhi"); // 有效期至
        int yanshoupingding = body.getInt("yanshoupingding"); // 验收评定（0-待验，1-合格，2-不合格,3-拒收）
        int pingdingyuanyin = body.getInt("pingdingyuanyin"); // 评定原因
        String rongqibianhao = body.getStr("rongqibianhao"); // 容器编号
        String sjmxUUID = UUIDUtil.newUUID();

        shangjiadanDetail.set("zhengjianshu", zhengjianshu);
        shangjiadanDetail.set("shuliang", shuliang);
        shangjiadanDetail.set("lingsanshu", lingsanshu);
        shangjiadanDetail.set("billID", billID3);
        shangjiadanDetail.set("dingdanbianhao", head.getStr("dingdanbianhao"));
        shangjiadanDetail.set("shangpinbianhao", shangpinbianhao);
        shangjiadanDetail.set("shangpinmingcheng", shangpinmingcheng);
        shangjiadanDetail.set("shangpinguige", shangpinguige);
        shangjiadanDetail.set("baozhuangshuliang", baozhuangshuliang);
        shangjiadanDetail.set("baozhuangdanwei", baozhuangdanwei);
        shangjiadanDetail.set("pizhunwenhao", pizhunwenhao);
        shangjiadanDetail.set("shengchanchangjia", shengchanchangjia);
        shangjiadanDetail.set("pihao", pihao);
        shangjiadanDetail.set("shengchanriqi", shengchanriqi);
        shangjiadanDetail.set("youxiaoqizhi", youxiaoqizhi);
        shangjiadanDetail.set("rongqibianhao", rongqibianhao);
        shangjiadanDetail.set("yanshoupingding", yanshoupingding);
        shangjiadanDetail.set("pingdingyuanyin", pingdingyuanyin);
        // shangjiadanDetail.set("zhengjianshu", zhengjianshu);
        // shangjiadanDetail.set("lingsanshu", lingsanshu);
        shangjiadanDetail.set("choujianshuliang", choujianshuliang);

        shangjiadanDetail.set("billDtlID", sjmxUUID);
        shangjiadanDetail.set("orgId", body.getStr("orgId"));
        shangjiadanDetail.set("orgCode", body.getStr("orgCode"));
        shangjiadanDetail.set("rowID", UUIDUtil.newUUID());
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        shangjiadanDetail.set("createTime", currentTime);
        shangjiadanDetail.set("updateTime", currentTime);

        // 合格分到整件库和零件库
        if (yanshoupingding == 1) {
            // 如果有整件，就整散分离
            if (zhengjianshu.compareTo(BigDecimal.ZERO) > 0) {
                String newUUID2 = UUIDUtil.newUUID();
                shangjiadanDetail.set("billDtlID", newUUID2);
                shangjiadanDetail.set("zhengjianshu", zhengjianshu);
                shangjiadanDetail.set("lingsanshu", BigDecimal.ZERO);
                shangjiadanDetail.set("shuliang",
                        zhengjianshu.multiply(baozhuangshuliang));
                BillHuoWei billHuoWei = new BillHuoWei();
                List<Record> huoweiResult = billHuoWei.zidongZhengjianHuoWei(
                        head, body);
                if (shangjiadanDetail != null) {
                    Integer countShangjia = (Integer) count.get("shangjia") + 1;
                    count.put("shangjia", countShangjia);
                    if (null != huoweiResult && huoweiResult.size() > 0) {
                        for (Record record : huoweiResult) {
                            Record re = getHwBianhao(record.getStr("kuquId"));
                            String newUUID = UUIDUtil.newUUID();
                            shangjiadanDetail.set("billDtlID", newUUID);
                            shangjiadanDetail.set("kuqumingcheng",
                                    re.get("kuqumingcheng"));
                            String huoweibianhao = record.get("huoweibianhao");
                            shangjiadanDetail.set("jihuahuowei", huoweibianhao);
                            shangjiadanDetail.set("shijihuowei", huoweibianhao);
                            shangjiadanDetail.set("shuliang", record.getBigDecimal("shuliang"));
                            shangjiadanDetail.set("zhengjianshu", record.getBigDecimal("shuliang").divide(baozhuangshuliang));
                            Db.save("xyy_wms_bill_rukushangjiadan_details",
                                    shangjiadanDetail);
                            if (huoweibianhao != null && huoweibianhao != "") {
                                updateHuoweiKucun(newUUID, record, record.getBigDecimal("shuliang"),
                                        head, body);
                            }
                        }
                    } else {
                        //updateKuCun(sjmxUUID,head, body);
                        Db.save("xyy_wms_bill_rukushangjiadan_details",
                                shangjiadanDetail);
                    }
                    LOGGER.info("上架单明细生成成功，商品编号：" + "shangpinbianhao");
                }

            }
            if (lingsanshu.compareTo(BigDecimal.ZERO) > 0) {
                String newUUID3 = UUIDUtil.newUUID();
                shangjiadanDetail.set("billDtlID", newUUID3);
                shangjiadanDetail.set("lingsanshu", lingsanshu);
                shangjiadanDetail.set("zhengjianshu", BigDecimal.ZERO);
                shangjiadanDetail.set("shuliang", lingsanshu);
                if (shangjiadanDetail != null) {
                    Integer countShangjia = (Integer) count.get("shangjia") + 1;
                    count.put("shangjia", countShangjia);
                    BillHuoWei billHuoWei = new BillHuoWei();
                    List<Record> huoweiLSResult = billHuoWei
                            .zidongLingSanHuoWei(head, body);
                    if (null != huoweiLSResult && huoweiLSResult.size() > 0) {
                        for (Record record : huoweiLSResult) {
                            String newUUID = UUIDUtil.newUUID();
                            shangjiadanDetail.set("billDtlID", newUUID);
                            Record re = getHwBianhao(record.getStr("kuquId"));
                            shangjiadanDetail.set("kuqumingcheng",
                                    re.get("kuqumingcheng"));
                            String huoweibianhao = record.get("huoweibianhao");
                            shangjiadanDetail.set("jihuahuowei", huoweibianhao);
                            shangjiadanDetail.set("shijihuowei", huoweibianhao);
                            shangjiadanDetail.set("shuliang", record.getBigDecimal("shuliang"));
                            shangjiadanDetail.set("lingsanshu", record.getBigDecimal("shuliang"));
                            Db.save("xyy_wms_bill_rukushangjiadan_details",
                                    shangjiadanDetail);
                            // 更新商品批号货位库存的入库预占xyy_wms_bill_shangpinpihaohuoweikucun
                            // 质量状态(1-合格，2-不合格，3-待验，4-停售 )
                            // ？有问题
                            if (huoweibianhao != null && huoweibianhao != "") {
                                updateHuoweiKucun(newUUID, record, record.getBigDecimal("shuliang"), head, body);
                            }
                        }
                    } else {
                        //updateKuCun(sjmxUUID,head, body);
                        Db.save("xyy_wms_bill_rukushangjiadan_details",
                                shangjiadanDetail);
                    }
                    LOGGER.info("上架单明细生成成功，商品编号：" + "shangpinbianhao");
                }
            }
        }
        // 不合格分到不合格品库
        if (yanshoupingding == 2) {
            BillHuoWei billHuoWei = new BillHuoWei();
            Record huoweiResult = billHuoWei.daiyanHuoWei(head, body);
            if (huoweiResult != null) {
                shangjiadanDetail.set("kuqumingcheng",
                        huoweiResult.get("kuqumingcheng"));
                String huoweibianhao = huoweiResult.get("huoweibianhao");
                shangjiadanDetail.set("jihuahuowei", huoweibianhao);
                shangjiadanDetail.set("shijihuowei", huoweibianhao);
                String huowei = shangjiadanDetail.getStr("jihuahuowei");
                Db.save("xyy_wms_bill_rukushangjiadan_details", shangjiadanDetail);
                if (huoweibianhao != null && huoweibianhao != "" && huowei != null && huowei != "") {
                    updateHuoweiKucun(sjmxUUID, huoweiResult, shuliang, head, body);
                }
            } else {
                //updateKuCun(sjmxUUID,head, body);
                Db.save("xyy_wms_bill_rukushangjiadan_details", shangjiadanDetail);
            }
            Integer countShangjia = (Integer) count.get("shangjia") + 1;
            count.put("shangjia", countShangjia);
            LOGGER.info("不合格品上架明细单生成成功，商品编号：" + "shangpinbianhao");
        }

        // 拒收分到待验库
        if (yanshoupingding == 3) {
            BillHuoWei billHuoWei = new BillHuoWei();
            Record huoweiResult = billHuoWei.daiyanHuoWei(head, body);

            if (huoweiResult != null) {// 分到货位
                shangjiadanDetail.set("kuqumingcheng",
                        huoweiResult.get("kuqumingcheng"));
                String huoweibianhao = huoweiResult.get("huoweibianhao");
                shangjiadanDetail.set("jihuahuowei", huoweibianhao);
                shangjiadanDetail.set("shijihuowei", huoweibianhao);
                Db.save("xyy_wms_bill_rukushangjiadan_details", shangjiadanDetail);
                if (huoweibianhao != null && huoweibianhao != "") {
                    updateHuoweiKucun(sjmxUUID, huoweiResult, shuliang, head, body);
                }
            } else {
                //updateKuCun(sjmxUUID,head, body);
                Db.save("xyy_wms_bill_rukushangjiadan_details", shangjiadanDetail);
            }
            Integer countShangjia = (Integer) count.get("shangjia") + 1;
            count.put("shangjia", countShangjia);
            LOGGER.info("拒收商品上架明细单生成成功，商品编号：" + "shangpinbianhao");
        }

        // 待处理分到待验库，同时生成质量复检单
        if (yanshoupingding == 0) {
            BillHuoWei billHuoWei = new BillHuoWei();
            Record huoweiResult = billHuoWei.daiyanHuoWei(head, body);
            if (huoweiResult != null) {// 分到货位
                shangjiadanDetail.set("kuqumingcheng",
                        huoweiResult.get("kuqumingcheng"));
                String huoweibianhao = huoweiResult.get("huoweibianhao");
                shangjiadanDetail.set("jihuahuowei", huoweibianhao);
                shangjiadanDetail.set("shijihuowei", huoweibianhao);
                Db.save("xyy_wms_bill_rukushangjiadan_details", shangjiadanDetail);
                if (huoweibianhao != null && huoweibianhao != "") {
                    updateHuoweiKucun(sjmxUUID, huoweiResult, shuliang, head, body);
                }

                // 更新质量复检明细单
                zhiliangfujiandetail(head, body, billID4, sjmxUUID, huoweibianhao, count);
            } else {
                //updateKuCun(sjmxUUID,head, body);
                Db.save("xyy_wms_bill_rukushangjiadan_details", shangjiadanDetail);

                // 更新质量复检明细单
                zhiliangfujiandetail(head, body, billID4, sjmxUUID, "", count);
            }
            Integer countShangjia = (Integer) count.get("shangjia") + 1;
            count.put("shangjia", countShangjia);
            LOGGER.info("待验品上架明细单生成成功，商品编号：" + "shangpinbianhao");
        }
    }

    /**
     * 查询货位编号
     *
     * @param kuquId
     * @return
     */
    public Record getHwBianhao(String kuquId) {
        String sql = "select b.kuqumingcheng kuqumingcheng from xyy_wms_dic_huoweiziliaoweihu a INNER JOIN xyy_wms_dic_kuqujibenxinxi b on a.kuquId = b.ID AND a.kuquId = ? limit 1";
        Record r = Db.findFirst(sql, kuquId);
        return r;
    }

    /**
     * 根据商品编号和库区类别确定商品可以存放在那些货位上
     *
     * @param shangpinbianhao
     * @param kuquleibie
     * @return
     */
    public List<Record> getHouwei(String shangpinbianhao, int kuquleibie) {
        List<Record> list1 = new ArrayList<Record>();
        List<Record> list = new ArrayList<Record>();
        String sql = "select a.lluojiquyu from xyy_wms_dic_spccxdwh_list a inner join xyy_wms_dic_spccxdwh b on a.id = b.id"
                + " and  b.shangpinbianhao = ? and b.kuquleibie =?";
        list1 = Db.find(sql, shangpinbianhao, kuquleibie);
        if (list1 != null && list1.size() > 0) {
            for (Record r : list1) {
                String lluojiquyu = r.get("lluojiquyu");
                String sql2 = "select huoweibianhaos from xyy_wms_dic_ljqhwgxwh where ljqbh=? and shifouqiyong =1";
                list = Db.find(sql2, lluojiquyu);
            }
        }
        return list;
    }

    /**
     * 得到每个商品的重量和体积
     *
     * @param shangpinbianhao
     * @param shangpinmingcheng
     * @return
     */
    public static Record getDimensionsWeight(String shangpinbianhao,
                                             String shangpinmingcheng) {
        Record r = new Record();
        String sql = "select t.dbztj/t.dbzsl tj,t.dbzzl/t.dbzsl kg, t.goodsid goodsid from xyy_wms_dic_shangpinziliao t where t.shangpinbianhao=? and  t.shangpinmingcheng=?";
        r = (Record) Db.find(sql, shangpinbianhao, shangpinmingcheng);
        return r;
    }

    /**
     * 回写入库收货明细单验收数量
     *
     * @param BillID
     * @param rukushuliang
     * @return
     */
    public void updateRukushuliang(String dingdanbianhao,
                                   String shangpinbianhao, BigDecimal shuliang, String rukuID,
                                   String rukudanID) {
        String sql3 = "update xyy_wms_bill_rukushouhuo_details set yanshoushuliang = yanshoushuliang +"
                + shuliang
                + " where billID = ? and shangpinbianhao = ? and BillDtlID = ?";
        // 回写收货明细单验收数量
        Db.update(sql3, rukudanID, shangpinbianhao, rukuID);
        LOGGER.info("入库收货单验收数量回写成功，商品编号：" + shangpinbianhao);
        // 当【入库收货单】所有明细的“收货数量”=“已完成数量”时，回写【入库收货单】汇总的状态为“已完成”
    }

    /**
     * 回写入库收货汇总单验收状态
     *
     * @param BillID
     * @param rukushuliang
     * @return
     */
    public void updateRukuZhuangtai(String dingdanbianhao,
                                    int yanshouzhuangtai, String rukudanID) {
        String sql = "update xyy_wms_bill_rukushouhuo set yanshouzhuangtai ="
                + yanshouzhuangtai + " where dingdanbianhao = ? and billid = ?";
        // 回写收货明细单验收数量
        Db.update(sql, dingdanbianhao, rukudanID);
        LOGGER.info("入库收货单验收状态回写成功，订单编号：" + dingdanbianhao);
    }

    /**
     * 商品批号是否存在
     *
     * @param goodsId
     * @param shangpinpihao
     * @return
     */
    public boolean getShangPinPHById(String shangpinpihao,
                                     String shangpinbianhao) {
        boolean flag = false;
        String sql = "select * from xyy_wms_dic_shangpinpihao where pihao = ? and goodsId in (select goodsId from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?)";
        List<Record> list = Db.find(sql, shangpinpihao, shangpinbianhao);
        if (list.size() > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 更新商品批号货位库存
     *
     * @param danjubianhao
     * @param shangpinbianhao
     * @param rukushuliang
     */
    public static void updateHuoweiKucun(String yewubianhao, Record record,
                                         BigDecimal yanshoushuliang, Record head, Record body) {
        String huoweibianhao = record.getStr("huoweibianhao");
        if (huoweibianhao != "" && huoweibianhao != null) {
            String shangpinbianhao = body.getStr("shangpinbianhao");
            String shangpinId = Db
                    .queryStr(
                            "select goodsId from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?",
                            shangpinbianhao);
            String pihao = body.getStr("pihao");
            String pihaoId = Db
                    .queryStr(
                            "SELECT pihaoId FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId=?",
                            pihao, shangpinId);
            Record hwidAndCkid = Db.findFirst(
                    "SELECT ID,cangkuID FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=?",
                    huoweibianhao);
            String cangkuID = hwidAndCkid.getStr("cangkuID");
            String huoweiId = hwidAndCkid.getStr("ID");
            int yewuleixing = 1;
            String orgId = head.getStr("orgId");
            String danjubianhao = head.getStr("dingdanbianhao");
            KucuncalcService kucuncalcService = new KucuncalcService();
            KuncunParameter kuncunParameter = new KuncunParameter(orgId,
                    cangkuID, shangpinId, pihaoId, huoweiId, yewubianhao,
                    danjubianhao, yewuleixing, yanshoushuliang);
            kucuncalcService.insertKCRecord(kuncunParameter);
            LOGGER.info("商品批号库存更新成功，商品编号：" + shangpinbianhao + ",批号:" + pihao
                    + ",货位编号：" + huoweiId);
        }
    }

    /**
     * 查询上架规则
     */
    public List<Record> getShangjiaRule(int dingdanleixing) {
        String sql = "select * from xyy_wms_bill_sjgzwh where dingdanleixing = ?";
        List<Record> list = Db.find(sql, dingdanleixing);
        return list;
    }

    /**
     * 查询商品存储规则
     *
     * @param shangpinbianhao
     * @return
     */
    public List<Record> getSpccxdRule(String shangpinbianhao) {
        String sql = "select * from xyy_wms_dic_spccxdwh where shangpinbianhao = ?";
        List<Record> list = Db.find(sql, shangpinbianhao);
        return list;
    }

    /**
     * 查询所有可用货位
     */
    public List<Record> getAllowance(String shangpinbianhao,
                                     String shangpinmingcheng, String pihaoId, String huoweiId) {
        List<Record> r = new ArrayList<Record>();
        List<Record> list = new ArrayList<Record>();
        List<Record> huoweiList = new ArrayList<Record>();
        Record huowei = new Record();
        String sql1 = "select a.goodsid goodsid,t.kucunshangxian kucunshangxian from xyy_wms_dic_shangpinziliao a,"
                + "xyy_wms_dic_spccxdwh t where a.shangpinbianhao = t.shangpinbianhao and shangpinbianhao = ? and shangpinmingcheng =?";
        r = Db.find(sql1, shangpinbianhao, shangpinmingcheng);
        if (r != null && r.size() > 0) {
            String goodsid = r.get(0).get("goodsid");
            BigDecimal kucunshangxian = r.get(0)
                    .getBigDecimal("kucunshangxian");
            String sql = "select * from xyy_wms_bill_shangpinpihaohuoweikucun t"
                    + " where  t.shangpinId= ? and t.pihaoId = ? and huoweiId =?";
            list = Db.find(sql, goodsid, pihaoId, huoweiId);
            if (list != null && list.size() > 0) {
                for (Record record : list) {
                    BigDecimal kucunshuliang = record
                            .getBigDecimal("kucunshuliang");
                    BigDecimal chukuyukou = record.getBigDecimal("chukuyukou");
                    BigDecimal buruyuzhan = record.getBigDecimal("buruyuzhan");
                    BigDecimal yiruyuzhan = record.getBigDecimal("yiruyuzhan");
                    // 把所有的预占加上
                    BigDecimal huo = kucunshuliang.add(chukuyukou)
                            .add(buruyuzhan).add(yiruyuzhan);
                    BigDecimal tj = kucunshangxian.subtract(huo);// 货位剩余还空间
                    if (!tj.equals(0)) {
                        huowei.set("tj", tj);
                        huowei.set("huo", huo);
                        huowei.set("huoweiId", huoweiId);
                        huowei.set("goodsid", goodsid);
                        huowei.set("pihaoId", pihaoId);
                        huoweiList.add(huowei);
                    }
                }
            }

        }
        return huoweiList;
    }

    /**
     * 根据货位面积大小对map进行排序，私用。后续改成公用工具包
     *
     * @param map
     * @return
     */
    public Map<String, Record> sortMap(Map<String, Record> map) {
        // 获取entrySet
        Set<Map.Entry<String, Record>> mapEntries = map.entrySet();

        for (Entry<String, Record> entry : mapEntries) {
            System.out.println("key:" + entry.getKey() + "   value:"
                    + entry.getValue());
        }

        // 使用链表来对集合进行排序，使用LinkedList，利于插入元素
        List<Map.Entry<String, Record>> result = new LinkedList<>(mapEntries);
        // 自定义比较器来比较链表中的元素
        java.util.Collections.sort(result,
                new Comparator<Entry<String, Record>>() {
                    // 基于entry的值（Entry.getValue()），来排序链表
                    @Override
                    public int compare(Entry<String, Record> o1,
                                       Entry<String, Record> o2) {

                        return o2.getValue().getBigDecimal("tiji")
                                .compareTo(o1.getValue().getBigDecimal("tiji"));
                    }

                });

        // 将排好序的存入到LinkedHashMap(可保持顺序)中，需要存储键和值信息对到新的映射中。
        Map<String, Record> linkMap = new LinkedHashMap<>();
        for (Entry<String, Record> newEntry : result) {
            linkMap.put(newEntry.getKey(), newEntry.getValue());
        }
        // 根据entrySet()方法遍历linkMap
        for (Map.Entry<String, Record> mapEntry : linkMap.entrySet()) {
            System.out.println("key:" + mapEntry.getKey() + "  value:"
                    + mapEntry.getValue());
        }
        return linkMap;
    }

}
