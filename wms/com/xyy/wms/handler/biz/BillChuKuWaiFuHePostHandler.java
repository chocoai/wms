package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.util.StringUtil;

/**
 * 
 * @author 王博臣
 *外复核保存后处理 
 *
 */
public class BillChuKuWaiFuHePostHandler implements PostSaveEventListener {
	private static Logger LOGGER = Logger.getLogger(BillChuKuWaiFuHePostHandler.class);

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
			return;
		// 单据头数据
		Record record = getHead(dsi);
		// 单据体数据
		List<Record> body = getBody(dsi);
		// User user = (User)context.get("user");

		String sourceId = record.getStr("SourceID");
		for (Record r : body) {
			
			int shuliang = r.getInt("shuliang");
			int dingdanshuliang = r.getInt("zhengjianshu");
			
			String dingdanbianhao = r.getStr("dingdanbianhao");
			String shangpinbianhao = r.getStr("shangpinbianhao");
			
			//1.更新【商品总库存】
			ZongKuCun(r, shuliang, shangpinbianhao);
			
			//2.更新【商品批号库存】
			QiHaoKuCun(r, shuliang, shangpinbianhao);
			
			//3,更新【商品批号货位库存】
			QiHaoHuoWeiKUCun(r, shuliang, shangpinbianhao);
			
			//4,更新商品账页表
            ShangPinZhangYe(record, r);
            
            //5,释放集货位
    		String jihuoweibianhao = record.get("zancunqu");	
    		Object[] params= {1, jihuoweibianhao};
    		Db.update("update xyy_wms_dic_jihuowei set shifousuoding=1 where jihuoweibianhao=?",params);
    		LOGGER.info("释放集货位");
		}
		
		//释放集货位
		String BillID = record.getStr("BillID");
	    Object[] params= {1, BillID};
		Db.update("update xyy_wms_bill_dabaorenwu set suoding=? where BillID=?",params);
		LOGGER.info("释放复核台");

	}

	

	
	
	//1.更新【商品总库存】
	private void ZongKuCun(Record r, int shuliang, String shangpinbianhao) {
		// 获取商品数据
		Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=?", shangpinbianhao);
		//获取商品id
		String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
		 
		String ckParams = shangpinId;
		//查询商品总库存表
		Record sphwkcData = Db.findFirst("select * from xyy_wms_bill_shangpinzongkucun where shangpinId = ?   order by createTime desc limit 1", ckParams);
		
		//出库库存预扣对象
		Record ckkcyk = null;
		
		// 库存预扣和持久化
		if (sphwkcData==null) {
			 ckkcyk = new Record();
		     ckkcyk.set("shangpinId", shangpinId);
		     ckkcyk.set("chukuyukou", shuliang);
		     ckkcyk.set("createTime", new Date());
		     ckkcyk.set("beizhu", "");
		} else {
			ckkcyk = new Record();
			 BigDecimal chukuyukoushuliang = sphwkcData.getBigDecimal("chukuyukou");
			 //“出库预扣”=“出库预扣” – 订单数量
			 chukuyukoushuliang=chukuyukoushuliang.subtract(BigDecimal.valueOf(shuliang));
			 //“库存数量”=“库存数量”- 实际出库数量
			 BigDecimal kucunshuliang = sphwkcData.getBigDecimal("kucunshuliang");
			 
			 ckkcyk = new Record();
		     ckkcyk.set("shangpinId", shangpinId);
		     ckkcyk.set("chukuyukou", chukuyukoushuliang);
		     ckkcyk.set("createTime", new Date());
		     ckkcyk.set("beizhu", "");
		}
		if(ckkcyk != null) Db.save("xyy_wms_bill_shangpinzongkucun", ckkcyk);
	}

	//2.更新【商品批号库存】
		private void QiHaoKuCun(Record r, int shuliang, String shangpinbianhao) {
			// 获取商品数据
			Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=?", shangpinbianhao);
			//获取商品id
			String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
			// 获取批号数据
			Record pihaoRecord = Db.findFirst("select * from xyy_wms_dic_shangpinpihao where pihao=?", r.getStr("pihao"));
			//获取批号id
			String pihaoId = pihaoRecord == null ? "" : pihaoRecord.getStr("pihaoId");
			 
			String ckParams = shangpinId;
			//查询商品总库存表
			Record sphwkcData = Db.findFirst("select * from xyy_wms_bill_shangpinpihaokucun where shangpinId = ?   order by createTime desc limit 1", ckParams);
			
			//出库库存预扣对象
			Record ckkcyk = null;
			
			// 库存预扣和持久化
			if (sphwkcData==null) {
				 ckkcyk = new Record();
			     ckkcyk.set("shangpinId", shangpinId);
			     ckkcyk.set("pihaoId", pihaoId);
			     ckkcyk.set("chukuyukou", shuliang);
			     ckkcyk.set("createTime", new Date());
			     ckkcyk.set("beizhu", "");
			} else {
				ckkcyk = new Record();
				 BigDecimal chukuyukoushuliang = sphwkcData.getBigDecimal("chukuyukou");
				 //“出库预扣”=“出库预扣” – 订单数量
				 chukuyukoushuliang=chukuyukoushuliang.subtract(BigDecimal.valueOf(shuliang));
				 //“库存数量”=“库存数量”- 实际出库数量
				 BigDecimal kucunshuliang = sphwkcData.getBigDecimal("kucunshuliang");
				 
				 ckkcyk = new Record();
			     ckkcyk.set("shangpinId", shangpinId);
			     ckkcyk.set("pihaoId", pihaoId);
			     ckkcyk.set("chukuyukou", chukuyukoushuliang);
			     ckkcyk.set("createTime", new Date());
			     ckkcyk.set("beizhu", "");
			}
			if(ckkcyk != null) Db.save("xyy_wms_bill_shangpinpihaokucun", ckkcyk);
		}
	
	//3,更新【商品批号货位库存】
	private void QiHaoHuoWeiKUCun(Record r, int shuliang, String shangpinbianhao) {
		// 获取商品数据
		Record shangpinRecord = Db.findFirst("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=?", shangpinbianhao);
		//获取商品id
		String shangpinId = shangpinRecord == null ? "" : shangpinRecord.getStr("goodsid");
		// 获取批号数据
		Record pihaoRecord = Db.findFirst("select * from xyy_wms_dic_shangpinpihao where pihao=?", r.getStr("pihao"));
		//获取批号id
		String pihaoId = pihaoRecord == null ? "" : pihaoRecord.getStr("pihaoId");
         
		String ckParams = shangpinId;
		//查询商品批号货位库存表
		Record sphwkcData = Db.findFirst("select * from xyy_wms_bill_shangpinpihaohuoweikucun where shangpinId = ?   order by createTime desc limit 1", ckParams);
		
		//出库库存预扣对象
		Record ckkcyk = null;
		
		// 库存预扣和持久化
		if (sphwkcData==null) {
			 ckkcyk = new Record();
		     ckkcyk.set("shangpinId", shangpinId);
		     ckkcyk.set("pihaoId", pihaoId);
		     ckkcyk.set("chukuyukou", shuliang);
		     ckkcyk.set("createTime", new Date());
		     ckkcyk.set("beizhu", "");
		} else {
			ckkcyk = new Record();
			 BigDecimal chukuyukoushuliang = sphwkcData.getBigDecimal("chukuyukou");
			 //“出库预扣”=“出库预扣” – 订单数量
			 chukuyukoushuliang=chukuyukoushuliang.subtract(BigDecimal.valueOf(shuliang));
			 //“库存数量”=“库存数量”- 实际出库数量
			 BigDecimal kucunshuliang = sphwkcData.getBigDecimal("kucunshuliang");
			 
			 ckkcyk = new Record();
		     ckkcyk.set("shangpinId", shangpinId);
		     ckkcyk.set("pihaoId", pihaoId);
		     ckkcyk.set("chukuyukou", chukuyukoushuliang);
		     ckkcyk.set("createTime", new Date());
		     ckkcyk.set("beizhu", "");
		}
		
		if(ckkcyk != null) Db.save("xyy_wms_bill_shangpinpihaohuoweikucun", ckkcyk);
	}
	
	
	//4,更新商品账页表
		private void ShangPinZhangYe(Record record, Record r) {
			Record zhangYe=new Record();
			zhangYe.set("zhidanren", record.get("zhidanren"));
			zhangYe.set("huozhuId", r.get("huozhuId"));
			zhangYe.set("huoweiId", "");
			zhangYe.set("rukushuliang", 0);
			zhangYe.set("spzjy", 0.0);
			zhangYe.set("spphjy", 0.0);
			zhangYe.set("spphhwjy", 0.0);
			zhangYe.set("zhiliangzhuangtai", 0.0);
			zhangYe.set("zhidanriqi", new Date());
			zhangYe.set("createTime", new Date());
			//插入商品账页数据
			Db.save("xyy_wms_bill_shangpinzhangye", zhangYe);
			LOGGER.info("商品账页表生成完毕");
		}
}
