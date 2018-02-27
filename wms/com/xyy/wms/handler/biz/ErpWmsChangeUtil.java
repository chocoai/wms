package com.xyy.wms.handler.biz;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.util.UUIDUtil;



/**
 * 中间表数据写到对应的WMS业务表
 * 
 * */
public class ErpWmsChangeUtil {
	
	
	
	/***
	 * 从中间表V_ERP_XSCK中拉取销售订单数据到wms对应表中
	 * 
	 * */
	public Map<String,Record> XSDDInsertData(Record view){
		
		Record body=new Record();
		String billID=UUIDUtil.newUUID();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		body.set("BillID", billID);
		body.set("status", 40);
		body.set("orgId", view.getStr("orgId"));
		body.set("orgCode", view.getStr("orgCode"));
		body.set("createTime", currentTime);
		body.set("updateTime", currentTime);
		body.set("rowID", UUIDUtil.newUUID());
		body.set("danjubianhao", view.getStr("DJBH"));//单据编号
		body.set("kaipiaoriqi", strToDate("YYYY-MM-DD",view.getStr("RQ")));//开票日期
		body.set("yewuyuan", view.getStr("RY_YWY"));//业务员
		body.set("kehubianhao", view.getStr("dwbh"));//客户编号
		Record kh=getKh(view.getStr("dwbh"));
		body.set("kehumingcheng", kh==null?"":kh.getStr("kehumingcheng"));//客户名称
		body.set("lianxiren", view.getStr("SHR"));//联系人
		body.set("lianxidianhua", view.getStr("LXFS"));//联系电话
		body.set("lianxidizhi", view.getStr("SHDZ"));//收货地址
		body.set("tihuofangshi", view.getStr("THFS"));//提货方式
		body.set("beizhu", view.getStr("KHBZ"));//备注
		body.set("remark", view.getStr("KHBZ"));//备注
		body.set("shifouxiatui", 0);//是否下推
		body.set("shifouchuku", 0);//是否出库
		body.set("shifouchonghong", 0);//是否冲红
		body.set("shifoutongbu", 0);//是否同步
		
		
		Record head=new Record();
		head.set("BillDtlID", UUIDUtil.newUUID());
		head.set("BillID", billID);
		head.set("createTime", currentTime);
		head.set("updateTime", currentTime);
		body.set("orgId", view.getStr("orgId"));
		body.set("orgCode", view.getStr("orgCode"));
		head.set("rowID", UUIDUtil.newUUID());
		head.set("danjubianhao", view.getStr("DJBH"));//单据编号
		head.set("shangpinbianhao", view.getStr("SPID"));//商品编号
		head.set("SN", view.getInt("DJ_SORT"));//行号
		Record sp=getSp(view.getStr("SPID"));
		if(sp!=null){
			head.set("goodsid", sp.getStr("goodsid"));
			head.set("shangpinmingcheng", sp.getStr("shangpinmingcheng"));
			head.set("guige", sp.getStr("guige"));
			head.set("danwei", sp.getInt("danwei"));
			head.set("shengchanchangjia", sp.getStr("shengchanchangjia"));
			head.set("pizhunwenhao", sp.getStr("pizhunwenhao"));
			head.set("chandi", sp.getStr("chandi"));
		}
		head.set("shuliang", view.getBigDecimal("SL"));
		head.set("beizhu", view.getStr("KHBZ"));//备注
		head.set("chonghongshuliang", 0);
		head.set("shifouchonghong", 0);
		head.set("shifoutongbu", 0);
		
		Map<String, Record> map=new HashMap<String, Record>();
		map.put("body", body);
		map.put("head", head);
		return map;
		
	}
	
	
	
	/***
	 * 从中间表V_ERP_CGDD中拉取采购订单数据到wms对应表中
	 * 
	 * */
	public Map<String,Record> CGDDInsertData(Record view){
		
		Record body=new Record();
		String billID=UUIDUtil.newUUID();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		body.set("BillID", billID);
		body.set("status", 40);
		body.set("orgId", view.getStr("orgId"));
		body.set("orgCode", view.getStr("orgCode"));
		body.set("createTime", currentTime);
		body.set("updateTime", currentTime);
		body.set("rowID", UUIDUtil.newUUID());
		body.set("danjubianhao", view.getStr("DJBH"));//单据编号
		body.set("caigouyuan", view.getStr("RY_CGY"));// 采购员
		body.set("gysbh", view.getStr("DWID"));//供应商编号gysmc
		Record gys=getGYS(view.getStr("DWID"));
		body.set("gysmc", gys==null?"":gys.getStr("gysmc")); //供应商名称
		body.set("jiesuanfangshi", 1); // 结算方式
		body.set("sfyfk", 1); // 是否预付款
		body.set("songhuofangshi", 1); // 送货方式
		body.set("kaipiaoriqi", strToDate("YYYY-MM-DD",view.getStr("RQ")));//开票日期
		body.set("kaipiaoyuan", view.getStr("RY_CGY")); // 开票员
		
		Record head=new Record();
		head.set("BillDtlID", UUIDUtil.newUUID());
		head.set("BillID", billID);
		head.set("createTime", currentTime);
		head.set("updateTime", currentTime);
		body.set("orgId", view.getStr("orgId"));
		body.set("orgCode", view.getStr("orgCode"));
		head.set("rowID", UUIDUtil.newUUID());
		head.set("shangpinbianhao", view.getStr("SPID"));//商品编号
		Record sp=getSp(view.getStr("SPID"));
		if(sp!=null){
			head.set("shangpinmingcheng", sp.getStr("shangpinmingcheng"));
			head.set("guige", sp.getStr("guige"));
			head.set("danwei", sp.getInt("danwei"));
			head.set("shengchanchangjia", sp.getStr("shengchanchangjia"));
			head.set("pizhunwenhao", sp.getStr("pizhunwenhao"));
			head.set("chandi", sp.getStr("chandi"));
		}
		head.set("shuliang", view.getBigDecimal("SL")); // 数量
		head.set("hanshuijia", view.getBigDecimal("dj")); // 单价
		head.set("hanshuijine", view.getBigDecimal("je")); // 金额
//		head.set("beizhu", view.getStr("KHBZ"));//备注
		
		Map<String, Record> map=new HashMap<String, Record>();
		map.put("body", body);
		map.put("head", head);
		return map;
		
	}
	
	
	
	/***
	 * 从中间表V_ERP_XSTH中拉取销售退回订单数据到wms对应表中
	 * 
	 * */
	public Map<String,Record> XSTHInsertData(Record view){
		
		Record body=new Record();
		String billID=UUIDUtil.newUUID();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		body.set("BillID", billID);
		body.set("status", 40);
		body.set("orgId", view.getStr("orgId"));
		body.set("orgCode", view.getStr("orgCode"));
		body.set("createTime", currentTime);
		body.set("updateTime", currentTime);
		body.set("rowID", UUIDUtil.newUUID());
		body.set("danjubianhao", view.getStr("DJBH"));//单据编号
		body.set("yewuyuan", view.getStr("RY_YWY"));//业务员
		body.set("kehubianhao", view.getStr("dwbh"));//客户编号
		Record kh=getKh(view.getStr("dwbh"));
		body.set("kehumingcheng", kh==null?"":kh.getStr("kehumingcheng"));//客户名称
		body.set("shifouxiatui", 0); // 是否下推
	
		
		Record head=new Record();
		head.set("BillDtlID", UUIDUtil.newUUID());
		head.set("BillID", billID);
		head.set("createTime", currentTime);
		head.set("updateTime", currentTime);
		body.set("orgId", view.getStr("orgId"));
		body.set("orgCode", view.getStr("orgCode"));
		head.set("rowID", UUIDUtil.newUUID());
		head.set("shangpinbianhao", view.getStr("SPID"));//商品编号
		head.set("danjubianhao", view.getStr("DJBH"));//单据编号
		head.set("shangpinbianhao", view.getStr("SPID"));//商品编号
		Record sp=getSp(view.getStr("SPID"));
		if(sp!=null){
			head.set("goodsid", sp.getStr("goodsid"));
			head.set("shangpinmingcheng", sp.getStr("shangpinmingcheng"));
			head.set("guige", sp.getStr("guige"));
			head.set("danwei", sp.getInt("danwei"));
			head.set("shengchanchangjia", sp.getStr("shengchanchangjia"));
			head.set("pizhunwenhao", sp.getStr("pizhunwenhao"));
			head.set("chandi", sp.getStr("chandi"));
		}
		
		head.set("shuliang", view.getBigDecimal("SL")); // 数量
		head.set("pihao", view.get("PH"));
		head.set("shengchanriqi", view.get("rq_sc"));
		head.set("youxiaoqizhi", view.get("yxqz"));
		head.set("tuihuoyuanyin", view.get("THYY"));
//		head.set("hanshuijia", view.getBigDecimal("dj")); // 单价
//		head.set("hanshuijine", view.getBigDecimal("je")); // 金额
//		head.set("beizhu", view.getStr("KHBZ"));//备注
		
		Map<String, Record> map=new HashMap<String, Record>();
		map.put("body", body);
		map.put("head", head);
		return map;
		
	}
	
	
	/***
	 * 从中间表V_ERP_GJTC中拉取购进退出(采购退出)订单数据到wms对应表中
	 * 
	 * */
	public Map<String,Record> GJXTInsertData(Record view){
		
		Record body=new Record();
		String billID=UUIDUtil.newUUID();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		body.set("BillID", billID);
		body.set("status", 40);
		body.set("orgId", view.getStr("orgId"));
		body.set("orgCode", view.getStr("orgCode"));
		body.set("createTime", currentTime);
		body.set("updateTime", currentTime);
		body.set("rowID", UUIDUtil.newUUID());
		body.set("danjubianhao", view.getStr("DJBH"));//单据编号
		body.set("caigouyuan", view.getStr("cgy")); //采购员
		body.set("yewuyuan", view.getStr("RY_YWY"));//业务员
		body.set("gysbh", view.getStr("dwbh"));//供应商编号gysmc
		Record gys=getGYS(view.getStr("dwbh"));
		body.set("gysmc", gys==null?"":gys.getStr("gysmc")); //供应商名称
		body.set("kaipiaoriqi", strToDate("YYYY-MM-DD",view.getStr("RQ")));//开票日期
		body.set("kaipiaoyuan", view.getStr("RY_YWY")); // 开票员
		body.set("beizhu", view.getStr("beizhu"));
		Record head=new Record();
		head.set("BillDtlID", UUIDUtil.newUUID());
		head.set("BillID", billID);
		head.set("createTime", currentTime);
		head.set("updateTime", currentTime);
		body.set("orgId", view.getStr("orgId"));
		body.set("orgCode", view.getStr("orgCode"));
		head.set("rowID", UUIDUtil.newUUID());
		head.set("shangpinbianhao", view.getStr("SPID"));//商品编号
		head.set("danjubianhao", view.getStr("DJBH"));//单据编号
		head.set("shangpinbianhao", view.getStr("SPID"));//商品编号
		Record sp=getSp(view.getStr("SPID"));
		if(sp!=null){
			head.set("goodsid", sp.getStr("goodsid"));
			head.set("shangpinmingcheng", sp.getStr("shangpinmingcheng"));
			head.set("guige", sp.getStr("guige"));
			head.set("danwei", sp.getInt("danwei"));
			head.set("shengchanchangjia", sp.getStr("shengchanchangjia"));
			head.set("pizhunwenhao", sp.getStr("pizhunwenhao"));
			head.set("chandi", sp.getStr("chandi"));
		}
		
		head.set("pihao", view.get("PH"));
		Record re = getPH(sp.getStr("goodsid"),view.get("PH"));
		if(re!=null){
			head.set("pihaoId", re.get("pihaoId")); // 批号id
			head.set("shengchanriqi", re.get("shengchanriqi")); // 生产日期
			head.set("shengchanriqi", re.get("youxiaoqizhi")); // 有效期至
		}
		head.set("shuliang", view.getBigDecimal("SL")); // 数量
		head.set("tuihuoyuanyin", view.get("THYY"));
//		head.set("hanshuijia", view.getBigDecimal("dj")); // 单价
//		head.set("hanshuijine", view.getBigDecimal("je")); // 金额
//		head.set("beizhu", view.getStr("KHBZ"));//备注
		
		Map<String, Record> map=new HashMap<String, Record>();
		map.put("body", body);
		map.put("head", head);
		return map;
		
	}
	
	
	private Date strToDate(String format,String dateStr){
		  SimpleDateFormat sdf = new SimpleDateFormat(format);
		  try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			return new Date();
		}
	}
	//通过客户编号获取客户对象
	private Record getKh(String kehubianhao){
		StringBuffer sb=new StringBuffer("select * from xyy_wms_dic_kehudizhi where kehubianhao=? ");
		Record record=Db.findFirst(sb.toString(),kehubianhao);
		return record;
	}
	//通过商品编号获取商品对象
	private Record getSp(String shangpinbianhao){
		StringBuffer sb=new StringBuffer("select * from xyy_wms_dic_shangpinziliao where shangpinbianhao=? ");
		Record record=Db.findFirst(sb.toString(),shangpinbianhao);
		return record;
	}
		
	//通过供应商编号获取供应商
	private Record getGYS(String gysbh){
		StringBuffer sb=new StringBuffer("select * from xyy_wms_dic_gongyingshangjibenxinxi where gysbh=? ");
		Record record=Db.findFirst(sb.toString(),gysbh);
		return record;
	}
	
	// 通过商品id和批号获取批号信息
	private Record getPH(String spid ,String pihao){
		StringBuffer sb=new StringBuffer("select * from xyy_wms_dic_shangpinpihao where goodsId=? and pihao=? ");
		Record record=Db.findFirst(sb.toString(),spid,pihao);
		return record;
	}
	
}
