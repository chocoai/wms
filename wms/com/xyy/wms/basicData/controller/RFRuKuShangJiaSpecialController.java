package com.xyy.wms.basicData.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.app.model.base.PageData;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.erp.platform.system.task.SQLTarget;
import com.xyy.util.StringUtil;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.basicData.controller.RFWaiFuSpecialController.SPEConstant;
import com.xyy.wms.handler.biz.BillRuKuShangJiaPostHandler;

/**
 * rf入库上架提供接口
 *
 * @author dell
 */
public class RFRuKuShangJiaSpecialController extends Controller {

    /**
     * 公共路由
     */
    // 查询所有可以上架的单子
    private static List<Record> shangjiadanList;



    public void route() {
        String code = this.getPara();
        if (StringUtil.isEmpty(code)) {
            this.jumpTo404();
            return;
        }
        switch (code) {
            case SPEConstant.SPE_CODE_RUKUSHANGJIAINFO:
                this.rukushangjiainfo();
                break;

           case SPEConstant.SPE_CODE_RUKUSHANGJIAINFO2:
                this.rukushangjia();
                break;

            case SPEConstant.SPE_CODE_MYINFO:
                this.myInfo();
                break;

            case SPEConstant.SPE_CODE_SAVESJ:
                this.saveSj();
                break;
            case SPEConstant.SPE_CODE_ISEXIST:
                this.isExist();
                break;

            default:
                this.render("/404.html");
                break;
        }
    }

    // 我的界面信息
    private void myInfo() {
        BillContext context = this.buildBillContext();
        String id = context.getString("id");
        String sql = "SELECT * FROM tb_sys_user WHERE id = ?";
        Record record = Db.findFirst(sql, id);
        this.setAttr("status", 1);
        this.setAttr("result", record == null ? "" : record);
        this.renderJson();

    }




    private void rukushangjiainfo() {
        // 订单编号
//        String dingdanbianhao = this.getPara("dingdanbianhao");
        //商品条码先放下
        String shangpingtiaoma = this.getPara("shangpintiaoma");
//        String huoweibianhao = this.getPara("huoweibianhao");

        // 容器编号
        String rongqibianhao = this.getPara("rongqibianhao");
        // 主键id
        String BillDtlID = this.getPara("BillDtlID");
        
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT (b.shuliang-b.wmsshuliang) syshuliang,b.*,c.zbzsl FROM xyy_wms_bill_rukushangjiadan a INNER JOIN xyy_wms_bill_rukushangjiadan_details b ON a.BillID = b.BillID INNER JOIN xyy_wms_dic_shangpinziliao c ON c.shangpinbianhao = b.shangpinbianhao AND b.zhuangtai != 1");
//        if (dingdanbianhao != null && dingdanbianhao.length() != 0) {
//            sb.append(" AND b.dingdanbianhao = '" + dingdanbianhao + "'");
//        }
        if (rongqibianhao != null && rongqibianhao.length() != 0) {
            sb.append(" AND b.rongqibianhao = '" + rongqibianhao + "'");
        }
        if (shangpingtiaoma != null && shangpingtiaoma.length() != 0) {
            sb.append(" AND c.dbztm = '" + shangpingtiaoma + "'");
            sb.append(" UNION SELECT (b.shuliang-b.wmsshuliang) syshuliang,b.*,c.zbzsl FROM xyy_wms_bill_rukushangjiadan a INNER JOIN xyy_wms_bill_rukushangjiadan_details b ON a.BillID = b.BillID INNER JOIN xyy_wms_dic_shangpinziliao c ON c.shangpinbianhao = b.shangpinbianhao AND b.zhuangtai != 1 and c.zbztm = '" + shangpingtiaoma + "'");
            sb.append(" UNION SELECT (b.shuliang-b.wmsshuliang) syshuliang,b.*,c.zbzsl FROM xyy_wms_bill_rukushangjiadan a INNER JOIN xyy_wms_bill_rukushangjiadan_details b ON a.BillID = b.BillID INNER JOIN xyy_wms_dic_shangpinziliao c ON c.shangpinbianhao = b.shangpinbianhao AND b.zhuangtai != 1 and c.xbztm = '" + shangpingtiaoma + "'");

        }
        if (BillDtlID != null && BillDtlID.length() != 0) {
            sb.append(" AND b.BillDtlID = '" + BillDtlID + "'");
        }
        
//        if (huoweibianhao != null && huoweibianhao.length() != 0) {
//            sb.append(" AND b.jihuahuowei = '" + huoweibianhao + "'");
//        }

        
        List<Record> recordList = Db.find(sb.toString());
        this.setAttr("status", 1);
        this.setAttr("result", recordList == null ? "" : recordList);
        this.renderJson();
    }

    private void rukushangjia() {
        String BillDtlID = this.getPara("BillDtlID");
        String sql = "SELECT (b.shuliang-b.wmsshuliang) syshuliang,b.*,c.zbzsl FROM xyy_wms_bill_rukushangjiadan a INNER JOIN xyy_wms_bill_rukushangjiadan_details b ON a.BillID = b.BillID INNER JOIN xyy_wms_dic_shangpinziliao c ON c.shangpinbianhao = b.shangpinbianhao and b.BillDtlID=? AND b.zhuangtai != 1";
        Record record = Db.findFirst(sql,BillDtlID);
        this.setAttr("status", 1);
        this.setAttr("result", record == null ? "" : record);
        this.renderJson();
    }


    // 保存修改后的数据
    private void saveSj() {

        String requestMsg = this.getPara("spObj");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        String shangjiayuan = this.getPara("shangjiayuan");
        BigDecimal shuliang = new BigDecimal(msg.getString("syshuliang"));
        BigDecimal zhengjianshu = new BigDecimal(msg.getString("zhengjianshu"));
        BigDecimal lingsanshu = new BigDecimal(msg.getString("lingsanshu"));
        String orgId = msg.getString("orgId");
        String orgCode = msg.getString("orgCode");
        String danjubianhao = msg.getString("dingdanbianhao");
        String jhhuowei = msg.getString("jihuahuowei");
        // 主键id
        String BillDtlId = msg.getString("BillDtlID");
        // 上架单id
        String sjID = msg.getString("BillID");


        // 商品编号
        String shangpinbianhao = msg.getString("shangpinbianhao");
        // 商品批号
        String pihao = msg.getString("pihao");

        String huowei = msg.getString("shijihuowei");

        int zhiliangzhuangtai = Integer.parseInt(msg.getString("yanshoupingding"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date shengchanriqi = null;
        Date youxiaoqizhi = null;
        try {
            shengchanriqi = sdf.parse(msg.getString("shengchanriqi"));
            youxiaoqizhi = sdf.parse(msg.getString("youxiaoqizhi"));
        } catch (ParseException e) {

            e.printStackTrace();
        }

        String bid = UUIDUtil.newUUID();

        String bdid = UUIDUtil.newUUID();

        Record record = new Record();

        // 生产上架确认单汇总
        String shangjaidanSql = "select ? as BillID,status,orgId,orgCode,SYSDATE() as createTime,SYSDATE() as updateTime,rowID,SYSDATE() as dingdanriqi,dingdanbianhao,dingdanleixing,huozhubianhao,huozhumingcheng,danweibianhao,danweimingcheng,yanshouyuan,caigouyuan,shouhuoyuan,shangjidanhao,kufang,shangjiayuan,? as shangjiaID from xyy_wms_bill_rukushangjiadan where BillID = ?";
        Record shangjiadanObj = Db.findFirst(shangjaidanSql, bid, sjID, sjID);
        // 生产上架确认单明细
        shangjiadanObj.set("shangjiayuan", shangjiayuan);
        EDB.save("xyy_wms_bill_rukushangjia", shangjiadanObj);
        record.set("BillDtlID", bdid);
        record.set("BillID", bid);
        record.set("createTime", new Date());
        record.set("updateTime", new Date());
        record.set("orgId", msg.getString("orgId"));
        record.set("orgCode", msg.getString("orgCode"));
        record.set("rowID", bdid);
        record.set("dingdanbianhao", msg.getString("dingdanbianhao"));
        record.set("shangpinbianhao", msg.getString("shangpinbianhao"));
        record.set("shangpinmingcheng", msg.getString("shangpinmingcheng"));
        record.set("shangpinguige", msg.getString("shangpinguige"));
        record.set("baozhuangshuliang", new BigDecimal(msg.getString("baozhuangshuliang")));
        record.set("pizhunwenhao", msg.getString("pizhunwenhao"));
        record.set("shengchanchangjia", msg.getString("shengchanchangjia"));
        record.set("zhengjianshu", zhengjianshu);
        record.set("lingsanshu", lingsanshu);
        record.set("shuliang", shuliang);
        record.set("pihao", msg.getString("pihao"));
        record.set("shengchanriqi", shengchanriqi);
        record.set("youxiaoqizhi", youxiaoqizhi);
        record.set("rongqibianhao", msg.getString("rongqibianhao"));
        record.set("kuqumingcheng", msg.getString("kuqumingcheng"));
        record.set("jihuahuowei", msg.getString("jihuahuowei"));
        record.set("shijihuowei", msg.getString("shijihuowei"));
        record.set("baozhuangdanwei", Integer.parseInt(msg.getString("baozhuangdanwei")));
        record.set("yanshoupingding", zhiliangzhuangtai);
        record.set("shangjiadetailID", BillDtlId);
        EDB.save("xyy_wms_bill_rukushangjia_details", record);

        // 回写状态及数量
        if (BillDtlId != null && BillDtlId.length() != 0) {
            BillRuKuShangJiaPostHandler.getShangJiaById(BillDtlId, shuliang);
        	BillRuKuShangJiaPostHandler.updateKuCun(shangpinbianhao, pihao, shuliang,orgId,orgCode,BillDtlId,danjubianhao);
        	BillRuKuShangJiaPostHandler.updateZongKuCun(shangpinbianhao,shuliang,orgId,orgCode,BillDtlId,danjubianhao);
        	BillRuKuShangJiaPostHandler.updateHouWeiZongKuCun(shangpinbianhao, huowei,jhhuowei, pihao, shuliang, zhiliangzhuangtai, shengchanriqi, youxiaoqizhi,orgId,orgCode,BillDtlId,danjubianhao);

            String sql = "select * from xyy_wms_bill_rukushangjiadan_details where BillDtlID = ?";
            Record r = Db.findFirst(sql, BillDtlId);
            String BillID = r.getStr("BillID");
            List<Map<String, BigDecimal>> list = new ArrayList<>();
            Map<String, BigDecimal> map = new HashMap<>();
            map.put(BillDtlId, shuliang);
            list.add(map);
            BillRuKuShangJiaPostHandler.updaSJAllZT(BillID);
            
            List<Record> body = new ArrayList<>();
            body.add(record);
            // 减库存
             // 生成账页
            BillRuKuShangJiaPostHandler.insertZhangye(shangjiadanObj, body);

        }
        String sql = "SELECT (b.shuliang-b.wmsshuliang) syshuliang,b.*,c.zbzsl FROM xyy_wms_bill_rukushangjiadan a INNER JOIN xyy_wms_bill_rukushangjiadan_details b ON a.BillID = b.BillID INNER JOIN xyy_wms_dic_shangpinziliao c ON c.shangpinbianhao = b.shangpinbianhao  AND b.zhuangtai != 1";
        shangjiadanList=new ArrayList<>();
        shangjiadanList = Db.find(sql);
        if (shangjiadanList.size()==0) {
            this.setAttr("status", 1);
            this.setAttr("result", "complete");
            this.renderJson();
            return;
        }
        for (int i = 0; i < shangjiadanList.size(); i++) {
            Record jhsp = shangjiadanList.get(i);
            if (BillDtlId.equals(jhsp.getStr("BillDtlID"))) {
                // 循环当前 体 后面的 体
                    for (int j = i + 1; j < shangjiadanList.size(); j++) {
                        if (shangjiadanList.get(j).getInt("zhuangtai") == 0)
                        this.setAttr("status", 1);
                        this.setAttr("result", shangjiadanList.get(j));
                        this.renderJson();
                        return;
                    }
            }
                // 当前数据后面的数据循环完了后，没有返回，从头开始遍历
                for (int j = 0; j < shangjiadanList.size(); j++) {
                    // 全部上架完毕
                    if (BillDtlId.equals(shangjiadanList.get(j).getStr("BillDtlID"))) {
                        this.setAttr("status", 1);
                        this.setAttr("result", "complete");
                        this.renderJson();
                        return;
                    }
                    // 从头开始定位未上架的数据
                    if (shangjiadanList.get(j).getInt("zhuangtai") == 0) {

                        this.setAttr("status", 1);
                        this.setAttr("result", shangjiadanList.get(j));
                        this.renderJson();
                        return;
                    }
                }
            }
        }


    // 查询货位是否存在
    private void isExist() {
        // 实际货位
        String sql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao = ?";
        String shijihuowei = this.getPara("shijihuowei");
        ;
        Record re = Db.findFirst(sql, shijihuowei);
        this.setAttr("status", 1);
        this.setAttr("result", re == null ? "" : re);
        this.renderJson();
    }

    /**
     * 接收rf传过来的参数
     *
     * @return
     */
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
        public static final String SPE_CODE_RUKUSHANGJIAINFO = "rukushangjiainfo";
        public static final String SPE_CODE_MYINFO = "myInfo";
        public static final String SPE_CODE_SAVESJ = "saveSj";
        public static final String SPE_CODE_ISEXIST = "isExist";
        public static final String SPE_CODE_RUKUSHANGJIAINFO2 = "rukushangjia";

    }
}
