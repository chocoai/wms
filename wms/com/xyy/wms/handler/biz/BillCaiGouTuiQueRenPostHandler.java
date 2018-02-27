package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.biz.ZhangYeInsertService;
import com.xyy.wms.outbound.util.KuncunParameter;
import com.xyy.wms.outbound.util.ZhangYeParameter;

public class BillCaiGouTuiQueRenPostHandler implements PostSaveEventListener {

	private static Logger logger = Logger.getLogger(BillCaiGouTuiQueRenPostHandler.class);

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

		String danjubianhao = record.getStr("dingdanbianhao");
		for (Record r : body) {
			String shangpinbianhao = r.getStr("shangpinbianhao");
			String pihao = r.getStr("pihao");
			String huoweibianhao = r.getStr("huoweibianhao");
			BigDecimal rukushuliang = r.getBigDecimal("shuliang");
			getCaiGouTuiChuiQR(danjubianhao, shangpinbianhao, rukushuliang);
			String yewubianhao = r.getStr("caigoutuichuiID"); // 是采购退出单明细的主键
			// 回写批号货位库存
			updatePhhwKun(shangpinbianhao,pihao,huoweibianhao,rukushuliang,yewubianhao,danjubianhao);
			// 更新总库存
			
//			updateZKC(shangpinbianhao,rukushuliang);
			
			// 更新账页
		}

		// 回写采购退出单的数量和状态
		updateCaiGouTuiChuAllZT(danjubianhao);
		
		insertZhangye(record,body);
	}

	
	public static void updateZKC(String shangpinbianhao , BigDecimal rukushuliang){
		
		String spObj = "select * from xyy_wms_dic_shangpinziliao where shangpinbianhao = ?";
		Record record = Db.findFirst(spObj,shangpinbianhao);
		String goodsid = record.getStr("goodsid");
		
		
		String zObj = "select * from xyy_wms_bill_shangpinzongkucun where shangpinId = ?";
		Record Zrecord = Db.findFirst(zObj,goodsid);
		// 总预扣
		BigDecimal chukuyukou = Zrecord.getBigDecimal("chukuyukou");
		// 总库存
		BigDecimal kucunshuliang = Zrecord.getBigDecimal("kucunshuliang");
		String Id =  Zrecord.getStr("ID");
		
		// 总预扣 - 出库数量
		BigDecimal zchukuyukou = chukuyukou.subtract(rukushuliang);
		// 总库存 - 出库数量
		BigDecimal zkucunshuliang = kucunshuliang.subtract(rukushuliang);
		String updateSql = "update xyy_wms_bill_shangpinzongkucun set chukuyukou = ? and zkucunshuliang = ? and where ID=?";
		Db.update(updateSql, zchukuyukou, zkucunshuliang , Id);
		
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
		KucuncalcService.kcCalc.deleteKCRecord(paras,new Object[] {2});
		
	}
	
	
	public static void getCaiGouTuiChuiQR(String danjubianhao, String shangpinbianhao, BigDecimal rukushuliang) {
		String sql = "select a.BillDtlID,a.wmsshuliang,a.zhuangtai,a.shuliang from xyy_wms_bill_caigoutuichu_details a inner join "
				+ "xyy_wms_bill_caigoutuichu b  on a.BillID = b.BillID and b.dingdanbianhao = ?"
				+ " and a.shangpinbianhao=?";
		List<Record> list = Db.find(sql, danjubianhao, shangpinbianhao);
		// 是否收货完成收货完成，0没完成，1完成
		int zhuangTai = 0;
		if (null != list && list.size() > 0) {
			for (Record record : list) {
				String BillDtlID = record.get("BillDtlID");
				BigDecimal caiGrukushuliang = record.getBigDecimal("wmsshuliang");
				String sqlCaiGouDetils = "update xyy_wms_bill_caigoutuichu_details set wmsshuliang =? where BillDtlID = ?";
				BigDecimal zongruku = caiGrukushuliang.add(rukushuliang);
				Db.update(sqlCaiGouDetils, zongruku, BillDtlID);
				List<Record> Alllist = Db.find(sql, danjubianhao, shangpinbianhao);

				BigDecimal cshuliang = Alllist.get(0).get("shuliang");
				if (zongruku.equals(cshuliang)) {
					zhuangTai = 1;
				} else {
					zhuangTai = 0;
				}
				String sqlZhai = "update xyy_wms_bill_caigoutuichu_details set zhuangtai =?  where BillDtlID = ?";
				Db.update(sqlZhai, zhuangTai, BillDtlID);
			}
		}
		logger.info("采购订单回写成功，采购商品编号：" + shangpinbianhao);
	}

	public static void updateCaiGouTuiChuAllZT(String danjubianhao) {
		String count = "select count(*) zongshu from xyy_wms_bill_caigoutuichu_details a inner join xyy_wms_bill_caigoutuichu b "
				+ "on a.BillID = b.BillID and b.dingdanbianhao = ? where a.zhuangTai=1";
		String countHuizong = "select count(*) zongshu from xyy_wms_bill_caigoutuichu_details a inner join xyy_wms_bill_caigoutuichu b "
				+ "on a.BillID = b.BillID and b.dingdanbianhao = ?";
		Record record = Db.findFirst(count, danjubianhao);
		Record recordHuizong = Db.findFirst(countHuizong, danjubianhao);
		Long countmingxi = record.get("zongshu");
		Long counthuizong = recordHuizong.get("zongshu");
		int huizongZhuangtai = 0;
		if (countmingxi == counthuizong) {
			huizongZhuangtai = 1;
		} else {
			huizongZhuangtai = 0;
		}
		String sqlCaiGou = "update xyy_wms_bill_caigoutuichu set zhuangtai =? where dingdanbianhao=?";
		Db.update(sqlCaiGou, huizongZhuangtai, danjubianhao);
		logger.info("采购订单编号：" + danjubianhao + "采购订单状态：" + huizongZhuangtai);
	}
	
	public static void insertZhangye(Record record,List<Record> body){
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
			String huoweibianhao = r.getStr("huoweibianhao");
			String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao = ?";
			Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhao);
			
			String cangkuID = huoweiObj.getStr("cangkuID");
			
			String huoweiId = huoweiObj.getStr("ID");
			
			
			BigDecimal rukushuliang = new BigDecimal("0.00");
			
			ZhangYeParameter pams =  new ZhangYeParameter();
			pams.setOrgId(r.getStr("orgId"));
			pams.setOrgCode(r.getStr("orgCode"));
			pams.setDanjubianhao(dingdanbianhao);
//			pams.setZhiliangzhuangtai(r.getInt("yanshoupingding"));
			pams.setCangkuId(cangkuID);
			pams.setHuoweiId(huoweiId);
			pams.setPihaoId(pihaoId);
			pams.setHuozhuId("0001");
			pams.setChukushuliang(shuliang);
			pams.setRukushuliang(rukushuliang);
			pams.setShangpinId(goodsId);
			pams.setZhidanren(record.getStr("shangjiayuan"));
			pams.setZhidanriqi(record.get("dingdanriqi"));
			list.add(pams);
		}
		ZhangYeInsertService.zhangyeInsert.updateZhangye(list, 2);
	}
}
