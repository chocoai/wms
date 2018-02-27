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
public class XianLuSpecialController extends Controller {

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
		case SPEConstant.SPE_CODE_XIAN_LU :
			this.xianluinfo();
			break;
	
		default:
			this.render("/404.html");
			break;
		}
	}
	
   /*
    * 根据路线ID查询线路信息
    */
	private void xianluinfo() {
		BillContext context = this.buildBillContext();
		String luxianid = context.getString("luxianid");
		String sql = "SELECT * FROM xyy_wms_dic_peisongxianlu WHERE ID=?";
		Record xianlu = Db.findFirst(sql,luxianid);
		this.setAttr("status", 1);
		this.setAttr("result", xianlu==null?"":xianlu);
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

		public static final String SPE_CODE_XIAN_LU = "xianlu";
	
	}

}
