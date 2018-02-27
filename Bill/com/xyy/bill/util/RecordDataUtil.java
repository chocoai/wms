package com.xyy.bill.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.erp.platform.common.tools.RandomUtil;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.erp.platform.common.tools.ToolDateTime;
import com.xyy.util.UUIDUtil;

/**
 * 1：采购入库单，2：采购退出开票单，3：采购退出出库单，4:采购退补价执行单, 5:销售订单,6:销售出库单,7:销售退回入库单,8:销售退补价执行单,
 * 9:采购付款单,10:销售收款单,11:损溢执行单)
 * 
 * @author SKY
 *
 */
public class RecordDataUtil {

	private static final Log log = Log.getLog(RecordDataUtil.class);

	/**
	 * 获取头部数据
	 * 
	 * @param dsi
	 *            数据集实例
	 * @return
	 */
	public static Record getHead(DataSetInstance dsi) {
		DataTableInstance headDti = dsi.getHeadDataTableInstance();
		if (headDti.getRecords().size() != 1) {
			return null;
		}
		Record record = headDti.getRecords().get(0);
		return record;
	}

	/**
	 * 获取体部数据
	 * 
	 * @param dsi
	 *            数据集实例
	 * @return
	 */
	public static List<Record> getBody(DataSetInstance dsi) {
		List<DataTableInstance> bodyDataTableInstance = dsi.getBodyDataTableInstance();
		if (bodyDataTableInstance.size() != 1) {
			return null;
		}
		List<Record> records = bodyDataTableInstance.get(0).getRecords();
		return records;
	}

	/**
	 * 查询商品
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	public static Record getGoods(String shangpinbianhao) {
		String sql = "select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = ?";
		Record goods = Db.findFirst(sql, shangpinbianhao);
		return goods;
	}

	/**
	 * 查询供应商
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getSupplier(String gysbh) {
		String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where gysbh = ?";
		Record gysbhs = Db.findFirst(sql, gysbh);
		return gysbhs;
	}

	/**
	 * 查询商品批号
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getPiHaoByName(String pihao, String goodsid) {
		String sql = "select * from xyy_erp_dic_shangpinpihao where pihao = ? and goodsId = ? ";
		Record record = Db.findFirst(sql, pihao, goodsid);
		return record;
	}

	/**
	 * 商品总库存
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	public static Record getGoodsStock(String shangpinId) {
		String sql = "select * from xyy_erp_bill_shangpinzongkucun where shangpinId = ?";
		Record record = Db.findFirst(sql, shangpinId);
		return record;
	}

	/**
	 * 商品账页处理
	 * 
	 * @param dsi
	 * @param type
	 *            业务类型( 1：采购入库单，2：采购退出开票单，3：采购退出出库单，4:采购退补价执行单,
	 *            5:销售订单,6:销售出库单,7:销售退回入库单,8:销售退补价执行单,
	 *            9:采购付款单,10:销售收款单,11:损溢执行单)
	 */
	public static void addGoodsFolio(Record head, List<Record> bodys, int type) {
		String danjubianhao = head.getStr("danjubianhao");
		Date kaipiaoriqi = head.getDate("kaipiaoriqi");
		String kaipiaoyuan = head.getStr("kaipiaoyuan");
		List<Record> list = new ArrayList<>();
		for (Record body : bodys) {
			try {

				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				Record record = new Record();
				// record.set("ID", UUIDUtil.newUUID());
				record.set("rowID", UUIDUtil.newUUID());
				record.set("createTime", currentTime);
				record.set("updateTime", currentTime);
				record.set("danjubianhao", danjubianhao);// 单据编号：取“采购入库单”的“单据编号”；
				record.set("kaipiaoriqi", kaipiaoriqi);// 开票日期：取“采购入库单”的“开票日期”；
				record.set("kaipiaoyuan", kaipiaoyuan);// 开票日期：取“采购入库单”的“开票员”；
				switch (type) {
				case 1:
					record.set("zhaiyao", "采购入库单");// 摘要：“采购入库单”；
					record.set("rkhsj", body.getBigDecimal("hanshuijia"));// 入库含税价：取“采购入库单”明细的“含税价”；
					record.set("beizhu", "采购入库单");// 备注：（空白）；
					break;
				// case 2:
				// record.set("zhaiyao", "采购退出开票单");
				// break;
				case 3:
					record.set("zhaiyao", "采购退出出库单");
					record.set("rkhsj", body.getBigDecimal("hanshuijia"));// 入库含税价：取“采购退出出库单”明细的“含税价”；
					record.set("beizhu", "采购退出出库单");// 备注：（空白）；
					break;
				case 4:
					record.set("zhaiyao", "采购退补价执行单");
					record.set("beizhu", "采购退补价执行单");// 备注：（空白）；
					record.set("rkhsj", body.getBigDecimal("hanshuichajia"));// 入库含税价：取“采购退补价执行单”明细的“含税差价”；
					break;

				}
				record.set("rukushuliang", body.getBigDecimal("shuliang"));// 入库数量：取“采购入库单”明细的“数量”；
				record.set("rkhsje", body.getBigDecimal("hanshuijine"));// 入库含税金额：取“采购入库单”明细的“含税金额”；
				record.set("chukushuliang", 0);// 出库数量：0
				record.set("ckhsj", 0);// 出库含税价：0
				record.set("ckhsje", 0);// 出库含税金额：0
				record.set("danjuxuhao", body.getInt("SN"));// 单据序号：取“采购入库单”明细的“单据序号”；
				String shangpinbianhao = body.getStr("shangpinbianhao");
				Record goods = getGoods(shangpinbianhao);
				String goodsid = null;
				if (goods != null) {
					goodsid = goods.getStr("goodsid");
				}
				record.set("shangpinId", goodsid);// 商品ID：取“采购入库单”明细的“商品ID”；
				record.set("shangpinshuilv", body.getBigDecimal("shuilv"));// 税率：取“采购入库单”明细的“税率”；

				Record goodsStock = getGoodsStock(goodsid);

				// 库存数量：取商品库存的“库存数量”；
				record.set("kucunshuliang", goodsStock == null || goodsStock.getBigDecimal("kucunshuliang") == null
						? BigDecimal.ZERO : goodsStock.getBigDecimal("kucunshuliang"));

				// 库存含税价：取商品库存的“成本单价”；
				record.set("kchsj", goodsStock == null || goodsStock.getBigDecimal("chengbendanjia") == null
						? BigDecimal.ZERO : goodsStock.getBigDecimal("chengbendanjia"));

				// 库存含税金额：库存数量*库存金额；
				BigDecimal kchsje = record.getBigDecimal("kucunshuliang").multiply(record.getBigDecimal("kchsj"));
				record.set("kchsje", kchsje);

				// 批号ID：取“采购入库单”明细的“批号ID”；
				Record phRecord = getPiHaoByName(body.getStr("pihao"), goodsid);
				record.set("pihaoId",
						phRecord == null || phRecord.getStr("pihaoId") == null ? null : phRecord.getStr("pihaoId"));

				list.add(record);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Db.batchSave("xyy_erp_bill_shangpinzhangye", list, 500);
		log.info("【 商品账页处理】");

	}

	/**
	 * 查询往来帐页的某往来对象的最后一笔
	 * 
	 * @param suppliersid
	 * @return
	 */
	public static Record findWangLaiZhang(String suppliersid) {
		String sql = "select * from xyy_erp_bill_wanglaizhangye where wlId = ? order by ID desc limit 1";
		Record records = Db.findFirst(sql, suppliersid);
		return records;
	}

	/**
	 * 往来账处理（采购入库单）
	 * 
	 * @param dsi
	 * @param type
	 *            业务类型( 1：采购入库单，2：采购退出开票单，3：采购退出出库单，4:采购退补价执行单,
	 *            5:销售订单,6:销售出库单,7:销售退回入库单,8:销售退补价执行单,
	 *            9:采购付款单,10:销售收款单,11:损溢执行单)
	 **/
	public static void addDealingFolio(Record headObject, List<Record> bodys, int type) {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		Record record = new Record();
		try {
			record.set("createTime", currentTime);
			record.set("updateTime", currentTime);
			record.set("danjubianhao", headObject.getStr("danjubianhao"));// 单据编号：取“采购入库单”的“单据编号”；
			record.set("kaipiaoriqi", headObject.getDate("kaipiaoriqi"));// 开票日期：取“采购入库单”的“开票日期”；
			record.set("kaipiaoyuan", headObject.getStr("kaipiaoyuan"));//
			// 开票员：取“采购入库单”的“开票员”；
			// record.set("kaipiaoyuan", headObject.getStr("caigouyuan"));
			record.set("orgId", headObject.getStr("orgId"));// 机构
			// record.set("orgId", "E0EHOUA9KDE");//机构
			switch (type) {
			case 1:
				record.set("zhaiyao", "采购入库单");// 摘要：“采购入库单”；
				break;
			case 2:
				record.set("zhaiyao", "采购退出开票单");
				break;
			case 3:
				record.set("zhaiyao", "采购退出出库单");
				break;
			case 4:
				record.set("zhaiyao", "采购退补价执行单");
				break;
			}
			Record supplier = getSupplier(headObject.getStr("gysbh"));
			if (supplier == null || supplier.getStr("suppliersid") == null) {
				log.info("【没有该供应商的信息】");
				return;
			}
			String suppliersid = supplier.getStr("suppliersid");
			// 往来对象ID：取“采购入库单”的“供货商ID”；//
			record.set("wlId", suppliersid);
			BigDecimal zhsje = PullWMSDataUtil.getSumMoney(bodys, "hanshuijine");
			record.set("jiefang", zhsje);// 借方：取“采购入库单”汇总的“含税金额”；
			record.set("daifang", BigDecimal.ZERO);// 贷方：0；
			record.set("isSupplier", 1);// 是否供应商：0客户，1供应商
			// 余额：上期余额+借方-贷方；
			Record wlzRecord = findWangLaiZhang(suppliersid);
			BigDecimal shangqiyue = wlzRecord == null || wlzRecord.getBigDecimal("yue") == null ? BigDecimal.ZERO
					: wlzRecord.getBigDecimal("yue");
			BigDecimal yue = (shangqiyue.add(zhsje)).subtract(record.getBigDecimal("daifang"));
			record.set("yue", yue);//
			record.set("beizhu", null);// 备注：（空白）；
			record.set("rowID", UUIDUtil.newUUID());
			Db.save("xyy_erp_bill_wanglaizhangye", record);
			log.info("【 往来账处理】");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 商品总库存处理
	 */
	// TODO
	public static void operateTotalStock(Record head, List<Record> bodys, int type) {
		for (Record object : bodys) {
			try {
				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				String shangpinbianhao = object.getStr("shangpinbianhao");
				Record goods = getGoods(shangpinbianhao);

				// 商品总库存
				Record goodsStock = getGoodsStock(
						goods == null || goods.getStr("goodsid") == null ? null : goods.getStr("goodsid"));
				// 库存状态
				Integer kucunzhuangtai = 1;
				kucunzhuangtai = object.getInt("kucunzhuangtai");
				// if(4==type){//退补价开票单需要去总库存获得库存状态
				// kucunzhuangtai = goodsStock.getInt("kucunzhuangtai");
				// }
				if (goodsStock == null) {
					BigDecimal hanshuijia = object.getBigDecimal("hanshuijia") == null ? BigDecimal.ZERO
							: object.getBigDecimal("hanshuijia");
					BigDecimal shuliang = object.getBigDecimal("shuliang");
					Record totalStock = new Record();
					totalStock.set("ID", UUIDUtil.newUUID());
					totalStock.set("rowID", UUIDUtil.newUUID());
					totalStock.set("createTime", currentTime);
					totalStock.set("orgId", object.getStr("orgId"));
					totalStock.set("shangpinId", object.getStr("goodsid"));
					// totalStock.set("kucunzhuangtai", kucunzhuangtai);//库存状态

					// 库存数量：insert操作时，库存数量= “采购入库单”的“数量”；
					totalStock.set("kucunshuliang", object.getBigDecimal("shuliang"));

					// 可销数量：库存状态为“1”，可销数量=可销数量+“采购入库单”的“数量”；
					BigDecimal kexiaoshuliang = BigDecimal.ZERO;
					// 库存状态为“2”、“3”、“4”，不可销数量=不可销数量+“采购入库单”的“数量”；
					BigDecimal bkxsl = BigDecimal.ZERO;
					if (kucunzhuangtai == 1) {
						kexiaoshuliang = shuliang;
					} else {
						bkxsl = object.getBigDecimal("shuliang");
					}
					totalStock.set("kexiaoshuliang", kexiaoshuliang);
					totalStock.set("bkxsl", bkxsl);

					// 成本单价；insert操作时，成本单价= “采购入库单”的“含税价”；
					totalStock.set("chengbendanjia", hanshuijia);

					// 核算成本价价:insert操作时，核算成本价= “采购入库单”的“核算成本价”；
					// totalStock.set("hscbj", object.getBigDecimal("hscbj"));
					// 库存含税金额:库存数量*成本单价
					BigDecimal kchsje = object.getBigDecimal("shuliang")
							.multiply(hanshuijia == null ? BigDecimal.ZERO : hanshuijia)
							.setScale(2, BigDecimal.ROUND_HALF_UP);
					totalStock.set("kchsje", kchsje);

					// 最高含税进价：最高含税进价>“采购入库单”的“含税价”,
					// 最高含税进价不变，反之最高含税进价=“采购入库单”的“含税价”；
					totalStock.set("zghsjj", hanshuijia);

					// 最低含税进价：最低含税进价<“采购入库单”的“含税价”,
					// 最低含税进价不变，反之最低含税进价=“采购入库单”的“含税价”；
					totalStock.set("zdhsjj", hanshuijia);

					// 最后含税进价：取“采购入库单”的“含税价”；
					totalStock.set("zhhsjj", hanshuijia);

					// 最后进货单位ID
					String gysbh = head.getStr("gysbh");
					Record supplier = getSupplier(gysbh);
					totalStock.set("zhjhId", supplier == null || supplier.getStr("suppliersid") == null ? null
							: supplier.getStr("suppliersid"));
					// 最后入库日期
					totalStock.set("zhrksj", ToolDateTime.format(new Date(), ToolDateTime.pattern_ymd));
					boolean save = Db.save("xyy_erp_bill_shangpinzongkucun", "ID", totalStock);
					log.info("总库存新增成功：" + shangpinbianhao + ":" + save);
				} else {
					goodsStock.set("updateTime", currentTime);
					// 采购入库单和采购退出出库单
					if (type == 1 || type == 3) {
						// 成本单价:update操作时，成本单价=（当前数量*当前含税价+历史库存数量*历史成本单价）/（当前数量+历史库存数量）；
						BigDecimal current = object.getBigDecimal("shuliang")
								.multiply(object.getBigDecimal("hanshuijia") == null ? BigDecimal.ZERO
										: object.getBigDecimal("hanshuijia"));
						BigDecimal pre = goodsStock.getBigDecimal("kucunshuliang")
								.multiply(goodsStock.getBigDecimal("chengbendanjia") == null ? BigDecimal.ZERO
										: goodsStock.getBigDecimal("chengbendanjia"));
						BigDecimal zongjia = current.add(pre);
						BigDecimal zongshuliang = goodsStock.getBigDecimal("kucunshuliang")
								.add(object.getBigDecimal("shuliang"));
						BigDecimal chengbendanjia = BigDecimal.ZERO;
						if (zongshuliang.compareTo(BigDecimal.ZERO) != 0) {
							chengbendanjia = zongjia.divide(zongshuliang, 6, BigDecimal.ROUND_HALF_UP);
						}
						goodsStock.set("chengbendanjia", chengbendanjia);
					} else if (type == 4) {// 采购退补价开票单
						// 成本单价:update操作时，成本单价=（当前未销数量*当前含税差价+历史库存数量*历史成本单价）/（当前未销数量+历史库存数量）；
						BigDecimal current = object.getBigDecimal("weixiaoshuliang") == null ? BigDecimal.ZERO
								: object.getBigDecimal("weixiaoshuliang")
										.multiply(object.getBigDecimal("hanshuichajia") == null ? BigDecimal.ZERO
												: object.getBigDecimal("hanshuichajia"));
						BigDecimal pre = goodsStock.getBigDecimal("kucunshuliang")
								.multiply(goodsStock.getBigDecimal("chengbendanjia") == null ? BigDecimal.ZERO
										: goodsStock.getBigDecimal("chengbendanjia"));
						BigDecimal zongjia = current.add(pre);
						// 当前未销数量+历史库存数量
						BigDecimal zongshuliang = object.getBigDecimal("weixiaoshuliang") == null ? BigDecimal.ZERO
								: object.getBigDecimal("weixiaoshuliang").add(goodsStock.get("kucunshuliang"));
						BigDecimal chengbendanjia = BigDecimal.ZERO;
						if (zongshuliang.compareTo(BigDecimal.ZERO) != 0) {
							chengbendanjia = zongjia.divide(zongshuliang, 6, BigDecimal.ROUND_HALF_UP);
						}
						goodsStock.set("chengbendanjia", chengbendanjia);
					}
					switch (type) {
					case 1:
						// goodsStock.set("kucunzhuangtai",
						// kucunzhuangtai);//库存状态
						BigDecimal kexiaoshuliang = goodsStock.getBigDecimal("kexiaoshuliang");// 可销数量
						BigDecimal bkxsl = goodsStock.getBigDecimal("bkxsl");// 不可销数量
						if (kucunzhuangtai == 1) {
							// 可销数量：库存状态为“1”，可销数量=可销数量+“采购入库单”的“数量”；
							kexiaoshuliang = goodsStock.getBigDecimal("kexiaoshuliang")
									.add(object.getBigDecimal("shuliang"));
						} else {
							// 库存状态为“2”、“3”、“4”，不可销数量=不可销数量+“采购入库单”的“数量”；
							bkxsl = goodsStock.getBigDecimal("bkxsl").add(object.getBigDecimal("shuliang"));
						}
						goodsStock.set("kexiaoshuliang", kexiaoshuliang);
						goodsStock.set("bkxsl", bkxsl);

						// 核算成本价价:update操作时，（当前数量*当前核算成本价+历史库存数量*历史核算成本价）/（当前数量+历史库存数量）
						/*
						 * BigDecimal newHscbj = new
						 * object.getBigDecimal("shuliang").multiply(
						 * object.getBigDecimal("hscbj") == null ?
						 * BigDecimal.ZERO : object.getBigDecimal("hscbj"));
						 * BigDecimal preHscbj =
						 * goodsStock.getBigDecimal("kucunshuliang")
						 * .multiply(goodsStock.getBigDecimal("hscbj") == null ?
						 * BigDecimal.ZERO : goodsStock.getBigDecimal("hscbj"));
						 * BigDecimal zongHscbj = newHscbj.add(preHscbj);
						 * BigDecimal hscbj = zongHscbj.divide(lishishuliang, 6,
						 * BigDecimal.ROUND_HALF_UP); goodsStock.set("hscbj",
						 * hscbj);
						 */

						// update操作时，库存 数量=库存数量+“采购入库单”的“数量”；
						BigDecimal kucunshuliang = object.getBigDecimal("shuliang")
								.add(goodsStock.getBigDecimal("kucunshuliang"));
						goodsStock.set("kucunshuliang", kucunshuliang);

						// 库存含税金额:库存数量*成本单价
						BigDecimal kchsje = goodsStock.getBigDecimal("kucunshuliang")
								.multiply(goodsStock.getBigDecimal("chengbendanjia"))
								.setScale(2, BigDecimal.ROUND_HALF_UP);
						goodsStock.set("kchsje", kchsje);

						goodsStock.set("beizhu", "采购入库单");// 摘要：“采购入库单”；
						// 最高含税进价：最高含税进价>“采购入库单”的“含税价”,
						// 最高含税进价不变，反之最高含税进价=“采购入库单”的“含税价”；
						// 含税价大于最高含税价，则最高含税价取含税价
						BigDecimal newHsj = object.getBigDecimal("hanshuijia") == null ? BigDecimal.ZERO
								: object.getBigDecimal("hanshuijia");
						BigDecimal oldHsj = goodsStock.getBigDecimal("zghsjj") == null ? BigDecimal.ZERO
								: goodsStock.getBigDecimal("zghsjj");
						if (newHsj.compareTo(oldHsj) != -1) {
							goodsStock.set("zghsjj", newHsj);
						}

						// 最低含税进价：最低含税进价<“采购入库单”的“含税价”,
						// 最低含税进价不变，反之最低含税进价=“采购入库单”的“含税价”；
						BigDecimal oldZdhsjj = goodsStock.getBigDecimal("zdhsjj") == null ? BigDecimal.ZERO
								: goodsStock.getBigDecimal("zdhsjj");
						if (newHsj.compareTo(oldZdhsjj) == -1) {
							goodsStock.set("zdhsjj", newHsj);
						}

						// 最后含税进价：取“采购入库单”的“含税价”；
						goodsStock.set("zhhsjj", newHsj);

						// 最后进货单位ID
						String gysbh = head.getStr("gysbh");
						Record supplier = getSupplier(gysbh);
						goodsStock.set("zhjhId", supplier == null || supplier.getStr("suppliersid") == null ? null
								: supplier.getStr("suppliersid"));

						// 最后入库日期
						goodsStock.set("zhrksj", ToolDateTime.format(new Date(), ToolDateTime.pattern_ymd));
						break;
					// case 2:
					// goodsStock.set("beizhu", "采购退出开票单");
					// break;
					case 3:
						goodsStock.set("beizhu", "3采购退出出库单");
						// goodsStock.set("kucunzhuangtai",
						// kucunzhuangtai);//库存状态
						BigDecimal kexiaoshuliang1 = goodsStock.getBigDecimal("kexiaoshuliang");
						BigDecimal bkxsl1 = goodsStock.getBigDecimal("bkxsl");// 不可销数量
						if (kucunzhuangtai == 1) {
							// 可销数量：库存状态为“1”，可销数量=可销数量+“采购入库单”的“数量”；
							kexiaoshuliang1 = goodsStock.getBigDecimal("kexiaoshuliang")
									.add(object.getBigDecimal("shuliang"));
						} else {
							// 库存状态为“2”、“3”、“4”，不可销数量=不可销数量+“采购入库单”的“数量”；
							bkxsl1 = goodsStock.getBigDecimal("bkxsl").add(object.getBigDecimal("shuliang"));
						}
						goodsStock.set("kexiaoshuliang", kexiaoshuliang1);
						goodsStock.set("bkxsl", bkxsl1);

						// 核算成本价价:update操作时，（当前数量*当前核算成本价+历史库存数量*历史核算成本价）/（当前数量+历史库存数量）
						/*
						 * BigDecimal newHscbj1 = new
						 * object.getBigDecimal("shuliang").multiply(
						 * object.getBigDecimal("hscbj") == null ?
						 * BigDecimal.ZERO : object.getBigDecimal("hscbj"));
						 * BigDecimal preHscbj1 =
						 * goodsStock.getBigDecimal("kucunshuliang")
						 * .multiply(goodsStock.getBigDecimal("hscbj") == null ?
						 * BigDecimal.ZERO : goodsStock.getBigDecimal("hscbj"));
						 * BigDecimal zongHscbj1 = newHscbj1.add(preHscbj1);
						 * BigDecimal hscbj1 = zongHscbj1.divide(lishishuliang,
						 * 6, BigDecimal.ROUND_HALF_UP); goodsStock.set("hscbj",
						 * hscbj1);
						 */

						// update操作时，库存 数量=库存数量+“采购入库单”的“数量”；
						BigDecimal kucunshuliang1 = object.getBigDecimal("shuliang")
								.add(goodsStock.getBigDecimal("kucunshuliang"));
						goodsStock.set("kucunshuliang", kucunshuliang1);

						// 库存含税金额:库存数量*成本单价
						BigDecimal kchsje2 = goodsStock.getBigDecimal("kucunshuliang")
								.multiply(goodsStock.getBigDecimal("chengbendanjia"))
								.setScale(2, BigDecimal.ROUND_HALF_UP);
						goodsStock.set("kchsje", kchsje2);
						break;
					case 4:
						goodsStock.set("beizhu", "采购退补价执行单");
						// 库存含税金额:库存数量*成本单价
						BigDecimal kchsje3 = goodsStock.getBigDecimal("kucunshuliang")
								.multiply(goodsStock.getBigDecimal("chengbendanjia"))
								.setScale(2, BigDecimal.ROUND_HALF_UP);
						goodsStock.set("kchsje", kchsje3);
						break;
					}
					boolean update = Db.update("xyy_erp_bill_shangpinzongkucun", "ID", goodsStock);
					log.info("总库存修改成功：" + shangpinbianhao + ":" + update);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查询商品批号
	 * 
	 * @param shangpinId
	 * @param pihao
	 * @return
	 */
	public static Record getBatchGoods(String goodsId, String pihao) {
		String sql = "select * from xyy_erp_dic_shangpinpihao where goodsId = ? and pihao = ?";
		Record record = Db.findFirst(sql, goodsId, pihao);
		return record;
	}

	/**
	 * 查询商品批号库存（加状态）
	 * 
	 * @param shangpinId
	 * @param pihao
	 * @return
	 */
	public static Record getBatchStock(String shangpinId, String pihaoId, int kucunzhuangtai) {
		String sql = "select * from xyy_erp_bill_shangpinpihaokucun where shangpinId = ? and  pihaoId = ? and kucunzhuangtai = ?";
		Record record = Db.findFirst(sql, shangpinId, pihaoId, kucunzhuangtai);
		return record;
	}

	/**
	 * 查询商品批号库存(正常状态)
	 * 
	 * @param shangpinId
	 * @param pihao
	 * @return
	 */
	public static Record getBatchStock(String shangpinId, String pihaoId) {
		String sql = "select * from xyy_erp_bill_shangpinpihaokucun where kucunzhuangtai = 1 and  shangpinId = ? and  pihaoId = ?";
		Record record = Db.findFirst(sql, shangpinId, pihaoId);
		return record;
	}

	/**
	 * 商品批号处理(只有【采购入库单】)xyy_erp_dic_shangpinpihao
	 */
	public static void operteBatchGoods(Record headObject, List<Record> body, int type) {
		List<Record> newList = new ArrayList<>();
		List<Record> updateList = new ArrayList<>();
		for (Record r : body) {
			String shangpinbianhao = r.getStr("shangpinbianhao");
			Record goods = getGoods(shangpinbianhao);
			String goodsid = null;
			if (goods != null) {
				goodsid = goods.getStr("goodsid");
			}
			String pihao = r.getStr("pihao");
			Record batchGoods = getBatchGoods(goodsid, pihao);
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			if (batchGoods == null) {// 新增
				Record newRecord = new Record();
				String ID = "PH" + TimeUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.getRandomCode(3);
				newRecord.set("ID", ID);
				newRecord.set("createTime", currentTime);
				newRecord.set("updateTime", null);
				newRecord.set("orgId", r.getStr("orgId"));
				// newRecord.set("orgId", "E0EHOUA9KDE");
				newRecord.set("shengchanriqi", r.getDate("shengchanriqi"));
				newRecord.set("youxiaoqizhi", r.getDate("youxiaoqizhi"));
				newRecord.set("pihao", r.getStr("pihao"));
				newRecord.set("pihaoId", ID);
				newRecord.set("goodsid", goodsid);
				newRecord.set("zhrkrq", new Date());
				newRecord.set("miejunpihao", r.getStr("miejunpihao"));
				newRecord.set("beizhu", "商品批号");
				newRecord.set("rowID", UUIDUtil.newUUID());
				newList.add(newRecord);
			} else {// 修改
				batchGoods.set("updateTime", currentTime);
				batchGoods.set("zhrkrq", new Date());
				batchGoods.set("miejunpihao", r.getStr("miejunpihao"));
				batchGoods.set("shengchanriqi", r.getDate("shengchanriqi"));
				batchGoods.set("youxiaoqizhi", r.getDate("youxiaoqizhi"));
				updateList.add(batchGoods);
			}
		}
		if (newList != null && newList.size() > 0) {
			Db.batchSave("xyy_erp_dic_shangpinpihao", newList, 100);
		}
		if (updateList != null && updateList.size() > 0) {
			Db.batchUpdate("xyy_erp_dic_shangpinpihao", "ID", updateList, 100);
		}
		log.info("【商品批号处理】");

	}

	/**
	 * 商品批号库存处理
	 */
	// TODO
	public static void operateBatchStock(Record headObject, List<Record> body, int type) {
		for (Record r : body) {
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			String shangpinbianhao = r.getStr("shangpinbianhao");
			int kucunzhuangtai = r.getInt("kucunzhuangtai");
			String pihao = r.getStr("pihao");
			Record goods = getGoods(shangpinbianhao);
			String goodsid = null;
			if (goods != null) {
				goodsid = goods.getStr("goodsid");
			}
			Record phRecord = getPiHaoByName(pihao, goodsid);
			String phId = null;
			if (phRecord != null) {
				phId = phRecord.getStr("pihaoId");
			}
			Record batchStock = getBatchStock(goodsid, phId, kucunzhuangtai);
			if (batchStock == null) {
				Record record = new Record();
				record.set("ID", UUIDUtil.newUUID());
				record.set("createTime", currentTime);
				record.set("updateTime", currentTime);
				// record.set("orgId", "E0EHOUA9KDE");
				record.set("orgId", r.getStr("orgId"));
				record.set("shangpinId", goodsid);
				record.set("pihaoId", phId);
				record.set("kucunzhuangtai", r.getInt("kucunzhuangtai"));
				record.set("kucunshuliang", r.getBigDecimal("shuliang"));
				record.set("beizhu", "商品批号库存");
				record.set("rowID", UUIDUtil.newUUID());
				// 1：采购入库单，2：采购退出开票单，3：采购退出出库单，4:采购退补价执行单
				switch (type) {
				case 1:
					record.set("beizhu", "采购入库单");
					break;
				case 2:
					record.set("beizhu", "采购退出开票单");
					break;
				case 3:
					record.set("beizhu", "采购退出出库单");
					break;
				case 4:
					record.set("beizhu", "采购退补价执行单");
					break;
				}
				boolean save = Db.save("xyy_erp_bill_shangpinpihaokucun", "ID", record);
				log.info("商品批号库存的【新增】，商品编号：【"+shangpinbianhao+"】:"+save);
			} else {
				// update操作时，库存数量=库存数量+“采购入库单”的“数量”；
				BigDecimal add = r.getBigDecimal("shuliang").add((batchStock.getBigDecimal("kucunshuliang") == null
						? BigDecimal.ZERO : batchStock.getBigDecimal("kucunshuliang")));
				batchStock.set("kucunshuliang", add);
				batchStock.set("updateTime", currentTime);
				boolean update = Db.update("xyy_erp_bill_shangpinpihaokucun", "ID", batchStock);
				log.info("商品批号库存的【修改】，商品编号：【"+shangpinbianhao+"】:"+update);
			}
		}
		log.info("【商品批号库存处理】");
	}

	/**
	 * 商品库存数量占用表(采购退出开票单)
	 */
	public static void operateGoodsStockUses(Record head, List<Record> body, int type) {

		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		// Record head = getHead(dsi);
		// List<Record> body = getBody(dsi);
		List<Record> list = new ArrayList<>();
		for (Record r : body) {
			Record record = new Record();
			record.set("ID", UUIDUtil.newUUID());
			record.set("createTime", currentTime);
			record.set("updateTime", currentTime);
			// record.set("orgId", "E0EHOUA9KDE");
			record.set("orgId", r.getStr("orgId"));
			Record goods = getGoods(r.getStr("shangpinbianhao"));
			record.set("shangpinId", goods == null || goods.getStr("goodsid") == null ? null : goods.getStr("goodsid"));
			Record phRecord = getPiHaoByName(r.getStr("pihao"), goods.getStr("goodsid"));
			record.set("pihaoId",
					phRecord == null || phRecord.getStr("pihaoId") == null ? null : phRecord.getStr("pihaoId"));
			record.set("danjubianhao", head.getStr("danjubianhao"));
			record.set("kaipiaoriqi", head.getDate("kaipiaoriqi"));
			record.set("kaipiaoyuan", head.getStr("kaipiaoyuan"));
			record.set("zhaiyao", "采购退出开票单");
			record.set("danjuxuhao", r.getInt("SN"));
			record.set("zhanyongshuliang", r.getBigDecimal("shuliang"));
			record.set("kucunzhuangtai", r.getInt("kucunzhuangtai"));
			record.set("beizhu", "采购退出开票单");
			list.add(record);
		}

		int[] batchSave = Db.batchSave("xyy_erp_bill_shangpinkucunzhanyong", list, 100);
		for (int i : batchSave) {
			log.info("【采购退出开票单】是否成功：" + i);
		}
		log.info("【商品库存数量占用表处理】");

	}

	/**
	 * 清除商品库存数量占用表(采购退出出库单)
	 */
	public static void clearGoodsStockUses(DataSetInstance dsi, int type) {
		Record head = getHead(dsi);
		String danjubianhao = head.getStr("sjdjbh");// 原采购订单的单据编号
		String sql = "delete from  xyy_erp_bill_shangpinkucunzhanyong where danjubianhao ='" + danjubianhao + "'";
		int update = Db.update(sql);
		log.info("购退出出库单】清占是否成功：" + update);
	}

	/**
	 * 自动生成采购退补价执行单
	 */
	public static void generateCGTBJZX(Record head, List<Record> body) {
		// 生成采购退补价执行单
		head.remove("BillID");
		head.set("status", 40);
		head.set("sjdjbh", head.get("danjubianhao"));
		String danjubianhao = PullWMSDataUtil.orderno();
		head.set("danjubianhao", danjubianhao);
		for (Record record : body) {
			record.remove("BillID");
			record.remove("BillDtlID");
			record.remove("rowID");
			BigDecimal hanshuijine = record.getBigDecimal("hanshuijine");
			// 金额=含税金额/(1+税率)
			BigDecimal jine = BigDecimal.ZERO;
			BigDecimal shuilvs = record.getBigDecimal("shuilv").add(new BigDecimal("100")).divide(new BigDecimal("100"),
					2, RoundingMode.HALF_UP);
			if (hanshuijine != null) {
				jine = hanshuijine.divide(shuilvs, 2, RoundingMode.HALF_UP);
			}
			// 单价=含税价/(1+税率)
			BigDecimal danjia = record.getBigDecimal("hanshuichajia").divide(shuilvs, 6, BigDecimal.ROUND_HALF_UP);
			record.set("danjia", danjia);
			record.set("jine", jine);
			// 税额=含税金额-金额
			BigDecimal shuie = hanshuijine.subtract(jine);
			record.set("shuie", shuie);
		}
		BigDecimal zhsje = PullWMSDataUtil.getSumMoney(body, "hanshuijine");
		head.set("zhsje", zhsje);// 总含税金额
		head.set("zongjine", PullWMSDataUtil.getSumMoney(body, "jine"));// 总金额
		head.set("zongshuie", PullWMSDataUtil.getSumMoney(body, "shuie"));// 总税额
		// 调用统一保存操作
		BillContext context = new BillContext();
		JSONObject buildContext = PullWMSDataUtil.buildContext(head, body, "caigoutuibujiazhixingdan",
				"caigoutuibujiazhixingdan_details");
		PullWMSDataUtil.saveAction(context, buildContext, "caigoutuibujiazhixingdan");

		// 回写采购退补价开票单
		String sjdjbh = head.getStr("sjdjbh");
		backWriteCaiGouTuiBuJiaKaiPiaoDan(sjdjbh);

		// 商品总库存处理
		RecordDataUtil.operateTotalStock(head, body, 4);

		// 商品账页处理
		RecordDataUtil.addGoodsFolio(head, body, 4);

		// 往来账处理
		RecordDataUtil.addDealingFolio(head, body, 4);
	}

	/**
	 * 回写采购退补价开票单
	 * 
	 * @param danjubianhao
	 */
	private static void backWriteCaiGouTuiBuJiaKaiPiaoDan(String danjubianhao) {
		String sql = "update xyy_erp_bill_caigoutuibujiakaipiaodan set shifoutiqu = 1 where danjubianhao = ?";
		int update = Db.update(sql, danjubianhao);
		log.info("【回写采购退补价开票单】是否成功：" + update);

	}

	/**
	 * 生成新商品批号记录
	 * 
	 * @param shangpin
	 * @param sp
	 * @param iD
	 */
	public static void createNewShangPinPiHao(Record shangpin, Record sp, String phId) {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Record newRecord = new Record();
		newRecord.set("ID", phId);
		newRecord.set("createTime", currentTime);
		newRecord.set("updateTime", null);
		newRecord.set("orgId", sp.getStr("orgId"));
		try {
			newRecord.set("shengchanriqi", format.parse(shangpin.getStr("rq_sc")));
			newRecord.set("youxiaoqizhi", format.parse(shangpin.getStr("yxqz")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newRecord.set("pihao", shangpin.getStr("ph"));
		newRecord.set("pihaoId", phId);
		newRecord.set("goodsid", sp.getStr("goodsid"));
		newRecord.set("zhrkrq", new Date());
		newRecord.set("rowID", UUIDUtil.newUUID());

		Db.save("xyy_erp_dic_shangpinpihao", newRecord);

	}

	/**
	 * 生成新商品批号库存记录
	 * 
	 * @param shangpin
	 * @param sp
	 * @param phId
	 */
	public static void createNewShangPinPiHaoKuCun(Record shangpin, Record sp, String phId) {
		List<Record> list = new ArrayList<>();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		Record record = new Record();
		record.set("ID", UUIDUtil.newUUID());
		record.set("createTime", currentTime);
		record.set("updateTime", currentTime);
		// record.set("orgId", "E0EHOUA9KDE");
		record.set("orgId", sp.getStr("orgId"));
		record.set("shangpinId", sp.getStr("goodsid"));
		record.set("pihaoId", phId);
		record.set("kucunshuliang", 0.00);
		record.set("kucunzhuangtai", 1);
		record.set("rowID", UUIDUtil.newUUID());

		list.add(record);

		Db.batchSave("xyy_erp_bill_shangpinpihaokucun", list, 500);
	}

	public static void createNewShangPinZongKuCun(Record shangpin, Record order) {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Record newRecord = new Record();
		newRecord.set("ID", UUIDUtil.newUUID());
		newRecord.set("createTime", currentTime);
		newRecord.set("updateTime", null);
		newRecord.set("orgId", order.getStr("orgId"));
		newRecord.set("zhjhId", 0);
		newRecord.set("chengbendanjia", order.getBigDecimal("hanshuijia"));
		try {
			newRecord.set("zhrksj", format.parse(format.format(new Date())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newRecord.set("shangpinId", shangpin.getStr("spid"));
		newRecord.set("rowID", UUIDUtil.newUUID());

		Db.save("xyy_erp_bill_shangpinzongkucun", newRecord);
	}

}
