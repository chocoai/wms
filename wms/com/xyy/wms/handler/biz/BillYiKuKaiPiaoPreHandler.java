package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillYiKuKaiPiaoPreHandler implements PreSaveEventListener {

    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
		//获取组织机构id
		String orgId=event.getContext().getString("orgId");
        List<Record> body = getBody(dsi);
        // 后台校验 YiKuKaiPiaoValidate
        Date zhidanriqi=head.get("zhidanriqi");
        String zhidanren=head.getStr("zhidanren");
        if(zhidanriqi==null||zhidanren==null) {
        	event.getContext().addError("移库开票单","制单人，制单日期等数据不能为空");
        	return;
        }
        for(Record r:body){
        	String shangpinbianhao=r.getStr("shangpinbianhao");
        	String shangpinmingcheng=r.getStr("shangpinmingcheng");
        	String yichuhuowei=r.getStr("yichuhuowei");
        	String yiruhuowei=r.getStr("yiruhuowei");
			Record ychwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? and orgId=?", yichuhuowei,orgId);
			String ychwId = ychwRecord.getStr("ID");
			Record yrhwRecord = Db.findFirst("select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao=? and orgId=?", yiruhuowei,orgId);
			String yrhwId = yrhwRecord.getStr("ID");
			// 商品数据
			Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? and orgId=?", shangpinbianhao,orgId);
			String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
			// 批号数据
			String pihaoId = r.getStr("pihaoId");
			String[] ycParams = {shangpinId, pihaoId, ychwId,orgId};
			String[] yrParams = {shangpinId, pihaoId, yrhwId,orgId};
			Record ycrecord=Db.findFirst("select * from xyy_wms_bill_kucunyuzhanyukou where goodsid = ? and pihaoId = ? and huoweiId = ? and orgId=? order by createTime desc limit 1",ycParams);

			Record yrrecord=Db.findFirst("select * from xyy_wms_bill_kucunyuzhanyukou where goodsid = ? and pihaoId = ? and huoweiId = ? and orgId=? order by createTime desc limit 1",yrParams);
			if (ycrecord!=null){
				event.getContext().addError("移库开票单","当前移出货位存在其他业务操作不能进行操作");
				return;
			}
			if (yrrecord!=null){
				event.getContext().addError("移库开票单","当前移入货位存在其他业务操作不能进行操作");
				return;
			}
			int yckqlb=r.getInt("yckqlbbh");
        	int yrkqlb=r.getInt("yrkqlbbh");
        	if(0==yckqlb){
        		if(1==yrkqlb){
					event.getContext().addError("移库开票单","零件库商品不能移入整件库");
					return;
				}
			}
			if(1==yckqlb){
        		BigDecimal lingsanshu=r.getBigDecimal("lingsanshu");
        		if (lingsanshu.compareTo(BigDecimal.ZERO)!=0){
					event.getContext().addError("移库开票单","整件库移出商品时，零散数必须是0");
					return;
				}
			}
        	if(yichuhuowei.equals(yiruhuowei)){
        		event.getContext().addError("移库开票单","移出货位与移入货位不能相等");
        		return;
        	}
        	String yikuyuanyin=r.getStr("yikuyuanyin");
        	if(shangpinbianhao==null||shangpinmingcheng==null||yichuhuowei==null||yiruhuowei==null||yikuyuanyin==null){
        		event.getContext().addError("移库开票单","商品编号，批号，货位等数据不能为空");
        		return;
        	}
        	BigDecimal shuliang=r.getBigDecimal("shuliang");
        	BigDecimal ychwkcsl=r.getBigDecimal("ychwkcsl");
        	BigDecimal lingsanshu=r.getBigDecimal("lingsanshu");
        	BigDecimal zhengjianshu=r.getBigDecimal("zhengjianshu");
        	if(shuliang.compareTo(BigDecimal.ZERO)==-1||shuliang.compareTo(BigDecimal.ZERO)==0||lingsanshu.compareTo(BigDecimal.ZERO)==-1||zhengjianshu.compareTo(BigDecimal.ZERO)==-1){
        		event.getContext().addError("移库开票单","数量，整件数量，零散数量，不准确");
        		return;
        	}
        	if(zhengjianshu.compareTo(shuliang)==1||shuliang.compareTo(ychwkcsl)==1||lingsanshu.compareTo(shuliang)==1){
        		event.getContext().addError("移库开票单","整件数量，零散数量，不能超过移出货位库存数量");
        		return;
        	}
        }
    }
}
