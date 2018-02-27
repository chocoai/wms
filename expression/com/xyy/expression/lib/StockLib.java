package com.xyy.expression.lib;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.expression.Context;
import com.xyy.expression.ILib;
import com.xyy.expression.OperatorData;

/**
 * 商品总库存-函数库
 * @author SKY
 *
 */
public class StockLib implements ILib {

	public StockLib() {

	}

	private static Map<String, Integer> functionMaps = new HashMap<String, Integer>();
	static {
		functionMaps.put("getHscbj", 1);
		functionMaps.put("getKchsje", 2);
		functionMaps.put("getZghsjj", 3);
		functionMaps.put("getZdhsjj", 4);
		functionMaps.put("getGysmc", 5);
		functionMaps.put("getYxsl", 6);
		functionMaps.put("getWxsl", 7);
		functionMaps.put("getZhhsjj", 8);
		functionMaps.put("getDbzsl", 9);
		functionMaps.put("getluxianmingcheng", 10);
		functionMaps.put("getluxianyouxianji", 11);

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
			return this.getHscbj(para);
		case 2:
			return this.getKchsje(para);
		case 3:
			return this.getZghsjj(para);
		case 4:
			return this.getZdhsjj(para);
		case 5:
			return this.getGysmc(para);
		case 6:
			return this.getYxsl(para);
		case 7:
			return this.getWxsl(para);
		case 8:
			return this.getZhhsjj(para);
		case 9:
			return this.getDbzsl(para);
		case 10:
			return this.getluxianmingcheng(para);
		case 11:
			return this.getluxianyouxianji(para);
	}
		return null;
	}
	
	
	private Object getluxianyouxianji(Stack<OperatorData> para) {

		OperatorData pop = para.pop();
		Object kehubianhao = pop.value;
		Record record=getKhxl(kehubianhao.toString());
		if(record!=null){
			return record.getInt("xlyxj");
		}
		return "";
	
	}

	private Record getKhxl(String kehubianhao){
		StringBuffer sb=new StringBuffer("select  t2.ID,t2.xianlubianhao,t2.xianlumingcheng,t2.xlyxj ");
		sb.append("  from xyy_wms_dic_kehuxianlu t1,xyy_wms_dic_peisongxianlu t2,xyy_wms_dic_kehuxianlu_list t3  where t1.xianluID = t2.ID and t1.ID=t3.ID  ");
		sb.append("  and t3.kehubianhao=? and t1.shifouhuodong=1 and t2.shifouhuodong=1 order by t2.xlyxj ");
		Record record=Db.findFirst(sb.toString(),kehubianhao);
		return record;
	}
	private String getluxianmingcheng(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object kehubianhao = pop.value;
		Record record=getKhxl(kehubianhao.toString());
		if(record!=null){
			return record.getStr("xianlumingcheng");
		}
		return "";
	}

	/**
	 * 查商品
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findGoods(String shangpinbianhao){
		String sql = "select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = '" + shangpinbianhao + "'";
		List<Record> record = Db.find(sql);
		if (record == null || record.size() != 1) {
			return null;
		}
		return record.get(0);
	}
	
	/**
	 * wms商品资料
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findWMSGoods(String shangpinbianhao){
		String sql = "select * from xyy_wms_dic_shangpinziliao where shangpinbianhao = '" + shangpinbianhao + "'";
		List<Record> record = Db.find(sql);
		if (record == null || record.size() != 1) {
			return null;
		}
		return record.get(0);
	}
	/**
	 * 大包装数量
	 * @param para
	 * @return
	 */
	private BigDecimal getDbzsl(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object shangpinbianhao = pop.value;
		Record record = findWMSGoods(shangpinbianhao.toString());
		if(record == null){
			return null;
		}
		return record.getBigDecimal("dbzsl") == null ? new BigDecimal("0")
				: record.getBigDecimal("dbzsl");
	}
	
	
	/**
	 * 查库存
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findStock(String shangpinId){
		String sql = "select * from xyy_erp_bill_shangpinzongkucun where shangpinId = '" + shangpinId + "'";
		List<Record> record = Db.find(sql);
		if (record == null || record.size() != 1) {
			return null;
		}
		return record.get(0);
	}
	
	/**
	 * 供应商
	 * @param shangpinbianhao
	 * @return
	 */
	private Record finduppliers(String suppliersid){
		String ssql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where suppliersid = '"+suppliersid+"'";
		List<Record> record = Db.find(ssql);
		if (record == null || record.size() != 1) {
			return null;
		}
		return record.get(0);
	}
	
	
	/**
	 * 核算成本价
	 * @param para
	 * @return
	 */
	private String getHscbj(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object shangpinbianhao = pop.value;
		
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		Record record = findStock(goods.getStr("goodsid"));
		if(record == null){
			return null;
		}
		return record.getBigDecimal("hscbj") == null ? new BigDecimal("0").toString()
				: record.getBigDecimal("hscbj").toString();
	}
	
	/**
	 * 库存含税金额
	 * @param para
	 * @return
	 */
	private String getKchsje(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object shangpinbianhao = pop.value;
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		Record record = findStock(goods.getStr("goodsid"));
		if(record == null){
			return null;
		}
		return record.getBigDecimal("kchsje") == null ? new BigDecimal("0").toString()
				: record.getBigDecimal("kchsje").toString();
	}
	
	/**
	 * 最高含税进价
	 * @param para
	 * @return
	 */
	private String getZghsjj(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object shangpinbianhao = pop.value;
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		Record record = findStock(goods.getStr("goodsid"));
		if(record == null){
			return null;
		}
		return record.getBigDecimal("zghsjj") == null ? new BigDecimal("0").toString()
				: record.getBigDecimal("zghsjj").toString();
	}
	
	/**
	 * 最低含税进价
	 * @param para
	 * @return
	 */
	private String getZdhsjj(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object shangpinbianhao = pop.value;
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		Record record = findStock(goods.getStr("goodsid"));
		if(record == null){
			return null;
		}
		return record.getBigDecimal("zdhsjj") == null ? new BigDecimal("0").toString()
				: record.getBigDecimal("zdhsjj").toString();
	}
	
	/**
	 * 最后含税进价
	 * @param para
	 * @return
	 */
	private String getZhhsjj(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object shangpinbianhao = pop.value;
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		Record record = findStock(goods.getStr("goodsid"));
		if(record == null){
			return null;
		}
		return record.getBigDecimal("zhhsjj") == null ? new BigDecimal("0").toString()
				: record.getBigDecimal("zhhsjj").toString();
	}
	
	/**
	 * 最后进货单位
	 * @param para
	 * @return
	 */
	private String getGysmc(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object shangpinbianhao = pop.value;	
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		Record stock = findStock(goods.getStr("goodsid"));
		if(stock == null){
			return null;
		}
		Record record = finduppliers(stock.getStr("zhjhId"));
		if(record == null){
			return null;
		}
		return record.getStr("gysmc");
	}
	
	
	/**
	 * 已销数量
	 * 当库存数量>数量，未销数量=数量，已销数量=0；反之，未销数量= 库存数量，已销数量=数量-库存数量；
	 * @param para
	 * @return
	 */
	private String getYxsl(Stack<OperatorData> para) {
		String shangpinbianhao = null;
		BigDecimal shuliang = BigDecimal.ZERO;
		Object param1 = para.get(0).value;
		Object param2 = para.get(1).value;
		if (param1 instanceof BigDecimal) {
			shuliang =(BigDecimal)param1;
		}else if (param1 instanceof String) {
			shangpinbianhao = param1.toString();
		} 
		if (param2 instanceof Integer) {
			shuliang = (BigDecimal)param1;
		}else if (param2 instanceof String) {
			shangpinbianhao = param2.toString();
		}
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		Record record = findStock(goods.getStr("goodsid"));
		if(record == null){
			return null;
		}
		BigDecimal kucunshuliang = record.getBigDecimal("kucunshuliang") == null ? BigDecimal.ZERO
				: record.getBigDecimal("kucunshuliang");
		if(kucunshuliang.compareTo(shuliang)>0){
			return "0";
		}else{
			BigDecimal subtract = shuliang.subtract(kucunshuliang);
			return String.valueOf(subtract);
		}
	}
	
	
	/**
	 * 未销数量
	 * 当库存数量>数量，未销数量=数量，已销数量=0；反之，未销数量= 库存数量，已销数量=数量-库存数量；
	 * @param para
	 * @return
	 */
	private String getWxsl(Stack<OperatorData> para) {
		String shangpinbianhao = null;
		BigDecimal shuliang = BigDecimal.ZERO;
		Object param1 = para.get(0).value;
		Object param2 = para.get(1).value;
		if (param1 instanceof Integer) {
			shuliang = (BigDecimal) param1;
		}else if (param1 instanceof String) {
			shangpinbianhao = param1.toString();
		} 
		if (param2 instanceof Integer) {
			shuliang = (BigDecimal) param2;
		}else if (param2 instanceof String) {
			shangpinbianhao = param2.toString();
		}
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		Record record = findStock(goods.getStr("goodsid"));
		if(record == null){
			return null;
		}
		BigDecimal kucunshuliang = record.getBigDecimal("kucunshuliang") == null ? new BigDecimal("0")
				: record.getBigDecimal("kucunshuliang");
		
		if(kucunshuliang.compareTo(shuliang)>1){
			return String.valueOf(shuliang);
		}else{
			return String.valueOf(kucunshuliang);
		}
	}
	
	


}
