package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.util.PullWMSDataUtil;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.StringUtil;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;

public class BillRuKuShouHuoPostHandler implements PostSaveEventListener {

	private static Logger logger = Logger.getLogger(BillRuKuShouHuoPostHandler.class);
	
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
        
        String danjubianhao = record.getStr("dingdanbianhao");
        

        /*int status = record.getInt("status");
        if (status != 20) {
        	return;
        }*/
        String rukudanID = record.getStr("rukudanID");
 
        for(Record r : body) {
        	// 采购订单的明细
        	String rukuID = r.getStr("rukuID");
        	
        	//收货数量
        	BigDecimal shuliang = r.getBigDecimal("shuliang");
        	//采购订单数量
//        	BigDecimal caigoushuliang = r.getBigDecimal("caigoushuliang");
        	//商品编号
        	String shangpinbianhao = r.getStr("shangpinbianhao");
        	String pihao = r.getStr("pihao");
        	// 业务编号
//        	String yewubianhao = r.getStr("yewubianhao");
        	// 机构id
//        	String orgId = r.getStr("orgId");
        	
        	
        	//回写采购订单明细的数量和状态
        	getCaiGouById(shuliang,rukuID);
        	
        	//如果商品为新批号时，更新商品批号表xyy_wms_dic_shangpinpihao
        	boolean falg = getShangPinPHById(pihao,shangpinbianhao);
        	if(!falg){
        		Record sppihao = new Record();
        		String pihaoId = "PH" + PullWMSDataUtil.orderno2();//批号id
        		//商品对象
        		String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
        		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
        		String goodsId = shangpinObj.getStr("goodsid");
        		String orgId = shangpinObj.getStr("orgId");
        		
        		sppihao.set("ID", UUIDUtil.newUUID());
        		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        		sppihao.set("status",40);
        		sppihao.set("orgId", orgId);
        		sppihao.set("createTime", currentTime);
        		sppihao.set("updateTime", currentTime);
        		sppihao.set("shengchanriqi", r.get("shengchanriqi")); //生产日期
        		sppihao.set("youxiaoqizhi", r.get("youxiaoqizhi"));  //有效期至
        		sppihao.set("pihao", pihao); //批号
        		sppihao.set("goodsId", goodsId); //商品id
        		sppihao.set("pihaoId", pihaoId); 
//        		EDB.save("xyy_wms_dic_shangpinpihao",sppihao);
        		Db.save("xyy_wms_dic_shangpinpihao",sppihao);
        		logger.info("更新商品批号："+pihao);
        	}
        	//更新商品总库存的入库预占xyy_wms_bill_shangpinzongkucun
//        	updateZongKuCun(yewubianhao,shangpinbianhao,orgId,shuliang,danjubianhao);
//        	
//        	//更新商品批号库存的入库预占xyy_wms_bill_shangpinpihaokucun
//        	updateKuCun(pihao,yewubianhao,shangpinbianhao,orgId,shuliang,danjubianhao);

        	
        }
        //回写采购订单状态
    	updateCaiGouAllZT(danjubianhao,rukudanID);
    }
	/**
	 * 根据BillID，查询据收单明细
	 * @param BillID
	 * @param tableName
	 * @return
	 */
	public static List<Record> getJuShouDetailsById(String BillID, String tableName) {
		String sql = "select * from " + tableName + " where BillID = ?";
		List<Record> list = Db.find(sql, BillID);
		return list;
	}
	
	
	/**
	 * 回写采购订明细数量和状态
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public static void getCaiGouById(BigDecimal shuliang,String rukuID) {
		String sql = "UPDATE xyy_wms_bill_caigoudingdan_details set zhuangtai = (CASE WHEN (shuliang=wmsshuliang+?) THEN 1 ELSE 0 END), wmsshuliang = wmsshuliang+? where BillDtlID = ?";
		Db.update(sql,shuliang,shuliang,rukuID);
		logger.info("采购订单回写成功，采购商品的BillDtlID："+rukuID);
//		return lists;
	}
	
	/**
	 * 回写采购订单状态
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public static void updateCaiGouAllZT(String danjubianhao,String billID) {
		String sql = "SELECT COUNT(*) tiaomu FROM xyy_wms_bill_caigoudingdan_details WHERE BillID = ?";
		String sql2 = "SELECT COUNT(*) zhuangtaiShu FROM xyy_wms_bill_caigoudingdan_details WHERE zhuangtai = 1 AND BillID = ?";
		Record countTiMu = Db.findFirst(sql,billID);
		Record zhuangtaiTiMu = Db.findFirst(sql2,billID);
		Long counthuizong = countTiMu.get("tiaomu");
		Long zhuangtaiShu = zhuangtaiTiMu.get("zhuangtaiShu");
		int huizongZhuangtai = 0;
		if(counthuizong==zhuangtaiShu){
			huizongZhuangtai = 1;
		}else{
			huizongZhuangtai = 0;
		}
		String sqlCaiGou="update xyy_wms_bill_caigoudingdan set zhuangtai =? where danjubianhao=? and BillID = ?";
//			Db.update(sqlCaiGou,huizongZhuangtai,danjubianhao);
		EDB.update("xyy_wms_bill_caigoudingdan", sqlCaiGou,huizongZhuangtai,danjubianhao,billID);
		logger.info("采购订单编号："+danjubianhao+"采购订单状态："+ huizongZhuangtai);
		}
	
	
	/**
	 * 商品批号是否存在
	 * @param goodsId
	 * @param shangpinpihao
	 * @return
	 */
	public static boolean getShangPinPHById(String shangpinpihao , String shangpinbianhao) {
		boolean flag = false;
		String sql = "select * from xyy_wms_dic_shangpinpihao where pihao = ? and goodsId in (select goodsId from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?)";
		List<Record> list = Db.find(sql, shangpinpihao,shangpinbianhao);
		if(list.size()>0){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 更新商品批号库存
	 * @param danjubianhao
	 * @param shangpinbianhao
	 * @param rukushuliang
	 */
	public static void updateKuCun(String pihao,String yewubianhao,String shangpinbianhao,
			String orgId, BigDecimal shuliang,String danjubianhao) {
		//商品对象
		String spObj = "select goodsId from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?";
		Record shangpinObj = Db.findFirst(spObj, shangpinbianhao);
		String goodsid = shangpinObj.get("goodsId");
		
		//批号对象
		String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId=? limit 1";
		Record pihaoObj = Db.findFirst(pihaoSql, pihao ,goodsid);
		String pihaoId = pihaoObj.get("pihaoId");
		
		String cangkuId = "";
		String huoweiId = "";
		int zuoyeleixing = 1;
		KuncunParameter paras = new KuncunParameter(orgId,cangkuId,goodsid,pihaoId,
				huoweiId,yewubianhao,danjubianhao,zuoyeleixing,shuliang);
		KucuncalcService.kcCalc.insertKCRecord(paras);
		logger.info("商品批号库存更新成功，商品编号："+shangpinbianhao+ "批号:"+pihao);
	}
		
		/**
		 * 更新商品总库存
		 * @param danjubianhao
		 * @param shangpinbianhao
		 * @param rukushuliang
		 */
		public static void updateZongKuCun(String yewubianhao,String shangpinbianhao,
				String orgId, BigDecimal shuliang,String danjubianhao) {
			//商品对象
			String spObj = "select goodsId from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?";
			Record shangpinObj = Db.findFirst(spObj, shangpinbianhao);
			String goodsid = shangpinObj.get("goodsId");
			
					
			String cangkuId = "";
			String huoweiId = "";
			String pihaoId = "";
			int zuoyeleixing = 1;
			KuncunParameter paras = new KuncunParameter(orgId,cangkuId,goodsid,pihaoId,
					huoweiId,yewubianhao,danjubianhao,zuoyeleixing,shuliang);
			KucuncalcService.kcCalc.insertKCRecord(paras);
			logger.info("商品总库存库存更新成功，商品编号："+shangpinbianhao+ "数量:"+shuliang);
	}

}
