package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;

import java.util.ArrayList;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author by lc.liao
 * */
public class DicfenjianhuadaoPreHandler implements PreSaveEventListener {


    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        String id = head.getStr("ID");
        //新增校验
        if(StringUtil.isEmpty(id)) {
            String cangkubianhao=head.getStr("cangkubianhao");
            String fjhdbh=head.getStr("fjhdbh");
            Integer fjhdlx = head.getInt("fjhdlx");
            if(fjhdlx==0) {
                event.getContext().addError("分拣滑道资料维护","分拣滑道类型不能为空");
                return;
            }
            String sql="select * from xyy_wms_dic_fenjianhuadaoziliao  where cangkubianhao=? AND fjhdbh=?";
            Record record= Db.findFirst(sql,cangkubianhao,fjhdbh);
            if (record!=null){
                event.getContext().addError("分拣滑道资料维护","该仓库已经存在相同分拣滑道编号");
                return;
            }
        }

        //编辑校验
     /*   if(!StringUtil.isEmpty(id)) {
            String cangkubianhao=head.getStr("cangkubianhao");
            String fjhdbh=head.getStr("fjhdbh");
            Integer fjhdlx = head.getInt("fjhdlx");

            String sql="select * from xyy_wms_dic_fenjianhuadaoziliao  where cangkubianhao=? AND fjhdbh=?";
            Record record= Db.findFirst(sql,cangkubianhao,fjhdbh);
            if (record!=null){
                event.getContext().addError("分拣滑道资料维护","该仓库已经存在相同分拣滑道编号");
                return;
            }
        }*/
     /*   String rqSql="select * from xyy_wms_dic_kehudizhi";
        List<Record> rqRecords= Db.find(rqSql);
        List<Record> cqRecord=new ArrayList<>();

        for (Record r:rqRecords){
            r.set("ID", UUIDUtil.newUUID());
            r.set("orgId","d57e905010e34c64939d04fb5ef403d2");
            r.set("orgCode","A-B-003");
            r.set("kehubianhao","CQ"+r.getStr("kehubianhao"));
         *//*   r.set("goodsid","CQ"+r.getStr("goodsid"));
            r.set("shangpinbianhao","CQ"+r.getStr("shangpinbianhao"));*//*
            r.set("rowID", UUIDUtil.newUUID());

            cqRecord.add(r);


        }
        Db.batchSave("xyy_wms_dic_kehudizhi",cqRecord,cqRecord.size());*/




    }
}
