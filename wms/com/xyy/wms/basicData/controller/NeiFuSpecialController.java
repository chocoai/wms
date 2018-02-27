package com.xyy.wms.basicData.controller;


import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.util.SequenceBuilder;
import com.xyy.erp.platform.app.model.base.PingXiangData;
import com.xyy.erp.platform.app.model.base.RongQiData;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.util.StringUtil;
import com.xyy.wms.basicData.util.PrintDataContext;

import java.util.*;


/**
 * 为内复核拼箱号需求的控制器
 *
 * @author wbc
 */
public class NeiFuSpecialController extends Controller {

    /**
     * 公共路由
     */
    public void route() {
        String code = this.getPara();
        if (StringUtil.isEmpty(code)) {
            this.jumpTo404();
            return;
        }
        switch (code) {
            case SPEConstant.SPE_CODE_NEIFUHEPINXIANGPRE:
                this.neifuhepinxiangPre();
                break;
            case SPEConstant.SPE_CODE_NEIFUHEPINXIANGNext:
                this.neifuhepinxiangNext();
                break;
            case SPEConstant.SPE_CODE_NEIFUHEBODY:
                this.neifuheibodynfo();
                break;
            case SPEConstant.SPE_CODE_NEIFUHEPINXIANGPRINT:
                this.neifuhepinxiangprint();
                break;
            case SPEConstant.SPE_CODE_NEIFUHERONGQIHAO:
                this.neifuherongqihao();
                break;
            default:
                this.render("/404.html");
                break;
        }
    }

    /*
     *  生成的当前拼箱号内复核的拼箱号
     */
    private void neifuhepinxiangPre() {
        BillContext context = this.buildBillContext();
        String dingdanbianhao = context.getString("dingdanbianhao");
        String pxnum = SequenceBuilder.preSequence(dingdanbianhao + TimeUtil.format(new Date
                (), "yyyyMMdd"), "", 10);
        //截取后三位
        int length = pxnum.length();
        String pingxianghao = "P" + pxnum.substring(length - 3, length);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("num", pingxianghao);
        this.setAttr("status", 1);
        this.setAttr("result", map == null ? "" : map);
        this.renderJson();
    }

    /*
     * 生成的下次拼箱号内复核的拼箱号
     */
    private void neifuhepinxiangNext() {
        BillContext context = this.buildBillContext();
        String dingdanbianhao = context.getString("dingdanbianhao");
        String pxnum = SequenceBuilder.nextSequence(dingdanbianhao + TimeUtil.format(new Date
                (), "yyyyMMdd"), "", 10);
        //截取后三位
        int length = pxnum.length();
        String pingxianghao = "P" + pxnum.substring(length - 3, length);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("num", pingxianghao);
        this.setAttr("status", 1);
        this.setAttr("result", map == null ? "" : map);
        this.renderJson();
    }

    /*
     * 生成内复核编号的数据
     */
    private void neifuheibodynfo() {
        BillContext context = this.buildBillContext();

        String shangpinbianhao = context.getString("shangpinbianhao");
        String dingdanbianhao = context.getString("dingdanbianhao");

      /*
       //通过商品条码,来查询商品资料表
        String shangpingtiaoma = context.getString("shangpingtiaoma");
        StringBuffer sb = new StringBuffer();
        String shangpinbianhao = "";
        sb.append("SELECT DISTINCT * FROM xyy_wms_dic_shangpinziliao WHERE xbztm ='"+shangpingtiaoma+"'  or dbztm='"+shangpingtiaoma+"' or zbztm='"+shangpingtiaoma+"'");
        Record record = Db.findFirst(sb.toString());
        if (record != null) {
            shangpinbianhao = record.get("shangpinbianhao");
        } else {
            context.addError(dingdanbianhao, "该商品条码不存在");
            return;
        }*/

       /* sb.append("SELECT DISTINCT * FROM xyy_wms_dic_shangpinziliao WHERE dbztm =?");
        Record record = Db.findFirst(sb.toString(), shangpingtiaoma);
        if (record != null) {
            shangpinbianhao = record.get("shangpinbianhao");
        } else {
            sb.setLength(0);
            sb.append("SELECT DISTINCT * FROM xyy_wms_dic_shangpinziliao WHERE zbztm =?");
            Record record1 = Db.findFirst(sb.toString(), shangpingtiaoma);
            if (record1 != null) {
                shangpinbianhao = record1.get("shangpinbianhao");
            } else {
                sb.setLength(0);
                sb.append("SELECT DISTINCT * FROM xyy_wms_dic_shangpinziliao WHERE xbztm =?");
                Record record2 = Db.findFirst(sb.toString(), shangpingtiaoma);
                if (record2 != null) {
                    shangpinbianhao = record2.get("shangpinbianhao");
                } else {
                    context.addError(dingdanbianhao, "该商品条码不存在");
                    return;
                }
            }

        }*/


        String sql1 = "SELECT DISTINCT * FROM xyy_wms_bill_dabaorenwu_details WHERE taskType='10' AND dingdanbianhao =? AND                           shangpinbianhao=? ";
        Record record1 = Db.findFirst(sql1, dingdanbianhao, shangpinbianhao);

        this.setAttr("status", 1);
        this.setAttr("result", record1 == null ? "" : record1);
        this.renderJson();
    }


    /*
   * 生成拼箱打印
   */
    private void neifuhepinxiangprint() {
        BillContext context = this.buildBillContext();
        String dingdanbianhao = context.getString("dingdanbianhao");
        String kehubeizhu = context.getString("kehubeizhu");
        String fuhetai = context.getString("fuhetai");
        String caozuoren = context.getString("caozuoren");
        String zancunqu = context.getString("zancunqu");
        String kehumingcheng = context.getString("kehumingcheng");
        String pinxianghao = context.getString("pinxianghao");
        String createTime = context.getString("createTime");
        String xianlumingcheng = context.getString("xianlumingcheng");

        PingXiangData pingXiangData = new PingXiangData();
        pingXiangData.setDingdanbianhao(dingdanbianhao);
        pingXiangData.setKehubeizhu(kehubeizhu);
        pingXiangData.setCaozuoren(caozuoren);
        pingXiangData.setFuhetai(fuhetai);
        pingXiangData.setZancunqu(zancunqu);
        pingXiangData.setKehumingcheng(kehumingcheng);
        pingXiangData.setPinxianghao(pinxianghao);
        pingXiangData.setCreateTime(createTime);
        pingXiangData.setXianlumingcheng(xianlumingcheng);


        PrintDataContext.Band head = new PrintDataContext.Band();
        head.setDataSourceType(PrintDataContext.DataSourceType.JSON);
        String toJSON = JSON.toJSONString(pingXiangData);
        head.setJsonData(toJSON);

        PrintDataContext.Band body = new PrintDataContext.Band();

        this.setAttr("result", JSON.toJSONString(new PrintDataContext(head, body, true, "", "chukuneifuhe", "")));
        this.setAttr("status", 1);
        this.renderJson();

    }

    /*
     * 通过容器号获取相应的数据
     */
    private void neifuherongqihao() {
        BillContext context = this.buildBillContext();
        RongQiData rongQiData = new RongQiData();
        String rongqihao = context.getString("rongqihao");
        String sql = "SELECT * FROM `xyy_wms_bill_dabaorenwu` where rongqihao=? and status=36";
        Record head = Db.findFirst(sql, rongqihao);
            if (head==null) {
                rongQiData.setRecord(null);
                rongQiData.setRecordList(null);
                rongQiData.setMessage("请容器号检查是否正确或容器已进行过內复核");
            }else {
                rongQiData.setRecord(head);
                String BillID = head.get("BillID");
                String sql1 = "SELECT * FROM `xyy_wms_bill_dabaorenwu_details` where BillID=?";
                List<Record> bodys = Db.find(sql1, BillID);
                List<Record> neifuhrBody = new ArrayList<>();

                for (Record body : bodys) {
                    Record record = new Record();
                    record.set("dingdanbianhao", body.get("dingdanbianhao"));
                    record.set("jihuashuliang", body.get("shuliang"));
                    record.set("shangpingpihao", body.get("shangpingpihao"));
                    record.set("shangpinbianhao", body.get("shangpinbianhao"));
                    record.set("shangpinmingcheng", body.get("shangpinmingcheng"));
                    record.set("shengchanchangjia", body.get("shengchanchangjia"));
                    record.set("guige", body.get("guige"));
                    record.set("shuliang", body.get("shuliang"));
                    record.set("danwei", body.get("danwei"));
                    record.set("pihao", body.get("pihao"));
                    record.set("shengchanriqi", body.get("shengchanriqi"));
                    record.set("youxiaoqizhi", body.get("youxiaoqizhi"));
                    record.set("pizhunwenhao", body.get("pizhunwenhao"));
                    record.set("goodsid", body.get("goodsid"));
                    record.set("xiajiahuoweibianhao", body.get("xiajiahuoweibianhao"));
                    record.set("shangpingpihaosn", body.get("shangpingpihaosn"));
                    record.set("huoweiID", body.get("xiajiahuowei"));

                    record.set("chacuoyuanyin", 1);
                    record.set("yifuheshuliang", 0);
                    record.set("chonghongshuliang", 0);

            neifuhrBody.add(record);
        }

        rongQiData.setRecordList(neifuhrBody);
    }


        this.setAttr("status", 1);
        this.setAttr("result", rongQiData == null ? "" : rongQiData);
        this.renderJson();
}

    private BillContext buildBillContext() {
        BillContext context = new BillContext();
        // 遍历参数
        Enumeration<String> params = this.getParaNames();
        while (params.hasMoreElements()) {
            // 获取request中的请求参数
            String name = params.nextElement();
            String value = this.getPara(name);
            context.set(name, value);
        }
        return context;
    }

    private void jumpTo404() {
        this.render("/404.html");
    }

    public class SPEConstant {

        public static final String SPE_CODE_NEIFUHEPINXIANGPRE = "neifuhepinxiangPre";
        public static final String SPE_CODE_NEIFUHEPINXIANGNext = "neifuhepinxiangNext";
        public static final String SPE_CODE_NEIFUHEBODY = "neifuhebody";
        public static final String SPE_CODE_NEIFUHEPINXIANGPRINT = "neifuhepinxiangprint";
        public static final String SPE_CODE_NEIFUHERONGQIHAO = "neifuherongqihao";
    }

}
