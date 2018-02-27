package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.UUIDUtil;
import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

public class BillYhzxPostHandler implements PostSaveEventListener{
	private static Logger LOGGER = Logger.getLogger(BillYhzxPostHandler.class);

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance)context.get("$model");
        if(dsi == null || StringUtil.isEmpty(dsi.getFullKey())) return;
        // 单据头数据
        Record record = getHead(dsi);
        // 单据体数据
        List<Record> body = getBody(dsi);
        int status = record.getInt("status");
        //生成质量复检单
        String BillID= UUIDUtil.newUUID();
        Record fjrecordHead=new Record();
        fjrecordHead.set("BillID",BillID);
        fjrecordHead.set("createTime",new Date());
        fjrecordHead.set("updateTime",new Date());
        String dingdanbianhao=record.getStr("danjubianhao");
        fjrecordHead.set("dingdanbianhao",dingdanbianhao);
		fjrecordHead.set("dingdanriqi",new Date());
		String bumenmingcheng=record.getStr("bumenmingcheng");
		fjrecordHead.set("bumenmingcheng",bumenmingcheng);
        fjrecordHead.set("orgId",context.getString("orgId"));
        fjrecordHead.set("rowID",UUIDUtil.newUUID());
        fjrecordHead.set("orgCode",record.getStr("orgCode"));
        fjrecordHead.set("status",1);
		fjrecordHead.set("taskType",20);
        fjrecordHead.set("dingdanleixing",5);
        fjrecordHead.set("huozhumingcheng","武汉小药药");
        fjrecordHead.set("huozhubianhao","001");
        EDB.save("xyy_wms_bill_zhiliangfujian",fjrecordHead);
        LOGGER.info("质量单据头生成完毕");
        //质量单据体集合
        List<Record> zlSaveList=new ArrayList<>();
        for (Record r:body){
            int fujianjielun=r.getInt("fujianjielun");
            if (fujianjielun==3){
                Record fjRecordDetail=new Record();
                fjRecordDetail.set("BillID",BillID);
                fjRecordDetail.set("BillDtlID",UUIDUtil.newUUID());
                fjRecordDetail.set("createTime",new Date());
                fjRecordDetail.set("updateTime",new Date());
                fjRecordDetail.set("shangpinbianhao",r.getStr("shangpinbianhao"));
                fjRecordDetail.set("shangpinmingcheng",r.getStr("shangpinmingcheng"));
                fjRecordDetail.set("shangpinguige",r.getStr("guige"));
                fjRecordDetail.set("baozhuangdanwei",r.getStr("baozhuangdanwei"));
                fjRecordDetail.set("baozhuangshuliang",r.get("baozhuangshuliang"));
                fjRecordDetail.set("pizhunwenhao",r.getStr("pizhunwenhao"));
                fjRecordDetail.set("shengchanchangjia",r.getStr("shengchanchangjia"));
                fjRecordDetail.set("pihao",r.getStr("pihao"));
                fjRecordDetail.set("shuliang",r.get("shuliang"));
                fjRecordDetail.set("huowei",r.getStr("huoweibianhao"));
                fjRecordDetail.set("fujianjielun",0);
                fjRecordDetail.set("orgId",context.getString("orgId")
                );
                fjRecordDetail.set("rowID",UUIDUtil.newUUID());
                fjRecordDetail.set("orgCode",record.getStr("orgCode"));
                zlSaveList.add(fjRecordDetail);
            }

        }
        if (zlSaveList.size()>0){
            EDB.batchSave("xyy_wms_bill_zhiliangfujian_details",zlSaveList);
            LOGGER.info("质量单据体生成完毕");
        }
    }

}
