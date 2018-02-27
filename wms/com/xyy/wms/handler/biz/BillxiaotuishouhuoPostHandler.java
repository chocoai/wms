package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class BillxiaotuishouhuoPostHandler implements PostSaveEventListener {

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
        
       
        List<Map<String, BigDecimal>> listInfo = new ArrayList<>();
        for(Record r : body) {
        	//收货数量
        	BigDecimal shuliang = r.getBigDecimal("shuliang");
        	String rukuID = r.getStr("rukuID");
        	//商品编号
        	String shangpinbianhao = r.getStr("shangpinbianhao");
        	String pihao = r.getStr("pihao");
      	
        	//回写销退开票单明细的数量和状态
        	getxtById(rukuID,shangpinbianhao,shuliang);
        	
        	//如果商品为新批号时，更新商品批号表xyy_wms_dic_shangpinpihao
        	boolean falg = getShangPinPHById(pihao,shangpinbianhao);
        	if(!falg){
        		Record sppihao = new Record();
        		
        		String pihaoId = "PH" + PullWMSDataUtil.orderno2();//批号id
        		//商品对象
        		String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
        		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
        		String goodsId = shangpinObj.getStr("goodsid");
        		sppihao.set("ID", UUIDUtil.newUUID());
        		sppihao.set("shengchanriqi", r.get("shengchanriqi")); //生产日期
        		sppihao.set("youxiaoqizhi", r.get("youxiaoqizhi"));  //有效期至
        		sppihao.set("pihao", pihao); //批号
        		sppihao.set("goodsId", goodsId); //商品id
        		sppihao.set("pihaoId", pihaoId); 
        		Db.save("xyy_wms_dic_shangpinpihao",sppihao);
        		logger.info("更新商品批号："+pihao);
        	}
        	
        }
        //回写销退开票单状态
    	updateCaiGouAllZT(danjubianhao,rukudanID);
        //生成拒收单据头信息
//    	 List<Record> list = getJuShouDetailsById(billID,"xyy_wms_bill_rukujushou");
//         List<Record> listdetals = getJuShouDetailsById(billID,"xyy_wms_bill_rukujushou_details");
//        if(!listdetals.isEmpty()){
//        	if(list.isEmpty()){
//            	jushouruku = new Record();
//            	//订单编号
//            	String dingdanbianhao = record.getStr("dingdanbianhao");
//            	//订单类型
//        		String dingdanleixing = record.getStr("dingdanleixing");
//        		//单位编号
//        		String danweibianhao = record.getStr("danweibianhao");
//        		//单位名称
//        		String danweimingcheng = record.getStr("danweimingcheng");
//        		//库房
//        		String kufang = record.getStr("kufang");
//        		//收货员
//        		String shouhuoyuan = record.getStr("shouhuoyuan");
//        		
//        		jushouruku.set("billID", billID);
//        		jushouruku.set("dingdanbianhao", dingdanbianhao);
//        		jushouruku.set("dingdanleixing", dingdanleixing);
//        		jushouruku.set("danweibianhao", danweibianhao);
//        		jushouruku.set("danweimingcheng", danweimingcheng);
//        		jushouruku.set("kufang", kufang);
//        		jushouruku.set("shouhuoyuan", shouhuoyuan);
//        		
//        		if(jushouruku != null) {
//        			EDB.save("xyy_wms_bill_rukujushou", jushouruku);
//        		}
//        		logger.info("拒收单据生成成功，拒收汇总："+dingdanbianhao);
//        		
//            }
//        }
//         
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
	 * 回写消退细数量和状态
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public static void getxtById(String rukuID,String shangpinbianhao, BigDecimal shuliang) {
		String sql = "UPDATE xyy_wms_bill_xiaoshoutuihuidan_details set zhuangtai = (CASE WHEN (tuihuoshuliang=wmsshuliang+?) THEN 1 ELSE 0 END), wmsshuliang = wmsshuliang+? where BillDtlID = ?";
		Db.update(sql,shuliang,shuliang,rukuID);
		logger.info("销退开票单回写成功回写成功，商品编号："+shangpinbianhao);
	}
	
	/**
	 * 回写销退订单状态
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public static void updateCaiGouAllZT(String danjubianhao,String billID) {
		String sql = "SELECT COUNT(*) tiaomu FROM xyy_wms_bill_xiaoshoutuihuidan_details WHERE BillID = ?";
		String sql2 = "SELECT COUNT(*) zhuangtaiShu FROM xyy_wms_bill_xiaoshoutuihuidan_details WHERE zhuangtai = 1 AND BillID = ?";
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
		String sqlCaiGou="update xyy_wms_bill_xiaoshoutuihuidan set zhuangtai =? where danjubianhao=? and BillID = ?";
		EDB.update("xyy_wms_bill_caigoudingdan", sqlCaiGou,huizongZhuangtai,danjubianhao,billID);
		logger.info("销售退回订单编号:"+danjubianhao+"采购订单状态："+ huizongZhuangtai);
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
//	public static void updateKuCun(String shangpinbianhao, String pihao,BigDecimal rukushuliang) {
//		//商品对象
//		String spObj = "select goodsId from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?";
//		Record shangpinObj = Db.findFirst(spObj, shangpinbianhao);
//		String shangpinId = shangpinObj.get("goodsId");
//		
//		//批号对象
//		String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE goodsId = ? and pihao=? limit 1";
//		Record pihaoObj = Db.findFirst(pihaoSql,shangpinId, pihao);
//		String pihaoId = pihaoObj.get("pihaoId");
//		
//		String sql = "select * from xyy_wms_bill_shangpinpihaokucun where shangpinId = ? and pihaoId = ?";
//		Record list = Db.findFirst(sql,shangpinId,pihaoId);
//		Record r = new Record();
//		if(list!=null){
//			//库存中已经有的数量
//			BigDecimal sum = list.getBigDecimal("rukuyuzhan");
//			//更新之后的数量
//			BigDecimal rukuyuzhan = sum.add(rukushuliang);
//			String sql2 = "update xyy_wms_bill_shangpinpihaokucun set rukuyuzhan = ? where shangpinId = ? and pihaoId = ?";
////			Db.update(sql2,rukuyuzhan,shangpinId,pihaoId);
//			EDB.update("xyy_wms_bill_shangpinpihaokucun", sql2,rukuyuzhan,shangpinId,pihaoId);
//		} else{
//			r.set("ID", UUIDUtil.newUUID());
//			r.set("shangpinId", shangpinId);
//			r.set("huozhuId", "0001");
//			r.set("cangkuId", "394b2a54e5e04c098a117550d249a267");//东西湖仓库id，以后再改
//			r.set("pihaoId", pihaoId);
//			r.set("rukuyuzhan", rukushuliang);
//			Db.save("xyy_wms_bill_shangpinpihaokucun", r);
//		}
//		logger.info("商品批号库存更新成功，商品编号："+shangpinbianhao+ "批号:"+pihao);
//	}
//		
//		/**
//		 * 更新商品总库存
//		 * @param danjubianhao
//		 * @param shangpinbianhao
//		 * @param rukushuliang
//		 */
//		public static void updateZongKuCun(String shangpinbianhao, BigDecimal rukushuliang) {
//			//商品对象
//			String spObj = "select goodsId from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?";
//			Record shangpinObj = Db.findFirst(spObj, shangpinbianhao);
//			String shangpinId = shangpinObj.get("goodsId");
//			String sql = "select * from xyy_wms_bill_shangpinzongkucun where shangpinId = ? ";
//			Record list = Db.findFirst(sql,shangpinId);
//			Record r = new Record();
//			if(list!=null){
//				//库存中已经有的数量
//				BigDecimal sum = list.getBigDecimal("rukuyuzhan");
//				//更新之后的数量
//				BigDecimal rukuyuzhan = sum.add(rukushuliang);
//				String sql2 = "update xyy_wms_bill_shangpinzongkucun set rukuyuzhan = ? where shangpinId = ?";
//				EDB.update("xyy_wms_bill_shangpinzongkucun", sql2,rukuyuzhan,shangpinId);
////				Db.update(sql2,rukuyuzhan,shangpinId);
//			} else{
//				r.set("ID", UUIDUtil.newUUID());
//				r.set("huozhuId", "0001");
//				r.set("cangkuId", "394b2a54e5e04c098a117550d249a267");//东西湖仓库id，以后再改
//				r.set("rukuyuzhan", rukushuliang);
//				r.set("shangpinId", shangpinId);
//				EDB.save("xyy_wms_bill_shangpinzongkucun", r);
//			}
//			logger.info("商品总库存库存更新成功，商品编号："+shangpinbianhao+ "数量:"+rukushuliang);
//	}

}