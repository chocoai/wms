package com.xyy.erp.platform.system.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.Bidi;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.org.apache.regexp.internal.RE;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.util.PullWMSDataUtil;
import com.xyy.erp.platform.common.func.IFunc;
import com.xyy.erp.platform.common.tools.ToolWeb;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.StringUtil;


/**
 * 为满足特殊需求的控制器
 *
 * @author Chen
 */
public class SpecialController extends Controller {

    // 主动补货 空货位Map(key : 逻辑区编号)
    private Map<String, List<Record>> kongHuoWei_ljqbhKey = new HashMap<>();
    // 逻辑区对应空货位一次性查询个数 常量
    private static int QUERY_KONGHUOWEI_COUNT = 1000;

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
            case SPEConstant.SPE_CODE_CLAZZ:
                this.classRun();
                break;
            case SPEConstant.SPE_CODE_SQLSERVICE:
                this.sqlService();
                break;
                
                
            case SPEConstant.SPE_CODE_TOTAL_STORE:
                this.totalStore();
                break;
            case SPEConstant.SPE_CODE_CAIGOU_SAVE:
                this.caigouSave();
                break;
            case SPEConstant.SPE_CODE_GOODS_DETAILS:
                this.getGoodsDetails();
                break;
            case SPEConstant.SPE_CODE_GOODS_COS:
                this.getGoodsCos();
                break;
            case SPEConstant.SPE_CODE_QUERYCAIGOU_DETAILS:
                this.getQueryCaiGou();
                break;
            case SPEConstant.SPE_CODE_QUERY_DETAILS:
                this.getQueryDetailByBillID();
                break;
            case SPEConstant.SPE_CODE_QUERY_DETAILS1:
                this.getQueryDetailByBillID1();
                break;
            case SPEConstant.SPE_CODE_QUERY_DANJU:
                this.getQueryDetailByDanjubianhao();
                break;
            case SPEConstant.SPE_CODE_QUERY_CAIGOUDETAILS:
                this.getQueryCaiGouDetailByBillID();
                break;
            case SPEConstant.SPE_CODE_QUERY_XTDETAILS:
                this.getQueryxtDetailByBillID();
                break;
            case SPEConstant.SPE_CODE_QUERY_SHANGPIN_PIHAO_HUOWEI_KUCUN:
                this.getQueryKucunByspphhw();
                break;
            case SPEConstant.SPE_CODE_QUERY_SHOUHUODETAILS:
            	this.getQueryShouHuoDetailByBillID();
            	break;
            case SPEConstant.SPE_CODE_QUERY_XIAOTUISHOUHUODETAILS:
            	this.getQueryXiaoTuiShouHuoDetailByBillID();
            	break;
            case SPEConstant.SPE_CODE_QUERY_BUHEGEDETAILS:
            	this.getQueryBuHeGeDetailByBillID();
            	break;
            case SPEConstant.SPE_CODE_QUERY_XIAOTUIDINGDANCHAXUN:
            	this.getQueryXiaoTuiDingDanDetailByBillID();
            	break;
            case SPEConstant.SPE_CODE_QUERY_SHANGPIN_PIHAO_HUOWEI_KUCUN_Z:
                this.getQuerySpphhwKuCun();
                break;
                 case SPEConstant.SPE_CODE_CHECK_RONGQIHAO:
            	this.checkRongqihao();
            	break;
                 case SPEConstant.SPE_CODE_GET_TASK:
                	 this.getTask();
                	 break;
            default:
                this.render("/404.html");
                break;
        }
    }
    	
  	/**
  	 * 索取任务
  	 */
  	private void getTask() {		
  		BillContext context = this.buildBillContext();
  		//获取订单编号
  		 String dingdanbianhao = context.getString("dingdanbianhao");
  		//捡货单编号
  		String taskCode = context.getString("taskCode");
  		//获取当前操作人userName
  		String czrmc = context.getString("user[realName]");
  		//获取当前操作人UserID
  		String userId = context.getString("user[userId]");
  		//获取开始时间
  		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
  		String startDate = sdf.format(new Date());
  		//根据任务单编号查询任务单
  		Record jianhuodan = Db.findFirst("SELECT * from xyy_wms_bill_dabaorenwu WHERE taskCode = ?", taskCode);
  		if(jianhuodan == null){
  			context.addError(null, "查无此单");
  			return;
  		}
  		//获取任务单状态
        Integer status = jianhuodan.getInt("status");
        //获取拣货任务类型编号
  		Integer taskType = jianhuodan.getInt("taskType");
  		
  		Map<String, Object> map = new HashMap<String,Object>();
  		//零件，整件拣货
  		if(taskType == 10 || taskType == 20){
  			//查找订单下的所有未索取的拣货任务
  			StringBuffer findSql = new StringBuffer("SELECT * from xyy_wms_bill_dabaorenwu WHERE dingdanbianhao = ?");//查找所有的拣货单
  			List<Record> records = Db.find(findSql.toString(), dingdanbianhao);
  			boolean flag = true;
  			for (Record record : records) {
  				if(record.getInt("status") == 32){
  					continue;
  				}else{
  					flag = false;
  				}
			}
  		  if(flag){
  			  //更新波次计划明细表订单状态为开始拣货 
  		   	   StringBuffer updateSql = new StringBuffer("UPDATE xyy_wms_bill_bocijihua_details SET dingdanzhuangtai = 23 WHERE dingdanbianhao = ?");
  		   	   Db.update(updateSql.toString(),dingdanbianhao);
	  		   if(status == 32){
	  			   	//更新打包任务状态
	  	      		StringBuffer sql = new StringBuffer("UPDATE xyy_wms_bill_dabaorenwu SET status = 34,czrmc = ?,caozuoren = ?,startDate = ? WHERE taskCode = ? and status = 32");
	  	      		Db.update(sql.toString(),czrmc,userId,startDate,taskCode);
	  	      		map.put("msg", "任务索取成功");
	  	      	}
  		      }else{
  		    	if(status == 32){
	  			   	//更新打包任务状态
	  	      		StringBuffer sql = new StringBuffer("UPDATE xyy_wms_bill_dabaorenwu SET status = 34,czrmc = ?,caozuoren = ?,startDate = ? WHERE taskCode = ? and status = 32");
	  	      		Db.update(sql.toString(),czrmc,userId,startDate,taskCode);
	  	      		map.put("msg", "任务索取成功");
	  	      	}
  		      }
		}
  		//被动补货任务
  		if(taskType == 40){
  			//索取下架任务(27 下架索取，28 下架确认，29 上架索取)
  			if(status == 32){
  			 	//更新被动任务拣货单状态
  	      		StringBuffer sql = new StringBuffer("UPDATE xyy_wms_bill_dabaorenwu SET status = 27,czrmc = ?,xiajiayuan = ?,caozuoren = ?,startDate = ? WHERE taskCode = ?");
  	      		Db.update(sql.toString(),czrmc,czrmc,userId,startDate,taskCode);
  	      		map.put("msg", "任务索取成功");
  			}
  			//下架完成，索取上架任务
  			if(status == 28){
  			 	//更新被动任务状态
  	      		StringBuffer sql = new StringBuffer("UPDATE xyy_wms_bill_dabaorenwu SET status = 29,czrmc = ?,shangjiayuan = ? ,caozuoren = ? WHERE taskCode = ?");
  	      		Db.update(sql.toString(),czrmc,czrmc,userId,taskCode);
  	      		map.put("msg", "任务索取成功");
  			}
  		}
  		this.setAttr("status", 1);
      	this.setAttr("result", map);
      	this.renderJson();
	}

	/**
	 * 检验容器号
	 */
	private void checkRongqihao() {
	    BillContext context = this.buildBillContext();
	    String rongqihao = context.getString("rongqihao");
	    Map<Object, Object> map = new HashMap<Object,Object>();
	    StringBuffer sb = new StringBuffer("SELECT * from xyy_wms_dic_rqzlwh r where r.rongqibianhao=?");
		//根据容器号获取基础信息中该容器的信息
		Record record = Db.findFirst(sb.toString(), rongqihao);
		boolean flag = true;
		if (record == null) {
			map.put("msg", "容器不存在，请输入正确的容器号！");
		}else{
			if(record.getInt("shifouqiyong") == 0 || record.getInt("shifousuoding") == 1){
				map.put("msg", "此容器已被占用！");
			}else{
				map.put("msg", null);
			}
		}
	    this.setAttr("status", 1);
	    this.setAttr("result",map);
	    this.renderJson();
	}

    /**
     * 功能:主动补货
     * 通过 目的库区零散库区) 查询需要补货的 货位
     */
    private void getQuerySpphhwKuCun() {
        BillContext context = this.buildBillContext();

        // 主动补货 零散库区 不满足补货条件的商品集合
        Set<String> filterGoodsIdSet = new HashSet<>();

        // 仓库编号
        String cangKuId = context.getString("cangKuId");

        // 零散库集合
        List<Record> muDiHuoWeiKuCunList = queryLingSan(cangKuId);
        if(muDiHuoWeiKuCunList == null || muDiHuoWeiKuCunList.size() <= 0) return;
        // 通过上下限 过滤后需要补货的商品
        Map<String, List<Record>> productsMap = queryLingSanCompareUpperLowerLimit(queryLingSanSumProduct(muDiHuoWeiKuCunList), filterGoodsIdSet);
        if(productsMap == null || productsMap.size() <= 0) return;
        // 整件库集合
        List<Record> zjkqhwkcList = queryZhengJian(cangKuId);
        // 汇总商品批号级别 总库存数
        Map<String, List<Record>> zjkShangPinPiHaoLevelMap = queryZhengJianSumProductPiHaoLevel(zjkqhwkcList, filterGoodsIdSet);
        if(zjkShangPinPiHaoLevelMap == null || zjkShangPinPiHaoLevelMap.size() <= 0) return;
        // 设置 可补数量 属性
        queryZhengJianJiSuanKeBu(productsMap, zjkShangPinPiHaoLevelMap);
        // 计算 数量、整件数 属性。
        List<Record> result = zhengJianJiSuanShuLiang(productsMap, zjkShangPinPiHaoLevelMap, cangKuId);

        this.setAttr("status", 1);
        this.setAttr("result", result);
        this.renderJson();
        return;
    }

    /**
     * 功能:主动补货
     * 零散库区  计算哪些货位需要补货
     * @param cangKuId
     * @return
     */
    private List<Record> queryLingSan(String cangKuId) {
        // 目的货位库存集合
        String muDiHuoWeiKuCunSql ="SELECT DISTINCT " +
                " spzl.goodsid, " +
                " kc.kucunshuliang, " +
                " hw2.tiji, " +
                " xd.kucunxiaxian, " +
                " xd.kucunshangxian, " +
                " ljqh.ljqbh, " +
                " kc.pihaoId, " +
                " kc.huoweiId, " +
                " hw2.huoweibianhao, " +
                " hw2.id, " +
                " hw2.kuqubianhao AS shangjiakuqu, " +
                " spzl.dbzsl, " +
                " spzl.dbztj, " +
                " spzl.shangpinmingcheng, " +
                " spzl.guige, " +
                " spzl.danwei, " +
                " spzl.shangpinbianhao, " +
                " spzl.shengchanchangjia, " +
                " spzl.pizhunwenhao, " +
                " spzl.rowID, " +
                " '小药药' AS huozhu, " +
                " spph.youxiaoqizhi, " +
                " spph.pihao, " +
                " spph.shengchanriqi" +
                " FROM " +

                " ( " +
                "         SELECT " +
                " kucun.goodsid, " +
                " kucun.pihaoId, " +
                " kucun.huoweiId, " +
                " sum(kucun.kucunshuliang) AS kucunshuliang " +
                " FROM " +
                " ( " +
                "         SELECT " +
                "       k.shangpinId AS goodsid, " +
                "       k.pihaoId, " +
                "       k.huoweiId, " +
                "       k.kucunshuliang " +
                "       FROM " +
                "       xyy_wms_bill_shangpinpihaohuoweikucun k " +
                "       UNION ALL " +
                "       SELECT " +
                "       yy.goodsid, " +
                "       yy.pihaoId, " +
                "       yy.huoweiId, " +
                "       sum(yy.shuliang) AS kucunshuliang " +
                "       FROM " +
                "       xyy_wms_bill_kucunyuzhanyukou yy " +
                "       WHERE " +
                "       yy.zuoyeleixing IN (1, 3, 5, 8) " +
                "       GROUP BY " +
                "       yy.goodsid, " +
                "       yy.pihaoId, " +
                "       yy.huoweiId " +
                "       ORDER BY " +
                "       goodsid " +
                " ) kucun " +
                " GROUP BY " +
                " kucun.goodsid, " +
                " kucun.pihaoId, " +
                " kucun.huoweiId " +
                " ) kc " +
                " INNER JOIN ( " +
                " SELECT " +
                " kq.kuqubianhao, " +
                " hw.id, " +
                " hw.tiji, " +
                " hw.huoweibianhao " +
                " FROM " +
                " xyy_wms_dic_huoweiziliaoweihu hw " +
                " INNER JOIN xyy_wms_dic_kuqujibenxinxi kq ON hw.kuquid = kq.id " +
                " AND hw.huoweiqiyong = 1 " +
                " AND kq.qiyong = 1 " +
                " WHERE " +
                " kq.kqlbbh = 2 " +
                " and kq.cangkuID = ? " +
                " ) hw2 ON kc.huoweiId = hw2.id " +
                " INNER JOIN xyy_wms_dic_shangpinziliao spzl ON spzl.goodsid = kc.goodsid " +
                " INNER JOIN xyy_wms_dic_shangpinpihao spph ON spph.pihaoId = kc.pihaoId " +
                " LEFT JOIN xyy_wms_dic_ljqhwgxwh_list ljqb ON ljqb.huoweibianhao = hw2.huoweibianhao " +
                " LEFT JOIN xyy_wms_dic_ljqhwgxwh ljqh ON ljqb.ID = ljqh.ID " +
                " AND ljqh.shifouqiyong = 1 " +
                "LEFT JOIN ( " +
                " SELECT DISTINCT " +
                " ccxds.lluojiquyu, " +
                " ccxd.shangpinbianhao, " +
                " ccxds.kucunxiaxian, " +
                " ccxds.kucunshangxian " +
                " FROM " +
                " xyy_wms_dic_spccxdwh ccxd " +
                " INNER JOIN xyy_wms_dic_spccxdwh_list ccxds ON ccxd.ID = ccxds.ID " +
                " WHERE " +
                " ccxds.kuquleibie = 0 " +
                " ) xd ON xd.lluojiquyu = ljqh.ljqbh " +
                " AND xd.shangpinbianhao = spzl.shangpinbianhao " +
                " WHERE " +
                "xd.kucunshangxian > 0 " +
                "AND xd.kucunxiaxian > 0 ORDER BY goodsid, spph.youxiaoqizhi, kc.kucunshuliang asc ";
        List<Record> muDiHuoWeiKuCunList = Db.find(muDiHuoWeiKuCunSql, cangKuId);
        return muDiHuoWeiKuCunList;
    }

    /**
     * 功能:主动补货
     * 零散库区  商品库存汇总
     * @param muDiHuoWeiKuCunList
     * @return
     */
    private Map<String, List<Record>> queryLingSanSumProduct(List<Record> muDiHuoWeiKuCunList) {
        // 产品集
        Map<String, List<Record>> productsMap = new HashMap<String, List<Record>>();
        /**
         *  遍历目的库区每个货位，汇总 商品 级别的库存数量。
         */
        // 汇总 商品 级别的库存数量
        for (Record spphhwkc : muDiHuoWeiKuCunList) {
            if(spphhwkc==null) continue;

            // 商品goodsid
            String goodsid = spphhwkc.get("goodsid") == null ? "" : spphhwkc.getStr("goodsid");
            // 商品pihaoId
            String pihaoId = spphhwkc.get("pihaoId") == null ? "" : spphhwkc.getStr("pihaoId");
            // 库存数量
            BigDecimal kucunshuliang = spphhwkc.get("kucunshuliang") == null ? BigDecimal.ZERO : spphhwkc.getBigDecimal("kucunshuliang");
            // 数据校验
            if(StringUtil.isEmpty(goodsid) || StringUtil.isEmpty(pihaoId)) continue;

            /*
             * 计算每个商品在零散库区中 库存 的和
             * 结果集中如果没有当前商品，直接添加
             * 结果集中如果有当前商品，取出结果，累加 库存数量
             */
            List<Record> eachProductList;
            if(productsMap.get(goodsid) == null) {
                eachProductList = new ArrayList<>();
                spphhwkc.set("zongKuCun", kucunshuliang);
                eachProductList.add(spphhwkc);
                productsMap.put(goodsid, eachProductList);
            } else {
                eachProductList = productsMap.get(goodsid);
                for(int i=0; i<eachProductList.size(); i++) {
                    Record eachProduct = eachProductList.get(i);
                    eachProduct.set("zongKuCun", eachProduct.getBigDecimal("zongKuCun").add(kucunshuliang));

                    // 设置当前产品的总库存
                    if(i == 0) spphhwkc.set("zongKuCun", eachProduct.getBigDecimal("zongKuCun"));
                }
                eachProductList.add(spphhwkc);
                productsMap.put(goodsid, eachProductList);
            }
        }
        return productsMap;
    }

    /**
     * 功能:主动补货
     * 零散库区  判断上下限
     * @param productsMap
     * @param filterGoodsIdSet
     * @return
     */
    private Map<String, List<Record>> queryLingSanCompareUpperLowerLimit(Map<String, List<Record>> productsMap, Set<String> filterGoodsIdSet) {
        /**
         *  比较 商品存储限定 的上下线。
         *  1.小于下限值 && 和上限值的差值向上取整 >= 0件 才满足补货条件
         */
        // 比较 商品存储限定 的上下线
        Map<String, List<Record>> temp_productsMap = new HashMap<String, List<Record>>();
        temp_productsMap.putAll(productsMap);
        Set<String> productSet = temp_productsMap.keySet();
        for (String goodsId : productSet) {
            List<Record> eachProductList = (List<Record>) productsMap.get(goodsId);
            for(Record eachProduct : eachProductList) {
                // 商品大包装数量
                BigDecimal dbzsl = eachProduct.getBigDecimal("dbzsl");

                // 存储限定的 上限值(最小单位)
                BigDecimal kucunshangxian = eachProduct.get("kucunshangxian") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("kucunshangxian");
                // 下限值
                BigDecimal kucunxiaxian = eachProduct.get("kucunxiaxian") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("kucunxiaxian");
                // 库存数
                BigDecimal zongKuCun = eachProduct.get("zongKuCun") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("zongKuCun");

                // 应补货数量
                BigDecimal ybhsl = kucunshangxian.subtract(zongKuCun);
                // 应补货件数
                BigDecimal ybhjs = ybhsl.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP);
                /*
                 * 小于下限值 && 和上限值的差值(应补货件数)向上取整 >= 0件 才满足补货条件
                 */
                if (zongKuCun.compareTo(kucunxiaxian) == -1 && ybhjs.compareTo(BigDecimal.ZERO) == 1) {
                    // 设置 应补 对应的值
                    eachProduct.set("ybhsl", ybhsl);
                    eachProduct.set("ybhjs", ybhjs);
                    // 计算用的临时变量
                    eachProduct.set("temp_ybhsl", ybhsl);
                    eachProduct.set("temp_ybhjs", ybhjs);
                } else {
                    productsMap.remove(goodsId);
                    filterGoodsIdSet.add(goodsId);
                }
            }
        }
        return productsMap;
    }

    /**
     * 功能:主动补货
     * 整件库区  查询整件库所有库存
     * @param cangKuId
     * @return
     */
    private List<Record> queryZhengJian(String cangKuId) {
        /**
         * 整件库 开始表演
         */
        // 下架区(整件库区所有货位的库存)
        String zhengJianKuQuSql = " SELECT DISTINCT " +
                "                           spzl.goodsid, " +
                "                           kc.kucunshuliang, " +
                "                           kc.pihaoId, " +
                "                           kc.huoweiId, " +
                "                           hw2.huoweibianhao, " +
                "                           hw2.id, " +
                "                           hw2.kuqubianhao, " +
                "                   spzl.dbzsl, " +
                "                           spzl.dbztj, " +
                "                           spzl.shangpinmingcheng, " +
                "                           spzl.guige, " +
                "                           spzl.danwei, " +
                "                           spzl.shangpinbianhao, " +
                "                           spzl.shengchanchangjia, " +
                "                           spzl.pizhunwenhao, " +
                "                           spzl.rowID, " +
                "                           '小药药' AS huozhu, " +
                "                   spph.youxiaoqizhi, " +
                "                           spph.pihao, " +
                "                           spph.shengchanriqi " +
                "                   FROM " +
                "                               ( " +
                "                                    SELECT " +
                "                   kucun.goodsid, " +
                "                           kucun.pihaoId, " +
                "                           kucun.huoweiId, " +
                "                           sum(kucun.kucunshuliang) AS kucunshuliang " +
                "                   FROM " +
                "                           ( " +
                "                                   SELECT " +
                "                                        kc.shangpinId AS goodsid, " +
                "                                               kc.pihaoId, " +
                "                                               kc.huoweiId, " +
                "                                               CASE " +
                "                                       WHEN ISNULL(yyy.shuliang) THEN " +
                "                                       kc.kucunshuliang " +
                "                                               ELSE " +
                "                                       ( " +
                "                                               kc.kucunshuliang - yyy.shuliang " +
                "                                       ) " +
                "                                       END AS kucunshuliang " +
                "                                               FROM " +
                "                                       xyy_wms_bill_shangpinpihaohuoweikucun kc " +
                "                                       LEFT JOIN ( " +
                "                                               SELECT " +
                "                                       yy.goodsid, " +
                "                                               yy.pihaoId, " +
                "                                               yy.huoweiId, " +
                "                                               sum(yy.shuliang) AS shuliang " +
                "                                       FROM " +
                "                                       xyy_wms_bill_kucunyuzhanyukou yy " +
                "                                       WHERE " +
                "                                       yy.zuoyeleixing IN (2, 4, 6, 7) " +
                "                                       GROUP BY " +
                "                                       yy.goodsid, " +
                "                                               yy.pihaoId, " +
                "                                               yy.huoweiId " +
                "                                               ) yyy ON kc.shangpinId = yyy.goodsid " +
                "                                       AND kc.pihaoId = yyy.pihaoId " +
                "                                       AND kc.huoweiId = yyy.huoweiId " +
                "                                       ORDER BY " +
                "                                       goodsid " +
                "                           ) kucun " +
                "                           WHERE " +
                "                   kucun.kucunshuliang > 0 " +
                "                   GROUP BY " +
                "                   kucun.goodsid, " +
                "                           kucun.pihaoId, " +
                "                           kucun.huoweiId " +
                "               ) kc " +
                "                   INNER JOIN ( " +
                "                           SELECT " +
                "                   kq.kuqubianhao, " +
                "                           hw.id, " +
                "                           hw.tiji, " +
                "                           hw.huoweibianhao " +
                "                   FROM " +
                "                   xyy_wms_dic_huoweiziliaoweihu hw " +
                "                   INNER JOIN xyy_wms_dic_kuqujibenxinxi kq ON hw.kuquid = kq.id " +
                "                   AND hw.huoweiqiyong = 1 " +
                "                   AND kq.qiyong = 1 " +
                "                   WHERE " +
                "                   kq.kqlbbh = 1 " +
                "                   and kq.cangkuID = ? " +
                "           ) hw2 ON kc.huoweiId = hw2.id " +
                "                   INNER JOIN xyy_wms_dic_shangpinziliao spzl ON spzl.goodsid = kc.goodsid " +
                "                   INNER JOIN xyy_wms_dic_shangpinpihao spph ON spph.pihaoId = kc.pihaoId " +
                "                   ORDER BY " +
                "                   goodsid, " +
                "                   youxiaoqizhi, " +
                "                           pihaoId, " +
                "                           kc.kucunshuliang DESC ";
        List<Record> zjkqhwkcList = Db.find(zhengJianKuQuSql, cangKuId);
        return zjkqhwkcList;
    }

    /**
     * 功能:主动补货
     * 整件库区  商品批号级别汇总
     * @param zjkqhwkcList
     * @param filterGoodsIdSet
     * @return
     */
    private Map<String, List<Record>> queryZhengJianSumProductPiHaoLevel(List<Record> zjkqhwkcList, Set<String> filterGoodsIdSet) {
        /**
         * 整件库 对应的库存需要对 批号级别 进行汇总，然后通过大包装数量计算出 可补。
         */
        // 整件库商品(批号级别)map 汇总库存数量用
        Map<String, List<Record>> zjkShangPinPiHaoLevelMap = new HashMap();
        for (Record zjkqhwkc : zjkqhwkcList) {
            if(zjkqhwkc==null) continue;

            // 商品id
            String goodsid = zjkqhwkc.get("goodsid") == null ? "" : zjkqhwkc.getStr("goodsid");
            // 库存表批号id
            String pihaoId = zjkqhwkc.get("pihaoId") == null ? "" : zjkqhwkc.getStr("pihaoId");
            // 库存数量
            BigDecimal kucunshuliang = zjkqhwkc.get("kucunshuliang") == null ? new BigDecimal(0) : zjkqhwkc.getBigDecimal("kucunshuliang");

            // 零散库区 不满足补货条件的商品 跳过。
            if(filterGoodsIdSet.contains(goodsid)) continue;

            /*
             * 计算每个 商品(批号级别) 在 整件库 中 库存 的和
             * 结果集中如果没有当前 商品(批号级别)，直接添加
             * 结果集中如果有当前 商品(批号级别)，取出结果，累加 库存数量
             */
            List<Record> eachProductPiHaoList;
            if (zjkShangPinPiHaoLevelMap.get(goodsid+"--"+pihaoId) == null) {
                eachProductPiHaoList = new ArrayList<>();
                zjkqhwkc.set("zongKuCun", kucunshuliang);
                eachProductPiHaoList.add(zjkqhwkc);
                zjkShangPinPiHaoLevelMap.put(goodsid+"--"+pihaoId, eachProductPiHaoList);
            } else {
                eachProductPiHaoList = zjkShangPinPiHaoLevelMap.get(goodsid+"--"+pihaoId);
                for(int i=0; i<eachProductPiHaoList.size(); i++) {
                    Record eachProductPiHao = eachProductPiHaoList.get(i);
                    eachProductPiHao.set("zongKuCun", eachProductPiHao.getBigDecimal("zongKuCun").add(kucunshuliang));
                    // 设置当前 商品批号 总库存
                    if(i ==0) zjkqhwkc.set("zongKuCun", eachProductPiHao.getBigDecimal("zongKuCun"));
                }
                eachProductPiHaoList.add(zjkqhwkc);
                zjkShangPinPiHaoLevelMap.put(goodsid+"--"+pihaoId, eachProductPiHaoList);
            }
        }
        return zjkShangPinPiHaoLevelMap;
    }

    /**
     * 功能:主动补货
     * 整件库区  计算 可补
     * @param productsMap
     * @param zjkShangPinPiHaoLevelMap
     * @return
     */
    private void queryZhengJianJiSuanKeBu(Map<String, List<Record>> productsMap, Map<String, List<Record>> zjkShangPinPiHaoLevelMap) {
        // 计算 可补 数量。 可补和应补数量有区别， 可补数量需要定位到 批号 级别， 应补数量只需要定位到 商品 级别。

        Set<String> productSet = productsMap.keySet();
        for (String goodsId : productSet) {
            List<Record> eachProductList = productsMap.get(goodsId);
            // 遍历每一个 零散库 商品
            for(Record eachProduct : eachProductList) {
                if(eachProduct==null) continue;
                // 商品goodsid
                String goodsid = eachProduct.get("goodsid") == null ? "" : eachProduct.getStr("goodsid");
                // 商品pihaoId
                String pihaoId = eachProduct.get("pihaoId") == null ? "" : eachProduct.getStr("pihaoId");
                // 商品大包装数量
                BigDecimal dbzsl = eachProduct.getBigDecimal("dbzsl");
                // 数据校验
                if(StringUtil.isEmpty(goodsid) || StringUtil.isEmpty(pihaoId)) continue;

                // 判断当前 商品、批号 在整件库内是否有库存
                List<Record> eachProductPiHaoList = zjkShangPinPiHaoLevelMap.get(goodsid+"--"+pihaoId);
                if(eachProductPiHaoList != null && eachProductPiHaoList.size() > 0) {
                    Record eachProductPiHao = zjkShangPinPiHaoLevelMap.get(goodsid+"--"+pihaoId).get(0);
                    // 整件库 商品批号级别 库存数量汇总
                    BigDecimal zongKuCun = eachProductPiHao.get("zongKuCun") == null ? new BigDecimal(0) : eachProductPiHao.getBigDecimal("zongKuCun");
                    /*
                     * 计算 可补 数量
                     */
                    BigDecimal kbhjs = zongKuCun.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP);
                    // 设置 应补 对应的值
                    eachProduct.set("kbhjs", kbhjs);
                    eachProduct.set("kbhsl", zongKuCun);

                    // 设置每一个 整件库 商品 应补货数据、应补货数据
                    for (Record _eachProductPiHao : eachProductPiHaoList) {
                        _eachProductPiHao.set("kbhjs", kbhjs);
                        _eachProductPiHao.set("kbhsl", zongKuCun);
                        // 计算用的临时变量
                        _eachProductPiHao.set("temp_kbhjs", kbhjs);
                        _eachProductPiHao.set("temp_kbhsl", zongKuCun);
                    }
                }
            }
        }

        // 设置新商品，在零散库区货位中不存在的
        Set<String> productPiHaoSet = zjkShangPinPiHaoLevelMap.keySet();
        for (String goodsPiHaoId : productPiHaoSet) {
            List<Record> eachProductPiHaoList = zjkShangPinPiHaoLevelMap.get(goodsPiHaoId);
            // 遍历每一个 零散库 商品
            for(Record eachProductPiHao : eachProductPiHaoList) {
                if(eachProductPiHao==null) continue;
                // 商品goodsid
                String goodsid = eachProductPiHao.get("goodsid") == null ? "" : eachProductPiHao.getStr("goodsid");
                // 商品pihaoId
                String pihaoId = eachProductPiHao.get("pihaoId") == null ? "" : eachProductPiHao.getStr("pihaoId");
                // 商品大包装数量
                BigDecimal dbzsl = eachProductPiHao.getBigDecimal("dbzsl");
                // 数据校验
                if(StringUtil.isEmpty(goodsid) || StringUtil.isEmpty(pihaoId)) continue;

                if(eachProductPiHao.get("kbhjs") == null) {
                    // 整件库 商品批号级别 库存数量汇总
                    BigDecimal zongKuCun = eachProductPiHao.get("zongKuCun") == null ? new BigDecimal(0) : eachProductPiHao.getBigDecimal("zongKuCun");
                /*
                 * 计算 可补 数量
                 */
                    BigDecimal kbhjs = zongKuCun.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP);
                    // 设置 应补 对应的值
                    eachProductPiHao.set("kbhjs", kbhjs);
                    eachProductPiHao.set("kbhsl", zongKuCun);
                    // 计算用的临时变量
                    eachProductPiHao.set("temp_kbhjs", kbhjs);
                    eachProductPiHao.set("temp_kbhsl", zongKuCun);
                }
            }
        }
    }

    /**
     * 功能:主动补货
     * 整件库区  计算 数量 整件数
     * @param productsMap
     * @param zjkShangPinPiHaoLevelMap
     * @param cangKuId
     * @return
     */
    private List zhengJianJiSuanShuLiang(Map<String, List<Record>> productsMap, Map<String, List<Record>> zjkShangPinPiHaoLevelMap, String cangKuId) {
        List result = new ArrayList<>();
        /**
         * 计算 实际数量、件数
         * 通过零散货位 剩余面积(货位面积 - 目前已占面积)判断是否够放下 大包装 面积，够放则进行展示，不够放则找其他零散货位(通过面积大小排序)。 最后计算出 数量、整件数(实际补了多少)
         */
        // 遍历所有 上架货位库存
        Set<String> productSet = productsMap.keySet();
        for (String goodsId : productSet) {
            List<Record> eachProductList = productsMap.get(goodsId);
            // 遍历每一个 零散库 商品
            for(int i=0; i<eachProductList.size(); i++) {
                Record eachProduct = eachProductList.get(i);
                // 已完成的 商品 上架货位 跳过
                if(eachProduct.get("success") != null && eachProduct.getBoolean("success")) continue;
                // 商品goodsid
                String goodsid = eachProduct.get("goodsid") == null ? "" : eachProduct.getStr("goodsid");
                // 商品pihaoId
                String pihaoId = eachProduct.get("pihaoId") == null ? "" : eachProduct.getStr("pihaoId");
                // 大包装体积
                BigDecimal dbztj = eachProduct.get("dbztj") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("dbztj");
                // 大包装数量
                BigDecimal dbzsl = eachProduct.get("dbzsl") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("dbzsl");
                // 货位体积
                BigDecimal tiji = eachProduct.get("tiji") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("tiji");
                // 逻辑区编号
                String ljqbh = eachProduct.get("ljqbh") == null ? "" : eachProduct.getStr("ljqbh");
                // 库存数量
                BigDecimal kucunshuliang = eachProduct.get("kucunshuliang") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("kucunshuliang");
                // 应补货临时变量
                BigDecimal temp_ybhsl = eachProduct.get("temp_ybhsl") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("temp_ybhsl");
                BigDecimal temp_ybhjs = eachProduct.get("temp_ybhjs") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("temp_ybhjs");
                // 目前货位已占用体积
                BigDecimal huoWeiZhanYong = dbztj.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP).multiply(kucunshuliang);
                // 目前货位剩余体积
                BigDecimal huoWeiShengYu = tiji.subtract(huoWeiZhanYong);

                // 对当前商品已经补货完成
                if(temp_ybhjs.compareTo(BigDecimal.ZERO) <= 0) continue;;

                // 判断当前 商品对应批号 是否有整件
                List<Record> eachProductPiHaoList = zjkShangPinPiHaoLevelMap.get(goodsid+"--"+pihaoId);
                if(eachProductPiHaoList != null && eachProductPiHaoList.size() > 0) {
                    for(int j=0; j<eachProductPiHaoList.size(); j++) {

                        Record eachProductPiHao = eachProductPiHaoList.get(j);
                        // 可补货件数 小于等于0时跳过。
                        if(eachProductPiHao.getBigDecimal("kbhjs").compareTo(BigDecimal.ZERO) <= 0) continue;
                        if(eachProductPiHao.getBigDecimal("temp_kbhjs").compareTo(BigDecimal.ZERO) <= 0) continue;
                        /**
                         *  有整件 开始 上架判断
                         *  1.当前下架货位 可补货 >= 应补货(上架货位) 并且 上架货位够放，直接上架. 然后结束当前 商品 循环
                         *  2.当前下架货位 可补货 >= 应补货(上架货位) 并且 上架货位不够放，上架货位能放多少放多少；
                         *      判断下一个 上架货位 是不是对应的 商品、批号。
                         *          如果是，则循环下一个货位...依次递归 第2步
                         *          如果不是 后面剩余找新货位. 然后结束当前 商品 循环
                         *  3.当前下架货位 可补货 < 应补货(上架货位) 并且 上架货位够放，直接上架，然后结束当前 商品批号 循环
                         *  4.当前下架货位 可补货 < 应补货(上架货位) 并且 上架货位不够放，上架货位能放多少放多少；
                         *      判断下一个 上架货位 是不是对应的 商品、批号。
                         *          如果是，则循环下一个货位...依次递归 第4步
                         *          如果不是 后面剩余找新货位. 然后结束当前 商品批号 循环
                         */
                        // 下架货位 可补货
                        BigDecimal temp_kbhsl_xiajia = eachProductPiHao.get("temp_kbhsl") == null ? BigDecimal.ZERO : eachProductPiHao.getBigDecimal("temp_kbhsl");
                        // 当前 整件货位 库存数量
                        BigDecimal kucunshuliang_xiajia = eachProductPiHao.get("kucunshuliang") == null ? BigDecimal.ZERO : eachProductPiHao.getBigDecimal("kucunshuliang");
                        // 当前 整件货位 可补货件数
                        BigDecimal temp_kbhjs_xiajia = kucunshuliang_xiajia.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP);

                        // 下架货位 够补
                        if(temp_kbhjs_xiajia.compareTo(temp_ybhjs) >= 0) {
                            /*
                             *   判断上架货位是否够放
                             */
                            // 1.下架货位够补 上架货位够放
                            if(huoWeiShengYu.compareTo(temp_ybhjs.multiply(dbztj)) >= 0) {
                                eachProduct = shangJiaSetField(eachProduct, eachProductPiHao.getStr("huoweibianhao"), eachProductPiHao.getStr("kuqubianhao"),
                                        temp_ybhjs, temp_ybhjs.multiply(dbzsl), eachProductList, eachProductPiHaoList);
                                // 添加 结果 到结果集eachProductList
                                result.add(eachProduct.toJson());

                                //设置 商品 的所有 下架货位 为补货完成。
                                setProductSuccessByXiaJia(eachProductList);
                                break;
                            } else {
                                // 2.下架货位够补 上架货位不够放
                                List<Record> sjhwObjs = jiSuanShuLiangByNotCanPut(eachProductList, i, eachProductPiHao, eachProductPiHaoList);
                                if(sjhwObjs != null) {
                                    for(Record sjhwObj : sjhwObjs) {
                                        result.add(sjhwObj.toJson());
                                    }
                                    //设置 商品 的所有 下架货位 为补货完成。
                                    setProductSuccessByXiaJia(eachProductList);
                                    break;
                                }
                            }
                        } else {
                            // 3.下架货位不够补 上架货位够放
                            if(huoWeiShengYu.compareTo(temp_kbhjs_xiajia.multiply(dbztj)) >= 0) {
                                eachProduct = shangJiaSetField(eachProduct, eachProductPiHao.getStr("huoweibianhao"), eachProductPiHao.getStr("kuqubianhao"),
                                        temp_kbhjs_xiajia, temp_kbhjs_xiajia.multiply(dbzsl), eachProductList, eachProductPiHaoList);
                                // 添加 结果 到结果集eachProductList
                                result.add(eachProduct.toJson());

                                // 因为 下架货位 不够补，补了多少，应补货数量 就减多少。然后跳过当前循环，找下一个 下架货位
                                temp_ybhjs = temp_ybhjs.subtract(temp_kbhjs_xiajia);
                                continue;
                            } else {
                                // 4.下架货位不够补 上架货位不够放
                                List<Record> sjhwObjs = jiSuanShuLiangByNotCanPut(eachProductList, i, eachProductPiHao, eachProductPiHaoList);
                                if(sjhwObjs != null) {
                                    for(Record sjhwObj : sjhwObjs) {
                                        result.add(sjhwObj.toJson());
                                    }
                                    // 因为 下架货位 不够补，补了多少，应补货数量 就减多少。然后跳过当前循环，找下一个 下架货位
                                    temp_ybhjs = temp_ybhjs.subtract(temp_kbhjs_xiajia);
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * 未完成的商品可能在零散库中没有货位记录，这种新商品(仓库中新加入的商品)需要通过存储条件找新货位
         */
        setZhengJianKuIsSuccessProduct(productsMap, zjkShangPinPiHaoLevelMap);
        List<Record> sjhwObjs = createBuHuoByNewProduct(productsMap, zjkShangPinPiHaoLevelMap, cangKuId);
        if(sjhwObjs != null) {
            for(Record sjhwObj : sjhwObjs) {
                result.add(sjhwObj.toJson());
            }
        }
        return result;
    }

    /**
     * 通过新商品，创建补货任务
     *     仓库新进的品种，在零散库没有对应货位记录的
     * @param productsMap
     * @param zjkShangPinPiHaoLevelMap
     * @param cangKuId
     */
    private List<Record> createBuHuoByNewProduct(Map<String, List<Record>> productsMap, Map<String, List<Record>> zjkShangPinPiHaoLevelMap, String cangKuId) {
        // 结果集
        List<Record> results = new ArrayList<>();
        // 整件库剩余未补货的商品
        Map<String, List<Record>> zjkShengYuList = new HashMap<>();

        // 存放所有商品id
        String goodsids = "";
        // 所有key 商品id+批号id
        Set<String> productPiHaoSet = zjkShangPinPiHaoLevelMap.keySet();
        for (String shangPinPiHaoId : productPiHaoSet) {
            List<Record> eachProductPiHaoList = zjkShangPinPiHaoLevelMap.get(shangPinPiHaoId);
            for(Record eachProductPiHao : eachProductPiHaoList) {
                // 整件库 已补货完成的 商品
                if(eachProductPiHao.get("success") != null && eachProductPiHao.getBoolean("success")) continue;
                String goodsid = eachProductPiHao.get("goodsid") == null ? "" : eachProductPiHao.getStr("goodsid");
                goodsids += "'" + goodsid + "',";
                if(zjkShengYuList.get(goodsid) != null) {
                    zjkShengYuList.get(goodsid).add(eachProductPiHao);
                } else {
                    List<Record> list = new ArrayList<>();
                    list.add(eachProductPiHao);
                    zjkShengYuList.put(goodsid, list);
                }
            }
        }

        // 查询商品对应逻辑区存储上下线
        if(!StringUtil.isEmpty(goodsids) && zjkShengYuList.size() > 0) {
            if(goodsids.endsWith(",")) goodsids = goodsids.substring(0, goodsids.length() - 1);
            String sql = " SELECT " +
                    " pr.goodsid, " +
                    " xds.lluojiquyu, " +
                    " xds.kucunxiaxian, " +
                    " xds.kucunshangxian " +
                    " FROM " +
                    " xyy_wms_dic_spccxdwh xd " +
                    " INNER JOIN xyy_wms_dic_spccxdwh_list xds ON xd.ID = xds.id " +
                    " INNER JOIN xyy_wms_dic_cangkuziliao ck ON xd.cangkubianhao = ck.cangkubianhao " +
                    " INNER JOIN xyy_wms_dic_shangpinziliao pr ON pr.shangpinbianhao = xd.shangpinbianhao " +
                    " WHERE " +
                    " xds.kuquleibie = 0 " +
                    " AND ck.ID = ? " +
                    " AND xds.lluojiquyu != '' " +
                    " AND pr.goodsid IN ( " +
                        goodsids +
                    " ) ";
            List<Record> shangXiaXianList = Db.find(sql, cangKuId);
            // 赋值上下限给 整件库 对象
            if(shangXiaXianList != null && shangXiaXianList.size() > 0) {
                Set<String> goodsidSet = zjkShengYuList.keySet();
                for(Record sxx : shangXiaXianList) {
                    // 商品id
                    String goodsid = sxx.get("goodsid") == null ? "" : sxx.getStr("goodsid");
                    // 逻辑区编号
                    String lluojiquyu = sxx.get("lluojiquyu") == null ? "" : sxx.getStr("lluojiquyu");
                    // 逻辑区存储上限
                    BigDecimal kucunshangxian = sxx.get("kucunshangxian") == null ? BigDecimal.ZERO : sxx.getBigDecimal("kucunshangxian");
                    // 逻辑区存储下限
                    BigDecimal kucunxiaxian = sxx.get("kucunxiaxian") == null ? BigDecimal.ZERO : sxx.getBigDecimal("kucunxiaxian");
                    for(String _goodsid : goodsidSet) {
                        if(goodsid.equals(_goodsid)) {
                            // 设置上下限、 上架库区
                            List<Record> products = zjkShengYuList.get(_goodsid);

                            // 对集合中商品的 有效期至 进行正向排序，实现先补近效期商品
                            Collections.sort(products, new Comparator(){
                                @Override
                                public int compare(Object o1, Object o2) {
                                    Record r1=(Record)o1;
                                    Record r2=(Record)o2;
                                    if(r1.getDate("youxiaoqizhi").after(r2.getDate("youxiaoqizhi"))){
                                        return 1;
                                    }else if(r1.getDate("youxiaoqizhi").before(r2.getDate("youxiaoqizhi"))){
                                        return -1;
                                    }else{
                                        return 0;
                                    }
                                }
                            });

                            if(products != null && products.size() > 0) {
                                for(Record p : products) {
                                    p.set("ljqbh", lluojiquyu);
                                    p.set("kucunshangxian", kucunshangxian);
                                    p.set("kucunxiaxian", kucunxiaxian);

                                    /*
                                     * 应补货数量 需要通过上限计算
                                     */
                                    // 大包装数量
                                    BigDecimal dbzsl = p.get("dbzsl") == null ? BigDecimal.ZERO : p.getBigDecimal("dbzsl");
                                    // 应补货数量
                                    BigDecimal ybhsl = kucunshangxian;

                                    // 当零散库中商品对应批号在整件库中没有可补时，可是整件库又有其他批号，此时当前商品 应补货数量 需要比较 下限值, 满足补货条件情况下 上限值 - 零散库 总库存数量
                                    List<Record> productList = productsMap.get(goodsid);
                                    if(productList != null && productList.size() > 0) {
                                        BigDecimal zongKuCun = productList.get(0).get("zongKuCun") == null ? BigDecimal.ZERO : productList.get(0).getBigDecimal("zongKuCun");
                                        if(zongKuCun.compareTo(kucunxiaxian) == -1) {
                                            ybhsl = kucunshangxian.subtract(zongKuCun);
                                        }
                                    }

                                    // 应补货件数
                                    BigDecimal ybhjs = ybhsl.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP);
                                    // 设置 应补 对应的值
                                    p.set("ybhsl", ybhsl);
                                    p.set("ybhjs", ybhjs);
                                    // 计算用的临时变量
                                    p.set("temp_ybhsl", ybhsl);
                                    p.set("temp_ybhjs", ybhjs);
                                }
                            }
                        }
                    }
                }
            }
        }
        /*
         * 仓库进的新商品，在货位中还没有任何库存，这种商品直接找新货位
         */
        if(zjkShengYuList != null && zjkShengYuList.size() > 0) {
            Set<String> goodsidSet = zjkShengYuList.keySet();
            for(String _goodsid : goodsidSet) {
                List<Record> products = zjkShengYuList.get(_goodsid);
                if(products != null && products.size() > 0) {
                    for(Record p : products) {
                        // 已补货完成的 商品
                        if(p.get("success") != null && p.getBoolean("success")) continue;
                        // 应补货 完成
                        if(p.get("temp_ybhjs") == null || p.getBigDecimal("temp_ybhjs").compareTo(BigDecimal.ZERO) == 0) continue;
                        // 可补货 完成
                        if(p.get("temp_kbhjs") == null || p.getBigDecimal("temp_kbhjs").compareTo(BigDecimal.ZERO) == 0) continue;


                        // 大包装数量
                        BigDecimal dbzsl = p.get("dbzsl") == null ? BigDecimal.ZERO : p.getBigDecimal("dbzsl");
                        // 当前 整件货位 库存数量
                        BigDecimal kucunshuliang_xiajia = p.get("kucunshuliang") == null ? BigDecimal.ZERO : p.getBigDecimal("kucunshuliang");
                        // 当前 整件货位 可补货件数
                        BigDecimal temp_kbhjs_xiajia = kucunshuliang_xiajia.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP);
                        // 可补 和 应补 取最小值
                        BigDecimal min = p.getBigDecimal("temp_ybhjs").min(temp_kbhjs_xiajia);

                        Record sjhwObj = findNewHuoWei(p, products, p, products, min);

                        if(sjhwObj != null) {
                            if(temp_kbhjs_xiajia.subtract(min).compareTo(BigDecimal.ZERO) > 0 ) {
                                // 当前 下架货位 剩余库存数量
                                p.set("kucunshuliang", p.getBigDecimal("kucunshuliang").subtract(min.multiply(dbzsl)));
                            }
                            results.add(sjhwObj);
                        }
                    }
                }
            }
        }

        return results;
    }

    /**
     * 设置整件库已补货完成商品
     * 零散库商品补货指令生成的商品，在整件库中对应商品也进行设置 补货完成
     * 未完成的商品可能在零散库中没有货位记录，这种新商品(仓库中新加入的商品)需要通过存储条件找新货位
     * 1.在eachProductList中，success为ture
     * 2.在eachProductList中，temp_ybhjs <= 0
     * @param productsMap
     * @param zjkShangPinPiHaoLevelMap
     */
    private void setZhengJianKuIsSuccessProduct(Map<String, List<Record>> productsMap, Map<String, List<Record>> zjkShangPinPiHaoLevelMap) {
        // 遍历零散库集合
        Set<String> productSet = productsMap.keySet();
        for (String goodsId : productSet) {
            List<Record> eachProductList = productsMap.get(goodsId);
            for(Record eachProduct : eachProductList) {

                // 商品goodsid
                String goodsid = eachProduct.get("goodsid") == null ? "" : eachProduct.getStr("goodsid");
                // 应补货件数 临时变量
                BigDecimal temp_ybhjs = eachProduct.get("temp_ybhjs") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("temp_ybhjs");

                // 零散库 已完成的 商品 1.success为ture 2.temp_ybhjs <= 0
                if(eachProduct.get("success") != null && eachProduct.getBoolean("success") || temp_ybhjs.compareTo(BigDecimal.ZERO) <= 0) {
                    // 设置 整件库 中对应商品 success 为true
                    Set<String> productPiHaoSet = zjkShangPinPiHaoLevelMap.keySet();
                    for (String shangPinPiHaoId : productPiHaoSet) {
                        List<Record> eachProductPiHaoList = zjkShangPinPiHaoLevelMap.get(shangPinPiHaoId);
                        for(Record eachProductPiHao : eachProductPiHaoList) {
                            // 商品goodsid
                            String _goodsid = eachProductPiHao.get("goodsid") == null ? "" : eachProductPiHao.getStr("goodsid");
                            // 应补货件数 临时变量
                            BigDecimal temp_kbhjs = eachProductPiHao.get("temp_kbhjs") == null ? BigDecimal.ZERO : eachProductPiHao.getBigDecimal("temp_kbhjs");
                            if(goodsId.equals(_goodsid) || temp_kbhjs.compareTo(BigDecimal.ZERO) <= 0) {
                                eachProductPiHao.set("success", true);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置 商品 所有 下架货位 补货完成
     * @param eachProductList
     */
    private void setProductSuccessByXiaJia(List<Record> eachProductList) {
        for(Record eachProduct : eachProductList) {
            eachProduct.set("success", true);
        }
    }

    /**
     * 功能:主动补货
     * 上架货位不够放
     * 上架货位不够放，上架货位能放多少放多少；
     *      判断下一个 上架货位 是不是对应的 商品、批号。
     *          如果是，则循环下一个货位...依次递归 第2步
     *          如果不是 后面剩余找新货位. 然后结束当前 商品 循环
     * @param eachProductList
     * @param index
     * @param eachProductPiHao
     * @param eachProductPiHaoList
     */
    private List<Record> jiSuanShuLiangByNotCanPut(List<Record> eachProductList, int index, Record eachProductPiHao, List<Record> eachProductPiHaoList) {
        List<Record> results = new ArrayList<>();
        if(index <= eachProductList.size() - 1) {

            // 下架货位 商品goodsid
            String goodsid_xiajia = eachProductPiHao.get("goodsid") == null ? "" : eachProductPiHao.getStr("goodsid");
            // 下架货位 商品pihaoId
            String pihaoId_xiajia = eachProductPiHao.get("pihaoId") == null ? "" : eachProductPiHao.getStr("pihaoId");

            // 已经是最后一个货位，直接找 新货位
            if(index == eachProductList.size() - 1) {
                Record lastEachProduct = eachProductList.get(index);

                // 大包装数量
                BigDecimal dbzsl = lastEachProduct.get("dbzsl") == null ? BigDecimal.ZERO : lastEachProduct.getBigDecimal("dbzsl");

                // 当前 整件货位 库存数量
                BigDecimal kucunshuliang_xiajia = eachProductPiHao.get("kucunshuliang") == null ? BigDecimal.ZERO : eachProductPiHao.getBigDecimal("kucunshuliang");
                // 当前 整件货位 可补货件数
                BigDecimal temp_kbhjs_xiajia = kucunshuliang_xiajia.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP);

                BigDecimal min = lastEachProduct.getBigDecimal("temp_ybhjs").min(temp_kbhjs_xiajia);

                Record result = findNewHuoWei(lastEachProduct, eachProductList, eachProductPiHao, eachProductPiHaoList, min);
                if(result != null) {
                    if(temp_kbhjs_xiajia.subtract(min).compareTo(BigDecimal.ZERO) > 0 ) {
                        // 当前 下架货位 剩余库存数量
                        eachProductPiHao.set("kucunshuliang", eachProductPiHao.getBigDecimal("kucunshuliang").subtract(min.multiply(dbzsl)));
                    }
                    results.add(result);
                }
                return results;
            } else {
                Record eachProduct = eachProductList.get(index);
                // 商品goodsid
                String goodsid = eachProduct.get("goodsid") == null ? "" : eachProduct.getStr("goodsid");
                // 商品pihaoId
                String pihaoId = eachProduct.get("pihaoId") == null ? "" : eachProduct.getStr("pihaoId");
                // 大包装体积
                BigDecimal dbztj = eachProduct.get("dbztj") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("dbztj");
                // 大包装数量
                BigDecimal dbzsl = eachProduct.get("dbzsl") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("dbzsl");
                // 货位体积
                BigDecimal tiji = eachProduct.get("tiji") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("tiji");
                // 库存数量
                BigDecimal kucunshuliang = eachProduct.get("kucunshuliang") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("kucunshuliang");
                // 应补货临时变量
                BigDecimal temp_ybhsl = eachProduct.get("temp_ybhsl") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("temp_ybhsl");
                BigDecimal temp_ybhjs = eachProduct.get("temp_ybhjs") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("temp_ybhjs");
                // 目前货位已占用体积
                BigDecimal huoWeiZhanYong = dbztj.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP).multiply(kucunshuliang);
                // 目前货位剩余体积
                BigDecimal huoWeiShengYu = tiji.subtract(huoWeiZhanYong);

                // 因为 下架货位 是商品级别汇总， 有可能下一个货位对应的商品不是当前要补货的批号， 所以此处要判断批号也相等
                if(goodsid.equals(goodsid_xiajia) && pihaoId.equals(pihaoId_xiajia)) {
                    // 计算剩余货位可以放几个整件
                    BigDecimal shengYuKeFangJianShu = huoWeiShengYu.divide(dbztj,0,BigDecimal.ROUND_DOWN);

                    // 当前 整件货位 库存数量
                    BigDecimal kucunshuliang_xiajia = eachProductPiHao.get("kucunshuliang") == null ? BigDecimal.ZERO : eachProductPiHao.getBigDecimal("kucunshuliang");
                    // 当前 整件货位 可补货件数
                    BigDecimal temp_kbhjs_xiajia = kucunshuliang_xiajia.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP);

                    BigDecimal min = shengYuKeFangJianShu.min(temp_kbhjs_xiajia);

                    // 剩余体积可放件数 和 当前整件货位可补货件数 去最小值
                    if(min.compareTo(BigDecimal.ZERO) > 0) {
                        eachProduct = shangJiaSetField(eachProduct, eachProductPiHao.getStr("huoweibianhao"), eachProductPiHao.getStr("kuqubianhao"),
                                min, min.multiply(dbzsl), eachProductList, eachProductPiHaoList);
                        // 添加 结果 到结果集
                        if(eachProduct != null) results.add(eachProduct);
                        // 应补货数量 > 0 && 可补货数量 还有,表示还没补够
                        temp_ybhjs = eachProduct.get("temp_ybhjs") == null ? BigDecimal.ZERO : eachProduct.getBigDecimal("temp_ybhjs");

                        // BigDecimal temp_kbhjs = eachProductPiHao.get("temp_kbhjs") == null ? BigDecimal.ZERO : eachProductPiHao.getBigDecimal("temp_kbhjs");
                        // 应补货 > 0 ,当前 下架货位 可补 > 0
                        if(temp_ybhjs.compareTo(BigDecimal.ZERO) > 0 && temp_kbhjs_xiajia.subtract(min).compareTo(BigDecimal.ZERO) > 0 ) {
                            // 当前 下架货位 剩余库存数量
                            eachProductPiHao.set("kucunshuliang", eachProductPiHao.getBigDecimal("kucunshuliang").subtract(min.multiply(dbzsl)));
                            List<Record> _results = jiSuanShuLiangByNotCanPut(eachProductList, index + 1, eachProductPiHao, eachProductPiHaoList);
                            if(_results != null && _results.size() > 0) results.addAll(_results);
                            return results;
                        }
                    } else {
                        // 当前上架货位 剩余体积 不够放
                        List<Record> _results = jiSuanShuLiangByNotCanPut(eachProductList, index + 1, eachProductPiHao, eachProductPiHaoList);
                        if(_results != null && _results.size() > 0) results.addAll(_results);
                        return results;
                    }
                } else {
                    // 当前商品、批号在下架货位中全部遍历完，找新货位
                    // Record lastEachProduct = eachProductList.get(index);

                    // 当前 整件货位 库存数量
                    BigDecimal kucunshuliang_xiajia = eachProductPiHao.get("kucunshuliang") == null ? BigDecimal.ZERO : eachProductPiHao.getBigDecimal("kucunshuliang");
                    // 当前 整件货位 可补货件数
                    BigDecimal temp_kbhjs_xiajia = kucunshuliang_xiajia.divide(dbzsl, 0, BigDecimal.ROUND_HALF_UP);

                    BigDecimal min = eachProduct.getBigDecimal("temp_ybhjs").min(temp_kbhjs_xiajia);

                    Record result = findNewHuoWei(eachProduct, eachProductList, eachProductPiHao, eachProductPiHaoList, min);
                    if(result != null) {
                        if(temp_kbhjs_xiajia.subtract(min).compareTo(BigDecimal.ZERO) > 0 ) {
                            // 当前 下架货位 剩余库存数量
                            eachProductPiHao.set("kucunshuliang", eachProductPiHao.getBigDecimal("kucunshuliang").subtract(min.multiply(dbzsl)));
                        }
                        results.add(result);
                    }
                    return results;
                }

            }
        }
        return results;
    }

    /**
     * 功能:主动补货
     * 货位不够放，找新货位
     * @param shangJiaHuoWei
     * @param eachProductList
     * @param xiaJiaHuoWei
     * @param eachProductPiHaoList
     * @param temp_ybhjs
     * @return
     */
    private Record findNewHuoWei(Record shangJiaHuoWei, List<Record> eachProductList, Record xiaJiaHuoWei, List<Record> eachProductPiHaoList, BigDecimal temp_ybhjs) {
        if(shangJiaHuoWei != null) {
            // 逻辑区编号
            String ljqbh = shangJiaHuoWei.get("ljqbh") == null ? "" : shangJiaHuoWei.getStr("ljqbh");
            if(!StringUtil.isEmpty(ljqbh)) {
                // 商品id
                String goodsid = shangJiaHuoWei.get("goodsid") != null ? shangJiaHuoWei.getStr("goodsid") : "";
                // 库存表批号id
                String pihaoId = shangJiaHuoWei.get("pihaoId") == null ? "" : shangJiaHuoWei.getStr("pihaoId");
                // 大包装数量
                BigDecimal dbzsl = shangJiaHuoWei.get("dbzsl") == null ? BigDecimal.ZERO : shangJiaHuoWei.getBigDecimal("dbzsl");

                // 货位默认查50条(缓存)，不够时候再继续查
                List<Record> kongHuoWei = kongHuoWei_ljqbhKey.get(ljqbh);
                if(kongHuoWei == null || kongHuoWei.size() <= 0) {
                    String khwSql = " SELECT " +
                            " * " +
                            " FROM " +
                            "       ( " +
                            "               SELECT " +
                            "               CASE " +
                            "               WHEN ISNULL(yzyk.shuliang) THEN " +
                            "               0.0 " +
                            "               ELSE " +
                            "                       yzyk.shuliang " +
                            "               END AS kucunshuliang, " +
                            "               hw.huoweibianhao, " +
                            "               ljqh.ljqbh, " +
                            "               kq.kuqubianhao, " +
                            "               hw.tiji, " +
                            "               hw.ID as huoweiId" +
                            "               FROM " +
                            "                   xyy_wms_dic_huoweiziliaoweihu hw " +
                            "               INNER JOIN xyy_wms_dic_kuqujibenxinxi kq ON hw.kuquId = kq.ID " +
                            "               INNER JOIN xyy_wms_dic_ljqhwgxwh_list ljqb ON hw.huoweibianhao = ljqb.huoweibianhao " +
                            "               INNER JOIN xyy_wms_dic_ljqhwgxwh ljqh ON ljqh.ID = ljqb.ID " +
                            "               LEFT JOIN xyy_wms_bill_kucunyuzhanyukou yzyk ON hw.ID = yzyk.huoweiId " +
                            "               AND yzyk.zuoyeleixing IN (1, 3, 5, 8) " +
                            "               WHERE " +
                            "               ljqh.ljqbh = ? " +
                            "               and hw.ID NOT in (select DISTINCT kc.huoweiId from xyy_wms_bill_shangpinpihaohuoweikucun kc where kc.huoweiId != '') " +
                            "               and hw.huoweiqiyong = 1 " +
                            "               ORDER BY huoweiId " +
                            "       ) khw " +
                            " WHERE " +
                            " khw.kucunshuliang = 0.0 ORDER BY huoweiId LIMIT " + QUERY_KONGHUOWEI_COUNT;
                    kongHuoWei = Db.find(khwSql, ljqbh);
                    kongHuoWei_ljqbhKey.put(ljqbh, kongHuoWei);
                }
                Record khwObj = null;
                // 从缓存中取空余货位
                if(kongHuoWei != null && kongHuoWei.size() > 0) khwObj = kongHuoWei.remove(0);
                if(khwObj != null) {

                    // copy货位对象，防止更新内存数据
                    Record _khwObj = new Record();
                    _khwObj.setColumns(shangJiaHuoWei.getColumns());
                    _khwObj.set("huoweibianhao", khwObj.getStr("huoweibianhao"));
                    _khwObj.set("shangjiakuqu", khwObj.getStr("kuqubianhao"));
                    _khwObj.set("kbhjs", xiaJiaHuoWei.get("kbhjs"));
                    _khwObj.set("kbhsl", xiaJiaHuoWei.get("kbhsl"));

                    _khwObj = shangJiaSetField(_khwObj, xiaJiaHuoWei.getStr("huoweibianhao"),xiaJiaHuoWei.getStr("kuqubianhao"),
                            temp_ybhjs, temp_ybhjs.multiply(dbzsl), eachProductList, eachProductPiHaoList);

                    // 添加 结果 到结果集
                    return _khwObj;
                }
            }
        }

        return null;
    }

    /**
     * 功能:主动补货
     * 设置属性
     * @param shangJiaHuoWei
     * @param xiajiahuowei
     * @param kuqubianhao
     * @param zhengjianshu
     * @param shuliang
     * @param eachProductList
     * @param eachProductPiHaoList
     * @return
     */
    private Record shangJiaSetField(Record shangJiaHuoWei, String xiajiahuowei, String kuqubianhao, BigDecimal zhengjianshu, BigDecimal shuliang, List<Record> eachProductList, List<Record> eachProductPiHaoList) {
        shangJiaHuoWei.set("xiajiahuowei", xiajiahuowei);
        shangJiaHuoWei.set("xiajiakuqu", kuqubianhao);
        shangJiaHuoWei.set("zhengjianshu", zhengjianshu);
        shangJiaHuoWei.set("shuliang", shuliang);
        shangJiaHuoWei.set("shangjiahuowei", shangJiaHuoWei.get("huoweibianhao"));

        // 设置上架货位的值，累加操作，用于后续计算 货位剩余体积 用
        shangJiaHuoWei.set("kucunshuliang", (shangJiaHuoWei.get("kucunshuliang") == null ? BigDecimal.ZERO : shangJiaHuoWei.getBigDecimal("kucunshuliang").add(shuliang)));

        // 商品goodsid
        String goodsid = shangJiaHuoWei.get("goodsid") == null ? "" : shangJiaHuoWei.getStr("goodsid");
        // 商品pihaoId
        String pihaoId = shangJiaHuoWei.get("pihaoId") == null ? "" : shangJiaHuoWei.getStr("pihaoId");

        // 补货记录 生成，更新 零散库商品 应补货、 整件库商品批号 可补货 临时变量
        for(Record eachProduct : eachProductList) {
            if(goodsid.equals(eachProduct.get("goodsid"))) {
                eachProduct.set("temp_ybhsl", eachProduct.getBigDecimal("temp_ybhsl").subtract(shuliang));
                eachProduct.set("temp_ybhjs", eachProduct.getBigDecimal("temp_ybhjs").subtract(zhengjianshu));
            }
        }
        for(Record eachProductPiHao : eachProductPiHaoList) {
            if(goodsid.equals(eachProductPiHao.get("goodsid")) && pihaoId.equals(eachProductPiHao.get("pihaoId"))) {
                eachProductPiHao.set("temp_kbhsl", eachProductPiHao.getBigDecimal("temp_kbhsl").subtract(shuliang));
                eachProductPiHao.set("temp_kbhjs", eachProductPiHao.getBigDecimal("temp_kbhjs").subtract(zhengjianshu));
            }
        }
        return shangJiaHuoWei;
    }




    /**
     * 通过商品编号 批号 货位编号 查询库存数量
     */
    private void getQueryKucunByspphhw() {
        BillContext context = this.buildBillContext();
        String shangpinbianhao = context.getString("shangpinbianhao");
        String pihaoid = context.getString("pihaoId");
        String huoweibianhao = context.getString("huoweibianhao");
        String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
        Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
        String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
        Record huoweiObj = Db.findFirst(huoweiSql, huoweibianhao);

        String sql = "SELECT * FROM xyy_wms_bill_shangpinpihaohuoweikucun WHERE shangpinId=? and pihaoId=? and huoweiId=? order by createTime desc limit 1";
        String[] params = {shangpinObj.getStr("goodsid"), pihaoid, huoweiObj.getStr("ID")};
        Record kucunObj = Db.findFirst(sql, params);
        if (kucunObj == null) {
            this.setAttr("status", 0);
            this.setAttr("result", "查不到任何库存");
        } else {
            kucunObj.set("dbzsl", shangpinObj.get("dbzsl"));    //大包装数量
            BigDecimal[] results = kucunObj.getBigDecimal("kucunshuliang").divideAndRemainder((shangpinObj.getBigDecimal("dbzsl")));
            kucunObj.set("lss", results[1]);        //零散数
            kucunObj.set("zjs", results[0]);        //整件数
            this.setAttr("status", 1);
            this.setAttr("result", kucunObj);
        }
        this.renderJson();

    }

    private void getQueryCaiGou() {
        BillContext context = this.buildBillContext();
        String danjubianhao = context.getString("danjubianhao");
        String sql = "SELECT * FROM xyy_erp_bill_migratejinxiangkaipiao_details WHERE danjubianhao=?";
        List<Record> list = Db.find(sql, danjubianhao);
        this.setAttr("status", 1);
        this.setAttr("result", list);
        this.renderJson();
    }

    /**
     * 通过单据id查询详情
     */
    private void getQueryDetailByBillID() {
        BillContext context = this.buildBillContext();
        String BillID = context.getString("BillID");
        String table = context.getString("table");
        String sql = "SELECT * FROM " + table + " WHERE BillID=?";
        List<Record> list = Db.find(sql, BillID);
        this.setAttr("status", 1);
        this.setAttr("result", list);
        this.renderJson();
    }
    /**
     * 通过单据id跟状态查询主动补货单详情
     */
    private void getQueryDetailByBillID1() {
        BillContext context = this.buildBillContext();
        String BillID = context.getString("BillID");
        String table = context.getString("table");
        String sql = "SELECT * FROM " + table + " WHERE BillID=? AND zhuangtai=28";
        List<Record> list = Db.find(sql, BillID);
        this.setAttr("status", 1);
        this.setAttr("result", list);
        this.renderJson();
    }

    /**
     * 通过BillID查询补货任务详情
     */
    private void getQueryDetailByDanjubianhao() {
        BillContext context = this.buildBillContext();
        String BillID = context.getString("BillID");
       Record zbObj= Db.findFirst("select * from xyy_wms_bill_zhudongbuhuo where BillID=?",BillID);
       List<Record> list=null;
       if(zbObj!=null) {
           String sql ="SELECT g.danjubianhao AS danjubianhao,shangpinbianhao,shangpinmingcheng,guige,danwei,dbzsl,"
                   +"pizhunwenhao,shengchanchangjia,xiajiakuqu,xiajiahuowei,d.createTime AS xiajiashijian,"
                   +"g.zhidanren AS xiajiayuan,shuliang,zhengjianshu,pihao,shengchanriqi,youxiaoqizhi,"
                   +" shangjiakuqu AS mudikuqu,shangjiahuowei AS mudihuowei,g.zhidanren AS shangjiarenyuan,g.updateTime AS shangjiashijian "
                   +"FROM xyy_wms_bill_zhudongbuhuo_details d "
                   +" LEFT JOIN xyy_wms_bill_zhudongbuhuo g ON d.BillId = g.BillID "
                   +" WHERE g.BillID=?";
           list= Db.find(sql,BillID);
       }else {
           String sql ="SELECT g.taskCode AS danjubianhao,shangpinbianhao,shangpinmingcheng,guige,danwei,baozhuangshuliang AS dbzsl,"
                   +"pizhunwenhao,shengchanchangjia,g.xiajiakuqu AS xiajiakuqu,xiajiahuoweibianhao AS xiajiahuowei,g.startDate AS xiajiashijian,"
                   +"g.caozuoren AS xiajiayuan,shuliang,d.jianshu AS zhengjianshu,shangpingpihao AS pihao,shengchanriqi,youxiaoqizhi,"
                   +"g.shangjiakuqu AS mudikuqu,shangjiahuoweibianhao AS mudihuowei,d.caozuoren AS shangjiarenyuan,g.querenshijian AS shangjiashijian "
                   +" FROM xyy_wms_bill_dabaorenwu_details d"
                   +" LEFT JOIN xyy_wms_bill_dabaorenwu g ON d.BillId = g.BillID "
                   +"WHERE g.BillID=?";
           list = Db.find(sql,BillID);
       }
        this.setAttr("status", 1);
        this.setAttr("result", list);
        this.renderJson();

    }


    /**
     * 通过单据id查询采购订单明细
     */
    private void getQueryCaiGouDetailByBillID() {
        BillContext context = this.buildBillContext();
        String BillID = context.getString("BillID");
        String table = context.getString("table");
        String sql = "SELECT a.*,b.dbzsl dbzsl FROM " + table + " a,xyy_wms_dic_shangpinziliao b WHERE a.BillID=? and a.shangpinbianhao=b.shangpinbianhao AND zhuangtai!=1";
        List<Record> list = Db.find(sql, BillID);
        this.setAttr("status", 1);
        this.setAttr("result", list);
        this.renderJson();
    }
    
    /**
     * 通过单据id查询采购订单明细
     */
    private void getQueryxtDetailByBillID() {
        BillContext context = this.buildBillContext();
        String BillID = context.getString("BillID");
        String table = context.getString("table");
        String sql = "SELECT a.*,b.dbzsl dbzsl FROM " + table + " a,xyy_wms_dic_shangpinziliao b WHERE a.BillID=? and a.shangpinbianhao=b.shangpinbianhao AND zhuangtai!=1";
        List<Record> list = Db.find(sql, BillID);
        this.setAttr("status", 1);
        this.setAttr("result", list);
        this.renderJson();
    }

    /**
     * 获取所有单据明细
     */
    private void getGoodsCos() {
        BillContext context = this.buildBillContext();
        String BillID = context.getString("BillID");
        String sql = "SELECT * FROM xyy_erp_bill_xiaoxiangfapiaoguanli_details WHERE BillID=?";
        List<Record> cos = Db.find(sql, BillID);
        this.setAttr("status", 1);
        this.setAttr("result", cos == null ? "" : cos);
        this.renderJson();
    }

    /**
     * 商品编号获取商品基本信息
     */
    private void getGoodsDetails() {
        BillContext context = this.buildBillContext();
        String shangpinbianhao = context.getString("shangpinbianhao");
        String sql = "SELECT * FROM xyy_erp_dic_shangpinjibenxinxi WHERE shangpinbianhao=?";
        Record goods = Db.findFirst(sql, shangpinbianhao);
        this.setAttr("status", 1);
        this.setAttr("result", goods == null ? "" : goods);
        this.renderJson();
    }

    /**
     * 采购请货单特殊的保存方法
     */
    private void caigouSave() {
        BillContext context = this.buildBillContext();
        String orgCode = ToolWeb.getCookieValueByName(getRequest(), "orgCode");
        String orgId = ToolWeb.getCookieValueByName(getRequest(), "orgId");
        context.set("orgId", orgId);
        context.set("orgCode", orgCode);
        JSONObject data = context.getJSONObject("data");
        JSONObject head = data.getJSONObject("head");
        Map<String, Object> map = JSONObject.parseObject(head.toJSONString(), new TypeReference<Map<String, Object>>() {
        });
        Record headRecord = new Record();
        headRecord.setColumns(map);
        headRecord.set("status", 20);
        JSONArray body = data.getJSONArray("body");
        Map<String, List<Record>> group = new HashMap<>();
        for (Object object : body) {
            String caigouyuan = ((JSONObject) object).getString("caigouyuan");
            if (!group.containsKey(caigouyuan)) {
                group.put(caigouyuan, new ArrayList<>());
            }
            Map<String, Object> bodyMap = JSONObject.parseObject(((JSONObject) object).toJSONString(), new TypeReference<Map<String, Object>>() {
            });
            Record bodyRecord = new Record();
            bodyRecord.setColumns(bodyMap);
            group.get(caigouyuan).add(bodyRecord);
        }
        for (String s : group.keySet()) {
            JSONObject model = PullWMSDataUtil.buildContext(headRecord, group.get(s), "caigouqinghuodan", "caigouqinghuodan_details");
            PullWMSDataUtil.saveAction(context, model, "caigouqinghuodan");
        }
        this.setAttr("status", 1);
        this.renderJson();
    }

    /**
     * 根据商品编号查询总库存信息
     */
    private void totalStore() {
        BillContext context = this.buildBillContext();
        String shangpinbianhao = context.getString("shangpinbianhao");
        String sql = "SELECT * FROM xyy_erp_bill_shangpinzongkucun WHERE shangpinId = (SELECT s.goodsid FROM xyy_erp_dic_shangpinjibenxinxi s WHERE s.shangpinbianhao = ?)";
        Record store = Db.findFirst(sql, shangpinbianhao);
        if (store != null) {
            //最后进货单位id
            String suppliersId = store.getStr("zhjhId");
            Record suppliers = getSuppliers(suppliersId);
            if (suppliers != null) {
                String gysmc = suppliers.getStr("gysmc");
                store.set("zhjhId", gysmc);
            } else {
                store.set("zhjhId", "");
            }
        }
        this.setAttr("status", 1);
        this.setAttr("result", store == null ? "" : store);
        this.renderJson();
    }

    /**
     * 查询供应商
     *
     * @param suppliersId
     * @return
     */
    private Record getSuppliers(String suppliersId) {
        String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where suppliersid = ?";
        Record record = Db.findFirst(sql, suppliersId);
        return record;
    }


    /**
     * 拉取中间表数据
     */
    private void sqlService() {
        BillContext context = this.buildBillContext();
        String code = context.getString("code");
        if (code.equals("Query")) {
            this.query(context);
        }
        this.setAttr("status", 1);
        this.renderJson();
    }

    /**
     * 点击头查询体
     *
     * @param context
     */
    private void query(BillContext context) {
        String billID = context.getString("BillID");
        String tableName = context.getString("tableName");
        StringBuffer sql = new StringBuffer("select * from " + tableName + " where BillID = '" + billID + "'");
        if (context.get("addQuery") != null) {
            sql.append(" and " + context.getString("addQuery"));
        }
        List<Record> list = Db.find(sql.toString());

        this.setAttr("list", list);
        this.renderJson();
    }

    private void classRun() {
        JSONArray result = new JSONArray();
        BillContext context = this.buildBillContext();
        Class<?> clazz;
        try {
            clazz = Class.forName(context.getString("clazz"));
            IFunc func = (IFunc) clazz.newInstance();
            JSONObject jObject = func.run(null);
            result.add(jObject);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.setAttr("status", 1);
        this.setAttr("result", result);
        this.renderJson();

    }
    /**
     * 通过单据id查询入库收货明细
     */
    private void getQueryShouHuoDetailByBillID() {
    	BillContext context = this.buildBillContext();
    	String BillID = context.getString("BillID");
    	String table = context.getString("table");
    	String sql = "SELECT * FROM " + table + " WHERE BillID=? AND yanshouzhuangtai=0";
    	List<Record> list = Db.find(sql, BillID);
    	this.setAttr("status", 1);
    	this.setAttr("result", list);
    	this.renderJson();
    }
    /**
     * 通过单据id查询销退收货明细
     */
    private void getQueryXiaoTuiShouHuoDetailByBillID() {
    	BillContext context = this.buildBillContext();
    	String BillID = context.getString("BillID");
    	String table = context.getString("table");
    	String sql = "SELECT * FROM " + table + " WHERE BillID=? AND yanshouzhuangtai=0";
    	List<Record> list = Db.find(sql, BillID);
    	this.setAttr("status", 1);
    	this.setAttr("result", list);
    	this.renderJson();
    }
    /**
     * 通过单据id查询不合格品明细
     */
    private void getQueryBuHeGeDetailByBillID() {
    	BillContext context = this.buildBillContext();
    	String BillID = context.getString("BillID");
    	String table = context.getString("table");
    	String sql = "SELECT * FROM " + table + " WHERE BillID=? AND xiaohuimxzhuangtai = 0 and sunyi = 1 and yanshoupingding=2";
    	List<Record> list = Db.find(sql, BillID);
    	this.setAttr("status", 1);
    	this.setAttr("result", list);
    	this.renderJson();
    }
    /**
     * 通过单据id查询销退订单明细
     */
    private void getQueryXiaoTuiDingDanDetailByBillID() {
    	BillContext context = this.buildBillContext();
    	String BillID = context.getString("BillID");
    	String table = context.getString("table");
    	String sql = "SELECT * FROM " + table + " WHERE BillID=? ";
    	List<Record> list = Db.find(sql, BillID);
    	this.setAttr("status", 1);
    	this.setAttr("result", list);
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


        public static final String SPE_CODE_SQLSERVICE = "sqlService";
        public static final String SPE_CODE_CLAZZ = "clazz";
        public static final String SPE_CODE_TOTAL_STORE = "totalStore";
        public static final String SPE_CODE_CAIGOU_SAVE = "caigouSave";
        public static final String SPE_CODE_GOODS_DETAILS = "goods-details";
        public static final String SPE_CODE_GOODS_COS = "goods-cos";
        public static final String SPE_CODE_QUERYCAIGOU_DETAILS = "querycaigou-details";
        public static final String SPE_CODE_QUERY_DETAILS = "query-details";
        public static final String SPE_CODE_QUERY_DETAILS1 = "query-details1";
        public static final String SPE_CODE_QUERY_DANJU = "query-danju"; //查询补货任务
        public static final String SPE_CODE_QUERY_CAIGOUDETAILS = "query-caigoudetails"; //采购订单
        public static final String SPE_CODE_QUERY_XTDETAILS = "query-xtdetails"; //消退明细
        public static final String SPE_CODE_QUERY_SHOUHUODETAILS = "query-shouhuodetails";//入库收货明细
        public static final String SPE_CODE_QUERY_XIAOTUISHOUHUODETAILS = "query-xiaotuishouhuodetails";//销退收货明细
        public static final String SPE_CODE_QUERY_BUHEGEDETAILS = "query-buhegedetails";//不合格品明细
        public static final String SPE_CODE_QUERY_XIAOTUIDINGDANCHAXUN = "query-xiaotuidingdandetails";//销退订单明细
        public static final String SPE_CODE_QUERY_SHANGPIN_PIHAO_HUOWEI_KUCUN = "query-spphhwkc";        //商品批号货位库存
        public static final String SPE_CODE_QUERY_SHANGPIN_PIHAO_HUOWEI_KUCUN_Z = "query-spphhwkcZDBH";  //商品批号货位库存 主动补货
        public static final String SPE_CODE_CHECK_RONGQIHAO = "checkRongqihao";  //容器号
        public static final String SPE_CODE_GET_TASK = "getTask";  //索取任务
    }

}
