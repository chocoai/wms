package com.xyy.bill.inf;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.common.tools.StringUtil;

public class WanglaizhangService extends BillBizService {

	@Override
	public Page<Record> findBizData(BillContext billContext) {
		if (billContext.getJSONObject("model")==null) {
			return null;
		}
		JSONObject page = billContext.getJSONObject("model").getJSONObject("wanglaiyue").getJSONObject("page");
		String sql = this.buildSQL(billContext);
		if (sql==null) {
			return new Page<>(new ArrayList<>(), 0, 0, 0, 0);
		}
		return Db.paginate(page.getInteger("pageNumber"), page.getInteger("pageSize"), "select *", "from ("+sql+") s");
	}

	@Override
	public Page<Record> findBizDataByPaginate(BillContext billContext, int pageNumber, int pageSize) {
		String sql = this.buildSQL(billContext);
		if (sql==null) {
			return new Page<>(new ArrayList<>(), 0, 0, 0, 0);
		}
		return Db.paginate(pageNumber, pageSize, "select *", "from ("+sql+") s");
	}
	
	private String buildSQL(BillContext billContext) {
		if (billContext.getJSONObject("model")==null) {
			return null;
		}
		JSONObject params = billContext.getJSONObject("model").getJSONObject("wanglaiyue").getJSONObject("params");
		int type =params.getInteger("duixiangleixing");
		String id = params.getString("id");
		String name = params.getString("name");
		StringBuffer sql = new StringBuffer();
		if (type==1) {
			sql = new StringBuffer("select x.yue,y.gysbh as id , y.gysmc as name from "
					+ "(select b.* FROM( SELECT MAX(ID) AS maxId FROM "
					+ "xyy_erp_bill_wanglaizhangye a where 1=1 ");
			
			sql.append(" GROUP BY wlId ) a,"
					+ "xyy_erp_bill_wanglaizhangye b where a.maxId = b.ID) x ,xyy_erp_dic_gongyingshangjibenxinxi y where x.wlId = y.suppliersid ");
			if(StringUtil.isEmpty(id)){
				sql.append("");
			}else {
				sql.append("and y.gysbh like '%"+id+"%'");
			}
			if(StringUtil.isEmpty(name)){
				sql.append("");
			}else {
				sql.append("and y.gysmc like '%"+name+"%'");
			}
		}else if (type==2) {
			sql = new StringBuffer("select x.yue,y.kehubianhao as id,y.kehumingcheng as name from "
					+ "(select b.* FROM( SELECT MAX(ID) AS maxId FROM "
					+ "xyy_erp_bill_wanglaizhangye a where 1=1 ");
			
			sql.append(" GROUP BY wlId ) a,xyy_erp_bill_wanglaizhangye b where a.maxId = b.ID) x ,xyy_erp_dic_kehujibenxinxi y where x.wlId = y.clientid ");
			if(StringUtil.isEmpty(id)){
				sql.append("");
			}else {
				sql.append("and y.kehubianhao like '%"+id+"%'");
			}
			if(StringUtil.isEmpty(name)){
				sql.append("");
			}else {
				sql.append(" and y.kehumingcheng like '%"+name+"%'");
			}
		}
		
		return sql.toString();
	}

}
