package com.xyy.bill.instance;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyy.bill.instance.DataTableInstance.Page;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.Parameter;
import com.xyy.bill.services.util.BILLConstant;
import com.xyy.bill.meta.DataTableMeta.TableType;
import com.xyy.bill.util.DataUtil;
import com.xyy.expression.Context;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;
import com.xyy.util.StringUtil;

public class DataTableRunContext implements Context {
	private Map<String, Object> _variants = new HashMap<>();
	private BillContext billContext;
	private DataTableInstance dataTableInstance;

	public DataTableRunContext(BillContext billContext, DataTableInstance dataTableInstance) {
		super();
		this.billContext = billContext;
		this.dataTableInstance = dataTableInstance;
		this.initiate();
	}

	/**
	 * 初始化运行上下文
	 * 
	 * 
	 */
	private void initiate() {
		if (this.billContext.containsName("model")) {
			try {
				JSONObject model = JSONObject.parseObject(this.billContext.getString("model"));
				if (model != null && model.containsKey(this.dataTableInstance.getDataTableMeta().getKey())) {// 解析他
					JSONObject cur_dti = model.getJSONObject(this.dataTableInstance.getDataTableMeta().getKey());
					if (cur_dti.getBooleanValue("head")) {
						JSONArray cur_tree = model.getJSONArray("$t_dictionary");
						if (cur_tree != null) {
							this.dataTableInstance.setTreeFromJSONArray(cur_tree);
						}
					}
					
					JSONArray cos = cur_dti.getJSONArray("cos");
					DataTableMeta dataTableMeta=this.getDataTableInstance().getDataTableMeta();
				
					JSONArray dels = cur_dti.getJSONArray("dels");
					JSONObject params = cur_dti.getJSONObject("params");
					JSONObject page = cur_dti.getJSONObject("page");
					if (cos != null) {
						this.dataTableInstance.setRecordsFromJSONArray(cos);
					}
					if (dels != null) {
						this.dataTableInstance.setDeleteRecordsFromJSONArray(dels);
					}
					// 参数信息
					if (params != null) {
						for (String key : params.keySet()) {
							this.setVariant(key, params.get(key));
						}

					}
					// 页面信息
					if (page != null) {
						for (String key : page.keySet()) {
							this.setVariant(key, page.get(key));
						}
						this.dataTableInstance
								.setPage(new Page(page.getInteger("pageNumber"), page.getInteger("pageSize"),
										page.getInteger("totalPage"), page.getInteger("totalRow")));

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (this.billContext.get("optExcel") != null) {
			this.dataTableInstance.setExport(true);
		}

		// 需要计算参数的初始值了
		for (Parameter p : this.dataTableInstance.getDataTableMeta().getParameters()) {
			//如果已经回传参数值，不需要处理
			if (this._variants.containsKey(p.getKey().toLowerCase())) {
				continue;
			}
			String defaultValut = p.getDefaultValue();
			if(StringUtil.isEmpty(defaultValut)){//获取不到默认值，则值为null
				continue;
			}
		    
			if (defaultValut.startsWith("=")) {
				OperatorData od;
				try {
					od = ExpService.instance().calc(defaultValut.substring(1), this);
					if (od != null && od.clazz != NullRefObject.class) {
						this.setVariant(p.getKey(), od.value);
					}
				} catch (ExpressionCalException e) {
					e.printStackTrace();
				}
			} else {
				this.setVariant(p.getKey(), DataUtil.convert(defaultValut, p.getType()));
			}
		}

	}
	
	public DataTableInstance getDataTableInstance() {
		return dataTableInstance;
	}

	public void setDataTableInstance(DataTableInstance dataTableInstance) {
		this.dataTableInstance = dataTableInstance;
	}

	@Override
	public Object getValue(String name) {
		if (this._variants.containsKey(name.toLowerCase())) {
			return this._variants.get(name.toLowerCase());
		} else {
			return this.billContext.get(name.toLowerCase());
		}
	}

	@Override
	public void setVariant(String name, Object value) {

		this._variants.put(name.toLowerCase(), value);

	}

	@Override
	public void removeVaraint(String name) {
		this._variants.remove(name.toLowerCase());

	}

	public BillContext getBillContext() {
		return this.billContext;
	}

	public void setBillContext(BillContext billContext) {
		this.billContext = billContext;
	}

}
