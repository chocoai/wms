package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;
import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

public class BillPanDianQueRenPostHandler implements PostSaveEventListener{
	private static Logger LOGGER = Logger.getLogger(BillPanDianQueRenPostHandler.class);
	@Override
	public void execute(PostSaveEvent event) {

		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
			return;
		// 单据头数据
		Record record = getHead(dsi);
		//获取组织机构id
		String orgId=context.getString("orgId");
		// 单据体数据
		List<Record> body = getBody(dsi);

		int status = record.getInt("status");
		if (status != 20) return;
		//损益数据单据题集合
        List<Record> syList=new ArrayList<>();
        //损益开票单据头体
		String billID = UUIDUtil.newUUID();
		String danjubianhao=record.getStr("danjubianhao");
		for(Record r : body) {
			//获取盘点确认单中损益数量
			BigDecimal sysl=r.getBigDecimal("sunyishuliang");
			if(sysl.compareTo(BigDecimal.ZERO)!=0){
				//生成损益开票单明细数据
				Record sykpDetail=new Record();
				sykpDetail.set("BillID",billID);
				sykpDetail.set("BillDtlID",UUIDUtil.newUUID());
				sykpDetail.set("huozhu", "武汉小药药");
				sykpDetail.set("shangpinbianhao", r.getStr("shangpinbianhao"));
				sykpDetail.set("danjubianhao", danjubianhao);
				String sql ="select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? and orgId='"+orgId+"'";
				Record record1=Db.findFirst(sql,r.getStr("shangpinbianhao"));
				sykpDetail.set("dbzsl",record1.getBigDecimal("dbzsl"));
				sykpDetail.set("shangpinmingcheng", r.getStr("shangpinmingcheng"));
				sykpDetail.set("guige", r.getStr("guige"));
				sykpDetail.set("danwei", r.getInt("danwei"));
				sykpDetail.set("shengchanchangjia", r.getStr("shengchanchangjia"));
				sykpDetail.set("pihao", r.getStr("pihao"));
				sykpDetail.set("shengchanriqi", r.getDate("shengchanriqi"));
				sykpDetail.set("youxiaoqizhi", r.getDate("youxiaoqizhi"));
				sykpDetail.set("kucunshuliang", r.get("kucunshuliang"));
				sykpDetail.set("kczjs", r.get("kucunzhengjianshu"));
				sykpDetail.set("kclss", r.get("kucunlingsanshu"));
				sykpDetail.set("shijishuliang", r.get("shijishuliang"));
				sykpDetail.set("sjzjs", r.get("shijizhengjianshu"));
				sykpDetail.set("sjlss", r.get("shijilingsanshu"));
				sykpDetail.set("orgId",r.getStr("orgId"));
				sykpDetail.set("orgCode",r.getStr("orgCode"));
				sykpDetail.set("sunyishuliang", r.get("sunyishuliang"));
				sykpDetail.set("syzjs", r.get("sunyizhengjianshu"));
				sykpDetail.set("sylss", r.get("sunyilingsanshu"));
				sykpDetail.set("sunyiyuanyin", r.get("sunyiyuanyin"));
				sykpDetail.set("pizhunwenhao", r.get("pizhunwenhao"));
				sykpDetail.set("kuqu", record.get("kuqu"));
				sykpDetail.set("huowei", r.getStr("huoweibianhao"));
				sykpDetail.set("BillDtlID", UUIDUtil.newUUID());
				sykpDetail.set("createTime",new Date());
				sykpDetail.set("updateTime",new Date());
				syList.add(sykpDetail);

				//生成损益预占预扣
				Record Rhuowei = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? and orgId=?",  r.getStr("huoweibianhao"),orgId);
				String huoweiId = Rhuowei.getStr("ID");
				String shangpinId = record1 == null ? "" : record1.getStr("goodsid");
				String pihaoId=r.getStr("pihaoId");
				//溢
				if(sysl.compareTo(BigDecimal.ZERO)<0) {
					BigDecimal syshuliang = new BigDecimal(0).subtract(sysl);
					KucuncalcService.kcCalc.insertKCRecord(new KuncunParameter(r.getStr("orgId"), "", shangpinId, pihaoId, huoweiId, "", danjubianhao, 8, syshuliang));
				}
				//损
				if(sysl.compareTo(BigDecimal.ZERO)>0) {

					KucuncalcService.kcCalc.insertKCRecord(new KuncunParameter(r.getStr("orgId"), "", shangpinId, pihaoId, huoweiId, "", danjubianhao, 7, sysl));
				}


			}
		}
		//批量生成损益开票单据体
		String cangkubianhao=record.getStr("cangkubianhao");
		if (syList.size()>0){
			//生成损益开票单头信息
			Record sunyikaipiao=new Record();
			sunyikaipiao.set("BillID",billID);
			sunyikaipiao.set("danjubianhao",danjubianhao);
			sunyikaipiao.set("zhidanren", record.getStr("zhidanren"));
			sunyikaipiao.set("kufang", record.getStr("cangkumingcheng"));
			sunyikaipiao.set("cangkubianhao", record.getStr("cangkubianhao"));
			sunyikaipiao.set("sunyileixing", 1);
			sunyikaipiao.set("createTime",new Date());
			sunyikaipiao.set("updateTime",new Date());
			sunyikaipiao.set("creatorName",record.getStr("zhidanren"));
			sunyikaipiao.set("creator",record.getStr("zhidanren"));
			sunyikaipiao.set("updatorName",record.getStr("zhidanren"));
			sunyikaipiao.set("updator",record.getStr("zhidanren"));
			sunyikaipiao.set("status",20);
			sunyikaipiao.set("zhuangtai",1);
			sunyikaipiao.set("zhidanriqi",new Date());
			sunyikaipiao.set("orgId",record.getStr("orgId"));
			sunyikaipiao.set("orgCode","A-B-001");
			EDB.save("xyy_wms_bill_sunyikaipiao", sunyikaipiao);
			LOGGER.info("损益开票单生成完毕");
			EDB.batchSave("xyy_wms_bill_sunyikaipiao_details",syList);
			LOGGER.info("损益开票单明细 数据生成完毕");
		}

	}

}
