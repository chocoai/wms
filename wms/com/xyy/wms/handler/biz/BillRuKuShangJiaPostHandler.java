package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.StringUtil;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.biz.ZhangYeInsertService;
import com.xyy.wms.outbound.util.KuncunParameter;
import com.xyy.wms.outbound.util.ZhangYeParameter;
                                                                                                                                                                            
public class BillRuKuShangJiaPostHandler implements PostSaveEventListener {

	private static final String String = null;
	private static Logger logger = Logger.getLogger(BillRuKuShouHuoPostHandler.class);

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey())) {
			return;
		}
		// 单据头数据
		Record record = getHead(dsi);
		// 单据体数据
		List<Record> body = getBody(dsi);
		
		String shangjiaID = record.getStr("shangjiaID");

		String dingdanbianhao = record.getStr("dingdanbianhao");
		List<Map<String, BigDecimal>> listinfo = new ArrayList<>();
		for (Record r : body) {
			// 数量
			BigDecimal shuliang = r.getBigDecimal("shuliang");
			String shangjiadetailid = r.getStr("shangjiadetailID");
			// 商品编号
			Date shengchanriqi = r.getDate("shengchanriqi");
			Date youxiaoqizhi = r.getDate("youxiaoqizhi");
			String shangpinbianhao = r.getStr("shangpinbianhao");
			String pihao = r.getStr("pihao");
			String huowei = r.getStr("shijihuowei");
			String jhhuowei = r.getStr("jihuahuowei");
			String orgId = r.getStr("orgId");
			String orgCode = r.getStr("orgCode");
			String yewubianhao = r.getStr("shangjiadetailID");
			int zhiliangzhuangtai = r.getInt("yanshoupingding");
			
			BigDecimal shangjiashuliang=r.getBigDecimal("shuliang");
			// 回写入库上架单数量和明细
			List list = getShangJiaById(shangjiadetailid, shangjiashuliang);
			// 更新商品总库存的入库预占xyy_wms_bill_shangpinzongkucun
			updateZongKuCun(shangpinbianhao,shuliang,orgId,orgCode,yewubianhao,dingdanbianhao);
			// 更新商品批号库存
			updateKuCun(shangpinbianhao, pihao, shuliang,orgId,orgCode,yewubianhao,dingdanbianhao);

			// 更新商品批号货位库存
			updateHouWeiZongKuCun(shangpinbianhao,huowei,jhhuowei, pihao, shuliang,zhiliangzhuangtai,shengchanriqi,youxiaoqizhi,orgId,orgCode,yewubianhao,dingdanbianhao);
			listinfo.addAll(list);
		}
		updaSJAllZT(shangjiaID); //采购订单状态
		insertZhangye(record,body); //账页
	}

	/**
	 * 修改上架单明细数量和状态
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public static List<Map<String, BigDecimal>> getShangJiaById(String shangjiadetailid , BigDecimal shangjiashuliang) {
		String sqlz = "UPDATE xyy_wms_bill_rukushangjiadan_details set zhuangtai = (CASE WHEN (shuliang=wmsshuliang+?) THEN 1 ELSE 0 END), wmsshuliang = wmsshuliang+? where BillDtlID = ?";
//		Db.update("xyy_wms_bill_rukushangjiadan_details", sqlz,shangjiashuliang,shangjiashuliang, shangjiadetailid);
		Db.update(sqlz,shangjiashuliang,shangjiashuliang, shangjiadetailid);
		List<Map<String, BigDecimal>> lists = new ArrayList<>();
		Map<String, BigDecimal> map = new HashMap<>();
		map.put(shangjiadetailid, shangjiashuliang);
		lists.add(map);
		logger.info("上架单回写成功，采购商品编号："+shangjiashuliang);
		return lists;
	}
	
	/**
	 * 回写上架单状态
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public static void updaSJAllZT(String shangjiaID) {
//		int count = 0;
//		for(int a = 0 ; a< list.size() ; a++){
//			Map<String, BigDecimal> map = list.get(a);
//			for(Entry<String, BigDecimal> vo : map.entrySet()){
//				String key = vo.getKey();
//				BigDecimal value = vo.getValue();
//				String sql = "select * from xyy_wms_bill_rukushangjiadan_details where BillID = ?";
//				List<Record> recordHuizong = Db.find(sql,shangjiaID);
//					for(Record r:recordHuizong){
//						BigDecimal shuliang = r.get("shuliang");
//						BigDecimal wmsshuliang = r.getBigDecimal("wmsshuliang");
//						if(wmsshuliang.compareTo(shuliang)==0){
//							count++;
//						}
//						if(shuliang.compareTo(value)==0){
//							count++;
//						}
//					}
//				}
//			}
//			String countHuizong = "select count(*) zongshu from xyy_wms_bill_rukushangjiadan_details a inner join xyy_wms_bill_rukushangjiadan b "
//					+ "on a.BillID = b.BillID and a.BillID = ?";
//			Record recordHuizong = Db.findFirst(countHuizong,shangjiaID);
//			Long counthuizong = recordHuizong.get("zongshu");
//			int zhuangtai = 0;
//			if(count==counthuizong){
//				zhuangtai = 1;
//			}else{
//				zhuangtai = 0;
//			}
//			String sqlCaiGou="update xyy_wms_bill_rukushangjiadan set zhuangtai =? where BillID=?";
//			EDB.update("xyy_wms_bill_rukushangjiadan", sqlCaiGou,zhuangtai,shangjiaID);
//			logger.info("采购订单编号："+shangjiaID+"采购订单状态："+ zhuangtai);
		
		String sql = "SELECT COUNT(*) tiaomu FROM xyy_wms_bill_rukushangjiadan_details WHERE BillID = ?";
		String sql2 = "SELECT COUNT(*) zhuangtaiShu FROM xyy_wms_bill_rukushangjiadan_details WHERE zhuangtai = 1 AND BillID = ?";
		Record countTiMu = Db.findFirst(sql,shangjiaID);
		Record zhuangtaiTiMu = Db.findFirst(sql2,shangjiaID);
		Long counthuizong = countTiMu.get("tiaomu");
		Long zhuangtaiShu = zhuangtaiTiMu.get("zhuangtaiShu");
		int huizongZhuangtai = 0;
		if(counthuizong==zhuangtaiShu){
			huizongZhuangtai = 1;
		}else{
			huizongZhuangtai = 0;
		}
		String sqlCaiGou="update xyy_wms_bill_rukushangjiadan set zhuangtai =? where BillID = ?";
		EDB.update("xyy_wms_bill_caigoudingdan", sqlCaiGou,huizongZhuangtai,shangjiaID);
		logger.info("采购订单编号："+shangjiaID+"采购订单状态："+ huizongZhuangtai);

		}
	
	
	/**
	 * 更新商品总库存
	 * 
	 * @param danjubianhao
	 * @param shangpinbianhao
	 * @param rukushuliang
	 */
	public static void updateZongKuCun(String shangpinbianhao, BigDecimal shuliang ,String orgId,String orgCode,String yewubianhao,String danjubianhao) {
		// 商品对象
		String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
		// 商品总库存对象
		String goodsid = shangpinObj.get("goodsid");
		String sql = "select * from xyy_wms_bill_shangpinzongkucun where shangpinId = ? ";
		Record kuCunList = Db.findFirst(sql, goodsid);
		// 如果是新商品重新生成一条记录
		if(null!=kuCunList){
			// 回写入库库存数量
			BigDecimal kucunshuliang = kuCunList.getBigDecimal("kucunshuliang");//库存中已经有的数量
			BigDecimal addSum = kucunshuliang.add(shuliang); //已经有的数量+上架的数量
			String sql2 = "update xyy_wms_bill_shangpinzongkucun set kucunshuliang = kucunshuliang+? where shangpinId = ?";
			EDB.update("xyy_wms_bill_shangpinzongkucun", sql2, shuliang, shangpinObj.get("goodsid"));
//			Db.update(sql2, rukuyuzhan, addSum, shangpinObj.get("goodsid"));
		}else{
			Record r = new Record();
			r.set("ID", UUIDUtil.newUUID());
			r.set("createTime", new Date());
			r.set("updateTime", new Date());
			r.set("status", 10);
			r.set("orgId", orgCode);
			r.set("rowID", UUIDUtil.newUUID());
			r.set("orgCode", orgCode);
			r.set("shangpinId", goodsid);
			r.set("huozhuId", "0001");
			r.set("cangkuId", "394b2a54e5e04c098a117550d249a267");//东西湖仓库id，以后再改
			r.set("kucunshuliang", shuliang);
			EDB.save("xyy_wms_bill_shangpinzongkucun", r);
		}

		logger.info("商品总库存库存更新成功，商品编号：" + shangpinbianhao + "数量:" + shuliang);
	}

	/**
	 * 更新商品批号库存
	 * 
	 * @param danjubianhao
	 * @param shangpinbianhao
	 * @param rukushuliang
	 */
	public static void updateKuCun(String shangpinbianhao, String pihao, BigDecimal shuliang,String orgId,String orgCode,
			String yewubianhao,String danjubianhao) {
		// 商品对象
		String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
		String goodsid = shangpinObj.get("goodsid");
		// 批号对象
		String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId= ? limit 1";
		Record pihaoObj = Db.findFirst(pihaoSql, pihao,goodsid);
		String pihaoId = pihaoObj.get("pihaoId");
		
		// 批号库存对象
		String sql = "select * from xyy_wms_bill_shangpinpihaokucun where shangpinId = ? and pihaoId = ? limit 1";
		Record pihaoList = Db.findFirst(sql, goodsid, pihaoId);
		if(null!=pihaoList){
			String sql2 = "update xyy_wms_bill_shangpinpihaokucun set kucunshuliang = kucunshuliang + ? where shangpinId = ? and pihaoId = ?";
			EDB.update("xyy_wms_bill_shangpinpihaokucun", sql2,  shuliang, shangpinObj.get("goodsid"), pihaoObj.get("pihaoId"));
		} else {
			Record r = new Record();
			r.set("ID", UUIDUtil.newUUID());
			r.set("createTime", new Date());
			r.set("updateTime", new Date());
			r.set("status", 10);
			r.set("rowID", UUIDUtil.newUUID());
			r.set("orgId", orgCode);
			r.set("orgCode", orgCode);
			r.set("shangpinId", goodsid);
			r.set("huozhuId", "0001");
			r.set("cangkuId", "394b2a54e5e04c098a117550d249a267");//东西湖仓库id，以后再改
			r.set("pihaoId", pihaoId);
			r.set("kucunshuliang", shuliang);
			EDB.save("xyy_wms_bill_shangpinpihaokucun", r);

			
		}
		
		logger.info("商品批号库存更新成功，商品编号：" + shangpinbianhao + "批号:" + pihaoObj.get("pihaoId"));
	}

	/**
	 * 更新商品批号货位库存,总库存,批号库存
	 * 
	 * @param danjubianhao
	 * @param shangpinbianhao
	 * @param rukushuliang
	 */
	public static void updateHouWeiZongKuCun(String shangpinbianhao,String huowei,String jhhuowei, String pihao, BigDecimal shuliang , int zhiliangzhuangtai ,
			Date shengchanriqi,Date youxiaoqizhi,String orgId,String orgCode,String yewubianhao,String danjubianhao) {
		Record r = new Record();
		// 商品对象
		String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
		String goodsid = shangpinObj.get("goodsid");
		// 实际货位对象
		Record huoweiObj = selectWHInfo(huowei);
		String kuquId = huoweiObj.getStr("kuquId");
		String cangkuID = huoweiObj.getStr("cangkuID");	
		String sjhuoweiId = huoweiObj.getStr("ID");
		// 批号对象
		String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId= ? limit 1";
		Record pihaoObj = Db.findFirst(pihaoSql, pihao,goodsid);
		String pihaoId = pihaoObj.getStr("pihaoId");
		
		// 批号货位库存
		String sqlWH = "select * from xyy_wms_bill_shangpinpihaohuoweikucun where shangpinId = ? and pihaoId=? and huoweiId=? limit 1";
		Record list = Db.findFirst(sqlWH, goodsid,pihaoId,sjhuoweiId);
		int zuoyeleixing = 1;
		// 减掉批号货位入库预占
		if(StringUtils.isNotEmpty(jhhuowei)){
			Record jhhuoweiObj = selectWHInfo(jhhuowei);
			String huoweiId = jhhuoweiObj.getStr("ID");
			String cangkuId = huoweiObj.getStr("cangkuID");	
			KuncunParameter paras = new KuncunParameter(orgId,cangkuId, goodsid, pihaoId,huoweiId,yewubianhao,danjubianhao,zuoyeleixing,shuliang);
			KucuncalcService.kcCalc.updateRkRecord(paras);
			// 如果预占等于0 就删除预占
			
			BigDecimal yuzhen = KucuncalcService.kcCalc.getKucunCalcOfshanpinPihaoHuowei(paras);
			if(yuzhen.compareTo(BigDecimal.ZERO)==0){
				StringBuffer sb=new StringBuffer();
				sb.append("delete from xyy_wms_bill_kucunyuzhanyukou  ");
				sb.append(" where orgId=?  and danjubianhao=? and goodsid=? and pihaoId =? and huoweiId=? and zuoyeleixing=1 ");
				sb.append("  and yewubianhao=?  ");
				Db.update(sb.toString(),paras.getOrgId(),paras.getDanjubianhao(),paras.getGoodsid(),
						paras.getPihaoId(),paras.getHuoweiId(),paras.getYewubianhao());

			}
			
		}
		if(null!=list){
			String sql2 = "update xyy_wms_bill_shangpinpihaohuoweikucun set kucunshuliang = kucunshuliang+? where shangpinId = ? and pihaoId=? and huoweiId=?";
			EDB.update("xyy_wms_bill_shangpinpihaohuoweikucun", sql2, shuliang, goodsid,pihaoId,sjhuoweiId);
		} else{
			r.set("ID", UUIDUtil.newUUID());
			r.set("createTime", new Date());
			r.set("updateTime", new Date());
			r.set("status", 10);
			r.set("rowID", UUIDUtil.newUUID());
			r.set("orgId", orgId);
			r.set("orgCode", orgCode);
			r.set("shangpinId", goodsid);
			r.set("huozhuId", "0001");
			r.set("cangkuId", cangkuID);
			r.set("pihaoId", pihaoId);
			r.set("shengchanriqi", shengchanriqi);
			r.set("youxiaoqizhi", youxiaoqizhi);
			r.set("huoweiId", sjhuoweiId);
			r.set("kucunshuliang", shuliang);
			r.set("kuquId", kuquId);
			r.set("zhiliangzhuangtai", zhiliangzhuangtai);
			EDB.save("xyy_wms_bill_shangpinpihaohuoweikucun", r);
		}

		logger.info("商品总库存库存更新成功，商品编号：" + shangpinObj.get("goodsid") + "数量:" + shuliang);
	}
	
	public static Record selectWHInfo(String huowei){
		String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
		Record huoweiObj = Db.findFirst(huoweiSql, huowei);
		return huoweiObj;
		
	}
	//更新【商品帐页表】，insert处理
	public static void insertZhangye(Record record,List<Record> body) {
		String dingdanbianhao = record.getStr("dingdanbianhao");
		List<ZhangYeParameter> list = new ArrayList<>();
		for(Record r : body){
			String shangpinbianhao = r.getStr("shangpinbianhao");
			String pihao = r.getStr("pihao");
			// 商品对象
			String spSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao = ?";
			Record spObj = Db.findFirst(spSql, shangpinbianhao);
			String goodsId = spObj.getStr("goodsid");
			// 批号对象
			String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao = ? and goodsId=?";
			Record pihaoObj = Db.findFirst(pihaoSql, pihao,goodsId);
			String pihaoId = pihaoObj.getStr("pihaoId");
			// 上架货位的数量
			BigDecimal shuliang = r.getBigDecimal("shuliang");
			// 货位对象
			String huoweibianhao = r.getStr("shijihuowei");
			String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao = ?";
			Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhao);
			//String kuquId = huoweiObj.getStr("kuquId");
			String cangkuID = huoweiObj.getStr("cangkuID");
			
			String huoweiId = huoweiObj.getStr("ID");
			
			// 出库数量 chukushuliang
			BigDecimal chukushuliang = new BigDecimal("0.00");
			// 账页对象
			ZhangYeParameter pams =  new ZhangYeParameter();
			pams.setOrgId(r.getStr("orgId"));
			pams.setOrgCode(r.getStr("orgCode"));
			pams.setDanjubianhao(dingdanbianhao);
			pams.setZhiliangzhuangtai(r.getInt("yanshoupingding"));
			pams.setCangkuId(cangkuID);
			pams.setHuoweiId(huoweiId);
			pams.setPihaoId(pihaoId);
			pams.setHuozhuId("0001");
			pams.setChukushuliang(chukushuliang);
			pams.setRukushuliang(shuliang);
			pams.setShangpinId(goodsId);
			pams.setZhidanren(record.getStr("shangjiayuan"));
			pams.setZhidanriqi(record.get("dingdanriqi"));
			list.add(pams);
		}
		ZhangYeInsertService.zhangyeInsert.updateZhangye(list, 1);
	}
		
}
