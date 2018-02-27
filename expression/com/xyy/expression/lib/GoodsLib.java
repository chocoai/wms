package com.xyy.expression.lib;

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
 * 商品-函数库
 * @author SKY
 *
 */
public class GoodsLib implements ILib {

	public GoodsLib() {

	}

	private static Map<String, Integer> functionMaps = new HashMap<String, Integer>();
	static {
		functionMaps.put("getJinxiangshuilv", 1);
		

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
			return this.getJinxiangshuilv(para);
		}
		return null;
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
	 * 进项税率
	 * @param para
	 * @return
	 */
	private String getJinxiangshuilv(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object shangpinbianhao = pop.value;
		Record goods = findGoods(shangpinbianhao.toString());
		if(goods == null){
			return null;
		}
		return goods.getBigDecimal("jinxiangshuilv").toString();
	}
	


}
