package com.xyy.wms.basicData.controller;


import java.util.Enumeration;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.util.StringUtil;


/**
 * 为满足特殊需求的控制器
 * @author ry
 *
 */
public class KuQuSpecialController extends Controller {

	/**
	 * 公共路由
	 */
	public void route() {
		String code = this.getPara();
		if (StringUtil.isEmpty(code)) {
			this.jumpTo404();
			return;
		}
		switch (code) {
		case SPEConstant.SPE_CODE_KU_QU :
			this.kuquinfo();
			break;
	
		default:
			this.render("/404.html");
			break;
		}
	}
	
   /*
    * 根据仓库ID查询仓库信息
    */
	private void kuquinfo() {
		BillContext context = this.buildBillContext();
		String kuquId = context.getString("kuquId");
	/*	String sql = "SELECT * FROM view_cangkuandkuqu WHERE kuquID=?"
				+ "";*/
		String sql = "SELECT k.*,c.cangkubianhao from xyy_wms_dic_kuqujibenxinxi k LEFT JOIN xyy_wms_dic_cangkuziliao c on k.cangkuID = c.ID WHERE k.ID = ?";
		Record kuqu = Db.findFirst(sql,kuquId);
		System.out.println(kuqu.toJson());
		this.setAttr("status", 1);
		this.setAttr("result", kuqu==null?"":kuqu);
		this.renderJson();
		
	}

	private BillContext buildBillContext() {
		BillContext context = new BillContext();
		// 遍历参数
		Enumeration<String> params = this.getParaNames();
		while (params.hasMoreElements()) {
			// 获取request中的请求参数
			String name = params.nextElement();
			String value = this.getPara(name);
			context.set(name, value);
		}
		return context;
	}

	private void jumpTo404() {
		this.render("/404.html");
	}
	
	public class SPEConstant {

		public static final String SPE_CODE_KU_QU = "kuqu";
	
	}

}
