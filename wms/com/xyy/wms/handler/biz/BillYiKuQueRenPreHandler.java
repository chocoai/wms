package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.UUIDUtil;

public class BillYiKuQueRenPreHandler implements PreSaveEventListener {

    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
        // 后台校验 YiKuKaiPiaoValidate
        Date zhidanriqi = head.get("zhidanriqi");
        String zhidanren = head.getStr("zhidanren");
        String shangjiayuan = head.getStr("shangjiayuan");
        String xiajiayuan = head.getStr("xiajiayuan");
        for (Record r : body) {
            BigDecimal shuliang = r.getBigDecimal("shijishuliang");

            BigDecimal lingsanshu = r.getBigDecimal("shijilingsanshu");
            BigDecimal zhengjianshu = r.getBigDecimal("shijizhengjianshu");
            if (shuliang.compareTo(BigDecimal.ZERO) == -1 || lingsanshu.compareTo(BigDecimal.ZERO) == -1
                    || zhengjianshu.compareTo(BigDecimal.ZERO) == -1) {
                event.getContext().addError("移库确认单", "实际数量，实际整件数量，实际零散数量，不能为负数");
                return;
            }
        }
        if (zhidanriqi == null || zhidanren == null) {
            event.getContext().addError("移库确认单", "制单人，上架员，下架员，制单日期，库房等数据不能为空");
            return;
        }
/*      int l=0;
        String ljSql="select * from xyy_wms_dic_ljqhwgxwh where orgCode='A-B-001'";
        List<Record> ljRecords= Db.find(ljSql);
        List<Record> ljhwRecords=new ArrayList<>();
        String hwSql="select * from xyy_wms_dic_huoweiziliaoweihu where orgCode='A-B-001'";
        List<Record> hwRecords= Db.find(hwSql);
        List<Record> huoiwei=new ArrayList<>();
        for (Record ljR:ljRecords){
            String ID=UUIDUtil.newUUID();
            ljR.set("orgId","08e78dd8ed6b44b2af3cba9ddc521e61");
            ljR.set("orgCode","A-B-001");
            ljR.set("ID",ID);
            ljR.set("rowID", UUIDUtil.newUUID());
            ljR.set("cangkuID","a8ba08d6a3904b9093f08f930e27380b");
            ljR.set("ljqmc",ljR.getStr("ljqmc"));
            ljR.set("ljqbh",ljR.getStr("ljqbh"));
            ljhwRecords.add(ljR);
            for (int i=l;i<hwRecords.size();i++){
                l++;
                Record record=new Record();
                record.set("ID",ID);
                record.set("orgId","08e78dd8ed6b44b2af3cba9ddc521e61");
                record.set("orgCode","A-B-001");
                record.set("rowID", UUIDUtil.newUUID());
                record.set("BillDtlID", UUIDUtil.newUUID());
                record.set("huoweibianhao",hwRecords.get(i).getStr("huoweibianhao"));
                huoiwei.add(record);
                if (l%1384==0){
                    break;
                }
            }
        }
        Db.batchSave("xyy_wms_dic_ljqhwgxwh",ljhwRecords,ljhwRecords.size());
        Db.batchSave("xyy_wms_dic_ljqhwgxwh_list",huoiwei,huoiwei.size());*/
    /*    List<Record> cqRecord=new ArrayList<>();
        for (Record r:ljRecords){
            r.set("ID", UUIDUtil.newUUID());
            r.set("orgId","d57e905010e34c64939d04fb5ef403d2");
            r.set("orgCode","A-B-003");
            String ljqbh="CQLJQ0"+i+"";
            i++;
            r.set("ljqbh",ljqbh);
            r.set("ljqmc","CQ"+r.getStr("ljqmc"));
            r.set("rowID", UUIDUtil.newUUID());
            r.set("cangkuID","4e2be093bf314e9c857fac7eba14b28d");
            cqRecord.add(r);
        }*/
/*    String sql ="select * from xyy_wms_dic_shangpinziliao";
    List<Record> lists=Db.find(sql);
    Db.batchSave("xyy_wms_dic_yanghupinzhong",lists,lists.size());*/
       // Db.batchSave("xyy_wms_dic_ljqzlwh",cqRecord,cqRecord.size());

    }

}
