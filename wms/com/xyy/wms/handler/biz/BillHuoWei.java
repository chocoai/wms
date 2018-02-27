package com.xyy.wms.handler.biz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;

/**
 * 自动分配货位逻辑
 * 
 * @author dell
 *
 */
public class BillHuoWei {

	private static Logger LOGGER = Logger.getLogger(BillRuKuYanShouPostHandler.class);

	// 待验货位/不合格货位,只取一个空货位,待验，不合格只有一个货位，不管预占
	@SuppressWarnings("unused")
	public Record daiyanHuoWei(Record head, Record body) {
		Record daiyanHuoWei = new Record();
		int kqlbbh = 0;
		int yanshoupingding = body.get("yanshoupingding");
		String shangpinbianhao = body.getStr("shangpinbianhao");
		String pihao = body.getStr("pihao");
		BigDecimal shuliang = body.getBigDecimal("shuliang");
		if (yanshoupingding == 2) {
			kqlbbh = 3;
		}

		if (yanshoupingding == 0 || yanshoupingding == 3) {
			kqlbbh = 12;
		}
		// 待验/不合格 货位
		String bhgSql = "SELECT a.huoweibianhao,a.ID,b.kuqumingcheng,a.cangkuID,a.orgId FROM xyy_wms_dic_huoweiziliaoweihu a,xyy_wms_dic_kuqujibenxinxi b WHERE "
				+ "a.kuquId = b.ID AND b.kqlbbh = ?";
		List<Record> list = Db.find(bhgSql, kqlbbh);

		// 商品对象
		String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
		String goodsid = shangpinObj.get("goodsid");

		// 批号对象
		String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId= ? limit 1";
		Record pihaoObj = Db.findFirst(pihaoSql, pihao, goodsid);
		String pihaoId = pihaoObj.getStr("pihaoId");
		if (list.size() > 0) {
			for(Record r : list){
				String houweiId = r.get("ID");
				daiyanHuoWei.set("houweiId", houweiId);
				daiyanHuoWei.set("huoweibianhao", r.get("huoweibianhao"));
				daiyanHuoWei.set("kuqumingcheng", r.get("kuqumingcheng"));
				daiyanHuoWei.set("shuliang", shuliang);
				return daiyanHuoWei;
				
			}
		}
		return null;
	}

	// 零散货位逻辑
	
	public List<Record> zidongLingSanHuoWei(Record head, Record body) {
		// 零散
		BigDecimal lingsanshu = body.get("lingsanshu");
		int dingdanleixing = head.getInt("dingdanleixing");
		List<Record> ruleObj = getShangjiaRule(dingdanleixing);
		if (ruleObj.size() <= 0) {
			return null;
		}
		// 商品对象
		String shangpinbianhao = body.get("shangpinbianhao");
		String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
		String goodsid = shangpinObj.getStr("goodsid");

		// 商品存储限定对象
		String spccxdwhSql = "SELECT b.* FROM xyy_wms_dic_spccxdwh a INNER JOIN xyy_wms_dic_spccxdwh_list b ON a.ID = b.ID AND b.kuquleibie = 0 and a.shangpinbianhao=?";
		Record spccxdwhObj = Db.findFirst(spccxdwhSql, shangpinbianhao);
		if (spccxdwhObj == null) {
			return null;
		}
		
		// 大包装体积 除 大包装数量 求 最小单位面积
		if ("0.00".equals(shangpinObj.getBigDecimal("dbztj")) && "0.00".equals(shangpinObj.getBigDecimal("dbzsl"))) {
			return null;
		}
		BigDecimal[] dar = shangpinObj.getBigDecimal("dbztj").divideAndRemainder(shangpinObj.getBigDecimal("dbzsl"));

		// 批号对象

		// 批号对象
		String pihao = body.getStr("pihao");
		String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId= ? limit 1";
		Record pihaoObj = Db.findFirst(pihaoSql, pihao, goodsid);
		String pihaoId = pihaoObj.getStr("pihaoId");

		// 所有规则
		 List<Record> list2 = getShangjiaRule(dingdanleixing);
		 if(null==list2){
			return null;
		 }

		// 最终 货位 结果集
		List<Record> huoweiResult = new ArrayList<Record>();
		// 走零散库区
		int kuquleibie = 0;
		List<Record> allHuoWei = getHouwei(shangpinbianhao, kuquleibie);
		
		if(allHuoWei == null || allHuoWei.size() ==0 ){
			return null;
		}
	
		for (Record eachHuoWeiId : allHuoWei) {
			// 货位对象
			String huoweibianhaos = eachHuoWeiId.get("huoweibianhao");

			String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
			Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhaos);
			// 货位体积
			BigDecimal hwtj = huoweiObj.getBigDecimal("tiji");
			// 每个空货位最多放多少个商品
			BigDecimal[] khwdir = hwtj.divideAndRemainder(dar[0]);
			// 商品或为库存对象
			String spphhwKuCunSql = "SELECT * FROM xyy_wms_bill_shangpinpihaohuoweikucun WHERE shangpinId=? and pihaoId=? and huoweiId=? order by createTime desc limit 1";
			String[] params = { goodsid, pihaoId, huoweiObj.get("ID") };
			Record kucunObj = Db.findFirst(spphhwKuCunSql, params);
			// 如果kucunObj！=null 说明不是新商品，新商品给个空货位
			if (null != kucunObj) {
				BigDecimal yuzhen = KucuncalcService.kcCalc.getYuzhanRuKuHW(
						new KuncunParameter(huoweiObj.getStr("orgId"), huoweiObj.getStr("cangkuID"), goodsid,
								pihaoId, huoweiObj.getStr("ID")),
						new Object[] { 1, 3, 6 },true);

				BigDecimal huoweishuliang = kucunObj.getBigDecimal("kucunshuliang").add(yuzhen);
				// 货位已暂用体积
				BigDecimal hwzytj = huoweishuliang.multiply(dar[0]);
				// 不是空货位
				if (hwtj.subtract(hwzytj).compareTo(new BigDecimal(0)) > 0) { // 货位被占满了
					// 货位剩余体积
					BigDecimal hwsytj = hwtj.subtract(hwzytj);
					// 货位剩余体积可放数量
					BigDecimal[] hwkfsl = hwsytj.divideAndRemainder(dar[0]);
					// 货位剩余体积 够放置 零散数
					if (hwkfsl[0].compareTo(lingsanshu) >= 0) {
						huoweiObj.set("shuliang", lingsanshu);
						huoweiResult.add(huoweiObj);
						return huoweiResult;
					} else { // 货位剩余体积 不够放置 零散数，需要多个货位
						huoweiObj.set("shuliang", lingsanshu);
						huoweiResult.add(huoweiObj);
						lingsanshu = lingsanshu.subtract(hwkfsl[0]);
						continue;
					}
					// 空货位,如果一个货位可以放下，就返回，否则就在拆分
				} else if (hwzytj.compareTo(new BigDecimal(0)) == 0) {
					if (hwtj.compareTo(lingsanshu.multiply(dar[0])) >= 0) {
						huoweiObj.set("shuliang", lingsanshu);
						huoweiResult.add(huoweiObj);
						return huoweiResult;
					} else { // 货位剩余体积 不够放置 零散数，需要多个货位
						huoweiObj.set("shuliang", lingsanshu);
						huoweiResult.add(huoweiObj);
						lingsanshu = lingsanshu.subtract(khwdir[0]);
						continue;
					}
				}
			} 
		}
			
		// 如果huoweiResult不是空的，但huoweiResult里的货位没有装下，就要给个新的货位
		if(huoweiResult.size()>0){
			BigDecimal zongshu = new BigDecimal("0.00");
			for(Record r:huoweiResult){
				// 每个货位上的数量
				BigDecimal huowei = r.get("shuliang");
				zongshu = zongshu.add(huowei);
			}
			if(zongshu.compareTo(lingsanshu)==-1){
				BigDecimal xushuliang = lingsanshu.subtract(zongshu); // 新货位上架的数量
				for(Record eachHuoWeiId : allHuoWei){
					for(Record r:huoweiResult){
						String huowei = r.getStr("huoweibianhao"); // 已经分配的货位的编号
						String huowei2 = eachHuoWeiId.getStr("huoweibianhao"); //新货位编号
						if(!huowei.equals(huowei2)){
							String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
							Record huoweiObj = Db.findFirst(huoweiSql, huowei2);
							String spphhwKuCunSql = "SELECT * FROM xyy_wms_bill_shangpinpihaohuoweikucun WHERE shangpinId=? and pihaoId=? and huoweiId=? order by createTime desc limit 1";
							String[] params = { shangpinObj.getStr("goodsid"), pihaoObj.getStr("pihaoId"), huoweiObj.get("ID") };
							Record kucunObj = Db.findFirst(spphhwKuCunSql, params);
							if(null==kucunObj){
								BigDecimal yuzhen = KucuncalcService.kcCalc.getYuzhanRuKuHW(
										new KuncunParameter(huoweiObj.getStr("orgId"), huoweiObj.getStr("cangkuID"), goodsid,
												pihaoId, huoweiObj.getStr("ID")),
										new Object[] { 1, 3, 6 },true);
								if(yuzhen.compareTo(BigDecimal.ZERO)==0){
									// 货位体积
									BigDecimal hwtj = huoweiObj.getBigDecimal("tiji");
									// 每个空货位最多放多少个商品
									BigDecimal[] khwdir = hwtj.divideAndRemainder(dar[0]);
									if (hwtj.compareTo(xushuliang.multiply(dar[0])) >= 0) {
										huoweiObj.set("shuliang", xushuliang);
										huoweiResult.add(huoweiObj);
										return huoweiResult;
									} else { // 货位剩余体积 不够放置 零散数，需要多个货位
										huoweiObj.set("shuliang", xushuliang);
										huoweiResult.add(huoweiObj);
										xushuliang = xushuliang.subtract(khwdir[0]);
										continue;
									}
								}
							}
						}
					}
				}
			}
		}
			
		// 如果huoweiResult里面是空的,说明是个新商品，新商品就需要找一个空货位
		if(huoweiResult.size()==0){
			for (Record eachHuoWeiId : allHuoWei){
				// 货位对象
				String huoweibianhaos = eachHuoWeiId.get("huoweibianhao");

				String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
				Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhaos);
				String spphhwKuCunSql = "SELECT * FROM xyy_wms_bill_shangpinpihaohuoweikucun WHERE shangpinId=? and pihaoId=? and huoweiId=? order by createTime desc limit 1";
				String[] params = { shangpinObj.getStr("goodsid"), pihaoObj.getStr("pihaoId"), huoweiObj.get("ID") };
				Record kucunObj = Db.findFirst(spphhwKuCunSql, params);
				
				if(null==kucunObj){
					BigDecimal yuzhen = KucuncalcService.kcCalc.getYuzhanRuKuHW(
							new KuncunParameter(huoweiObj.getStr("orgId"), huoweiObj.getStr("cangkuID"), goodsid,
									pihaoId, huoweiObj.getStr("ID")),
							new Object[] { 1, 3, 6 },true);
					if(yuzhen.compareTo(BigDecimal.ZERO)==0){
						// 货位体积
						BigDecimal hwtj = huoweiObj.getBigDecimal("tiji");
						// 每个空货位最多放多少个商品
						BigDecimal[] khwdir = hwtj.divideAndRemainder(dar[0]);
						if (hwtj.compareTo(lingsanshu.multiply(dar[0])) >= 0) {
							huoweiObj.set("shuliang", lingsanshu);
							huoweiResult.add(huoweiObj);
							return huoweiResult;
						} else { // 货位剩余体积 不够放置 零散数，需要多个货位
							huoweiObj.set("shuliang", lingsanshu);
							huoweiResult.add(huoweiObj);
							lingsanshu = lingsanshu.subtract(khwdir[0]);
							continue;
						}
					}
				}
			}
		}
		return huoweiResult;
	}

	// 整件逻辑
	public List<Record> zidongZhengjianHuoWei(Record head, Record body) {
		// 整件数
		BigDecimal zhengjianshu = body.get("zhengjianshu");
		int dingdanleixing = head.getInt("dingdanleixing");

		BigDecimal baozhuangshuliang = body.getBigDecimal("baozhuangshuliang");

		List<Record> ruleObj = getShangjiaRule(dingdanleixing);
		if (ruleObj.size() <= 0) {
			return null;
		}
		// 商品对象
		String shangpinbianhao = body.get("shangpinbianhao");
		String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
		// 商品存储限定对象
		String spccxdwhSql = "SELECT b.* FROM xyy_wms_dic_spccxdwh a INNER JOIN xyy_wms_dic_spccxdwh_list b ON a.ID = b.ID AND b.kuquleibie = 1 and a.shangpinbianhao=?";
		Record spccxdwhObj = Db.findFirst(spccxdwhSql, shangpinbianhao);
		if (spccxdwhObj == null) {
			return null;
		}
		// 商品存储上限数量 ****数据库数据类型要改成BigDecimal
		// BigDecimal kcsx = spccxdwhObj.getBigDecimal("kucunshangxian");
		// 大包装体积
		BigDecimal dbztj = shangpinObj.getBigDecimal("dbztj");
		if ("0.00".equals(dbztj)) {
			return null;
		}

		// 批号对象
		String pihao = body.getStr("pihao");
		String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao=? limit 1";
		Record pihaoObj = Db.findFirst(pihaoSql, pihao);
		// 最终 货位 结果集
		List<Record> huoweiResult = new ArrayList<Record>();

		int kuquleibie = 1;
		List<Record> allHuoWei = getHouwei(shangpinbianhao, kuquleibie);
		if(allHuoWei == null || allHuoWei.size() == 0){
			return null ;
		}
		for (Record eachHuoWeiId : allHuoWei) {
			// 货位对象
			String huoweibianhaos = eachHuoWeiId.get("huoweibianhao");

			String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
			Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhaos);
			// 货位体积
			BigDecimal hwtj = huoweiObj.getBigDecimal("tiji");
			// 每个空货位最多放几件
			if (dbztj.compareTo(BigDecimal.ZERO) == 0) {
				LOGGER.info("大包装体积为空，商品编号：" + shangpinbianhao);
				continue;
			}
			BigDecimal[] khwdir = hwtj.divideAndRemainder(dbztj);
			// 商品或为库存对象
			String spphhwKuCunSql = "SELECT * FROM xyy_wms_bill_shangpinpihaohuoweikucun WHERE shangpinId=? and pihaoId=? and huoweiId=? order by createTime desc limit 1";
			String[] params = { shangpinObj.getStr("goodsid"), pihaoObj.getStr("pihaoId"), huoweiObj.get("ID") };
			Record kucunObj = Db.findFirst(spphhwKuCunSql, params);
			// 如果kucunObj！=null 说明不是新商品，新商品给个空货位
			if (null != kucunObj) {
				BigDecimal yuzhen = KucuncalcService.kcCalc.getYuzhanRuKuHW(
						new KuncunParameter(kucunObj.getStr("orgId"), kucunObj.getStr("cangkuID"),
								shangpinObj.getStr("goodsid"), pihaoObj.getStr("pihaoId"), huoweiObj.get("ID")),
						new Object[] { 1, 3, 6 },true);
				BigDecimal huoweishuliang = kucunObj.getBigDecimal("kucunshuliang").add(yuzhen);

				// 货位已暂用体积
				BigDecimal hwzytj = huoweishuliang.multiply(dbztj);
				// 不是空货位
				if (hwtj.subtract(hwzytj).compareTo(new BigDecimal(0)) > 0) { // 货位被占满了
					// 货位剩余体积
					BigDecimal hwsytj = hwtj.subtract(hwzytj);
					// 货位剩余体积可放数量
					BigDecimal[] hwkfsl = hwsytj.divideAndRemainder(dbztj);
					// 货位剩余体积 够放置 零散数
					if (hwkfsl[0].compareTo(zhengjianshu) >= 0) {
						BigDecimal shuliang = baozhuangshuliang.multiply(zhengjianshu);
						huoweiObj.set("shuliang", shuliang);
						huoweiResult.add(huoweiObj);
						return huoweiResult;
					} else { // 货位剩余体积 不够放置 零散数，需要多个货位
						BigDecimal shuliang = baozhuangshuliang.multiply(zhengjianshu);
						huoweiObj.set("shuliang", shuliang);
						huoweiResult.add(huoweiObj);
						zhengjianshu = zhengjianshu.subtract(hwkfsl[0]);
						continue;
					}
					// 空货位,如果一个货位可以放下，就返回，否则就在拆分
				} else if (hwzytj.compareTo(new BigDecimal(0)) == 0) {
					if (hwtj.compareTo(zhengjianshu.multiply(dbztj)) >= 0) {
						BigDecimal shuliang = baozhuangshuliang.multiply(zhengjianshu);
						huoweiObj.set("shuliang", shuliang);
						huoweiResult.add(huoweiObj);
						return huoweiResult;
					} else { // 货位剩余体积 不够放置 零散数，需要多个货位
						BigDecimal shuliang = baozhuangshuliang.multiply(zhengjianshu);
						huoweiObj.set("shuliang", shuliang);
						huoweiResult.add(huoweiObj);
						zhengjianshu = zhengjianshu.subtract(khwdir[0]);
						continue;
					}
				}
			} 
		}
		
		
		// 如果huoweiResult不是空的，但huoweiResult里的货位没有装下，就要给个新的货位
		if(huoweiResult.size()>0){
			BigDecimal zongshu = new BigDecimal("0.00");
			for(Record r:huoweiResult){
				// 每个货位上的数量
				BigDecimal huowei = r.get("shuliang");
				zongshu = zongshu.add(huowei);
			}
			if(zongshu.compareTo(zhengjianshu.multiply(baozhuangshuliang))==-1){
				BigDecimal xushuliang = zhengjianshu.multiply(baozhuangshuliang).subtract(zongshu); // 新货位上架的数量
				BigDecimal syjs = xushuliang.divideAndRemainder(baozhuangshuliang)[0]; // 剩余件数
				
				for(Record eachHuoWeiId : allHuoWei){
					for(Record r:huoweiResult){
						String huowei = r.getStr("huoweibianhao"); // 已经分配的货位的编号
						String huowei2 = eachHuoWeiId.getStr("huoweibianhao"); //新货位编号
						if(!huowei.equals(huowei2)){
							String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
							Record huoweiObj = Db.findFirst(huoweiSql, huowei2);
							
							String spphhwKuCunSql = "SELECT * FROM xyy_wms_bill_shangpinpihaohuoweikucun WHERE shangpinId=? and pihaoId=? and huoweiId=? order by createTime desc limit 1";
							String[] params = { shangpinObj.getStr("goodsid"), pihaoObj.getStr("pihaoId"), huoweiObj.get("ID") };
							Record kucunObj = Db.findFirst(spphhwKuCunSql, params);
							if(kucunObj==null){
								// 查预占，如果预占等于0，就是新货位
								BigDecimal yuzhen = KucuncalcService.kcCalc.getYuzhanRuKuHW(
										new KuncunParameter(huoweiObj.getStr("orgId"), huoweiObj.getStr("cangkuID"),
												shangpinObj.getStr("goodsid"), pihaoObj.getStr("pihaoId"), huoweiObj.getStr("ID")),
										new Object[] { 1, 3, 6 },true);
								if(yuzhen.compareTo(BigDecimal.ZERO)==0){
									// 货位体积
									BigDecimal hwtj = huoweiObj.getBigDecimal("tiji");
									if (dbztj.compareTo(BigDecimal.ZERO) == 0) {
										LOGGER.info("大包装体积为空，商品编号：" + shangpinbianhao);
										continue;
									}
									BigDecimal[] khwdir = hwtj.divideAndRemainder(dbztj);
									
									if (hwtj.compareTo(syjs.multiply(dbztj)) >= 0) {
										huoweiObj.set("shuliang", xushuliang);
										huoweiResult.add(huoweiObj);
										return huoweiResult;
									} else { // 货位剩余体积 不够放置，需要多个货位
										huoweiObj.set("shuliang", xushuliang);
										huoweiResult.add(huoweiObj);
										syjs = syjs.subtract(khwdir[0]);
										continue;
									}
								}
							}
						}
					}
				}
			}
		}

		
		
		// 如果huoweiResult是空的，就说明是个新商品，新商品才走这段逻辑
		if(huoweiResult.size()==0){
			for(Record eachHuoWeiId : allHuoWei){
				// 货位对象
				String huoweibianhaos = eachHuoWeiId.get("huoweibianhao");

				String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
				Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhaos);
				
				String spphhwKuCunSql = "SELECT * FROM xyy_wms_bill_shangpinpihaohuoweikucun WHERE shangpinId=? and pihaoId=? and huoweiId=? order by createTime desc limit 1";
				String[] params = { shangpinObj.getStr("goodsid"), pihaoObj.getStr("pihaoId"), huoweiObj.get("ID") };
				Record kucunObj = Db.findFirst(spphhwKuCunSql, params);
				if(kucunObj==null){
					// 查预占，如果预占等于0，就是空货位
					BigDecimal yuzhen = KucuncalcService.kcCalc.getYuzhanRuKuHW(
							new KuncunParameter(huoweiObj.getStr("orgId"), huoweiObj.getStr("cangkuID"),
									shangpinObj.getStr("goodsid"), pihaoObj.getStr("pihaoId"), huoweiObj.getStr("ID")),
							new Object[] { 1, 3, 6 },true);
					if(yuzhen.compareTo(BigDecimal.ZERO)==0){
						// 货位体积
						BigDecimal hwtj = huoweiObj.getBigDecimal("tiji");
						if (dbztj.compareTo(BigDecimal.ZERO) == 0) {
							LOGGER.info("大包装体积为空，商品编号：" + shangpinbianhao);
							continue;
						}
						BigDecimal[] khwdir = hwtj.divideAndRemainder(dbztj);
						
						if (hwtj.compareTo(zhengjianshu.multiply(dbztj)) >= 0) {
							BigDecimal shuliang = baozhuangshuliang.multiply(zhengjianshu);
							huoweiObj.set("shuliang", shuliang);
							huoweiResult.add(huoweiObj);
							return huoweiResult;
						} else { // 货位剩余体积 不够放置 零散数，需要多个货位
							BigDecimal shuliang = baozhuangshuliang.multiply(zhengjianshu);
							huoweiObj.set("shuliang", shuliang);
							huoweiResult.add(huoweiObj);
							zhengjianshu = zhengjianshu.subtract(khwdir[0]);
							continue;
						}
					}
				}
			}
		}
		
		return huoweiResult;
	}

	// 根据商品编号和库区类别确定商品可以存放在那些货位上
	public List<Record> getHouwei(String shangpinbianhao, int kuquleibie) {
		List<Record> list = new ArrayList<Record>();
		String sql = "SELECT t.huoweibianhao FROM xyy_wms_dic_ljqhwgxwh_list t INNER JOIN xyy_wms_dic_ljqhwgxwh w ON t.ID = w.ID"
				+ " WHERE w.ljqbh IN (SELECT l.lluojiquyu FROM xyy_wms_dic_spccxdwh s INNER JOIN xyy_wms_dic_spccxdwh_list l ON s.ID = l.ID WHERE s.shangpinbianhao = ? and l.kuquleibie = ?)";
		
		list = Db.find(sql, shangpinbianhao, kuquleibie);
		return list;
	}

	/**
	 * 
	 * 查询上架规则
	 */
	public List<Record> getShangjiaRule(int dingdanleixing) {
		String sql = "select * from xyy_wms_bill_sjgzwh where dingdanleixing = ?";
		List<Record> list = Db.find(sql, dingdanleixing);
		return list;
	}

	/**
	 * 根据货位面积大小对map进行排序，私用。后续改成公用工具包
	 * 
	 * @param map
	 * @return
	 */
	public Map<String, Record> sortMap(Map<String, Record> map) {
		// 获取entrySet
		Set<Map.Entry<String, Record>> mapEntries = map.entrySet();

		for (Entry<String, Record> entry : mapEntries) {
			System.out.println("key:" + entry.getKey() + "   value:" + entry.getValue());
		}

		// 使用链表来对集合进行排序，使用LinkedList，利于插入元素
		List<Map.Entry<String, Record>> result = new LinkedList<>(mapEntries);
		// 自定义比较器来比较链表中的元素
		java.util.Collections.sort(result, new Comparator<Entry<String, Record>>() {
			// 基于entry的值（Entry.getValue()），来排序链表
			@Override
			public int compare(Entry<String, Record> o1, Entry<String, Record> o2) {

				return o2.getValue().getBigDecimal("tiji").compareTo(o1.getValue().getBigDecimal("tiji"));
			}

		});

		// 将排好序的存入到LinkedHashMap(可保持顺序)中，需要存储键和值信息对到新的映射中。
		Map<String, Record> linkMap = new LinkedHashMap<>();
		for (Entry<String, Record> newEntry : result) {
			linkMap.put(newEntry.getKey(), newEntry.getValue());
		}
		// 根据entrySet()方法遍历linkMap
		for (Map.Entry<String, Record> mapEntry : linkMap.entrySet()) {
			System.out.println("key:" + mapEntry.getKey() + "  value:" + mapEntry.getValue());
		}
		return linkMap;
	}

}
