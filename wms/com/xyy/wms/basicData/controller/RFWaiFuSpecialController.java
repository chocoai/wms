package com.xyy.wms.basicData.controller;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.util.SequenceBuilder;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;


/**
 * 外复核的RF,外复核的汇总和明细
 * 为PAD提供的json
 * @author wbc
 */
public class RFWaiFuSpecialController extends Controller {

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
            case SPEConstant.SPE_CODE_WAIFUHEHEAD:
                this.waifuheheadinfo();
                break;
            case SPEConstant.SPE_CODE_WAIFUHEBODY:
                this.waifuhebodyinfo();
                break;
            case SPEConstant.SPE_CODE_saveWFH:
                this.saveWFH();
                break;
            default:
                this.render("/404.html");
                break;
        }
    }

    /*
     * 生成外复核头的json串
     */
    private void waifuheheadinfo() {
        String sql = "SELECT DISTINCT * FROM xyy_wms_bill_chukuwaifuhe ";
        List<Record> recordList = Db.find(sql);
        this.setAttr("status", 1);
        this.setAttr("result", recordList == null ? "" : recordList);
        this.renderJson();
    }

    /*01
     * 生成外复核体的json串
     */
    private void waifuhebodyinfo() {
        BillContext context = this.buildBillContext();
        String BillID = context.getString("BillID");
        String BillDtlID = context.getString("BillDtlID");
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM xyy_wms_bill_chukuwaifuhe a INNER JOIN xyy_wms_bill_chukuwaifuhe_details b on a.BillID=b.BillID  ");
        if(BillID != null && BillID.length()!=0) {
            sb.append(" and a.BillID='"+BillID+"'");
        }
        if(BillDtlID != null && BillDtlID.length()!=0) {
            sb.append(" and b.BillDtlID='"+BillDtlID+"'");
        }
        List<Record> recordList = Db.find(sb.toString());
        this.setAttr("status", 1);
        this.setAttr("result", recordList == null ? "" : recordList);
        this.renderJson();
    }


    /**
     * 保存修改后的整件数
     * @return
     */
    private void saveWFH(){
        String requestMsg = this.getPara("spObj");
        JSONObject msg = JSONObject.parseObject(requestMsg);

        // 外复核id
        String wfhID = msg.getString("BillID");
        BigDecimal zhengjianshu = new BigDecimal(msg.getString("zhengjianshu"));
        Object[] paras={zhengjianshu,wfhID};
        Db.update("update xyy_wms_bill_chukuwaifuhe set zhengjianshu=? where  BillID=? ",paras);
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
        public static final String SPE_CODE_WAIFUHEHEAD = "waifuhehead";
        public static final String SPE_CODE_WAIFUHEBODY = "waifuhebody";
        public static final String SPE_CODE_saveWFH = "saveWFH";
    }

}
