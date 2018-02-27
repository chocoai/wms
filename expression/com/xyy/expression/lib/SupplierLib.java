package com.xyy.expression.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.expression.Context;
import com.xyy.expression.ILib;
import com.xyy.expression.OperatorData;

/**
 * 供应商-函数库
 * @author SKY
 *
 */
public class SupplierLib implements ILib {

	public SupplierLib() {

	}

	private static Map<String, Integer> functionMaps = new HashMap<String, Integer>();
	static {
		functionMaps.put("getLianXiRen", 1);
		functionMaps.put("getDianHua", 2);
		functionMaps.put("getDizhi", 3);
		

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
			return this.getLianXiRen(para);
		case 2:
			return this.getDianHua(para);
		case 3:
			return this.getDizhi(para);
		}
		return null;
	}
	
	/**
	 * 查供应商
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findSupplier(String gysbh){
		String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where gysbh = '" + gysbh + "'";
		Record record = Db.findFirst(sql);
		return record;
	}
	
	/**
	 * 联系人
	 * @param para
	 * @return
	 */
	private String getLianXiRen(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object gysbm = pop.value;
		Record supplier = findSupplier(gysbm.toString());
		if(supplier == null){
			return null;
		}
		return supplier.getStr("lianxiren")==null?null:supplier.getStr("lianxiren").toString();
	}
	
	/**
	 * 联系人电话
	 * @param para
	 * @return
	 */
	private String getDianHua(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object gysbm = pop.value;
		Record supplier = findSupplier(gysbm.toString());
		if(supplier == null){
			return null;
		}
		return supplier.getStr("dianhua")==null?null:supplier.getStr("dianhua").toString();
	}
	/**
	 * 地址
	 * @param para
	 * @return
	 */
	private String getDizhi(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object gysbm = pop.value;
		Record supplier = findSupplier(gysbm.toString());
		if(supplier == null){
			return null;
		}
		return supplier.getStr("dizhi")==null?null:supplier.getStr("dizhi").toString();
	}
	


}
