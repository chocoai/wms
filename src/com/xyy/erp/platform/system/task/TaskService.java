package com.xyy.erp.platform.system.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.handler.BillHandlerManger;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.BillInstance;
import com.xyy.bill.map.MapHandlerManger;
import com.xyy.bill.plugin.SysSelectCachePlug;
import com.xyy.bill.services.util.AreaService;
import com.xyy.bill.util.RecordDataUtil;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.RandomUtil;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.util.UUIDUtil;

import net.sf.json.JSONArray;

public class TaskService {
	
	private static String orgId = "6d8104f9abd748b8906caa19f81ddb8a";
	private static String orgCode = "A-001";
	private static final Log LOG = Log.getLog(TaskService.class);
	/**
	 * 电商订单拉取方法
	 * @param task 
	 * @return
	 */
	public void DSDDFun(Task task) {
		Record record = task.getRecord();
		
		record.set("KFSTATE", 1);
		Record jilu = Db.findFirst("select * from xyy_erp_bill_dianshangdingdan where danjubianhao = '"+record.getStr("ORDERNO")+"' limit 1");
		if (jilu!=null) {
			Db.use(DictKeys.db_dataSource_wms_mid).update("tb_order_tmp", "ID", record);
			return;
		}
		Record kehu = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where kehubianhao = '"+record.getStr("MERCHANPHO")+"' limit 1");
		if (kehu==null) {
//			record.set("STATE", 1);
//			record.set("yichangyuanyin", "客户基本信息不存在【"+record.getStr("MERCHANPHO")+"】");
			LOG.error("客户基本信息不存在【"+record.getStr("MERCHANPHO")+"】");
			
		}else {
			Record ds = new Record();
			ds.set("danjubianhao", record.getStr("ORDERNO"));//单据编号
			ds.set("kaipiaoriqi", record.getDate("CREATE_TIME"));
			ds.set("kaipiaoyuan", "汪高华");
			ds.set("yewuyuan", kehu.get("yewuyuan")!=null?kehu.getStr("yewuyuan"):"admins");
			ds.set("kpyID", "K1QTI78VAML");
			ds.set("xiaoshouyuan", "汪高华");
			ds.set("xsyID", "K1QTI78VAML");
			ds.set("kehubianhao", kehu.getStr("kehubianhao"));
			ds.set("kehumingcheng", kehu.getStr("kehumingcheng"));
			ds.set("lianxiren", record.getStr("CONTACTOR"));
			ds.set("danjulaiyuan", "电商平台");
			ds.set("lianxidianhua", record.getStr("MOBILE"));
			ds.set("lianxidizhi", record.getStr("ADDRESS"));
			ds.set("dingdanzonge", record.getBigDecimal("TOTAL_AMOUNT"));
			ds.set("youhuijine", record.getBigDecimal("DISCOUNT"));
			ds.set("hanshuijine", record.getBigDecimal("MONEY"));
			//在线支付（支付宝A 微信W  银联U） 线下转账Z
			if (record.get("paytype")==null) {
				ds.set("jiesuanfangshi", 0);
			}else if (record.getStr("paytype").equals("W")) {
				ds.set("jiesuanfangshi", 1);
			}else if (record.getStr("paytype").equals("Z")) {
				ds.set("jiesuanfangshi", 2);
			}else if (record.getStr("paytype").equals("A")) {
				ds.set("jiesuanfangshi", 3);
			}else if (record.getStr("paytype").equals("U")) {
				ds.set("jiesuanfangshi", 4);
			}
			ds.set("tihuofangshi", 1);//配送默认为配送类型
			ds.set("createTime", new Date());
			if (record.getStr("urgency_info")==null||record.getStr("urgency_info").equals("NULL")) {
				ds.set("beizhu", "无备注");
			}else {
				ds.set("beizhu", record.getStr("urgency_info"));
			}
			if (record.getStr("REMARK")==null||record.getStr("REMARK").equals("NULL")) {
				ds.set("remark", "无备注");
			}else {
				ds.set("remark", record.getStr("REMARK"));
			}
			ds.set("sfyzf", record.getInt("sfzf"));
			ds.set("kefuyichang", 0);
			ds.set("status", 20);
			ds.set("orgId", orgId);
			ds.set("orgCode",orgCode );
			ds.set("rowID", UUIDUtil.newUUID());
			
			this.relationSave(ds,record);
		}
		
		Db.use(DictKeys.db_dataSource_wms_mid).update("tb_order_tmp", "ID", record);
			
		
		
	}
	
	private void relationSave(Record ds, Record temp) {
	
		String danjubianhao = ds.getStr("danjubianhao");
		List<Record> orderDetails = Db.use(DictKeys.db_dataSource_wms_mid).find("select * from tb_orderdetail_tmp where ORDERNO = '"+danjubianhao+"'");
		if (orderDetails==null||orderDetails.size()==0) {
			return;
		}
		List<Record> list = new ArrayList<>();
		StringBuffer errorBuffer = new StringBuffer();
		StringBuffer spBuffer = new StringBuffer();
		for (Record record2 : orderDetails) {
			Record DSDetail = new Record();
			//验证明细内是否有重复一样的商品
			if (spBuffer.toString().contains(record2.getStr("CODE"))) {
				LOG.error("明细中商品["+record2.getStr("CODE")+"]有重复");
				temp.set("state", 1);
				temp.set("yichangyuanyin", "明细中商品["+record2.getStr("CODE")+"]有重复");
				
				ds.set("yichangyuanyin", "明细中商品["+record2.getStr("CODE")+"]有重复");
				ds.set("status", 21);
				ds.set("mxyc", 1);
				ds.set("BillID", UUIDUtil.newUUID());
				Db.save("xyy_erp_bill_dianshangdingdan", ds);
				break;
			}
			spBuffer.append("|"+record2.getStr("CODE")+"|");
			DSDetail.set("shangpinbianhao", record2.getStr("CODE"));
			DSDetail.set("shuliang", new BigDecimal(record2.getInt("PRODUCT_COUNT")));
			DSDetail.set("oldhanshuijia", record2.getBigDecimal("PRODUCT_PRICE"));//原含税价
			DSDetail.set("oldhanshuijine", DSDetail.getBigDecimal("oldhanshuijia").multiply(DSDetail.getBigDecimal("shuliang")));//原含税金额
			DSDetail.set("youhuijine", record2.getBigDecimal("DISCOUNTAMOUNT"));//优惠总金额
			//实际含税金额：原含税价*数量-优惠总金额
			DSDetail.set("hanshuijine", DSDetail.getBigDecimal("oldhanshuijia").multiply(DSDetail.getBigDecimal("shuliang")).subtract(DSDetail.getBigDecimal("youhuijine")));
			//实际含税价：实际含税金额/数量
			DSDetail.set("hanshuijia", DSDetail.getBigDecimal("hanshuijine").divide(DSDetail.getBigDecimal("shuliang"),3,RoundingMode.HALF_UP));
			DSDetail.set("orgId", orgId);
			DSDetail.set("orgCode", orgCode);
			DSDetail.set("rowID", UUIDUtil.newUUID());
			Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = '"+record2.getStr("CODE")+"' limit 1");
			if (sp==null) {
				LOG.error("商品基本信息不存在【"+record2.getStr("CODE")+"】");
				ds.set("status", 21);
				errorBuffer.append("商品【"+record2.getStr("CODE")+"】基本信息不存在|");
				list.add(DSDetail);
				continue;
			}
			DSDetail.set("goodsid", sp.getStr("goodsid"));
			DSDetail.set("shangpinmingcheng", sp.getStr("shangpinmingcheng"));
			DSDetail.set("guige", sp.getStr("guige"));
			DSDetail.set("danwei", sp.getInt("danwei"));
			DSDetail.set("shengchanchangjia", sp.getStr("shengchanchangjia"));
			DSDetail.set("shuilv", sp.getBigDecimal("xiaoxiangshuilv"));
			DSDetail.set("pizhunwenhao", sp.getStr("pizhunwenhao"));
			DSDetail.set("chandi", sp.getStr("chandi"));
			//单价=（含税金额/数量）/(1+税率/100)
			DSDetail.set("danjia", DSDetail.getBigDecimal("hanshuijine").divide(DSDetail.getBigDecimal("shuliang"),6,RoundingMode.HALF_UP)
					.divide(new BigDecimal(1).add(DSDetail.getBigDecimal("shuilv").divide(new BigDecimal(100))),6,RoundingMode.HALF_UP));
			//金额=含税金额/(1+税率/100)
			DSDetail.set("jine", DSDetail.getBigDecimal("hanshuijine").
					divide(new BigDecimal(1).add(DSDetail.getBigDecimal("shuilv").divide(new BigDecimal(100))),2,RoundingMode.HALF_UP));
			DSDetail.set("shuie", DSDetail.getBigDecimal("hanshuijine").subtract(DSDetail.getBigDecimal("jine")));
			Record spKC = Db.findFirst("select * from xyy_erp_bill_shangpinzongkucun where shangpinId = '"+sp.getStr("goodsid")+"' limit 1");
			if (spKC==null) {
				LOG.error("商品总库存不存在【"+record2.getStr("CODE")+"】");
				ds.set("status", 21);
				errorBuffer.append("商品【"+record2.getStr("CODE")+"】总库存不存在|");
				list.add(DSDetail);
				continue;
			}
			DSDetail.set("kekaishuliang", spKC.getBigDecimal("kucunshuliang"));
			
			list.add(DSDetail);
		}
		ds.set("yichangyuanyin", errorBuffer.toString());
		if (list!=null && list.size()>0 && orderDetails.size()==list.size()) {
			this.saveAction(new BillContext(),this.buildContext(ds , list,"dianshangdingdan","dianshangdingdan_details"),"dianshangdingdan");
		}
			
		
	}
	

	private void saveAction(BillContext context, JSONObject model,String billKey) {
		context.set("billKey", billKey);
		context.set("model", model);
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		context.set("$billDef", billDef);
		if (!context.containsName("$model")) {
			// preSave
			BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
			context.set("$model", billInstance.getDataSetInstance());
		}
		
		BillHandlerManger billMananger = BillHandlerManger.instance();
		billMananger.handler(context, "save"); //
		if (context.hasError()) {
			LOG.error(context.getErrorJSON());
			return;
		}
		
		
	}


	private JSONObject buildContext(Record head, List<Record> bodys,String headKey, String bodyKey) {
		JSONObject result = new JSONObject();
		JSONObject hjObject = new JSONObject();
		JSONObject bjObject = new JSONObject();
		JSONArray cos = new JSONArray();
		JSONArray cosBody = new JSONArray();
		cos.add(head.toJson());
		hjObject.put("head", true);
		hjObject.put("cos", cos);
		
		bjObject.put("head", false);
		for (Record body : bodys) {
			cosBody.add(body.toJson());
		}
		bjObject.put("cos", cosBody);
		
		result.put(headKey, hjObject);
		result.put(bodyKey, bjObject);
		
		return result;
	}

	/**
	 * 拉取WMS销售出库单
	 * @param task 
	 */
	public void XSCKDFun(Task task) {
		Record record = task.getRecord();
		Record ds = new Record();
		ds.set("danjubianhao", "XSCK"+TimeUtil.getOrderNo());
		ds.set("sjdjbh", record.getStr("djbh"));
		ds.set("zxs", record.getStr("zxs"));
		ds.set("zcqmc", record.getStr("zcqmc"));
		ds.set("kaipiaoriqi", new Date());
		Record xsOrder = Db.findFirst("select * from xyy_erp_bill_xiaoshoudingdan where danjubianhao = '"+record.getStr("djbh")+"' and status = 22 limit 1 ");
		if (xsOrder==null) {
			LOG.error("销售订单不存在【"+record.getStr("djbh")+"】");
//			Db.use(DictKeys.db_dataSource_wms_mid).update("update int_wms_xsck_bill set is_zx ='是' where djbh = '"+record.getStr("djbh")+"' ");
			return;
		}
		Record chukudan = Db.findFirst("select * from xyy_erp_bill_xiaoshouchukudan where sjdjbh = '"+record.getStr("djbh")+"' limit 1");
		if (chukudan!=null) {
			LOG.error("销售出库单已存在【"+chukudan.getStr("danjubianhao")+"】");
			Db.use(DictKeys.db_dataSource_wms_mid).update("update int_wms_xsck_bill set is_zx ='是' where djbh = '"+record.getStr("djbh")+"' ");
			return;
		}
		ds.set("yxsdh", xsOrder.getStr("danjubianhao"));
		ds.set("ydsdh", xsOrder.getStr("dianshangbianhao"));
		ds.set("kaipiaoyuan", xsOrder.getStr("kaipiaoyuan"));
		ds.set("yewuyuan", xsOrder.getStr("yewuyuan"));
		ds.set("xiaoshouyuan", xsOrder.getStr("xiaoshouyuan"));
		ds.set("xsyID", xsOrder.getStr("xsyID"));
		ds.set("kehubianhao", xsOrder.getStr("kehubianhao"));
		ds.set("kehumingcheng", xsOrder.getStr("kehumingcheng"));
		ds.set("lianxiren", xsOrder.getStr("lianxiren"));
		ds.set("lianxidianhua", xsOrder.getStr("lianxidianhua"));
		ds.set("lianxidizhi", xsOrder.getStr("lianxidizhi"));
		
		ds.set("jiesuanfangshi", xsOrder.getInt("jiesuanfangshi"));
		ds.set("tihuofangshi", xsOrder.getInt("tihuofangshi"));
		ds.set("hanshuijine", xsOrder.getBigDecimal("hanshuijine"));
		ds.set("beizhu", xsOrder.getStr("beizhu"));
		ds.set("orgId", orgId);
		ds.set("orgCode", orgCode);
		ds.set("rowID", UUIDUtil.newUUID());
		ds.set("status", 20);
			
		
		this.saveDetail(ds);
		
	}

	/**
	 * 保存销售出库单明细
	 * @param ds
	 */
	private void saveDetail(Record ds) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List<Record> ckList = new ArrayList<>();
		List<Record> list = new ArrayList<>();
		String danjubianhao = ds.getStr("sjdjbh");
		List<Record> orderDetails = Db.use(DictKeys.db_dataSource_wms_mid).find("select * from int_wms_xsck_bill where djbh = '"+danjubianhao+"'");
		List<Record> xList = Db.find("select * from xyy_erp_bill_xiaoshoudingdan_details "
				+ "where danjubianhao = '"+ds.getStr("sjdjbh")+"'  ");
		if (xList==null||xList.size()==0) {
			return;
		}
		int xSum = 0;
		for (Record xOrder : xList) {
			xSum = xSum + xOrder.getBigDecimal("shuliang").intValue();
		}
		
		BigDecimal jine = new BigDecimal(0);//金额
		BigDecimal shuie = new BigDecimal(0);//税额
		BigDecimal chengbenjine = new BigDecimal(0);//成本金额
		BigDecimal maoli = new BigDecimal(0);//毛利
		BigDecimal oldhanshuijine = new BigDecimal(0);//原单金额
		BigDecimal youhuijine = new BigDecimal(0);//优惠金额
		
		int chukuSum = 0;
		//存销售订单含税金额总额
		Map<String, JSONObject> jineList = new HashMap<>(); 
		for (Record record2 : orderDetails) {
			record2.set("is_zx", "是");
			Record DSDetail = new Record();
			
			Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where goodsid = '"+record2.getStr("spid")+"' limit 1");
			if (sp==null) {
				LOG.error("商品基本信息不存在【"+record2.getStr("spid")+"】");
				continue;
			}
			
			Record spKC = Db.findFirst("select * from xyy_erp_bill_shangpinzongkucun where shangpinId = '"+record2.getStr("spid")+"' limit 1");
			if (spKC==null) {
				LOG.error("商品总库存不存在【"+record2.getStr("CODE")+"】");
				continue;
			}
			
			Record oRecord = Db.findFirst("select * from xyy_erp_bill_xiaoshoudingdan_details "
					+ "where shangpinbianhao = '"+sp.getStr("shangpinbianhao")+"' and danjubianhao = '"+ds.getStr("sjdjbh")+"'  limit 1");
			if (oRecord==null) {
				LOG.error("销售订单明细不存在【"+ds.getStr("sjdjbh")+"】");
				continue;
			}
			if (jineList.get(oRecord.getStr("shangpinbianhao"))==null) {
				JSONObject object = new JSONObject();
				object.put("shuliang", oRecord.getBigDecimal("shuliang").intValue());
				object.put("hanshuijine", oRecord.getBigDecimal("hanshuijine"));
				object.put("jine", oRecord.getBigDecimal("jine"));
				jineList.put(oRecord.getStr("shangpinbianhao"), object);
			}
			JSONObject order = jineList.get(oRecord.getStr("shangpinbianhao"));
			
			DSDetail.set("shangpinbianhao", sp.getStr("shangpinbianhao"));
			DSDetail.set("shangpinmingcheng", sp.getStr("shangpinmingcheng"));
			DSDetail.set("kehubianhao", ds.getStr("kehubianhao"));
			DSDetail.set("guige", sp.getStr("guige"));
			DSDetail.set("jixing", sp.getInt("jixing"));
			DSDetail.set("danwei", sp.getInt("danwei"));
			DSDetail.set("shengchanchangjia", sp.getStr("shengchanchangjia"));
			
			DSDetail.set("goodsid", record2.getStr("spid"));
			DSDetail.set("shuliang", record2.getBigDecimal("sl"));
			chukuSum = chukuSum + DSDetail.getBigDecimal("shuliang").intValue();
			DSDetail.set("danjia", oRecord.getBigDecimal("danjia"));
			DSDetail.set("hanshuijia", oRecord.getBigDecimal("hanshuijia"));
			
			if (DSDetail.getBigDecimal("shuliang").intValue()==order.getIntValue("shuliang")) {
				DSDetail.set("hanshuijine", order.getBigDecimal("hanshuijine"));
				DSDetail.set("jine", order.getBigDecimal("jine"));
			}else if (order.getIntValue("shuliang")>
					DSDetail.getBigDecimal("shuliang").intValue()) {
				DSDetail.set("hanshuijine", DSDetail.getBigDecimal("shuliang").multiply(DSDetail.getBigDecimal("hanshuijia")).setScale(2,RoundingMode.HALF_UP));
				DSDetail.set("jine", DSDetail.getBigDecimal("shuliang").multiply(DSDetail.getBigDecimal("danjia")).setScale(2,RoundingMode.HALF_UP));
				order.put("shuliang", order.getIntValue("shuliang")-
					DSDetail.getBigDecimal("shuliang").intValue());
				order.put("hanshuijine", order.getBigDecimal("hanshuijine").
						subtract(DSDetail.getBigDecimal("hanshuijine")));
				order.put("jine", order.getBigDecimal("jine").
						subtract(DSDetail.getBigDecimal("jine")));
			}
			DSDetail.set("shuilv", oRecord.getBigDecimal("shuilv"));
			
			DSDetail.set("shuie", DSDetail.getBigDecimal("hanshuijine").subtract(DSDetail.getBigDecimal("jine")));
			DSDetail.set("oldhanshuijia", oRecord.getBigDecimal("oldhanshuijia"));
			DSDetail.set("oldhanshuijine", DSDetail.getBigDecimal("shuliang").multiply(DSDetail.getBigDecimal("oldhanshuijia")).setScale(2,RoundingMode.HALF_UP));
			DSDetail.set("youhuijine", DSDetail.getBigDecimal("oldhanshuijine").subtract(DSDetail.getBigDecimal("hanshuijine")));
			DSDetail.set("koulv", 100);
			
			Record ph = Db.findFirst("select * from xyy_erp_dic_shangpinpihao where pihao = '"+record2.getStr("ph")+"' and goodsId = '"+DSDetail.getStr("goodsid")+"' ");
			if (ph!=null) {
				DSDetail.set("pihaoId", ph.getStr("pihaoId"));
			}
			DSDetail.set("pihao", ph.getStr("pihao"));
			try {
				DSDetail.set("shengchanriqi", format.parse(record2.getStr("RQ_SC")));
				DSDetail.set("youxiaoqizhi", format.parse(record2.getStr("YXQZ")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DSDetail.set("chengbendanjia", spKC.getBigDecimal("chengbendanjia").setScale(6));
			DSDetail.set("chengbenjine", DSDetail.getBigDecimal("chengbendanjia").multiply(record2.getBigDecimal("sl")).setScale(2,RoundingMode.HALF_UP));
			DSDetail.set("maoli",DSDetail.getBigDecimal("hanshuijine").subtract(DSDetail.getBigDecimal("chengbenjine")));
			DSDetail.set("pizhunwenhao", oRecord.getStr("pizhunwenhao"));
			DSDetail.set("chandi", oRecord.getStr("chandi"));
			DSDetail.set("danjubianhao", ds.getStr("danjubianhao"));
			DSDetail.set("orgId", orgId);
			DSDetail.set("orgCode", orgCode);
			DSDetail.set("rowID", UUIDUtil.newUUID());
			
			jine = jine.add(DSDetail.getBigDecimal("jine"));
			shuie = shuie.add(DSDetail.getBigDecimal("shuie"));
			chengbenjine = chengbenjine.add(DSDetail.getBigDecimal("chengbenjine"));
			oldhanshuijine = oldhanshuijine.add(DSDetail.getBigDecimal("oldhanshuijine"));
			youhuijine = youhuijine.add(DSDetail.getBigDecimal("youhuijine"));
			maoli = maoli.add(DSDetail.getBigDecimal("maoli"));
			
			Record beforeRecord = this.findSimpleSP(list,DSDetail);
			if (beforeRecord!=null) {
				beforeRecord.set("shuliang", beforeRecord.getBigDecimal("shuliang").add(DSDetail.getBigDecimal("shuliang")));
				beforeRecord.set("hanshuijine", beforeRecord.getBigDecimal("hanshuijine").add(DSDetail.getBigDecimal("hanshuijine")));
				beforeRecord.set("jine", beforeRecord.getBigDecimal("jine").add(DSDetail.getBigDecimal("jine")));
				beforeRecord.set("chengbenjine", beforeRecord.getBigDecimal("chengbenjine").add(DSDetail.getBigDecimal("chengbenjine")));
				beforeRecord.set("oldhanshuijine", beforeRecord.getBigDecimal("oldhanshuijine").add(DSDetail.getBigDecimal("oldhanshuijine")));
				beforeRecord.set("shuie", beforeRecord.getBigDecimal("shuie").add(DSDetail.getBigDecimal("shuie")));
				beforeRecord.set("maoli", beforeRecord.getBigDecimal("maoli").add(DSDetail.getBigDecimal("maoli")));
				beforeRecord.set("youhuijine", beforeRecord.getBigDecimal("youhuijine").add(DSDetail.getBigDecimal("youhuijine")));
			}else {
				list.add(DSDetail);
			}
			
		}
		if (xSum>0&&chukuSum>0&&xSum==chukuSum) {
			ckList.addAll(orderDetails);

			ds.set("jine", jine);
			ds.set("shuie", shuie);
			ds.set("chengbenjine", chengbenjine);
			ds.set("maoli", maoli);
			ds.set("oldhanshuijine", oldhanshuijine);
			ds.set("youhuijine", youhuijine);
			
			BillContext context = new BillContext();
			//插入打印数据表
			this.printFunc(ds,list);
			this.saveAction(context,this.buildContext(ds, list, "xiaoshouchukudan", "xiaoshouchukudan_details"),"xiaoshouchukudan");
		
		}
		//回写中间表状态
		if (ckList!=null && ckList.size()>0) {
			Db.use(DictKeys.db_dataSource_wms_mid).batchUpdate("int_wms_xsck_bill","djbh,yzid,dj_sort,recnum", ckList, 500);
		}
	
	}
	
	//打印方法
	private void printFunc(Record record, List<Record> list) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		//插入头
		Record head = new Record();
		head.set("billcode", record.getStr("yxsdh"));
		head.set("ddbh", record.getStr("ydsdh"));
		head.set("dates", format.format(record.getDate("kaipiaoriqi")));
		
		head.set("zxs", record.getStr("zxs"));
		head.set("zcqmc", record.getStr("zcqmc"));
		
		head.set("khbh", record.getStr("kehubianhao"));
		head.set("khmc", record.getStr("kehumingcheng"));
		head.set("xiaoshouyuan", record.getStr("xiaoshouyuan"));
		head.set("shouhuodizhi", record.getStr("lianxidizhi"));
		head.set("taxamount", record.getBigDecimal("oldhanshuijine"));
		head.set("discount", record.getBigDecimal("youhuijine"));
		head.set("shifujine", record.getBigDecimal("hanshuijine"));
		Db.use(DictKeys.db_dataSource_print_mid).save("xsddmt", head);
		
		Record saveRecord = Db.use(DictKeys.db_dataSource_print_mid).findFirst("select * from xsddmt where billcode = '"+record.getStr("yxsdh")+"'");
		//插入体
		List<Record> bodys = new ArrayList<>();
		for (Record sp : list) {
			Record detail = new Record();
			detail.set("billno", saveRecord.getInt("BILLNO"));
			detail.set("spbh", sp.getStr("shangpinbianhao"));
			detail.set("spmc", sp.getStr("shangpinmingcheng"));
			detail.set("guige", sp.getStr("guige"));
			detail.set("jixing", SysSelectCachePlug.getValue("shangpinjixing", ""+sp.getInt("jixing")));
			detail.set("sccj", sp.getStr("shengchanchangjia"));
			detail.set("danwei", SysSelectCachePlug.getValue("baozhuangdanwei", ""+sp.getInt("danwei")));
			detail.set("shuliang", sp.getBigDecimal("shuliang").intValue());
			detail.set("danjia", sp.getBigDecimal("oldhanshuijia"));
			detail.set("jine", sp.getBigDecimal("oldhanshuijine"));
			detail.set("pihao", sp.getStr("pihao"));
			detail.set("scrq", format.format(sp.getDate("shengchanriqi")));
			detail.set("yxqz", format.format(sp.getDate("youxiaoqizhi")));
			detail.set("pzwh", sp.getStr("pizhunwenhao"));
			bodys.add(detail);
		}
		
		
		Db.use(DictKeys.db_dataSource_print_mid).batchSave("xsdddt", bodys, 500);
		
	}

	//判断是否商品ID和批号一样
	private Record findSimpleSP(List<Record> list, Record record2) {
		for (Record record : list) {
			if (record.getStr("goodsid").equals(record2.getStr("goodsid"))
					&&record.getStr("pihao").equals(record2.getStr("pihao"))) {
				return record;
			}
		}
		return null;
	}

	/**
	 * 拉取WMS销售销售退回入库单
	 * @param task 
	 */
	public void XSTHRKFun(Task task) {
		Record record = task.getRecord();
		Record ds = new Record();
		ds.set("danjubianhao", "XTRK"+TimeUtil.getOrderNo());
		ds.set("sjdjbh", record.getStr("djbh"));
		Record xsOrder = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuihuidan where danjubianhao = '"+record.getStr("djbh")+"' and status = 42 limit 1 ");
		if (xsOrder==null) {
			LOG.error("销售退回单不存在【"+record.getStr("djbh")+"】");
//			Db.use(DictKeys.db_dataSource_wms_mid).update("update int_wms_xsck_bill set is_zx = '是' where djbh = '"+record.getStr("djbh")+"' ");
			return;
		}
		Record xsth = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuihuirukudan where sjdjbh = '"+record.getStr("djbh")+"' limit 1");
		if (xsth!=null) {
			LOG.error("销售退回单已存在【"+xsth.getStr("danjubianhao")+"】");
			Db.use(DictKeys.db_dataSource_wms_mid).update("update int_wms_xsth_bill set is_zx = '是' where djbh = '"+record.getStr("djbh")+"' ");
			return;
		}
		ds.set("kehubianhao", xsOrder.getStr("kehubianhao"));
		ds.set("kehumingcheng", xsOrder.getStr("kehumingcheng"));
		ds.set("lianxiren", xsOrder.getStr("lianxiren"));
		Record order = Db.findFirst("select * from xyy_erp_bill_xiaoshoudingdan "
				+ "where danjubianhao = '"+xsOrder.getStr("yxsdh")+"' limit 1");
		if (order!=null) {
			ds.set("jiesuanfangshi", order.getInt("jiesuanfangshi"));
			ds.set("yxsdh", order.getStr("danjubianhao"));
			ds.set("ydsdh", order.getStr("dianshangbianhao"));
		}
		ds.set("kaipiaoriqi", xsOrder.getDate("createTime"));
		ds.set("kaipiaoyuan", xsOrder.getStr("creatorName"));
		ds.set("yewuyuan", xsOrder.getStr("yewuyuan"));
		ds.set("yxsdh", xsOrder.getStr("yxsdh"));
		ds.set("orgId", orgId);
		ds.set("orgCode", orgCode);
		ds.set("rowID", UUIDUtil.newUUID());
			
		this.saveDetail2(ds);
		
	}

	/**
	 * 保存销售退回入库单明细
	 * @param ds
	 */
	private void saveDetail2(Record ds) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List<Record> ckList = new ArrayList<>();
		List<Record> list = new ArrayList<>();
		String danjubianhao = ds.getStr("sjdjbh");
		List<Record> orderDetails = Db.use(DictKeys.db_dataSource_wms_mid).find("select * from int_wms_xsth_bill where  djbh = '"+danjubianhao+"'");
		List<Record> xList = Db.find("select * from xyy_erp_bill_xiaoshoutuihuidan_details "
				+ "where danjubianhao = '"+danjubianhao+"'  ");
		if (xList==null||xList.size()==0) {
			return;
		}
		int xSum = 0;
		for (Record xOrder : xList) {
			xSum = xSum + xOrder.getBigDecimal("tuihuoshuliang").intValue();
		}
		
		int rukuSum = 0;
		BigDecimal hanshuijine = new BigDecimal(0);
		BigDecimal jine = new BigDecimal(0);
		BigDecimal shuie = new BigDecimal(0);
		BigDecimal chengbenjine = new BigDecimal(0);
		BigDecimal maoli = new BigDecimal(0);
		
		for (Record record2 : orderDetails) {
			Record DSDetail = new Record();
			record2.set("is_zx", "是");
			
			Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where goodsid = '"+record2.getStr("spid")+"' limit 1");
			if (sp==null) {
				LOG.error("商品基本信息不存在【"+record2.getStr("spid")+"】");
				continue;
			}
			
			Record oRecord = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuihuidan_details "
					+ "where shangpinbianhao = '"+sp.getStr("shangpinbianhao")+"' and danjubianhao = '"+ds.getStr("sjdjbh")+"'  limit 1");
			if (oRecord==null) {
				LOG.error("销售退回单明细不存在【"+ds.getStr("sjdjbh")+"】");
				continue;
			}
			Record kc = Db.findFirst("select * from xyy_erp_bill_shangpinzongkucun where shangpinId = '"+record2.getStr("spid")+"' limit 1");
			if (kc==null) {
				//加入新增商品总库存逻辑
				RecordDataUtil.createNewShangPinZongKuCun(record2,oRecord);
				
			}
			DSDetail.set("danjubianhao", ds.getStr("danjubianhao"));
			DSDetail.set("shangpinbianhao", sp.getStr("shangpinbianhao"));
			DSDetail.set("shangpinmingcheng", sp.getStr("shangpinmingcheng"));
			DSDetail.set("kehubianhao", ds.getStr("kehubianhao"));
			DSDetail.set("goodsid", sp.getStr("goodsid"));
			DSDetail.set("guige", sp.getStr("guige"));
			DSDetail.set("danwei", sp.getInt("danwei"));
			DSDetail.set("shengchanchangjia", sp.getStr("shengchanchangjia"));
			DSDetail.set("shuliang", record2.getBigDecimal("sl").negate());
			rukuSum = rukuSum + record2.getBigDecimal("sl").intValue();
			DSDetail.set("hanshuijia", oRecord.getBigDecimal("hanshuijia"));
			DSDetail.set("hanshuijine", record2.getBigDecimal("sl").multiply(DSDetail.getBigDecimal("hanshuijia")).negate());
			DSDetail.set("shuilv", oRecord.getBigDecimal("shuilv"));
			
			DSDetail.set("danjia", DSDetail.getBigDecimal("hanshuijine").divide(DSDetail.getBigDecimal("shuliang"),6,RoundingMode.HALF_UP)
					.divide(new BigDecimal(1).add(DSDetail.getBigDecimal("shuilv").divide(new BigDecimal(100))),6,RoundingMode.HALF_UP));
			DSDetail.set("jine", record2.getBigDecimal("sl").multiply(DSDetail.getBigDecimal("danjia")).setScale(2, RoundingMode.HALF_UP).negate());
			DSDetail.set("shuie", DSDetail.getBigDecimal("hanshuijine").subtract(DSDetail.getBigDecimal("jine")));
			DSDetail.set("chengbendanjia", oRecord.getBigDecimal("chengbendanjia").setScale(6));
			DSDetail.set("chengbenjine", record2.getBigDecimal("sl").multiply(DSDetail.getBigDecimal("chengbendanjia")).setScale(2,RoundingMode.HALF_UP).negate());
			DSDetail.set("maoli", DSDetail.getBigDecimal("hanshuijine").subtract(DSDetail.getBigDecimal("chengbenjine")));
			if (!StringUtil.isEmpty(oRecord.getStr("pihaoId"))) {
				DSDetail.set("pihaoId", oRecord.getStr("pihaoId"));
			}else {
				//加入新增批号逻辑
				Record ph = Db.findFirst("select * from xyy_erp_dic_shangpinpihao "
						+ "where pihao = '"+record2.getStr("ph")+"' and goodsId = '"+sp.getStr("goodsid")+"' limit 1");
				if (ph!=null) {
					DSDetail.set("pihaoId", ph.getStr("pihaoId"));
				}else {
					String ID = "PH" + TimeUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.getRandomCode(3);
					RecordDataUtil.createNewShangPinPiHao(record2,sp,ID);
					RecordDataUtil.createNewShangPinPiHaoKuCun(record2,sp,ID);
					DSDetail.set("pihaoId", ID);
				}
			}
			DSDetail.set("pihao", record2.getStr("ph"));
			DSDetail.set("yanshoupingding", record2.getStr("yspd"));
			try {
				DSDetail.set("shengchanriqi", format.parse(record2.getStr("rq_sc")));
				DSDetail.set("youxiaoqizhi", format.parse(record2.getStr("yxqz")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DSDetail.set("pizhunwenhao", oRecord.getStr("pizhunwenhao"));
			DSDetail.set("chandi", oRecord.getStr("chandi"));
			DSDetail.set("tuihuoyuanyin", oRecord.getStr("tuihuoyuanyin"));
			DSDetail.set("beizhu", record2.getStr("yspd"));
			DSDetail.set("orgId", orgId);
			DSDetail.set("orgCode", orgCode);
			DSDetail.set("rowID", UUIDUtil.newUUID());
			DSDetail.set("danjubianhao", ds.getStr("danjubianhao"));
			
			
			list.add(DSDetail);
			
			hanshuijine = hanshuijine.add(DSDetail.getBigDecimal("hanshuijine"));
			jine = jine.add(DSDetail.getBigDecimal("jine"));
			shuie = shuie.add(DSDetail.getBigDecimal("shuie"));
			chengbenjine = chengbenjine.add(DSDetail.getBigDecimal("chengbenjine"));
			maoli = maoli.add(DSDetail.getBigDecimal("maoli"));
			
		}
		if (xSum>0&&rukuSum>0&&xSum==rukuSum) {
			ckList.addAll(orderDetails);
			
			ds.set("hanshuijine", hanshuijine);
			ds.set("jine", jine);
			ds.set("shuie", shuie);
			ds.set("chengbenjine", chengbenjine);
			ds.set("maoli", maoli);
			
			BillContext context = new BillContext();
			this.saveAction(context,this.buildContext(ds, list, "xiaoshoutuihuirukudan", "xiaoshoutuihuirukudan_details"),"xiaoshoutuihuirukudan");
			
		}
		if (ckList!=null && ckList.size()>0) {
			Db.use(DictKeys.db_dataSource_wms_mid).batchUpdate("int_wms_xsth_bill","djbh,yzid,dj_sort", ckList, 500);
		}
	}
	
	/**
	 * 推销售订单到WMS中间表
	 */
	public void XSDDFun(Record head,List<Record> bodys) {
		List<Record> list = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		Record jilu = Db.use(DictKeys.db_dataSource_wms_mid).findFirst("select * from v_erp_xsck where djbh = '"+head.getStr("danjubianhao")+"' ");
		if (jilu!=null) {
			//回写销售订单状态
			if (head!=null) {
				Db.update("update xyy_erp_bill_xiaoshoudingdan set status = 22,shifouxiatui = 1 where BillID = '"+head.getStr("BillID")+"'");
			}
			//回写电商中间表状态
			if (head!=null&&!StringUtil.isEmpty(head.getStr("dianshangbianhao"))) {
				Db.use(DictKeys.db_dataSource_wms_mid).update("update tb_order_tmp set STATE = 2 where ORDERNO = '"+head.getStr("dianshangbianhao")+"'");
			}
			return;
		}
		for (Record detail : bodys) {
			Record record = new Record();
			Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where goodsid ='"+detail.getStr("goodsid")+"' limit 1");
			if (sp==null) {
				LOG.error("商品基本信息不存在【"+detail.getStr("goodsid")+"】");
				continue;
			}
			Record kehu = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where kehubianhao = '"+head.getStr("kehubianhao")+"' limit 1");
			if (kehu==null) {
				LOG.error("客户基本信息不存在【"+head.getStr("kehubianhao")+"】");
				continue;
			}
			record.set("DJBH", head.getStr("danjubianhao"));
			record.set("dwbh", kehu.getStr("clientid"));
			record.set("RQ", format.format(head.getDate("createTime")));
			record.set("RY_YWY", head.getStr("kpyID"));
			record.set("BMID", "K0Z9XGCUW4J");
			record.set("THFS", "配送");
			record.set("KHBZ", head.getStr("remark")==null?"无":head.getStr("remark"));
			record.set("DDLX", "正常销售");
			record.set("SF_ZY", "否");
			record.set("YZID", "O0Z8M62IK57");
			record.set("DJ_SORT", detail.getInt("SN"));
			record.set("SPID", detail.getStr("goodsid"));
			record.set("SL", detail.getBigDecimal("shuliang"));
			record.set("JS", detail.getBigDecimal("shuliang").intValue()/(double)sp.getInt("dbzsl"));
			record.set("LSS", detail.getBigDecimal("shuliang").intValue()%sp.getInt("dbzsl").longValue());
			record.set("phyq", "无");
			record.set("SHDZ", head.getStr("lianxidizhi"));
			record.set("cityname", kehu.get("sheng")!=null?AreaService.findAreaById(kehu.getInt("sheng")).getStr("areaName"):null);
			record.set("cityname2", kehu.get("shi")!=null?AreaService.findAreaById(kehu.getInt("shi")).getStr("areaName"):null);
			record.set("cityname3", kehu.get("qu")!=null?AreaService.findAreaById(kehu.getInt("qu")).getStr("areaName"):null);
			record.set("SHR", head.getStr("lianxiren"));
			record.set("LXFS", head.getStr("lianxidianhua"));
			record.set("IDNUMBER", kehu.getStr("zhengjianhaoma"));
			record.set("is_jj", "");
			record.set("WMFLG", "N");
			record.set("lastmodifytime", format.format(head.getDate("createTime")));
			record.set("is_dshk", "N");
			record.set("dshkje", 0);
			
			list.add(record);
		}
			
		boolean flag = false;
		
		if (list!=null && list.size()>0) {
//			
//			StringBuffer sb=new StringBuffer();
//			for(Record r:list){
//				sb.append("insert into v_erp_xsck(");
//				for(String col:r.getColumnNames()){
//					sb.append(col);
//					sb.append(",");
//				}
//				sb=sb.deleteCharAt(sb.length()-1);
//				sb.append(") ");
//				sb.append(" values(");
//				for(String col:r.getColumnNames()){
//					sb.append(r.get(col).toString());
//					sb.append(",");
//				}
//				sb=sb.deleteCharAt(sb.length()-1);
//				sb.append(")");
//				sb.append(";");
//			}
//			System.out.println(sb);
			int[] result = Db.use(DictKeys.db_dataSource_wms_mid).batchSave("v_erp_xsck", list, 500);
			if (result[0]>0) {
				flag = true;
			}
		}
		
		
		if (!flag) {
			return;
		}
		//回写销售订单状态
		if (head!=null) {
			Db.update("update xyy_erp_bill_xiaoshoudingdan set status = 22,shifouxiatui = 1 where BillID = '"+head.getStr("BillID")+"'");
		}
		//回写电商中间表状态
		if (head!=null&&!StringUtil.isEmpty(head.getStr("dianshangbianhao"))) {
			Db.use(DictKeys.db_dataSource_wms_mid).update("update tb_order_tmp set STATE = 2 where ORDERNO = '"+head.getStr("dianshangbianhao")+"'");
		}
	}
	
	/**
	 * 推销售退回单到WMS中间表
	 */
	public void XSTHFun(Record head,List<Record> bodys) {
		Record jilu = Db.use(DictKeys.db_dataSource_wms_mid).findFirst("select * from v_erp_xsth where djbh = '"+head.getStr("danjubianhao")+"' ");
		if (jilu!=null) {
			//更新销售退回单状态
			if (head!=null) {
				Db.update("update xyy_erp_bill_xiaoshoutuihuidan set status = 42,shifouxiatui = 1 where BillID = '"+head.getStr("BillID")+"'");
			}
			return;
		}
		List<Record> list = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Record detail : bodys) {
			Record record = new Record();
			Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao ='"+detail.getStr("shangpinbianhao")+"' limit 1");
			if (sp==null) {
				LOG.error("商品基本信息不存在【"+detail.getStr("shangpinbianhao")+"】");
				continue;
			}
			Record kehu = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where kehubianhao = '"+head.getStr("kehubianhao")+"' limit 1");
			if (kehu==null) {
				LOG.error("客户基本信息不存在【"+head.getStr("kehubianhao")+"】");
				continue;
			}
			record.set("DJBH", head.getStr("danjubianhao"));
			record.set("dwbh", kehu.getStr("clientid"));
			Record dept = Db.findFirst("select b.deptId from tb_sys_dept_user_relation a,tb_sys_dept b "
			+ "where a.userId = '"+head.getStr("creator")+"' and a.deptId = b.id limit 1");
			if (dept!=null) {
				record.set("bmid", dept.getStr("deptId"));
			}
			record.set("RQ", format.format(head.getDate("createTime")));
			record.set("ry_ywy", head.getStr("yewuyuan"));
			record.set("ry_zjy","");
			record.set("THLB", "正常退货");
			record.set("YZID", "O0Z8M62IK57");
			record.set("DJ_SORT", detail.getInt("SN"));
			record.set("SPID", detail.getStr("goodsid"));
			record.set("SL", detail.getBigDecimal("tuihuoshuliang"));
			record.set("JS", record.getBigDecimal("SL").divide(BigDecimal.valueOf(sp.getInt("dbzsl").longValue()),2,RoundingMode.HALF_UP));
			record.set("LSS", detail.getBigDecimal("tuihuoshuliang").intValue()%sp.getInt("dbzsl").longValue());
			record.set("THYY", detail.getStr("tuihuoyuanyin"));
			record.set("PH", detail.getStr("pihao"));
			record.set("rq_sc", detail.getDate("shengchanriqi"));
			record.set("yxqz", detail.getDate("youxiaoqizhi"));
			record.set("YSPD", "1");
			record.set("MJPH", "");
			record.set("JKZCZH", "");
			record.set("WMFLG", "N");
			record.set("lastmodifytime", format2.format(head.getDate("createTime")));
			
			list.add(record);
		}
		
		boolean flag = false;
		
		if (list!=null && list.size()>0) {
			int[] result = Db.use(DictKeys.db_dataSource_wms_mid).batchSave("v_erp_xsth", list, 500);
			if (result[0]>0) {
				flag = true;
			}
		}
		
		if (!flag) {
			return;
		}
		//更新销售退回单状态
		if (head!=null) {
			Db.update("update xyy_erp_bill_xiaoshoutuihuidan set status = 42,shifouxiatui = 1 where BillID = '"+head.getStr("BillID")+"'");
		}
		
		
	}



	//推销售订单到中间库
	public void XSDDFun(Task task) {
		List<Record> bodys = Db.find("select * from xyy_erp_bill_xiaoshoudingdan_details "
				+ "where BillID = '"+task.getRecord().getStr("BillID")+"'");
		this.XSDDFun(task.getRecord(), bodys);
	}


	//推销售退回单到中间库
	public void XSTHFun(Task task) {
		List<Record> bodys = Db.find("select * from xyy_erp_bill_xiaoshoutuihuidan_details "
				+ "where BillID = '"+task.getRecord().getStr("BillID")+"'");
		this.XSTHFun(task.getRecord(), bodys);
		
	}
	
	//扫描电商订单，进行拦截下推
	public void checkDSDDFun(Task task) {
		List<Record> bodys = Db.find("select * from xyy_erp_bill_dianshangdingdan_details "
				+ "where BillID = '"+task.getRecord().getStr("BillID")+"'");
		this.checkDSDD(task.getRecord(),bodys);
		
	}
	
	private void checkDSDD(Record head, List<Record> bodys) {
		BillContext context = new BillContext();
		//先进行转化筛选，通过的再保存
		context.set("mapkey", "dsdd2xsdd");
		context.set("maptype", 1);
		context.set("data", this.buildContext(head, bodys,"dianshangdingdan","dianshangdingdan_details"));
		this.mapAction(context,head,bodys);
	}
	
	private void mapAction(BillContext context, Record record, List<Record> list) {
		//mapkey=xsckd2xsthd,maptype=4,data=[]
		MapHandlerManger mapananger = MapHandlerManger.instance();
		String mapType = context.getString("mapType");
		mapananger.handler(context, "s" + mapType);
		if (context.hasError()) {
			LOG.error(context.getErrorJSON().toString());
			return;
		}
		
	}
	
}
