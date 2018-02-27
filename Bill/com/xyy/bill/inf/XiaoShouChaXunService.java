package com.xyy.bill.inf;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.common.tools.StringUtil;

public class XiaoShouChaXunService extends BillBizService {

	@Override
	public Page<Record> findBizData(BillContext billContext) {
		if (billContext.getJSONObject("model")==null) {
			return null;
		}
		JSONObject page = billContext.getJSONObject("model").getJSONObject("xiaoshouchaxun").getJSONObject("page");
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
	
	
	/**
	 * @param billContext
	 * @return
	 */
	private String buildSQL(BillContext billContext) {
		if (billContext.getJSONObject("model")==null) {
			return null;
		}
		JSONObject params = billContext.getJSONObject("model").getJSONObject("xiaoshouchaxun").getJSONObject("params");
		String danjuleixing =params.getString("danjuleixing");
		String danjubianhao =params.getString("danjubianhao");
		String yxsdh =params.getString("yxsdh");
		String ydsdh =params.getString("ydsdh");
		String startTime =params.getString("startTime");
		String endTime =params.getString("endTime");
		String kaipiaoyuan =params.getString("kaipiaoyuan");
		String yewuyuan = params.getString("yewuyuan");
		String kehumingcheng = params.getString("kehumingcheng");
		String select = "";
		if (StringUtil.isEmpty(kehumingcheng)) {
			select = "select a.* from xyy_erp_bill_migratexiaoxiangfapiao a where 1=1 ";
		}else {
			select = "select a.* from xyy_erp_bill_migratexiaoxiangfapiao a,xyy_erp_dic_kehujibenxinxi b  "
					+ "where a.kehubianhao = b.kehubianhao and b.mixCondition like '%"+kehumingcheng+"%' ";
		}
		StringBuffer sql = new StringBuffer(select);
		StringBuffer type = new StringBuffer();
		if (!danjuleixing.contains("1") && !danjuleixing.contains("2") && !danjuleixing.contains("3")) {
			return null;
		}
		if (danjuleixing.contains("1")) {
			type.append(",1");
		}
		if (danjuleixing.contains("2")) {
			type.append(",2");
		}
		if (danjuleixing.contains("3")) {
			type.append(",3");
		}
		if (type.length()>0) {
			sql.append(" and  a.danjuleixin in ("+type.toString().substring(1)+") ");
		}
		if(!StringUtil.isEmpty(danjubianhao)){
			sql.append(" and  a.danjubianhao like '%"+danjubianhao+"%' ");
		}
		if(!StringUtil.isEmpty(yxsdh)){
			sql.append(" and  a.yxsdh like '%"+yxsdh+"%' ");
		}
		if(!StringUtil.isEmpty(ydsdh)){
			sql.append(" and  a.ydsdh like '%"+ydsdh+"%' ");
		}
		if(!StringUtil.isEmpty(startTime)){
			sql.append(" and DATE_FORMAT(a.kaipiaoriqi,'%Y-%m-%d') >=  DATE_FORMAT('"+startTime+"','%Y-%m-%d')");
		}
		if(!StringUtil.isEmpty(endTime)){
			sql.append(" and DATE_FORMAT(a.kaipiaoriqi,'%Y-%m-%d') <=  DATE_FORMAT('"+endTime+"','%Y-%m-%d')");
		}
		if(!StringUtil.isEmpty(kaipiaoyuan)){
			sql.append(" and a.kaipiaoyuan like '%"+kaipiaoyuan+"%' ");
		}
		if(!StringUtil.isEmpty(yewuyuan)){
			sql.append(" and a.yewuyuan like '%"+yewuyuan+"%' ");
		}
		sql.append(" order by a.kaipiaoriqi ");
		return sql.toString();
	}

}
