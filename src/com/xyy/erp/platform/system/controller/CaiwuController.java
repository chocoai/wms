package com.xyy.erp.platform.system.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.BillInstance;
import com.xyy.bill.services.util.BILLConstant;
import com.xyy.erp.platform.common.tools.ToolContext;
import com.xyy.erp.platform.common.tools.ToolWeb;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.CaiwuService;
import com.xyy.util.StringUtil;

/**
 * 财务管理控制类
 * @author 
 *
 */
public class CaiwuController extends Controller{
	private static CaiwuService caiwuService = Enhancer.enhance(CaiwuService.class);
	
	public void route(){
		String code = this.getPara();
		switch (code) {
		case ParamCodeConstant.MigrateDetial:
			getMigrationDetails();
			break;
		case ParamCodeConstant.YueValue:
			getYueValue();
			break;
		default:
			this.jumpTo404();
			break;
		}
	}
	
	/**
	 * 获取应付余额信息
	 */
	private void getYueValue() {
		BillContext context = this.buildBillContext();
		String numb = context.getString("numb");
		BigDecimal result=BigDecimal.ZERO;
		if (StringUtil.isEmpty(numb)) {
			this.setAttr("status", 0);
			this.renderJson();
			return;
		}
		Record record = Db.findFirst("select * from xyy_erp_bill_wanglaizhangye where wlId=? order by createTime desc limit 1", numb);
		if (record == null) {
			this.setAttr("status", 0);
			this.renderJson();
			return;
		}else{
			result = record.getBigDecimal("yue");
		}
		this.setAttr("status", 1);
		this.setAttr("result", result);
		this.renderJson();
	}

	/**
	 * 获取单据相关的详情信息
	 */
	private void getMigrationDetails() {
		BillContext context = this.buildBillContext();
		String[] danjubianhao = context.getString("danjubianhao").split(",");
		String tableName = context.getString("tableName");
		if (StringUtil.isEmpty(danjubianhao)|| StringUtil.isEmpty("tableName")) {
			this.setAttr("status",0);
			context.addError("999", "单据编号或表名为空！");
			this.renderJson();
		}
		List<String> list = new ArrayList<>();
		for(int i=0;i<danjubianhao.length;i++){
			if (!list.contains(danjubianhao[i])) {
				list.add(danjubianhao[i]);
			}
		}
		List<Record> result = new ArrayList<>();
		for (String str : list) {
			List<Record> records = Db.find("select * from "+ tableName +" where fapiaozhuangtai=0 and shifouyinyong=0  and danjubianhao=?",str);
			result.addAll(records);
		}
		
		this.setAttr("status", 1);
		this.setAttr("result", result);
		this.renderJson();
	}

	public void cancel(){
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		boolean result = billInstance.cancel();
		if (result) {
			result = caiwuService.cancel(context);
			if (result) {
				this.setAttr("status", 1);
				this.renderJson();
			}
			
		} 
		this.setAttr("status", 0);
		this.setAttr("error", "单据作废操作失败！");
		this.renderJson();
	}
	
	public void rollBack(){
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		boolean result = billInstance.rollBack();
		if (result) {
			this.setAttr("status", 1);
			this.renderJson();
			return;
		}
		this.setAttr("status", 0);
		this.setAttr("error", "单据提交回写信息失败！");
		this.renderJson();
		return;
	}
	
	public void blend(){
		String code = this.getPara();
		BillContext context = this.buildBillContext();
		context.set("code", code);
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		boolean result = billInstance.blend();
		if (result) {
			this.setAttr("status", 1);
			this.renderJson();
			return;
		}
		this.setAttr("status", 0);
		this.setAttr("error", "单据提交回写信息失败！");
		this.renderJson();
		return;
	}
	
	/**
	 * 收集请求参数，构建页面中的上下文
	 * 
	 * @return
	 */
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
		context.set("UA", this.getUserAgent());
		User user = ToolContext.getCurrentUser(getRequest(), true);
		String orgCode = ToolWeb.getCookieValueByName(getRequest(), "orgCode");
		String orgCodes = ToolWeb.getCookieValueByName(getRequest(), "orgCodes");
		String orgId = ToolWeb.getCookieValueByName(getRequest(), "orgId");
		context.set("user", user);
		context.set("orgId", orgId);
		context.set("orgCode", orgCode);
		context.set("orgCodes", orgCodes);
		context.set("time", new Timestamp(System.currentTimeMillis()));
		return context;
	}
	
	/**
	 * 获取用户代理
	 * 
	 * @return
	 */
	private String getUserAgent() {
		String userAgent = this.getRequest().getHeader("User-Agent");
		if (userAgent.contains("Mobile")) {
			return BILLConstant.UA_MOBILE;
		} else {
			return BILLConstant.UA_WEB;
		}
	}
	
	private void jumpTo404() {
		this.render("/404.html");
	}
	
	public class ParamCodeConstant{
		private static final String MigrateDetial="Migrate-Detial";
		
		private static final String YueValue="Yue-Value";
	}
}
