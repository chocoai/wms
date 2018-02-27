package com.xyy.bill.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.handler.BillHandlerManger;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.BillInstance;
import com.xyy.bill.instance.SequenceManager;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.erp.platform.common.tools.ToolDateTime;
import com.xyy.util.UUIDUtil;

import net.sf.json.JSONArray;

/**
 * 
 * @author SKY
 *
 */
public class PullWMSDataUtil {
	private static final Log log = Log.getLog(PullWMSDataUtil.class);

	// 公司机构 湖北分公司
	public static final String orgId = "08e78dd8ed6b44b2af3cba9ddc521e61";
	// 公司编号 湖北分公司
	public static final String orgCode = "A-B-001";

	// 采购订单v_erp_cgdd
	public static final String ERP_TABLE_CGDD = "v_erp_cgdd";

	// 购进退出v_erp_gjtc
	public static final String ERP_TABLE_GJTC = "v_erp_gjtc";

	// wms购进退出int_wms_gjtc_bill
	public static final String WMS_TABLE_GJTC = "int_wms_gjtc_bill";

	// 采购入库int_wms_cgrk_bill
	public static final String WMS_TABLE_CGRK = "int_wms_cgrk_bill";

	public static int getMaxSN(String realTableName) {
		// 调用序列管理器，下发序列值
		return SequenceManager.instance().nextSequence(realTableName);
	}
	
	public static String orderno() {// 10
		// 订单编号 varchar 规则：YBM+年月日时分秒+3位随机码, 例如：YBM20160426112240764
		return SequenceBuilder.nextSequence("orderNo_"+TimeUtil.format(new Date(), "yyyyMMdd"), "", 10);
	}
	
	public static String orderno2() {// 12
		// 订单编号 varchar 规则：YBM+年月日+4位随机码, 例如：YBM20160426112240764
		return SequenceBuilder.nextSequence("orderNo2_"+TimeUtil.format(new Date(), "yyyyMMdd"), "", 12);
	}


	/**
	 * 组装数据模型
	 * 
	 * @param head
	 * @param bodys
	 * @param headKey
	 * @param bodyKey
	 * @return
	 */
	public static JSONObject buildContext(Record head, List<Record> bodys, String headKey, String bodyKey) {
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
	 * 统一保存接口
	 * 
	 * @param context
	 * @param model
	 * @param billKey
	 */
	public static void saveAction(BillContext context, JSONObject model, String billKey) {
		context.set("billKey", billKey);
		context.set("model", model);
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		context.set("$billDef", billDef);
		if (!context.containsName("$model")) {
			BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
			context.set("$model", billInstance.getDataSetInstance());
		}

		BillHandlerManger billMananger = BillHandlerManger.instance();
		billMananger.handler(context, "save"); //
		if (context.hasError()) {
			log.error(context.getErrorJSON());
			return;
		}
	}

	/**
	 * 根据商品id查询商品批号
	 * 
	 * @param goodsid
	 * @return
	 */
	public static Record findGoodsBatch(String goodsid) {
		String sql = "select * from xyy_erp_dic_shangpinpihao where goodsId = ?";
		return Db.findFirst(sql, goodsid);
	}

	/**
	 * 根据商品id和批号查询商品批号
	 * 
	 * @param goodsid
	 * @return
	 */
	public static Record findGoodsBatchByName(String goodsid, String pihao) {
		String sql = "select * from xyy_erp_dic_shangpinpihao where pihao = ? and goodsId = ? limit 1";
		Record record = Db.findFirst(sql, pihao, goodsid);
		return record == null ? null : record;
	}

	/**
	 * 查询供应商
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getSupplier(String gysId) {
		String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where suppliersid = ?";
		Record gysbhs = Db.findFirst(sql, gysId);
		return gysbhs;
	}

	/**
	 * 查询用户
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getUserByUserId(String userId) {
		String sql = "select * from tb_sys_user where userId = ?";
		Record record = Db.findFirst(sql, userId);
		return record;
	}

	/**
	 * 查询用户根据id
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getUserById(String id) {
		Record record = Db.findById("tb_sys_user", id);
		return record;
	}

	/**
	 * 名称查询用户
	 * 
	 * @param userId
	 * @return
	 */
	public static Record getUserByName(String name) {
		String sql = "select * from tb_sys_user where realName = ?";
		Record record = Db.findFirst(sql, name);
		return record;
	}

	/**
	 * 查询部门
	 * 
	 * @param userId
	 * @return
	 */
	public static Record getDeptByUserId(String userId) {
		String sql = "SELECT * FROM tb_sys_dept WHERE id = (SELECT r.deptId FROM tb_sys_dept_user_relation r WHERE r.userId = ?)";
		Record record = Db.findFirst(sql, userId);
		return record;
	}

	/**
	 * 查询供应商
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getSupplierByName(String gysmc) {
		String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where gysmc = ?";
		Record record = Db.findFirst(sql, gysmc);
		return record;
	}

	/**
	 * 查询供应商根据供应商编号
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getSupplierByNumber(String gysbh) {
		String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where gysbh = ?";
		Record record = Db.findFirst(sql, gysbh);
		return record;
	}

	/**
	 * 供应商委托人
	 * 
	 * @param gysId
	 * @return
	 */
	public static Record getWeituoren(String id) {
		String sql = "select * from xyy_erp_dic_gongyingshang_weituoren where ID = ? order by sfzlxr desc";
		Record wtr = Db.findFirst(sql, id);
		return wtr;
	}

	/**
	 * 查询商品
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	public static Record getGoods(String shangpinbianhao) {
		String sql = "select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = ?";
		Record gysbhs = Db.findFirst(sql, shangpinbianhao);
		return gysbhs;
	}

	/**
	 * 查询商品
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	public static Record getGoodsByGoodsid(String goodsid) {
		String sql = "select * from xyy_erp_dic_shangpinjibenxinxi where goodsid = ?";
		Record gysbhs = Db.findFirst(sql, goodsid);
		return gysbhs;
	}

	/**
	 * 查询采购订单明细
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	public static Record getCGDDDetails(String danjubianhao, int SN, String headTable, String detailsTable) {
		String sql = "select d.* from " + detailsTable + " d where d.SN = ? and d.BillID = (SELECT c.BillID FROM "
				+ headTable + " c WHERE c.danjubianhao = ? LIMIT 1)";
		Record details = Db.findFirst(sql, SN, danjubianhao);
		return details;
	}

	/**
	 * 查询未执行的采购订单
	 * 
	 * @return
	 */
	public static List<Record> getCGDDHead() {
		String sql = "select * from xyy_erp_bill_caigoudingdan where shifouzhixing = 0";
		return Db.find(sql);
	}

	/**
	 * 查询采购订单明细
	 * 
	 * @param BillDtlID
	 * @return
	 */
	public static List<Record> getCGDDDetailsById(String BillID, String tableName) {
		String sql = "select * from " + tableName + " where BillID = ?";
		List<Record> list = Db.find(sql, BillID);
		return list;
	}

	/**
	 * 查询采购退出开票单明细
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	public static Record getCGTCKPDDetails(String danjubianhao, int SN) {
		String sql = "select d.* from xyy_erp_bill_caigoutuichukaipiaodan_details d where d.SN = ? and d.BillID = (SELECT c.BillID FROM xyy_erp_bill_caigoutuichukaipiaodan c WHERE c.danjubianhao = ? LIMIT 1)";
		Record details = Db.findFirst(sql, SN, danjubianhao);
		return details;
	}

	/**
	 * 计算单据体的总金额
	 * 
	 * @param bodys
	 * @return
	 */
	public static BigDecimal getSumMoney(List<Record> bodys, String fileKey) {
		BigDecimal sumMoney = BigDecimal.ZERO;
		for (Record record : bodys) {
			sumMoney = sumMoney
					.add(record.getBigDecimal(fileKey) == null ? BigDecimal.ZERO : record.getBigDecimal(fileKey));
		}
		return sumMoney;
	}

	/**
	 * 判断数据是否存在，存在不允许在新增
	 * 
	 * @param dajubianhao
	 * @return
	 */
	public static boolean findTableData(String tableName, String danjubianhao) {
		String sql = "select * from " + tableName + " where 1=1 and shifouzhixing = 1 and sjdjbh = ?";
		List<Record> list = Db.find(sql, danjubianhao);
		if (list != null && list.size() > 0)
			return true;
		return false;
	}

	/**
	 * 回写中间库采购入库单
	 * 
	 * @param dajubianhao
	 * @return
	 */
	public static void backWriteMidTableData(String tableName, String primaryKey, List<Record> recordList) {
		int[] batchUpdate = Db.use(DictKeys.db_dataSource_wms_mid).batchUpdate(tableName, primaryKey, recordList, 100);
		for (int i : batchUpdate) {
			log.info("中间数据库回写出错，重新回写数量:" + batchUpdate + ",【中间库“采购入库单”或“采购退出出库单”】状态,是否成功：" + i);
		}
	}

	/**
	 * 查询采购订单，根据单据编号
	 * 
	 * @param danjubianhao
	 * @return
	 */
	public static Record findCaiGouDingDan(String danjubianhao) {
		String sql = "select * from xyy_erp_bill_caigoudingdan where danjubianhao = ?";
		Record head = Db.findFirst(sql, danjubianhao);
		return head;
	}

	/**
	 * 拉取中间表wms采购入库单的数据
	 */
	public static void pullCaigourukudanData() {
		String sql2 = "select djbh from " + PullWMSDataUtil.WMS_TABLE_CGRK + " where is_zx = '否'  group by djbh";
		List<Record> wmsLists = Db.use(DictKeys.db_dataSource_wms_mid).find(sql2);
		List<Record> backWritelist = new ArrayList<>();
		for (Record r : wmsLists) {
			String sql3 = "select * from " + PullWMSDataUtil.WMS_TABLE_CGRK + " where is_zx = '否' AND djbh ='"
					+ r.getStr("djbh") + "'";
			List<Record> wmsList = Db.use(DictKeys.db_dataSource_wms_mid).find(sql3);

			// 如果采购入库单存在该数据，不用重新新增了
			boolean isExite = findTableData("xyy_erp_bill_caigourukudan", r.getStr("djbh_sj"));
			if (isExite) {
				for (Record record : wmsList) {
					record.set("is_zx", "是");
				}
				backWriteMidTableData(PullWMSDataUtil.WMS_TABLE_CGRK, "djbh,yzid,dj_sort,recnum", wmsList);
				continue;
			}
			Record head = new Record();
			Record wms = wmsList.get(0);
			String yuandanjubianhao = wms.getStr("djbh_sj");// 原采购订单单号
			String djbh = "RK"+orderno();
			head.set("danjubianhao", djbh);
			head.set("rkdjbh", r.getStr("djbh"));// 中间库的采购入库单单据编号
			Date kaipiaoriqi = ToolDateTime.parse(wms.getStr("rq"), ToolDateTime.pattern_ymd);
			head.set("kaipiaoriqi", kaipiaoriqi);
			Record caigouyuan = getUserByUserId(wms.getStr("cgy"));
			if (caigouyuan == null) {
				log.error("拉取【采购入库单】无法找到该订单的采购员！采购入库单单据编号：" + r.getStr("djbh") + ",采购员内码：" + wms.getStr("cgy"));
				continue;
			}
			head.set("caigouyuan",
					(caigouyuan == null || caigouyuan.getStr("realName") == null) ? "" : caigouyuan.getStr("realName"));

			String gysId = wms.getStr("dwbh");// 单位内码,供应商ID
			Record supplier = getSupplier(gysId);
			if (supplier == null) {
				log.error("拉取【采购入库单】无法找到该订单的供应商！采购入库单单据编号：" + r.getStr("djbh") + ",单位内码：" + gysId);
				continue;
			}
			head.set("gysbh", supplier == null || supplier.getStr("gysbh") == null ? null : supplier.getStr("gysbh"));
			head.set("gysmc", supplier == null || supplier.getStr("gysmc") == null ? null : supplier.getStr("gysmc"));
			head.set("sjdjbh", yuandanjubianhao);// 上级单据编号（sjdjbh） 就是采购订单的单据编号
			Record cgRecord = findCaiGouDingDan(yuandanjubianhao);
			// 取原采购订单的开票员
			head.set("kaipiaoyuan",
					cgRecord == null || cgRecord.getStr("kaipiaoyuan") == null ? "" : cgRecord.getStr("kaipiaoyuan"));
			// 取原采购订单的结算方式
			head.set("jiesuanfangshi", cgRecord == null || cgRecord.getInt("jiesuanfangshi") == null ? 0
					: cgRecord.getInt("jiesuanfangshi"));

			head.set("status", 20);
			head.set("rowID", UUIDUtil.newUUID());
			head.set("orgCode", PullWMSDataUtil.orgCode);//
			head.set("orgId", PullWMSDataUtil.orgId);// 湖北分公司
			head.set("createTime", new Timestamp(System.currentTimeMillis()));
			head.set("updateTime", new Timestamp(System.currentTimeMillis()));
			List<Record> bodyList = new ArrayList<>();
			boolean isExecute = true;
			for (Record record : wmsList) {
				Record body = new Record();
				body.set("danjubianhao", djbh);
				body.set("rkdjbh", r.getStr("djbh"));// 中间库的采购入库单单据编号
				body.set("rowID", UUIDUtil.newUUID());
				// 商品
				String goodsId = record.getStr("spid");// 商品内码,明细行商品ID
				Record goods = getGoodsByGoodsid(goodsId);
				if (goods == null) {
					log.error("拉取【采购入库单】无法找到该订单的商品信息！采购入库单单据编号：" + r.getStr("djbh") + ",商品内码：" + goodsId);
					isExecute = false;
					continue;
				}
				body.set("goodsid", goodsId);
				body.set("shangpinbianhao", goods == null || goods.getStr("shangpinbianhao") == null ? null
						: goods.getStr("shangpinbianhao"));
				body.set("shangpinmingcheng", goods == null || goods.getStr("shangpinmingcheng") == null ? null
						: goods.getStr("shangpinmingcheng"));
				body.set("guige", goods == null || goods.getStr("guige") == null ? null : goods.getStr("guige"));
				body.set("danwei", goods == null || goods.getInt("danwei") == null ? null : goods.getInt("danwei"));
				body.set("shengchanchangjia", goods == null || goods.getStr("shengchanchangjia") == null
						? BigDecimal.ZERO : goods.getStr("shengchanchangjia"));
				body.set("koulv", 100);
				body.set("shuilv", goods == null || goods.getBigDecimal("jinxiangshuilv") == null ? BigDecimal.ZERO
						: goods.getBigDecimal("jinxiangshuilv"));
				body.set("pizhunwenhao",
						goods == null || goods.getStr("pizhunwenhao") == null ? null : goods.getStr("pizhunwenhao"));
				body.set("chandi", goods == null || goods.getStr("chandi") == null ? null : goods.getStr("chandi"));

				// 批号
				body.set("pihao", record.getStr("ph") == null ? "" : record.getStr("ph"));
				Date rq_sc = null;
				Date yxqz = null;
				if (record.getStr("rq_sc") != null) {
					rq_sc = ToolDateTime.parse(record.getStr("rq_sc"), ToolDateTime.pattern_ymd);
				}
				if (record.getStr("yxqz") != null) {
					yxqz = ToolDateTime.parse(record.getStr("yxqz"), ToolDateTime.pattern_ymd);
				}
				body.set("shengchanriqi", rq_sc);
				body.set("youxiaoqizhi", yxqz);
				body.set("miejunpihao", "");

				body.set("shuliang", record.getBigDecimal("sl") == null ? BigDecimal.ZERO : record.getBigDecimal("sl"));// 数量
				// if ("1".equals(record.getStr("yspd")) ||
				// "正常".equals(record.getStr("yspd"))) {
				// 验收评定：从wms过来，库存状态 1合格，2不合格，3待验，4 停售'
				body.set("kucunzhuangtai", Integer.valueOf(record.getStr("yspd")));
				// } else {
				// body.set("kucunzhuangtai", 2);
				// }

				// 价格
				Integer dgddSN = record.getInt("cgdd_sort");
				body.set("dgddSN", dgddSN);// 采购订单行号（原采购订单行号）
				head.set("sjdjbh", yuandanjubianhao);// 上级单据编号（sjdjbh）就是采购订单的单据编号
				Record details = getCGDDDetails(yuandanjubianhao, dgddSN, "xyy_erp_bill_caigoudingdan",
						"xyy_erp_bill_caigoudingdan_details");
				log.info("【原采购订单】的单据编号是：" + yuandanjubianhao + "，【行号是】：" + dgddSN);
				if (details == null) {
					log.info("拉取【采购入库单】,无法找到【原采购订单明细】的数据");
					isExecute = false;
					continue;
				}
				body.set("hanshuijia", details == null || details.getBigDecimal("hanshuijia") == null ? BigDecimal.ZERO
						: details.getBigDecimal("hanshuijia"));
				// 含税金额
				BigDecimal hanshuijine = body.getBigDecimal("shuliang").multiply(body.getBigDecimal("hanshuijia"));
				body.set("hanshuijine", hanshuijine);
				// body.set("hscbj", details == null ||
				// details.getBigDecimal("hscbj") == null ? BigDecimal.ZERO
				// : details.getBigDecimal("hscbj"));
				// 金额=含税金额/(1+税率)
				BigDecimal jine = BigDecimal.ZERO;
				BigDecimal shuilvs = body.getBigDecimal("shuilv").add(new BigDecimal("100"))
						.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
				if (hanshuijine != null) {
					jine = hanshuijine.divide(shuilvs, 2, RoundingMode.HALF_UP);
				}
				body.set("jine", jine);
				// 单价=含税价/(1+税率)
				BigDecimal danjia = body.getBigDecimal("hanshuijia").divide(shuilvs, 6, BigDecimal.ROUND_HALF_UP);
				body.set("danjia", danjia);
				// 税额=含税金额-金额
				BigDecimal shuie = hanshuijine.subtract(jine);
				body.set("shuie", shuie);
				body.set("beizhu", "");
				body.set("orgCode", PullWMSDataUtil.orgCode);// 湖北分公司
				body.set("orgId", PullWMSDataUtil.orgId);// 湖北分公司
				bodyList.add(body);
				record.set("is_zx", "是");
				backWritelist.add(record);
			}
			if (!isExecute) {
				continue;
			}
			BigDecimal zhsje = getSumMoney(bodyList, "hanshuijine");
			head.set("zhsje", zhsje);// 总含税金额
			head.set("zongjine", getSumMoney(bodyList, "jine"));// 总金额
			head.set("zongshuie", getSumMoney(bodyList, "shuie"));// 总税额
			// 调用统一保存操作
			BillContext context = new BillContext();
			try {
				boolean result = Db.tx(new IAtom() {
					@Override
					public boolean run() throws SQLException {
						try {
							saveAction(context, buildContext(head, bodyList, "caigourukudan", "caigourukudan_details"),
									"caigourukudan");
							return true;
						} catch (Exception e) {
							return false;
						}
					}
				});
				if (result) {
					Db.tx(new IAtom() {
						@Override
						public boolean run() throws SQLException {
							try {
								int[] batchUpdate = Db.use(DictKeys.db_dataSource_wms_mid).batchUpdate(
										PullWMSDataUtil.WMS_TABLE_CGRK, "djbh,yzid,dj_sort,recnum", backWritelist, 100);
								for (int i : batchUpdate) {
									log.info("【回写中间表采购入库单,个数：" + batchUpdate.length + ",是否成功】:" + i);
								}
								return true;
							} catch (Exception e) {
								return false;
							}
							
						}
					});
					
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("--------------采购入库单保存成功！-----------------");
			}

			// 回写采购订单明细项数据
			/*
			 * backWriteCaiGouDingDanDetails(head, bodyList); // 商品批号
			 * RecordDataUtil.operteBatchGoods(head, bodyList, 1); // 商品批号库存处理
			 * RecordDataUtil.operateBatchStock(head, bodyList, 1); // 商品总库存处理
			 * RecordDataUtil.operateTotalStock(head, bodyList, 1); // 商品账页处理
			 * RecordDataUtil.addGoodsFolio(head, bodyList, 1); // 往来账处理
			 * RecordDataUtil.addDealingFolio(head, bodyList, 1);
			 */
		}

	}

	/**
	 * 拉取中间表wms采购入库单的数据
	 */
	// TODO 测试一个订单多个采购入库单
	/*
	 * public static void pullCaigourukudanDataPlus() { String sql2 =
	 * "select djbh_sj from " + PullWMSDataUtil.WMS_TABLE_CGRK +
	 * " where is_zx = '2'  group by djbh_sj"; List<Record> wmsLists =
	 * Db.use(DictKeys.db_dataSource_wms_mid).find(sql2); List<Record>
	 * backWritelist = new ArrayList<>(); for (Record r : wmsLists) { String
	 * sql3 = "select * from " + PullWMSDataUtil.WMS_TABLE_CGRK +
	 * " where is_zx = '2' AND djbh_sj ='" + r.getStr("djbh_sj") + "'";
	 * List<Record> wmsList = Db.use(DictKeys.db_dataSource_wms_mid).find(sql3);
	 * 
	 * // 如果采购入库单存在该数据，不用重新新增了 boolean isExite =
	 * findTableData("xyy_erp_bill_caigourukudan", r.getStr("djbh_sj")); if
	 * (isExite) { for (Record record : wmsList) { record.set("is_zx", "是"); }
	 * backWriteMidTableData(PullWMSDataUtil.WMS_TABLE_CGRK,
	 * "djbh,yzid,dj_sort,recnum", wmsList); continue; } Record head = new
	 * Record(); Record wms = wmsList.get(0); String yuandanjubianhao =
	 * wms.getStr("djbh_sj");// 原采购订单单号 String danjubianhao = "RK" +
	 * TimeUtil.format(new Date(), "yyyyMMddHHmmss") +
	 * RandomUtil.getRandomCode(3); head.set("danjubianhao", danjubianhao); Date
	 * kaipiaoriqi = ToolDateTime.parse(wms.getStr("rq"),
	 * ToolDateTime.pattern_ymd); head.set("kaipiaoriqi", kaipiaoriqi); Record
	 * caigouyuan = getUserByUserId(wms.getStr("cgy")); if(caigouyuan==null){
	 * log.info("拉取【采购入库单】无法找到该订单的采购员！上级单据编号："+r.getStr("djbh_sj")+",采购员内码："+wms
	 * .getStr("cgy")); continue; } head.set("caigouyuan", (caigouyuan == null
	 * || caigouyuan.getStr("realName") == null) ? "" :
	 * caigouyuan.getStr("realName"));
	 * 
	 * String gysId = wms.getStr("dwbh");// 单位内码,供应商ID Record supplier =
	 * getSupplier(gysId); if(supplier == null){
	 * log.info("拉取【采购入库单】无法找到该订单的供应商！上级单据编号："+r.getStr("djbh_sj")+",单位内码："+
	 * gysId); continue; } head.set("gysbh", supplier == null ||
	 * supplier.getStr("gysbh") == null ? null : supplier.getStr("gysbh"));
	 * head.set("gysmc", supplier == null || supplier.getStr("gysmc") == null ?
	 * null : supplier.getStr("gysmc")); head.set("sjdjbh", yuandanjubianhao);//
	 * 上级单据编号（sjdjbh） 就是采购订单的单据编号 Record cgRecord =
	 * findCaiGouDingDan(yuandanjubianhao); // 取原采购订单的开票员
	 * head.set("kaipiaoyuan", cgRecord == null ||
	 * cgRecord.getStr("kaipiaoyuan") == null ? "" :
	 * cgRecord.getStr("kaipiaoyuan")); // 取原采购订单的结算方式
	 * head.set("jiesuanfangshi", cgRecord == null ||
	 * cgRecord.getInt("jiesuanfangshi") == null ? 0 :
	 * cgRecord.getInt("jiesuanfangshi"));
	 * 
	 * head.set("status", 20); head.set("rowID", UUIDUtil.newUUID());
	 * head.set("orgCode", PullWMSDataUtil.orgCode);// head.set("orgId",
	 * PullWMSDataUtil.orgId);// 湖北分公司 head.set("createTime", new
	 * Timestamp(System.currentTimeMillis())); head.set("updateTime", new
	 * Timestamp(System.currentTimeMillis())); List<Record> bodyList = new
	 * ArrayList<>(); boolean isExecute = true; for (Record record : wmsList) {
	 * Record body = new Record(); body.set("danjubianhao", danjubianhao);
	 * body.set("rowID", UUIDUtil.newUUID()); // 商品 String goodsId =
	 * record.getStr("spid");// 商品内码,明细行商品ID Record goods =
	 * getGoodsByGoodsid(goodsId); if(goods==null){
	 * log.info("拉取【采购入库单】无法找到该订单的商品信息！上级单据编号："+r.getStr("djbh_sj")+",商品内码："+
	 * goodsId); isExecute = false; continue; } body.set("goodsid", goodsId);
	 * body.set("shangpinbianhao", goods == null ||
	 * goods.getStr("shangpinbianhao") == null ? null :
	 * goods.getStr("shangpinbianhao")); body.set("shangpinmingcheng", goods ==
	 * null || goods.getStr("shangpinmingcheng") == null ? null :
	 * goods.getStr("shangpinmingcheng")); body.set("guige", goods == null ||
	 * goods.getStr("guige") == null ? null : goods.getStr("guige"));
	 * body.set("danwei", goods == null || goods.getInt("danwei") == null ? null
	 * : goods.getInt("danwei")); body.set("shengchanchangjia", goods == null ||
	 * goods.getStr("shengchanchangjia") == null ? BigDecimal.ZERO :
	 * goods.getStr("shengchanchangjia")); body.set("koulv", 100);
	 * body.set("shuilv", goods == null || goods.getBigDecimal("jinxiangshuilv")
	 * == null ? BigDecimal.ZERO : goods.getBigDecimal("jinxiangshuilv"));
	 * body.set("pizhunwenhao", goods == null || goods.getStr("pizhunwenhao") ==
	 * null ? null : goods.getStr("pizhunwenhao")); body.set("chandi", goods ==
	 * null || goods.getStr("chandi") == null ? null : goods.getStr("chandi"));
	 * 
	 * // 批号 body.set("pihao", record.getStr("ph") == null ? "" :
	 * record.getStr("ph")); Date rq_sc = null; Date yxqz = null; if
	 * (record.getStr("rq_sc") != null) { rq_sc =
	 * ToolDateTime.parse(record.getStr("rq_sc"), ToolDateTime.pattern_ymd); }
	 * if (record.getStr("yxqz") != null) { yxqz =
	 * ToolDateTime.parse(record.getStr("yxqz"), ToolDateTime.pattern_ymd); }
	 * body.set("shengchanriqi", rq_sc); body.set("youxiaoqizhi", yxqz);
	 * body.set("miejunpihao", ""); // 灭菌批号
	 * 
	 * body.set("shuliang", record.getBigDecimal("sl") == null ? BigDecimal.ZERO
	 * : record.getBigDecimal("sl"));// 数量 // if
	 * ("1".equals(record.getStr("yspd")) || //
	 * "正常".equals(record.getStr("yspd"))) { // 验收评定：从wms过来，库存状态 1合格，2不合格，3待验，4
	 * 停售' body.set("kucunzhuangtai", Integer.valueOf(record.getStr("yspd")));
	 * // } else { // body.set("kucunzhuangtai", 2); // }
	 * 
	 * // 价格 Integer dgddSN = record.getInt("cgdd_sort"); body.set("dgddSN",
	 * dgddSN);// 采购订单行号（原采购订单行号） head.set("sjdjbh", yuandanjubianhao);//
	 * 上级单据编号（sjdjbh）就是采购订单的单据编号 Record details =
	 * getCGDDDetails(yuandanjubianhao, dgddSN, "xyy_erp_bill_caigoudingdan",
	 * "xyy_erp_bill_caigoudingdan_details"); log.info("【原采购订单】的单据编号是：" +
	 * yuandanjubianhao + "，【行号是】：" + dgddSN); if (details == null) {
	 * log.info("拉取【采购入库单】,无法找到【原采购订单明细】的数据"); isExecute = false; continue; }
	 * body.set("hanshuijia", details == null ||
	 * details.getBigDecimal("hanshuijia") == null ? BigDecimal.ZERO :
	 * details.getBigDecimal("hanshuijia")); //含税金额 BigDecimal hanshuijine =
	 * body.getBigDecimal("shuliang").multiply(body.getBigDecimal("hanshuijia"))
	 * ; body.set("hanshuijine", hanshuijine); // body.set("hscbj", details ==
	 * null || details.getBigDecimal("hscbj") == null ? BigDecimal.ZERO // :
	 * details.getBigDecimal("hscbj")); // 金额=含税金额/(1+税率) BigDecimal jine =
	 * BigDecimal.ZERO; BigDecimal shuilvs =
	 * body.getBigDecimal("shuilv").add(new BigDecimal("100")) .divide(new
	 * BigDecimal("100"), 2, RoundingMode.HALF_UP); if (hanshuijine != null) {
	 * jine = hanshuijine.divide(shuilvs, 2, RoundingMode.HALF_UP); }
	 * body.set("jine", jine);
	 * 
	 * //单价=含税价/(1+税率) BigDecimal danjia =
	 * body.getBigDecimal("hanshuijia").divide(shuilvs,6,BigDecimal.
	 * ROUND_HALF_UP); body.set("danjia", danjia);
	 * 
	 * // 税额=含税金额-金额 BigDecimal shuie = hanshuijine.subtract(jine);
	 * body.set("shuie", shuie); body.set("beizhu", ""); body.set("orgCode",
	 * PullWMSDataUtil.orgCode);// 湖北分公司 body.set("orgId",
	 * PullWMSDataUtil.orgId);// 湖北分公司 bodyList.add(body); record.set("is_zx",
	 * "是"); backWritelist.add(record); } if(!isExecute){ continue; } BigDecimal
	 * zhsje = getSumMoney(bodyList, "hanshuijine"); head.set("zhsje", zhsje);//
	 * 总含税金额 head.set("zongjine", getSumMoney(bodyList, "jine"));// 总金额
	 * head.set("zongshuie", getSumMoney(bodyList, "shuie"));// 总税额 // 调用统一保存操作
	 * BillContext context = new BillContext(); saveAction(context,
	 * buildContext(head, bodyList, "caigourukudan", "caigourukudan_details"),
	 * "caigourukudan");
	 * 
	 * int[] batchUpdate =
	 * Db.use(DictKeys.db_dataSource_wms_mid).batchUpdate(PullWMSDataUtil.
	 * WMS_TABLE_CGRK, "djbh,yzid,dj_sort,recnum", backWritelist, 100); for (int
	 * i : batchUpdate) { log.info("【回写中间表采购入库单,个数：" + batchUpdate.length +
	 * ",是否成功】:" + i); } // 回写采购订单明细项数据
	 * 
	 * backWriteCaiGouDingDanDetails(head, bodyList); // 商品批号
	 * RecordDataUtil.operteBatchGoods(head, bodyList, 1); // 商品批号库存处理
	 * RecordDataUtil.operateBatchStock(head, bodyList, 1); // 商品总库存处理
	 * RecordDataUtil.operateTotalStock(head, bodyList, 1); // 商品账页处理
	 * RecordDataUtil.addGoodsFolio(head, bodyList, 1); // 往来账处理
	 * RecordDataUtil.addDealingFolio(head, bodyList, 1);
	 * 
	 * }
	 * 
	 * }
	 */

	/**
	 * 采购入库单回写采购订单 采购订单:采购入库单=1:N 一个采购订单对应多个采购入库单 多次提取采购入库单，则可以多次回写采购订单
	 * 回写采购订单的明细数量 采购订单的某一条明细项的数量小于等于入库数量，则改变【采购订单】的字段“是否上引”为1
	 * 一个采购订单的所有明细项的回写数量都等于明细项的数量时，回写这个采购订单的提取状态为【已提取1】
	 */
	public static void backWriteCaiGouDingDanDetails(Record head, List<Record> list) {
		List<Record> bodyList = new ArrayList<>();
		String danjubianhao = head.getStr("sjdjbh");// 原采购订单单据编号
		Map<String, String> headMap = new HashMap<>();
		for (Record record : list) {
			BigDecimal shuliang = record.getBigDecimal("shuliang");// 采购入库单的数量
			Integer SN = record.getInt("dgddSN");// 原采购订单行号
			Record CGRecord = getCGDDDetails(danjubianhao, SN, "xyy_erp_bill_caigoudingdan",
					"xyy_erp_bill_caigoudingdan_details");
			if (CGRecord == null) {
				log.info("无法找到原采购订单");
				continue;
			}
			// 先判断是否上引
			if (CGRecord.getInt("isPull") != null && CGRecord.getInt("isPull") == 1) {
				continue;
			}
			BigDecimal rukushuliang = CGRecord.getBigDecimal("rukushuliang");
			// 入库数量+数量
			BigDecimal rl = rukushuliang.add(shuliang);
			CGRecord.set("rukushuliang", rl);
			// 如果（入库数量+数量）等于采购订单的数量，则改变采购订单的是否上引为1
			if (rl.compareTo(CGRecord.getBigDecimal("shuliang")) == 0) {
				CGRecord.set("isPull", 1);
			}
			bodyList.add(CGRecord);
			headMap.put(CGRecord.get("BillID"), CGRecord.get("BillID"));
		}
		Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					if (bodyList.size() > 0) {
						int[] batchUpdate = Db.batchUpdate("xyy_erp_bill_caigoudingdan_details", "BillDtlID", bodyList, 100);
						for (int i : batchUpdate) {
							log.info("回写【采购订单】的个数：+" + batchUpdate.length + "+是否成功:" + i);
						}
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				
			}
		});
		

		// 回写采购订单头部数据
		for (String BillID : headMap.keySet()) {
			backWriteCaiGouDingDanHead(BillID, "xyy_erp_bill_caigoudingdan", "xyy_erp_bill_caigoudingdan_details");
		}

	}

	/**
	 * 回写中间库采购入库单
	 * 
	 * @param dajubianhao
	 * @return
	 */
	public static void backWriteMidTuiChuTableData(String danjubianhao) {
		String sql = "update " + PullWMSDataUtil.WMS_TABLE_GJTC + " set is_zx = '是' where is_zx = '否' and djbh = ?";
		int update = Db.use(DictKeys.db_dataSource_wms_mid).update(sql, danjubianhao);
		log.info("中间数据库回写出错，重新回写【采购退出出库单】状态,是否成功：" + update);
	}

	/**
	 * 查询采购退出开票单，根据单据编号
	 * 
	 * @param danjubianhao
	 * @return
	 */
	public static Record findCaiGouTuiChuKaiPiaoDan(String danjubianhao) {
		String sql = "select * from xyy_erp_bill_caigoutuichukaipiaodan where danjubianhao = ?";
		Record head = Db.findFirst(sql, danjubianhao);
		return head;
	}

	/**
	 * 拉取中间表wms采购出库单的数据
	 */
	public static void pullCaigouchukudanData() {
		String sql2 = "select djbh from " + PullWMSDataUtil.WMS_TABLE_GJTC + " where is_zx = '否'  group by djbh";
		List<Record> wmsLists = Db.use(DictKeys.db_dataSource_wms_mid).find(sql2);
		List<Record> backWriteList = new ArrayList<>();
		for (Record r : wmsLists) {
			String sql3 = "select * from " + PullWMSDataUtil.WMS_TABLE_GJTC + " where djbh ='" + r.getStr("djbh") + "'";
			List<Record> wmsList = Db.use(DictKeys.db_dataSource_wms_mid).find(sql3);
			Record head = new Record();
			// 如果采购退出出库单存在该数据，不用重新新增了
			boolean isExite = findTableData("xyy_erp_bill_caigoutuichuchukudan", r.getStr("djbh"));
			if (isExite) {
				for (Record record : wmsList) {
					record.set("is_zx", "是");
				}
				backWriteMidTableData(PullWMSDataUtil.WMS_TABLE_GJTC, "djbh,yzid,dj_sort", wmsList);
				continue;
			}
			Record wms = wmsList.get(0);
			String danjubianhao = wms.getStr("djbh");// 原采购退出开票单的单据编号
			Date kaipiaoriqi = ToolDateTime.parse(wms.getStr("rq"), ToolDateTime.pattern_ymd);
			head.set("kaipiaoriqi", kaipiaoriqi);
			Record cgyUser = getUserByUserId(wms.getStr("cgy"));
			if (cgyUser == null) {
				log.info("拉取【采购退出出库单】,无法找到该订单【采购员】的数据");
				continue;
			}
			head.set("caigouyuan",
					cgyUser == null || cgyUser.getStr("realName") == null ? "" : cgyUser.getStr("realName"));
			head.set("status", 20);
			// 采购退出开票单
			Record kpRecord = findCaiGouTuiChuKaiPiaoDan(danjubianhao);
			if (kpRecord == null) {
				log.info("拉取【采购退出出库单】,无法找到【原采购退出开票单】的数据");
				continue;
			}
			// 收货地址
			head.set("shouhuodizhi",
					kpRecord == null || kpRecord.getStr("shouhuodizhi") == null ? "" : kpRecord.getStr("shouhuodizhi"));
			// 备注
			head.set("beizhu", kpRecord == null || kpRecord.getStr("beizhu") == null ? "" : kpRecord.getStr("beizhu"));
			// 开票员
			head.set("kaipiaoyuan",
					kpRecord == null || kpRecord.getStr("kaipiaoyuan") == null ? "" : kpRecord.getStr("kaipiaoyuan"));
			// 供应商
			String gysId = wms.getStr("dwbh");// 单位内码,供应商ID
			Record supplier = getSupplier(gysId);
			head.set("gysbh", supplier == null || supplier.getStr("gysbh") == null ? "" : supplier.getStr("gysbh"));
			head.set("gysmc", supplier == null || supplier.getStr("gysmc") == null ? "" : supplier.getStr("gysmc"));
			// head.set("lianxiren",
			// supplier == null || supplier.getStr("lianxiren") == null ? "" :
			// supplier.getStr("lianxiren"));
			// head.set("lxrdh",
			// supplier == null || supplier.getStr("dianhua") == null ? "" :
			// supplier.getStr("dianhua"));
			head.set("lianxiren",
					kpRecord == null || kpRecord.getStr("lianxiren") == null ? "" : kpRecord.getStr("lianxiren"));
			head.set("lxrdh", kpRecord == null || kpRecord.getStr("lxrdh") == null ? "" : kpRecord.getStr("lxrdh"));
			head.set("danjubianhao", "CK" + orderno());
			head.set("sjdjbh", danjubianhao);// 上级单据编号（sjdjbh）原采购退出开票单的单据编号
			head.set("orgCode", PullWMSDataUtil.orgCode);// 湖北分公司
			head.set("orgId", PullWMSDataUtil.orgId);// 湖北分公司
			head.set("createTime", new Timestamp(System.currentTimeMillis()));
			head.set("updateTime", new Timestamp(System.currentTimeMillis()));
			List<Record> bodyList = new ArrayList<>(); // erp采购订单编号
			boolean isExecute = true;
			for (Record record : wmsList) {
				Record body = new Record();
				// 商品
				String goodsId = record.getStr("spid");// 商品内码,明细行商品ID
				Record goods = getGoodsByGoodsid(goodsId);
				if (goods == null) {
					log.info("拉取【采购退出出库单】无法找到该订单的商品信息！上级单据编号：" + r.getStr("djbh") + ",商品内码：" + goodsId);
					isExecute = false;
					continue;
				}
				body.set("goodsid", goodsId);
				body.set("shangpinbianhao", goods == null || goods.getStr("shangpinbianhao") == null ? ""
						: goods.getStr("shangpinbianhao"));

				body.set("shangpinmingcheng", goods == null || goods.getStr("shangpinmingcheng") == null ? ""
						: goods.getStr("shangpinmingcheng"));
				body.set("guige", goods == null || goods.getStr("guige") == null ? "" : goods.getStr("guige"));
				body.set("danwei", goods == null || goods.getInt("danwei") == null ? 0 : goods.getInt("danwei"));
				body.set("shengchanchangjia", goods == null || goods.getStr("shengchanchangjia") == null ? ""
						: goods.getStr("shengchanchangjia"));
				body.set("koulv", 100);
				body.set("shuilv", goods == null || goods.getBigDecimal("jinxiangshuilv") == null ? BigDecimal.ZERO
						: goods.getBigDecimal("jinxiangshuilv"));
				body.set("pizhunwenhao",
						goods == null || goods.getStr("pizhunwenhao") == null ? "" : goods.getStr("pizhunwenhao"));
				body.set("chandi", goods == null || goods.getStr("chandi") == null ? "" : goods.getStr("chandi"));
				// 批号
				body.set("pihao", record.getStr("ph") == null ? "" : record.getStr("ph"));
				Date rq_sc = null;
				if (!StringUtil.isEmpty(record.getStr("rq_sc"))) {
					rq_sc = ToolDateTime.parse(record.getStr("rq_sc"), ToolDateTime.pattern_ymd);
				}
				body.set("shengchanriqi", rq_sc);
				body.set("kucunzhuangtai", record.getStr("kczt"));
				Date yxqz = null;
				if (!StringUtil.isEmpty(record.getStr("yxqz"))) {
					yxqz = ToolDateTime.parse(record.getStr("yxqz"), ToolDateTime.pattern_ymd);
				}
				body.set("youxiaoqizhi", yxqz);

				// 是否为正数 ，购进退出取负数
				BigDecimal shuliang = BigDecimal.ZERO;
				if (record.getBigDecimal("sl").signum() == 1) {// 转为负数
					shuliang = BigDecimal.ZERO.subtract(record.getBigDecimal("sl"));
				} else {
					shuliang = record.getBigDecimal("sl");// 数量
				}
				body.set("shuliang", shuliang);
				// 行号
				Integer dgddSN = record.getInt("dj_sort");// 原采购退出开票单的行号
				body.set("dgddSN", dgddSN);
				Record details = getCGTCKPDDetails(danjubianhao, dgddSN);
				if (details == null) {
					log.info("拉取【采购退出出库单】,无法找到【原采购退出开票单明细】的数据");
					isExecute = false;
					continue;
				}
				// 价格
				body.set("hanshuijia", details == null || details.getBigDecimal("hanshuijia") == null ? BigDecimal.ZERO
						: details.getBigDecimal("hanshuijia"));
				// 含税金额=含税价*数量
				BigDecimal hanshuijine = body.getBigDecimal("hanshuijia").multiply(body.getBigDecimal("shuliang"));
				body.set("hanshuijine", hanshuijine);
				// 金额=含税金额/(1+税率)
				BigDecimal jine = BigDecimal.ZERO;
				BigDecimal shuilvs = body.getBigDecimal("shuilv").add(new BigDecimal("100"))
						.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
				if (hanshuijine != null) {
					jine = hanshuijine.divide(shuilvs, 2, RoundingMode.HALF_UP);
				}
				body.set("jine", jine);

				// 单价=含税价/(1+税率)
				BigDecimal danjia = body.getBigDecimal("hanshuijia").divide(shuilvs, 6, BigDecimal.ROUND_HALF_UP);
				body.set("danjia", danjia);

				// 税额=含税金额-金额
				BigDecimal shuie = hanshuijine.subtract(jine);
				body.set("shuie", shuie);
				head.set("orgCode", PullWMSDataUtil.orgCode);// 湖北分公司
				head.set("orgId", PullWMSDataUtil.orgId);// 湖北分公司
				body.set("tuichuyuanyin", record.getStr("thyy") == null ? "" : record.getStr("thyy"));// 退货原因
				bodyList.add(body);
				record.set("is_zx", "是");
				backWriteList.add(record);

			}
			if (!isExecute) {
				continue;
			}
			BigDecimal zhsje = getSumMoney(bodyList, "hanshuijine");
			head.set("zhsje", zhsje);// 总含税金额
			head.set("zongjine", getSumMoney(bodyList, "jine"));// 总金额
			head.set("zongshuie", getSumMoney(bodyList, "shuie"));// 总税额
			BillContext context = new BillContext();
			try {
				saveAction(context,
						buildContext(head, bodyList, "caigoutuichuchukudan", "caigoutuichuchukudan_details"),
						"caigoutuichuchukudan");
				boolean result = Db.tx(new IAtom() {
					@Override
					public boolean run() throws SQLException {
						try {
							Db.use(DictKeys.db_dataSource_wms_mid).batchUpdate(PullWMSDataUtil.WMS_TABLE_GJTC,
									"djbh,yzid,dj_sort", backWriteList, 100);
							return true;
						} catch (Exception e) {
							return false;
						}
					}
				});
				if(result){
					log.info("【中间表购进退出】回写成功！");
					// 回写采购退出开票单
					backWriteCaiGouTuiChuKaiPiaoDan(head, bodyList);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("--------------采购退出出库单保存失败！-----------------");
			}

		}

	}

	/**
	 * 回写采购退出开票的逻辑
	 * 
	 * @param head
	 * @param list
	 */
	public static void backWriteCaiGouTuiChuKaiPiaoDan(Record head, List<Record> list) {
		List<Record> bodyList = new ArrayList<>();
		String danjubianhao = head.getStr("sjdjbh");// 原采购退出开票单单据编号
		Map<String, String> headMap = new HashMap<>();
		for (Record record : list) {
			BigDecimal shuliang = record.getBigDecimal("shuliang");// 采购出库单的数量
			Integer SN = record.getInt("dgddSN");// 原采购退出开票单行号
			Record CGRecord = getCGDDDetails(danjubianhao, SN, "xyy_erp_bill_caigoutuichukaipiaodan",
					"xyy_erp_bill_caigoutuichukaipiaodan_details");
			if (CGRecord == null) {
				log.info("无法找到原采购开票单");
				continue;
			}
			// 先判断是否上引
			if (CGRecord.getInt("isPull") != null && CGRecord.getInt("isPull") == 1) {
				continue;
			}
			BigDecimal yituishuliang = CGRecord.getBigDecimal("yituishuliang");
			// 已退数量+数量
			BigDecimal rl = yituishuliang.add(shuliang);
			CGRecord.set("yituishuliang", rl);
			// 如果（已退数量+数量都是负数）+采购订单的数量=0，则改变采购订单的是否上引为1
			if ((rl.add(CGRecord.getBigDecimal("shuliang"))).compareTo(BigDecimal.ZERO) == 0) {
				CGRecord.set("isPull", 1);
			}
			bodyList.add(CGRecord);
			headMap.put(CGRecord.get("BillID"), CGRecord.get("BillID"));
		}
		
		boolean result = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					Db.batchUpdate("xyy_erp_bill_caigoutuichukaipiaodan_details", "BillDtlID", bodyList,
							100);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
		if(result){
			// 回写采购退出开票单头部数据
			for (String BillID : headMap.keySet()) {
				backWriteCaiGouDingDanHead(BillID, "xyy_erp_bill_caigoutuichukaipiaodan",
						"xyy_erp_bill_caigoutuichukaipiaodan_details");
			}
		}

		
	}

	/**
	 * 回写采购订单头部状态是否执行
	 */
	public static void backWriteCaiGouDingDanHead(String BillID, String headTable, String detailsTable) {
		List<Record> body = getCGDDDetailsById(BillID, detailsTable);
		// 有一个为不等于0都不用执行修改头逻辑
		boolean isUpdate = true;
		for (Record record : body) {
			if (record.getInt("isPull") != 1) {
				isUpdate = false;
				break;
			}
		}
		// xyy_erp_bill_caigoudingdan
		if (isUpdate) {
			String sql = "update " + headTable + " set shifouzhixing = 1  where BillID = ?";
			int update = Db.update(sql, BillID);
			log.info("【回写" + headTable + "头部是否成功】:" + update);
		}
	}

	/**
	 * 采购退出开票单--->生产中间库数据采购退出
	 */
	public static void generateCaiGouChuKuDan(Record head, List<Record> body) {
		List<Record> wmsList = new ArrayList<>();
		String creator = head.getStr("creator");
		Record user = getUserById(creator);
		Record dept = getDeptByUserId(creator);
		for (Record record : body) {
			Record goods = getGoods(record.getStr("shangpinbianhao"));
			Record wms = new Record();
			wms.set("djbh", head.getStr("danjubianhao"));// 单据编号
			String gysbh = head.getStr("gysbh");
			Record supplier = getSupplierByNumber(gysbh);
			String suppliersid = null;
			if (supplier != null) {
				suppliersid = supplier.getStr("suppliersid");
			}
			// 单位内码,供应商
			wms.set("dwbh", suppliersid);
			Date createTime = head.getDate("kaipiaoriqi");
			wms.set("rq", ToolDateTime.getDate(createTime, ToolDateTime.pattern_ymd));// 日期(yyyy-MM-dd)
			Record cUser = getUserByName(head.getStr("caigouyuan"));
			wms.set("cgy", cUser == null || cUser.getStr("userId") == null ? "" : cUser.getStr("userId"));// 职员表采购员ID
			wms.set("ywy", user == null || user.getStr("userId") == null ? "" : user.getStr("userId"));// 业务员,当前系统登录人ID
			wms.set("bmid", dept == null || dept.getStr("deptId") == null ? "" : dept.getStr("deptId"));// 部门内码,当前登录人所在部门ID
			wms.set("yzid", "O0Z8M62IK57");// 业主内码,字符串：“O0Z8M62IK57”
			wms.set("dj_sort", record.getInt("SN"));// 单据行号,明细行唯一标识rowID
			wms.set("spid", goods == null || goods.getStr("goodsid") == null ? "" : goods.getStr("goodsid"));// 商品内码,明细shangpinbianhao
			wms.set("sl", record.getBigDecimal("shuliang"));// 数量,明细退回数量
			// 件数.明细退回数量/件包装数量 保留两位小数
			int dbzsl = goods == null || goods.getInt("dbzsl") == null ? 0 : goods.getInt("dbzsl");// 大包装数量
			BigDecimal js = BigDecimal.ZERO;
			BigDecimal lss = BigDecimal.ZERO;
			if (dbzsl != 0) {
				js = record.getBigDecimal("shuliang").divide(new BigDecimal(dbzsl), 2, RoundingMode.HALF_UP);
				// 件数.明细退回数量/件包装数量 保留两位小数
				lss = record.getBigDecimal("shuliang").divideAndRemainder(new BigDecimal(dbzsl))[1];
			}
			wms.set("lss", lss);
			wms.set("js", js);
			// 零散数,明细退回数量%件包装数量
			wms.set("wmflg", "N");// N(WMS取数据后会变成Y)
			wms.set("thyy", record.getStr("tuichuyuanyin") == null ? "" : record.getStr("tuichuyuanyin"));// 退货原因
			wms.set("ph", record.getStr("pihao") == null ? "" : record.getStr("pihao"));// 批号
			Date updateTime = record.getDate("updateTime");
			wms.set("lastmodifytime", ToolDateTime.format(updateTime, ToolDateTime.pattern_ymd));// 默认取日期
			wmsList.add(wms);
		}

		boolean result = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					Db.use(DictKeys.db_dataSource_wms_mid).batchSave(PullWMSDataUtil.ERP_TABLE_GJTC, wmsList, 100);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});

		if (result) {
			log.info("【中间库的购进退出单】添加成功!");
			head.set("isBackWrite", 1);
			boolean update = Db.update("xyy_erp_bill_caigoutuichukaipiaodan", "BillID", head);
			log.info("【erp采购退出开票单】回写成功：" + update);
		}
	}

	/**
	 * 【采购退出开票单】定时生成中间库的采购出库单 xyy_erp_bill_caigoutuichukaipiaodan
	 */
	public static void generateCaiGouChuKuDan() {
		String sql = "select * from xyy_erp_bill_caigoutuichukaipiaodan where isBackWrite = 0  and status = 40";
		List<Record> list = Db.find(sql);
		for (Record head : list) {
			String billID = head.getStr("BillID");
			String detailsSql = "select * from xyy_erp_bill_caigoutuichukaipiaodan_details where BillID = ?";
			List<Record> bodyList = Db.find(detailsSql, billID);
			generateCaiGouChuKuDan(head, bodyList);
		}
	}

	/**
	 * 【采购订单】定时生成中间库的采购订单 xyy_erp_bill_caigoudingdan
	 */
	public static void generateCaiGouDingDan() {
		String sql = "select * from xyy_erp_bill_caigoudingdan where isBackWrite = 0 and status = 40";
		List<Record> list = Db.find(sql);
		for (Record head : list) {
			String billID = head.getStr("BillID");
			String detailsSql = "select * from xyy_erp_bill_caigoudingdan_details where BillID = ?";
			List<Record> bodyList = Db.find(detailsSql, billID);
			generateCaiGouDingDan(head, bodyList);
		}
	}

	/**
	 * 采购订单--------->中间库的采购订单
	 * 
	 * @param head
	 * @param body
	 */
	public static void generateCaiGouDingDan(Record head, List<Record> body) {
		List<Record> wmsList = new ArrayList<>();
		String creator = head.getStr("creator");
		String caigouyuan = head.getStr("caigouyuan");
		Record dept = getDeptByUserId(creator);
		String gysmc = head.getStr("gysmc");
		Record cgyUser = getUserByName(caigouyuan);
		Record supplier = getSupplierByName(gysmc);
		for (Record record : body) {
			Record goods = getGoods(record.getStr("shangpinbianhao"));
			Record wms = new Record();
			wms.set("djbh", head.getStr("danjubianhao"));// 单据编号
			// 单位内码,供应商
			wms.set("dwid",
					supplier == null || supplier.getStr("suppliersid") == null ? "" : supplier.getStr("suppliersid"));
			Date createTime = head.getDate("kaipiaoriqi");
			wms.set("rq", ToolDateTime.format(createTime, ToolDateTime.pattern_ymd));// 日期(yyyy-MM-dd)

			// 采购员内码 userId
			wms.set("ry_cgy", cgyUser == null || cgyUser.getStr("userId") == null ? "" : cgyUser.getStr("userId"));
			// 部门内码,当前登录人所在部门ID deptId
			wms.set("bmid", dept == null || dept.getStr("deptId") == null ? "" : dept.getStr("deptId"));
			wms.set("sf_zy", "否");// 是否中药('否'),字符串：“否”
			wms.set("rktype", "正常");// 入库类型('正常'),字符串：“正常”
			wms.set("yzid", "O0Z8M62IK57");// 业主内码,字符串：“O0Z8M62IK57”
			wms.set("dj_sort", record.getInt("SN"));// 单据行号,明细行唯一标识rowID
			wms.set("spid", goods == null || goods.getStr("goodsid") == null ? "" : goods.getStr("goodsid"));// 商品内码,明细id
			wms.set("sl", record.getBigDecimal("shuliang"));// 数量,明细退回数量
			int dbzsl = goods == null || goods.getInt("dbzsl") == null ? 0 : goods.getInt("dbzsl");// 大包装数量
			BigDecimal js = BigDecimal.ZERO;
			BigDecimal lss = BigDecimal.ZERO;
			// 大包装数量是否要更改为BigDecimal类型
			if (dbzsl != 0) {
				js = record.getBigDecimal("shuliang").divide(new BigDecimal(dbzsl), 2, RoundingMode.HALF_UP);
				// 件数.明细退回数量/件包装数量 保留两位小数
				lss = record.getBigDecimal("shuliang").divideAndRemainder(new BigDecimal(dbzsl))[1];// 零散数,明细退回数量%件包装数量
			}
			wms.set("js", js);
			wms.set("lss", lss);
			wms.set("wmflg", "N");// N(WMS取数据后会变成Y)
			Date updateTime = record.getDate("updateTime");
			wms.set("lastmodifytime", ToolDateTime.format(updateTime, ToolDateTime.pattern_ymd));// 默认取日期
			wms.set("fhdz", supplier == null || supplier.getStr("dizhi") == null ? "" : supplier.getStr("dizhi"));// 发货地址,(供应商地址),关联查询
			wms.set("dj", record.getBigDecimal("hanshuijia"));// 单价,明细hanshuijia
			wms.set("je", record.getBigDecimal("hanshuijine"));// 金额,明细hanshuijine
			wmsList.add(wms);
		}
		boolean result = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					Db.use(DictKeys.db_dataSource_wms_mid).batchSave(PullWMSDataUtil.ERP_TABLE_CGDD, wmsList, 100);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
		if (result) {
			head.set("isBackWrite", 1);
			boolean update = Db.update("xyy_erp_bill_caigoudingdan", "BillID", head);
			log.info("【erp采购订单】回写成功：" + update);
		}

	}

	/**
	 * 中间库的采购订单生成中间库的采购入库单:xyy_erp_cgdd---->xyy_wms_cgrk_bill
	 */
	// TODO 后期删除
	/*public static void generateWMSCaiGouRuKu() {
		String sql = "select * from " + PullWMSDataUtil.ERP_TABLE_CGDD + " where wmflg = 'N'";
		List<Record> cgRecord = Db.use(DictKeys.db_dataSource_wms_mid).find(sql);
		List<Record> rkList = new ArrayList<>();
		List<Record> writeList = new ArrayList<>();
		for (Record cg : cgRecord) {
			Record cgrk = new Record();
			String danjubianhao = "RK" + TimeUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.getRandomCode(3);
			cgrk.set("djbh", danjubianhao);// 单据编号
			cgrk.set("dwbh", cg.getStr("dwid"));// 单位内码
			String rq = ToolDateTime.format(new Date(), ToolDateTime.pattern_ymd);
			cgrk.set("rq", rq);// 日期
			cgrk.set("cgy", cg.getStr("RY_cgy"));// 采购员
			String djbh = cg.getStr("djbh");
			cgrk.set("djbh_sj", djbh);// 原采购订单编号
			cgrk.set("rktype", cg.getStr("rktype"));// 入库类型
			cgrk.set("yzid", "O0Z8M62IK57");// 业主内码
			Integer dj_sort = cg.getInt("dj_sort");// 采购订单行号
			cgrk.set("dj_sort", dj_sort);// 单据行号
			String goodsid = cg.getStr("spid");
			cgrk.set("spid", goodsid);// 商品内码
			cgrk.set("sl", cg.getBigDecimal("sl"));// 数量
			cgrk.set("js", cg.getBigDecimal("js"));// 件数
			cgrk.set("lss", cg.getBigDecimal("lss"));// 零散数
			Record phRecord = findGoodsBatch(goodsid);
			if (phRecord == null) {// 为空，自动生产新的批号
				phRecord = new Record();
				String pihao = "NEW" + TimeUtil.format(new Date(), "yyyyMMdd") + RandomUtil.getRandomCode(3);
				Calendar rightNow = Calendar.getInstance();
				rightNow.add(Calendar.MONTH, 12);
				phRecord.set("pihao", pihao);
				phRecord.set("shengchanriqi", new Date());
				phRecord.set("youxiaoqizhi", rightNow.getTime());
			}
			cgrk.set("ph", phRecord.getStr("pihao"));// 批号
			cgrk.set("rq_sc", phRecord.getDate("shengchanriqi"));// 生产日期
			cgrk.set("yxqz", phRecord.getDate("youxiaoqizhi"));// 有效期至
			cgrk.set("cgdd_sort", cg.getInt("dj_sort"));// 采购订单行号
			cgrk.set("yspd", "1");// 验收评定
			String lastmodifytime = ToolDateTime.format(new Date(), ToolDateTime.pattern_ymd_hms);
			cgrk.set("lastmodifytime", lastmodifytime);// 上传单据时间
			cgrk.set("is_zx", "否");// 状态回写
			rkList.add(cgrk);
			cg.set("wmflg", "Y");
			writeList.add(cg);
		}
		if (rkList.size() > 0) {
			int[] batchSave = Db.use(DictKeys.db_dataSource_wms_mid).batchSave(PullWMSDataUtil.WMS_TABLE_CGRK, rkList,
					100);
			for (int i : batchSave) {
				log.info(batchSave.length + ",【是否成功】" + i);
			}
			int[] batchUpdate = Db.use(DictKeys.db_dataSource_wms_mid).batchUpdate(PullWMSDataUtil.ERP_TABLE_CGDD,
					"djbh,yzid,dj_sort", writeList, 100);
			for (int i : batchUpdate) {
				log.info("回写中间库【采购订单】," + batchSave.length + ",【是否成功】" + i);
			}
		}

	}*/
	
	
	
	/**
	 * 中间库的采购订单生成中间库的采购入库单:xyy_erp_cgdd---->xyy_wms_cgrk_bill
	 *  一个采购订单生成两个采购入库单 ，两个采购入库单的数量分别为1或其他
	 */
	// TODO 后期删除---------------
	/*public static void generateWMSCaiGouRuKuPlus() {
		String sql = "select * from " + PullWMSDataUtil.ERP_TABLE_CGDD + " where wmflg = 'N'";
		List<Record> cgRecord = Db.use(DictKeys.db_dataSource_wms_mid).find(sql);
		List<Record> rkList = new ArrayList<>();
		List<Record> rkList2 = new ArrayList<>();
		List<Record> writeList = new ArrayList<>();
		for (Record cg : cgRecord) {
			Record cgrk = new Record();
			Record cgrk2 = new Record();
			
			String danjubianhao = "RK" + TimeUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.getRandomCode(3);
			String rq = ToolDateTime.format(new Date(), ToolDateTime.pattern_ymd);
			String lastmodifytime = ToolDateTime.format(new Date(), ToolDateTime.pattern_ymd_hms);
			String djbh = cg.getStr("djbh");
			Integer dj_sort = cg.getInt("dj_sort");// 采购订单行号
			String goodsid = cg.getStr("spid");
			cgrk.set("djbh", danjubianhao);// 单据编号
			cgrk.set("dwbh", cg.getStr("dwid"));// 单位内码
			cgrk.set("rq", rq);// 日期
			cgrk.set("cgy", cg.getStr("RY_cgy"));// 采购员
			cgrk.set("djbh_sj", djbh);// 原采购订单编号
			cgrk.set("rktype", cg.getStr("rktype"));// 入库类型
			cgrk.set("yzid", "O0Z8M62IK57");// 业主内码
			cgrk.set("dj_sort", dj_sort);// 单据行号
			cgrk.set("spid", goodsid);// 商品内码
			cgrk.set("js", cg.getBigDecimal("js"));// 件数
			cgrk.set("lss", cg.getBigDecimal("lss"));// 零散数
			
			Record phRecord = findGoodsBatch(goodsid);
			if (phRecord == null) {// 为空，自动生产新的批号
				phRecord = new Record();
				String pihao = "NEW" + TimeUtil.format(new Date(), "yyyyMMdd") + RandomUtil.getRandomCode(3);
				Calendar rightNow = Calendar.getInstance();
				rightNow.add(Calendar.MONTH, 12);
				phRecord.set("pihao", pihao);
				phRecord.set("shengchanriqi", new Date());
				phRecord.set("youxiaoqizhi", rightNow.getTime());
			}
			cgrk.set("ph", phRecord.getStr("pihao"));// 批号
			cgrk.set("rq_sc", phRecord.getDate("shengchanriqi"));// 生产日期
			cgrk.set("yxqz", phRecord.getDate("youxiaoqizhi"));// 有效期至
			cgrk.set("cgdd_sort", cg.getInt("dj_sort"));// 采购订单行号
			cgrk.set("yspd", "1");// 验收评定
			cgrk.set("lastmodifytime", lastmodifytime);// 上传单据时间
			cgrk.set("is_zx", "否");// 状态回写
			cgrk.set("sl", 1);// 数量
			rkList.add(cgrk);
			
			BigDecimal sl = cg.getBigDecimal("sl");
			// 数量不等于1
			if(sl.compareTo(new BigDecimal("1"))!=0){
				String danjubianhao2 = "RK" + TimeUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.getRandomCode(3);
				cgrk2.set("djbh", danjubianhao2);// 单据编号
				cgrk2.set("dwbh", cg.getStr("dwid"));// 单位内码
				cgrk2.set("rq", rq);// 日期
				cgrk2.set("cgy", cg.getStr("RY_cgy"));// 采购员
				cgrk2.set("djbh_sj", djbh);// 原采购订单编号
				cgrk2.set("rktype", cg.getStr("rktype"));// 入库类型
				cgrk2.set("yzid", "O0Z8M62IK57");// 业主内码
				cgrk2.set("dj_sort", dj_sort);// 单据行号
				cgrk2.set("spid", goodsid);// 商品内码
				cgrk2.set("js", cg.getBigDecimal("js"));// 件数
				cgrk2.set("lss", cg.getBigDecimal("lss"));// 零散数
				cgrk2.set("ph", phRecord.getStr("pihao"));// 批号
				cgrk2.set("rq_sc", phRecord.getDate("shengchanriqi"));// 生产日期
				cgrk2.set("yxqz", phRecord.getDate("youxiaoqizhi"));// 有效期至
				cgrk2.set("cgdd_sort", cg.getInt("dj_sort"));// 采购订单行号
				cgrk2.set("yspd", "1");// 验收评定
				cgrk2.set("lastmodifytime", lastmodifytime);// 上传单据时间
				cgrk2.set("is_zx", "否");// 状态回写
				BigDecimal subtract = sl.subtract(new BigDecimal("1"));
				cgrk2.set("sl", subtract);// 数量=实际数量-1
				cgrk2.set("is_zx", "2");// 状态回写
				rkList2.add(cgrk2);
			}
			
			cg.set("wmflg", "Y");
			writeList.add(cg);
		}
		if (rkList.size() > 0) {
			if(rkList2.size() > 0){
				rkList.addAll(rkList2);
			}
			int[] batchSave = Db.use(DictKeys.db_dataSource_wms_mid).batchSave(PullWMSDataUtil.WMS_TABLE_CGRK, rkList,
					100);
			for (int i : batchSave) {
				log.info(batchSave.length + ",【是否成功】" + i);
			}
			
			int[] batchUpdate = Db.use(DictKeys.db_dataSource_wms_mid).batchUpdate(PullWMSDataUtil.ERP_TABLE_CGDD,
					"djbh,yzid,dj_sort", writeList, 100);
			for (int i : batchUpdate) {
				log.info("回写中间库【采购订单】," + batchSave.length + ",【是否成功】" + i);
			}
		}
	}*/

	/**
	 * 中间表购进退出表生成wms购进退出表：：xyy_erp_gjtc-----》xyy_wms_cgrk_bill
	 */
	// TODO 后期删除
	/*public static void generateWMSGouJinTuiChu() {
		String sql = "select * from " + PullWMSDataUtil.ERP_TABLE_GJTC + " where wmflg = 'N'";
		List<Record> gjRecord = Db.use(DictKeys.db_dataSource_wms_mid).find(sql);
		List<Record> wmsList = new ArrayList<>();
		List<Record> writeList = new ArrayList<>();
		for (Record record : gjRecord) {
			Record wms = new Record();
			String djbh = record.getStr("djbh");
			wms.set("djbh", djbh);
			wms.set("dwbh", record.getStr("dwbh"));// 单位内码
			String rq = ToolDateTime.format(new Date(), ToolDateTime.pattern_ymd);
			wms.set("rq", rq);//
			wms.set("cgy", record.getStr("cgy"));// 采购员
			wms.set("yzid", "O0Z8M62IK57");//
			int dj_sort = record.getInt("dj_sort");
			wms.set("dj_sort", dj_sort);// 单据行号
			String goodsid = record.getStr("spid");
			wms.set("spid", goodsid);// 商品内码
			wms.set("sl", record.getBigDecimal("sl"));// 数量
			wms.set("js", record.getBigDecimal("js"));// 件数
			wms.set("lss", record.getBigDecimal("lss"));// 零散数
			wms.set("thyy", record.getStr("thyy"));// 退货原因
			String pihao = record.getStr("ph") == null ? "" : record.getStr("ph");
			wms.set("ph", pihao);// 批号
			Record phRecord = findGoodsBatchByName(goodsid, pihao);
			wms.set("rq_sc", phRecord == null || phRecord.getDate("shengchanriqi") == null ? null
					: phRecord.getDate("shengchanriqi"));// 生产日期
			wms.set("yxqz", phRecord == null || phRecord.getDate("youxiaoqizhi") == null ? null
					: phRecord.getDate("youxiaoqizhi"));// 有效期至
			wms.set("kczt", 1);// 库存状态
			String lastmodifytime = ToolDateTime.format(new Date(), ToolDateTime.pattern_ymd_hms);
			wms.set("lastmodifytime", lastmodifytime);// 单据上传时间
			wms.set("is_zx", "否");
			wms.set("BILLSN", null);
			wmsList.add(wms);
			record.set("wmflg", "Y");
			writeList.add(record);
		}
		if (wmsList.size() > 0) {
			int[] batchSave = Db.use(DictKeys.db_dataSource_wms_mid).batchSave(PullWMSDataUtil.WMS_TABLE_GJTC, wmsList,
					100);
			for (int i : batchSave) {
				log.info("中间【WMS】表购进退出" + batchSave.length + ",【添加是否成功】" + i);
			}
			int[] batchUpdate = Db.use(DictKeys.db_dataSource_wms_mid).batchUpdate(PullWMSDataUtil.ERP_TABLE_GJTC,
					"djbh,yzid,dj_sort", writeList, 100);
			for (int i : batchUpdate) {
				log.info("【中间表购进退出，回写是否成功】:" + i);
			}
		}

	}*/

}
