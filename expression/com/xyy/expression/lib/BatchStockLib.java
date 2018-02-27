package com.xyy.expression.lib;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.expression.Context;
import com.xyy.expression.ILib;
import com.xyy.expression.OperatorData;

/**
 * 商品批号库存-函数库
 * 
 * @author SKY
 *
 */
public class BatchStockLib implements ILib {

	public BatchStockLib() {

	}

	private static Map<String, Integer> functionMaps = new HashMap<String, Integer>();
	static {
		functionMaps.put("getKunCunShuLiang", 1);
		functionMaps.put("getZongKunCunShuLiang", 2);
		functionMaps.put("getWxsl", 3);
		functionMaps.put("getYxsl", 4);

	}

	@Override
	public int getMethodID(String name) {
		if (functionMaps.containsKey(name)) {
			return functionMaps.get(name);
		}
		return -1;
	}

	@Override
	public Object call(Context ctx, Stack<OperatorData> para, int methodID) {
		switch (methodID) {
		case 1:
			return this.getKunCunShuLiang(para);
		case 2:
			return this.getZongKunCunShuLiang(para);
		case 3:
			return this.getWxsl(para);
		case 4:
			return this.getYxsl(para);
		}
		return null;
	}

	/**
	 * 查商品
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findGoods(String shangpinbianhao) {
		String sql = "select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = ?";
		Record record = Db.findFirst(sql, shangpinbianhao);
		return record;
	}

	/**
	 * 查商品批号
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findBatchByName(String pihao, String goodsId) {
		String sql = "select * from xyy_erp_dic_shangpinpihao where pihao = ? and goodsId = ?";
		Record record = Db.findFirst(sql, pihao, goodsId);
		return record;
	}

	/**
	 * 查商品批号库存
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findBatchStock(String pihaoId, String shangpinId, int kucunzhuangtai) {
		String sql = "select * from xyy_erp_bill_shangpinpihaokucun where pihaoId = ? and shangpinId = ? and kucunzhuangtai=?";
		Record record = Db.findFirst(sql, pihaoId, shangpinId, kucunzhuangtai);
		return record;
	}

	/**
	 * 查商品批号的总库存
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findTotalBatchStock(String pihaoId, String shangpinId) {
		String sql = "select s.*,SUM(s.kucunshuliang) AS zongkucunshuliang from xyy_erp_bill_shangpinpihaokucun s where kucunzhuangtai = 1 and pihaoId = ? and shangpinId = ? ";
		Record record = Db.findFirst(sql, pihaoId, shangpinId);
		return record;
	}

	/**
	 * 库存数量
	 * 
	 * @param para
	 * @return
	 */
	private String getKunCunShuLiang(Stack<OperatorData> para) {
		//kucunzhuangtai,shangpinbianhao,pihao,shuliang
		Object pihao = para.get(0).value;
		Object shangpinbianhao = para.get(1).value;
		Object kucunzhuangtai = para.get(2).value;
		Record goods = findGoods(shangpinbianhao.toString());
		if (goods == null) {
			return "0";
		}
		String goodsId = goods.getStr("goodsid");
		if (goodsId == null) {
			return "0";
		}
		Record record = findBatchByName(pihao.toString(), goodsId);
		if (record == null) {
			return "0";
		}
		Record batchStock = findBatchStock(record.getStr("ID") == null ? null : record.getStr("ID"), goodsId,
				Integer.valueOf(kucunzhuangtai.toString()));
		if (batchStock == null) {
			return "0";
		}
		BigDecimal kucunshuliang = batchStock.getBigDecimal("kucunshuliang") == null ? BigDecimal.ZERO
				: batchStock.getBigDecimal("kucunshuliang");
		return String.valueOf(kucunshuliang);
	}

	/**
	 * 商品库存总数量
	 * 
	 * @param para
	 * @return
	 */
	private String getZongKunCunShuLiang(Stack<OperatorData> para) {
		Object pihao = para.get(0).value;
		Object shangpinbianhao = para.get(1).value;
		Record goods = findGoods(shangpinbianhao.toString());
		if (goods == null) {
			return "0";
		}
		String goodsId = goods.getStr("goodsid");
		if (goodsId == null) {
			return "0";
		}
		Record record = findBatchByName(pihao.toString(), goodsId);
		if (record == null) {
			return "0";
		}
		Record batchStock = findTotalBatchStock(record.getStr("ID") == null ? null : record.getStr("ID"), goodsId);
		if (batchStock == null) {
			return "0";
		}
		BigDecimal kucunshuliang = batchStock.getBigDecimal("zongkucunshuliang") == null ? BigDecimal.ZERO
				: batchStock.getBigDecimal("zongkucunshuliang");
		return String.valueOf(kucunshuliang);
	}
	
	
	/**
	 * 未销数量
	 * 当库存数量>数量，未销数量=数量，已销数量=0；反之，未销数量= 库存数量，已销数量=数量-库存数量；
	 * @param para
	 * @return
	 */
	private String getWxsl(Stack<OperatorData> para) {
		//shuliang,kucunzhuangtai,shangpinbianhao,pihao
		Object pihao = para.get(0).value;
		Object shangpinbianhao = para.get(1).value;
		Object kucunzhuangtai = para.get(2).value;
		Object shuliang = para.get(3).value;
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		String goodsId = goods.getStr("goodsid");
		
		
		Record pRecord = findBatchByName(pihao.toString(), goodsId);
		if(pRecord==null){
			return null;
		}
		String pihaoId = pRecord.getStr("ID");
		Record record = findBatchStock(pihaoId, goodsId, (int)kucunzhuangtai);
		if(record == null){
			return null;
		}
		BigDecimal kucunshuliang = record.getBigDecimal("kucunshuliang") == null ? new BigDecimal("0")
				: record.getBigDecimal("kucunshuliang");
		
		if(kucunshuliang.compareTo((BigDecimal)shuliang)>0){
			return String.valueOf(shuliang);
		}else{
			return String.valueOf(kucunshuliang);
		}
	}
	
	
	/**
	 * 已销数量
	 * 当库存数量>数量，未销数量=数量，已销数量=0；反之，未销数量= 库存数量，已销数量=数量-库存数量；
	 * @param para
	 * @return
	 */
	private String getYxsl(Stack<OperatorData> para) {
		Object pihao = para.get(0).value;
		Object shangpinbianhao = para.get(1).value;
		Object kucunzhuangtai = para.get(2).value;
		Object shuliang = para.get(3).value;
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		String goodsId = goods.getStr("goodsid");
		Record pRecord = findBatchByName(pihao.toString(), goodsId);
		if(pRecord==null){
			return null;
		}
		String pihaoId = pRecord.getStr("ID");
		Record record = findBatchStock(pihaoId, goodsId, (int)kucunzhuangtai);
		if(record == null){
			return null;
		}
		BigDecimal kucunshuliang = record.getBigDecimal("kucunshuliang") == null ? BigDecimal.ZERO
				: record.getBigDecimal("kucunshuliang");
		if(kucunshuliang.compareTo((BigDecimal)shuliang)>0){
			return "0";
		}else{
			BigDecimal subtract = ((BigDecimal)shuliang).subtract(kucunshuliang);
			return String.valueOf(subtract);
		}
	}
	
	
	

}
