package com.xyy.bill.inf;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.common.tools.StringUtil;

public class WangLaiZhangYeService extends BillBizService {

	@Override
	public Page<Record> findBizData(BillContext billContext) {
		if (billContext.getJSONObject("model")==null) {
			return null;
		}
		JSONObject page = billContext.getJSONObject("model").getJSONObject("wanglaizhangye").getJSONObject("page");
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
		JSONObject params = billContext.getJSONObject("model").getJSONObject("wanglaizhangye").getJSONObject("params");
		int type =params.getInteger("duixiangleixing");
		String id = params.getString("id");
		String name = params.getString("name");
		StringBuffer sql = new StringBuffer();
		if (type==1) {//供应商
			sql = new StringBuffer("SELECT b.*,g.gysbh AS objectId,g.gysmc AS objectName FROM xyy_erp_bill_wanglaizhangye b, xyy_erp_dic_gongyingshangjibenxinxi g WHERE 1=1 and b.wlId = g.suppliersid ");
			if(!StringUtil.isEmpty(id)){
				sql.append(" AND g.gysbh like '%"+id+"%'");
			}
			if(!StringUtil.isEmpty(name)){
				sql.append(" AND g.gysmc like '%"+name+"%'");
			}
		}else if (type==2) {//客户
			sql = new StringBuffer("SELECT b.*,k.kehubianhao AS objectId,k.kehumingcheng AS objectName FROM xyy_erp_bill_wanglaizhangye b, xyy_erp_dic_kehujibenxinxi k WHERE 1 = 1 AND b.wlId = k.clientid");
			if(!StringUtil.isEmpty(id)){
				sql.append(" AND k.kehubianhao like '%"+id+"%'");
			}
			if(!StringUtil.isEmpty(name)){
				sql.append(" AND k.kehumingcheng like '%"+name+"%'");
			}
		}
		sql.append(" order by b.ID ");
		return sql.toString();
	}

}
