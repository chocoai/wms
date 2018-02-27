package com.xyy.bill.inf;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.common.tools.StringUtil;

public class ShangpinzhangyeService extends BillBizService {

	@Override
	public Page<Record> findBizData(BillContext billContext) {
		if (billContext.getJSONObject("model")==null) {
			return null;
		}
		JSONObject page = billContext.getJSONObject("model").getJSONObject("shangpinzhangye").getJSONObject("page");
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
		JSONObject params = billContext.getJSONObject("model").getJSONObject("shangpinzhangye").getJSONObject("params");
		String id = params.getString("shangpinbianhao");
		String name = params.getString("shangpinmingcheng");
		StringBuffer sql = new StringBuffer();
		
		sql = new StringBuffer(" SELECT  a.*,b.shangpinbianhao,b.shangpinmingcheng, ");
		sql.append("ROUND(a.rkhsj/(1+(a.shangpinshuilv/100)),2) as rkdj, ");
		sql.append(" ROUND(a.rkhsje/(1+(a.shangpinshuilv/100)),2) as rkdje,");
//		sql.append("ROUND(a.kchsj/(1+(a.shangpinshuilv/100)),6) as kcdj, ");
//		sql.append("ROUND(a.kchsje/(1+(a.shangpinshuilv/100)),2) as kcdje, ");
		sql.append("ROUND(a.ckhsj/(1+(a.shangpinshuilv/100)),2) as ckdj, ");
		sql.append("ROUND(a.ckhsje/(1+(a.shangpinshuilv/100)),2) as ckdje ");
		sql.append("FROM xyy_erp_bill_shangpinzhangye a ,xyy_erp_dic_shangpinjibenxinxi b where a.shangpinId=b.goodsId ");
		if(!StringUtil.isEmpty(id)){
			sql.append(" AND shangpinbianhao = '"+id+"'");
		}
		if(!StringUtil.isEmpty(name)){
			sql.append(" AND shangpinmingcheng = '"+name+"'");
		}
		sql.append("order BY id ");
		return sql.toString();
	}

}
