package com.xyy.bill.services.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.DictKeys;

public class WMSTempService {

	/**
	 * 生成销售出库单
	 * @param head
	 * @param bodys
	 */
	public void createXSCKD(Record head, List<Record> bodys) {
			
		List<Record> list = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for (Record detail : bodys) {
			Record record = new Record();
			Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where goodsid ='"+detail.getStr("goodsid")+"' limit 1");
			if (sp==null) {
				System.out.println("商品基本信息不存在【"+detail.getStr("goodsid")+"】");
				continue;
			}
			Record ph = Db.findFirst("select * from xyy_erp_dic_shangpinpihao where goodsid = '"+detail.getStr("goodsid")+"' limit 1");
			if (ph==null) {
				System.out.println("商品批号信息不存在【"+detail.getStr("goodsid")+"】");
				continue;
			}
			Record oldRecord = Db.use(DictKeys.db_dataSource_wms_mid).findFirst("select * from int_wms_xsck_bill "
					+ "where djbh = '"+head.getStr("danjubianhao")+"' and spid ='"+detail.getStr("goodsid")+"' ");
			if (oldRecord!=null) {
				continue;
			}
			record.set("DJBH", head.getStr("danjubianhao"));
			record.set("ZXS", "整箱数001");
			record.set("ZCQMC", "暂存区暂无");
			record.set("DWBH", head.getStr("kehubianhao"));
			record.set("RQ", format.format(head.getDate("createTime")));
			record.set("YWY", head.getStr("yewuyuan"));
			record.set("THFS", "配送");
			record.set("beizhu", head.getStr("remark")==null?"无":head.getStr("remark"));
			record.set("ddlx", "正常销售");
			record.set("yzid", "O0Z8M62IK57");
			record.set("DJ_SORT", detail.getInt("SN"));
			record.set("SPID", detail.getStr("goodsid"));
			record.set("SL", detail.getBigDecimal("shuliang"));
			record.set("JS", record.getBigDecimal("SL").divide(BigDecimal.valueOf(sp.getInt("dbzsl").longValue()),2,RoundingMode.HALF_UP));
			record.set("LSS", detail.getBigDecimal("shuliang").intValue()%sp.getInt("dbzsl").longValue());
			record.set("PH", ph.getStr("pihao"));
			record.set("lastmodifytime", format2.format(head.getDate("createTime")));
			record.set("RQ_SC", format.format(ph.getDate("shengchanriqi")));
			record.set("YXQZ", format.format(ph.getDate("youxiaoqizhi")));
			record.set("is_zx", "否");
			
			list.add(record);
		}
			
		if (list!=null && list.size()>0) {
			Db.use(DictKeys.db_dataSource_wms_mid).batchSave("int_wms_xsck_bill",list, 500);
		}
	}

	/**
	 * 生成销售退回入库单
	 * @param head
	 * @param bodys
	 */
	public void createXSTHRKD(Record head, List<Record> bodys) {
		List<Record> list = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Record detail : bodys) {
			Record record = new Record();
			Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao ='"+detail.getStr("shangpinbianhao")+"' limit 1");
			if (sp==null) {
				System.out.println("商品基本信息不存在【"+detail.getStr("shangpinbianhao")+"】");
				continue;
			}
			Record oldRecord = Db.use(DictKeys.db_dataSource_wms_mid).findFirst("select * from int_wms_xsth_bill "
					+ "where djbh = '"+head.getStr("danjubianhao")+"' and spid ='"+detail.getStr("goodsid")+"' ");
			if (oldRecord!=null) {
				continue;
			}
			record.set("DJBH", head.getStr("danjubianhao"));
			record.set("DWBH", head.getStr("kehubianhao"));
			record.set("RQ", format.format(head.getDate("createTime")));
			record.set("YZID", "O0Z8M62IK57");
			record.set("thlb", "退货入库");
			record.set("DJ_SORT", detail.getInt("SN"));
			record.set("SPID", sp.getStr("goodsid"));
			record.set("SL", detail.getBigDecimal("tuihuoshuliang"));
			record.set("JS", record.getBigDecimal("SL").divide(BigDecimal.valueOf(sp.getInt("dbzsl").longValue()),2,RoundingMode.HALF_UP));
			record.set("LSS", detail.getBigDecimal("tuihuoshuliang").intValue()%sp.getInt("dbzsl").longValue());
			record.set("PH", detail.getStr("pihao"));
			record.set("yspd", "1");
			record.set("lastmodifytime", format2.format(head.getDate("createTime")));
			record.set("RQ_SC", format.format(detail.getDate("shengchanriqi")));
			record.set("YXQZ", format.format(detail.getDate("youxiaoqizhi")));
			record.set("is_zx", "否");
			
			
			list.add(record);
		}
			
		if (list!=null && list.size()>0) {
			Db.use(DictKeys.db_dataSource_wms_mid).batchSave("int_wms_xsth_bill", list, 500);
		}
		
	}

}
