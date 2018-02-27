package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.StringUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;

public class BillCaiGouTuiPostHandler implements PostSaveEventListener {

	private static Logger logger = Logger.getLogger(BillCaiGouTuiPostHandler.class);

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance)context.get("$model");
        if(dsi == null || StringUtil.isEmpty(dsi.getFullKey())){
        	return;
        	
        } 
        // 单据头数据
        Record record = getHead(dsi);
        // 单据体数据
        List<Record> body = getBody(dsi);
        
        String rukudanID = record.getStr("rukudanID");
        
        String danjubianhao = record.getStr("dingdanbianhao");
        for(Record r : body){
        	String shangpinbianhao = r.getStr("shangpinbianhao");
        	String pihao = r.getStr("pihao");
        	String huoweibianhao = r.getStr("huoweibianhao");
        	BigDecimal rukushuliang = r.getBigDecimal("shuliang");
        	String rukuID = r.getStr("rukuID");
        	getCaiGouTuiChuiById(rukuID,rukushuliang);
        	
        	String yewubianhao = r.getStr("BillDtlID");
        	
        	//回写批号货位库存，回写批号货位预占
        	updatePhhwKun(shangpinbianhao,pihao,huoweibianhao,rukushuliang,yewubianhao,danjubianhao);
  
        }
        //回写采购退出单的数量和状态
        updateCaiGouTuiChuAllZT(rukudanID);
	}
	
	
	public static void updateZKC(String shangpinbianhao , BigDecimal rukushuliang){
		
		String spObj = "select * from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?";
		Record record = Db.findFirst(spObj,shangpinbianhao);
		String goodsid = record.getStr("goodsid");
		
		
		String zObj = "select * from xyy_wms_bill_shangpinzongkucun where shangpinId = ?";
		Record Zrecord = Db.findFirst(zObj,goodsid);
		BigDecimal chukuyukou = Zrecord.getBigDecimal("chukuyukou");
		String Id =  Zrecord.getStr("ID");
		// 总预扣
		BigDecimal zchukuyukou;
		if(chukuyukou==null){
			zchukuyukou = rukushuliang;
		}else{
			zchukuyukou = chukuyukou.add(rukushuliang);
		}
		String updateSql = "update xyy_wms_bill_shangpinzongkucun set chukuyukou = ? where ID=?";
		Db.update(updateSql, zchukuyukou , Id);
		
	}
	
	public static void updatePhhwKun(String shangpinbianhao,String pihao,String huoweibianhao,BigDecimal shuliang,String yewubianhao,String danjubianhao){
		// 查询货位id
		String sql = "select * from xyy_wms_dic_huoweiziliaoweihu where huoweibianhao =?";
		Record record = Db.findFirst(sql, huoweibianhao);
		String huoweiId = record.getStr("ID");
		String cangkuId = record.getStr("cangkuID");
		int zuoyeleixing = 2;
		
		String spObj = "select * from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?";
		Record sprecord = Db.findFirst(spObj,shangpinbianhao);
		String goodsid = sprecord.getStr("goodsid");
		String orgId = sprecord.getStr("orgId");
		
		// 批号对象
		String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId= ? limit 1";
		Record pihaoObj = Db.findFirst(pihaoSql, pihao, goodsid);
		String pihaoId = pihaoObj.getStr("pihaoId");
	
		
		KuncunParameter paras = new KuncunParameter(orgId,cangkuId, goodsid, pihaoId, huoweiId,yewubianhao,danjubianhao,zuoyeleixing,shuliang);
		KucuncalcService.kcCalc.insertKCRecord(paras);
		
	}
	
	 public static void getCaiGouTuiChuiById(String rukuID, BigDecimal rukushuliang) {
			String sqlz = "UPDATE xyy_wms_bill_caigoutuichukaipiaodan_details set zhuangtai = (CASE WHEN (shuliang=wmsshuliang+?) THEN 1 ELSE 0 END), wmsshuliang = wmsshuliang+? where BillDtlID = ?";
			Db.update(sqlz,rukushuliang,rukushuliang, rukuID);
 		logger.info("采购退出订单订单回写成功，采购商品编号："+rukuID);
 	}
	 
	 public static void updateCaiGouTuiChuAllZT(String rukudanID) {
		String sql = "SELECT COUNT(*) tiaomu FROM xyy_wms_bill_caigoutuichukaipiaodan_details WHERE BillID = ?";
		String sql2 = "SELECT COUNT(*) zhuangtaiShu FROM xyy_wms_bill_caigoutuichukaipiaodan_details WHERE zhuangtai = 1 AND BillID = ?";
		Record countTiMu = Db.findFirst(sql,rukudanID);
		Record zhuangtaiTiMu = Db.findFirst(sql2,rukudanID);
		Long counthuizong = countTiMu.get("tiaomu");
		Long zhuangtaiShu = zhuangtaiTiMu.get("zhuangtaiShu");
		int huizongZhuangtai = 0;
		if(counthuizong==zhuangtaiShu){
			huizongZhuangtai = 1;
		}else{
			huizongZhuangtai = 0;
		}
		String sqlCaiGou="update xyy_wms_bill_caigoutuichukaipiaodan set zhuangtai =? where BillID = ?";
		EDB.update("xyy_wms_bill_caigoudingdan", sqlCaiGou,huizongZhuangtai,rukudanID);
		logger.info("采购退回单编号："+rukudanID+"采购退回订单状态："+ huizongZhuangtai);
	}
     
}
