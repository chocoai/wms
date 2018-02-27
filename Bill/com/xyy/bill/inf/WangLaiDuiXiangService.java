package com.xyy.bill.inf;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.common.tools.StringUtil;

public class WangLaiDuiXiangService extends BillBizService {

	@Override
	public Page<Record> findBizData(BillContext billContext) {
		if (billContext.getJSONObject("model")==null) {
			return null;
		}
		JSONObject page = billContext.getJSONObject("model").getJSONObject("wanglaiduixiang").getJSONObject("page");
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
		int type =billContext.getInteger("duixiangleixing");
		String id =billContext.getString("objectId");
//		String name =billContext.getString("objectName");
		StringBuffer sql = new StringBuffer();
		if (type==1) {//供应商
			sql = new StringBuffer("SELECT g.gysbh AS objectId,g.gysmc AS objectName,'供应商' AS zhaiyao  FROM xyy_erp_dic_gongyingshangjibenxinxi g WHERE 1=1 ");
			if(!StringUtil.isEmpty(id)){
				sql.append(" AND (g.gysbh like '%"+id+"%'  OR  g.gysmc like '%"+id+"%' OR  g.zhujima like '%"+id+"%')");
			}
		}else if (type==2) {//客户
			sql = new StringBuffer("SELECT k.kehubianhao AS objectId,k.kehumingcheng AS objectName,'客户' AS zhaiyao FROM xyy_erp_dic_kehujibenxinxi k WHERE 1=1 ");
			if(!StringUtil.isEmpty(id)){
				sql.append(" AND (k.kehubianhao like '%"+id+"%'  OR  k.kehumingcheng like '%"+id+"%' OR  k.zhujima like '%"+id+"%')");
			}
		}
		return sql.toString();
	}

}
