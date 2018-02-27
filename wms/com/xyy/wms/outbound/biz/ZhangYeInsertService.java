package com.xyy.wms.outbound.biz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.outbound.util.ZhangYeParameter;

/**
 * 商品账页，适用于入库，出库，库内
 * 
 * zhaiyao 1:入库  2：出库
 * @author dell
 *
 */
public class ZhangYeInsertService {
	public static ZhangYeInsertService zhangyeInsert=new ZhangYeInsertService();
	
	public void updateZhangye(List<ZhangYeParameter> pams,int zhaiyao){
		List<Record> list = new ArrayList<Record>();
		for(ZhangYeParameter zy : pams){
			Record re = new Record();
			re.set("status", 10);
			re.set("orgId", zy.getOrgId());
			re.set("orgCode", zy.getOrgCode());
			re.set("createTime", new Date());
			re.set("updateTime", new Date());
			re.set("zhidanren", zy.getZhidanren());
			re.set("zhidanriqi", zy.getZhidanriqi());
			re.set("rowID", UUIDUtil.newUUID());
			re.set("danjubianhao", zy.getDanjubianhao());
			re.set("shangpinId", zy.getShangpinId());
			re.set("chukushuliang",zy.getChukushuliang());
			re.set("rukushuliang", zy.getRukushuliang());
			re.set("pihaoId", zy.getPihaoId());
			re.set("huoweiId", zy.getHuoweiId());
			re.set("huozhuId", zy.getHuozhuId());
			re.set("cangkuId", zy.getCangkuId());
			re.set("zhaiyao", zhaiyao);
			list.add(re);
		}
		
//		Db.batchSave("xyy_wms_bill_shangpinzhangye", list, 30);
		EDB.batchSave("xyy_wms_bill_shangpinzhangye", list);
		
	}
}
