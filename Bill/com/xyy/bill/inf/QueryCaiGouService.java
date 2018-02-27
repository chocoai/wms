package com.xyy.bill.inf;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.common.tools.StringUtil;

public class QueryCaiGouService extends BillBizService {

	@Override
	public Page<Record> findBizData(BillContext billContext) {
		if (billContext.getJSONObject("model")==null) {
			return null;
		}
		JSONObject page = billContext.getJSONObject("model").getJSONObject("querycaigou").getJSONObject("page");
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
		JSONObject params = billContext.getJSONObject("model").getJSONObject("querycaigou").getJSONObject("params");
		String danjubianhao = params.getString("danjubianhao");
		String caigouyuan = params.getString("caigouyuan");
		String kaipiaoyuan = params.getString("kaipiaoyuan");
		String gys = params.getString("gys");
		String startTimeStr = params.getString("startTime");
		String endTimeStr = params.getString("endTime");
		String caigouleixing = params.getString("caigouleixing");
		if(StringUtil.isEmpty(caigouleixing)){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql = new StringBuffer("SELECT * FROM xyy_erp_bill_migratejinxiangkaipiao r where 1=1 ");
		StringBuffer condition1 = condition1(caigouleixing,startTimeStr,endTimeStr,danjubianhao, caigouyuan, kaipiaoyuan, gys);
		sql.append(condition1);
		
		return sql.toString();
	}
	
	private StringBuffer condition1(String danjuleixing,String startTimeStr,String endTimeStr,String danjubianhao,String caigouyuan,String kaipiaoyuan,String gys){
		StringBuffer sql = new StringBuffer();
		StringBuffer type = new StringBuffer();
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
			sql.append(" and  r.danjuleixin in ("+type.toString().substring(1)+") ");
		}
		if(!StringUtil.isEmpty(danjubianhao)){
			sql.append(" AND r.danjubianhao like '%"+danjubianhao+"%'");
		}
		if(!StringUtil.isEmpty(caigouyuan)){
			sql.append(" AND r.caigouyuan like '%"+caigouyuan+"%'");
		}
		if(!StringUtil.isEmpty(kaipiaoyuan)){
			sql.append(" AND r.kaipiaoyuan like '%"+kaipiaoyuan+"%'");
		}
		if(!StringUtil.isEmpty(gys)){
			sql.append(" AND (r.gysmc like '%"+gys+"%' OR r.gysbh like '%"+gys+"%')");
		}
		if(!StringUtil.isEmpty(gys)){
			sql.append(" AND (r.gysmc like '%"+gys+"%' OR r.gysbh like '%"+gys+"%')");
		}
		if(startTimeStr!=null){
			sql.append(" AND r.kaipiaoriqi >= '"+startTimeStr+"'");
		}
		if(endTimeStr!=null){
			sql.append(" AND r.kaipiaoriqi <= '"+endTimeStr+"'");
		}
		
		return sql;
	}
}
