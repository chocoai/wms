package com.xyy.wms.basicData.controller;

import java.util.Enumeration;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.util.StringUtil;

public class ShangpingSpecialController extends Controller {
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
		case SPEConstant.SPE_CODE_SHANGPING_INFO :
			this.shangpingfo();
			break;
			
			case SPEConstant.SPE_CODE_LGQUMC_INFO :
				this.ljqmcfofo();
				break;	
	
		default:
			this.render("/404.html");
			break;
		}
	}
	
   /*
    * 根据商品信息
    */
	private void shangpingfo() {
		BillContext context = this.buildBillContext();
		String shangpinbianhao = context.getString("shangpinbianhao");
		String sql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=?";
		Record shangpingfo = Db.findFirst(sql,shangpinbianhao);
		this.setAttr("status", 1);
		this.setAttr("result", shangpingfo==null?"":shangpingfo);
		this.renderJson();
		
	}
	
	// 查询逻辑区域编号
	private void ljqmcfofo() {
		BillContext context = this.buildBillContext();
		String ljqbh = context.getString("ljqbh");
		String sql = "SELECT * from xyy_wms_dic_ljqzlwh where shifouqiyong='1' and ljqbh=?";
		Record shangpingfo = Db.findFirst(sql,ljqbh);
		this.setAttr("status", 1);
		this.setAttr("result", shangpingfo==null?"":shangpingfo);
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

		public static final String SPE_CODE_SHANGPING_INFO = "shangpingfo";
		public static final String SPE_CODE_LGQUMC_INFO = "ljqmcfofo";
	
	}

}
