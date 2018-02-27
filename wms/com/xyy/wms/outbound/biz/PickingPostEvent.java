package com.xyy.wms.outbound.biz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.amarsoft.core.util.StringUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.outbound.util.KuncunParameter;

/**
 * @author zhang.wk 出库拣货完成后处理
 */
public class PickingPostEvent implements PostSaveEventListener {

	private static Logger logger = Logger.getLogger(PickingPostEvent.class);

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = context.getDataSetInstance();
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
	    List<Record> body = dsi.getBodyDataTableInstance().get(0).getRecords();
		// 回写波次计划表的订单状态
		String dingdanbianhao = head.getStr("dingdanbianhao");
		boolean pickingCompleted = isPickingCompleted(dingdanbianhao);
		if (pickingCompleted) {
			Db.update("UPDATE xyy_wms_bill_bocijihua_details set dingdanzhuangtai =  24 WHERE dingdanbianhao = ?",dingdanbianhao);
		}
		StringBuffer sb = new StringBuffer("SELECT * from xyy_wms_bill_dabaorenwu where "
				+ "dingdanbianhao = ?  and bocibianhao = ? and fhqbh = ? and taskType = 10 and status in (34,36)");
		List<String> stringList = new ArrayList<String>();
		Integer taskType = head.getInt("taskType");
		Integer status = head.getInt("status");
		//拆零拣货
		if (taskType == 10) {
			// 获取该订单下的该复核区的所有未复核容器
			List<Record> list = Db.find(sb.toString(), head.getStr("dingdanbianhao"), head.getStr("bocibianhao"),head.get("fhqbh"));
			for (Record record : list) {
				String rongqihao = record.getStr("rongqihao");
				if(!StringUtil.isEmpty(rongqihao)){
					stringList.add(rongqihao);
				}
			}
			StringBuffer sbu = new StringBuffer();
			boolean flag = false;
			// 将容器号已逗号分开组成字符串保存
			for (String s : stringList) {
				if (flag) {
					sbu.append(",");
				} else {
					flag = true;
				}
				sbu.append(s);
			}
			//设置该订单下所有的拣货单未复核容器字段
			head.set("weifuherongqi", sbu.toString());
			for (Record record : list) {
				if(!record.getStr("taskCode").equals(head.getStr("taskCode"))){
					Db.update("UPDATE xyy_wms_bill_dabaorenwu SET weifuherongqi = ? WHERE taskCode = ?",sbu.toString(),record.getStr("taskCode"));
				}
			}
			// 设置推荐纸箱
			int boxSize = boxSize(dsi);
			head.set("zhixiangguige", boxSize);
		}
		
		//整件拣货
		if (taskType == 20 && head.getInt("status") == 36) {
			insertDataToWaifuhe(dsi);
		}
		
		//被动补货
		if(taskType == 40){
			//下架成功，添加商品账页
			if(status == 28){
				insertZhangye(head,body);
			}
			if(status == 26){
				//插入商品账页
				insertZhangye(head,body);
				//判断一个波次下的补货任务是否全部完成是否全部，如果完成，将所有的零货拣货任务都致为可拣货状态  
				changToPicked(head,body);
			}
		}
	}


	/**
	 * @param head
	 * @param body
	 * 被动补货后是否释放拣货任务
	 */
	private void changToPicked(Record head, List<Record> body) {
		String bocibianhao = head.getStr("bocibianhao");
		StringBuffer sql = new StringBuffer("SELECT * from xyy_wms_bill_dabaorenwu  WHERE bocibianhao = ? and taskType = 40");
		List<Record> tasklist = Db.find(sql.toString(), bocibianhao);
		boolean flag = true;
		for (Record record : tasklist) {
			if(record.getInt("status") == 26){//补货完成
				continue;
			}else{
				flag = false;
			}
		}
		//如果波次下所有的被动补货任务都完成了
		if(flag){
			//更新当前波次下的所有零件拣货任务状态（30->32）
			StringBuffer update_sql = new StringBuffer("UPDATE xyy_wms_bill_dabaorenwu set status = 32 WHERE bocibianhao = ? and taskType in (10,20)");
			Db.update(update_sql.toString(), bocibianhao);
			for (Record record : body) {
				String goodsid = record.getStr("goodsid");
				Record goods = Db.findFirst("SELECT * from xyy_wms_bill_bocijihua_details2 WHERE goodsid = ? and createTime > ? order by createTime ", goodsid,record.getDate("createTime"));
				if(goods == null){
					return;
				}
				String danjubianhao = goods.getStr("danjubianhao");//波次编号
				StringBuffer sq = new StringBuffer("SELECT * from xyy_wms_bill_dabaorenwu  WHERE bocibianhao = ?");
				List<Record> nextTasks = Db.find(sq.toString(), danjubianhao);
				if(nextTasks.size()>0){
					//更新当前波次下的所有零件拣货任务状态（30->32）
					StringBuffer sq2 = new StringBuffer("UPDATE xyy_wms_bill_dabaorenwu set status = 32 WHERE bocibianhao = ? and taskType in (10,20)");
					Db.update(sq2.toString(), danjubianhao);
				}
			}
		}
	}


	// 往外复核表中推荐箱子的添加数据
	private int boxSize(DataSetInstance dsi) {
		BigDecimal zongtiji = new BigDecimal(0.0);
		List<Record> bodyList = dsi.getBodyDataTableInstance().get(0).getRecords();
		for (Record record : bodyList) {
			BigDecimal dbztj = record.getBigDecimal("dbztj");
			int baozhuangshuliang = record.get("baozhuangshuliang");
			int shijianshuliang = record.get("shijianshuliang");
			//计算单件商品的体积
			BigDecimal tiji = dbztj.divide(new BigDecimal(baozhuangshuliang), 2, BigDecimal.ROUND_HALF_UP);
			//累加计算实总体积
			if (tiji.compareTo(new BigDecimal(0.0)) != 0 || shijianshuliang != 0) {
				zongtiji = zongtiji.add(tiji.multiply(new BigDecimal(shijianshuliang)));
			}


		}

		List<Record> records = Db.find("select * from xyy_wms_dic_zhixiangguige ");
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		for (Record head : records) {
			BigDecimal zhixiangtiji = head.getBigDecimal("zhixiangtiji");
			//如果总体积小于纸箱体积就添加到集合中去
			if (zongtiji.compareTo(zhixiangtiji) == -1) {
				list.add(zhixiangtiji);
			}
		}
		if (list.size() == 0) {
			Record record1 = Db.findFirst("select MIN(zhixiangguigesize) AS zhixiangguigesize  from xyy_wms_dic_zhixiangguige ");
			int zhixiangguigesize = record1.get("zhixiangguigesize");
			return zhixiangguigesize;
		} else {
			//获取最小的纸箱体积
			BigDecimal minZhiXiangTiJi = Collections.min(list);
			Record record1 = Db.findFirst("select * from xyy_wms_dic_zhixiangguige where zhixiangtiji=?", minZhiXiangTiJi);
			int zhixiangguigesize = record1.get("zhixiangguigesize");
			return zhixiangguigesize;
		}
	}

	// 往外复核表中添加数据
	private void insertDataToWaifuhe(DataSetInstance dsi) {
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		// 判断当前订单是否已经存在于外复核表中
		String dingdanbianhao = head.getStr("dingdanbianhao");
		StringBuffer sb = new StringBuffer("SELECT * from xyy_wms_bill_chukuwaifuhe WHERE dingdanbianhao = ?");
		Record re = Db.findFirst(sb.toString(), dingdanbianhao);
		List<Record> bodyList = dsi.getBodyDataTableInstance().get(0).getRecords();
		if (re != null) {
			copyBody(re, bodyList);
		} else {
			Record copyHead = copyHead(head);
			copyBody(copyHead, bodyList);
		}
	}

	// 插入外复核表头信息
	private Record copyHead(Record head) {
		Record waifuheHead = new Record();
		waifuheHead.set("BillID", UUIDUtil.newUUID());// BillID
		waifuheHead.set("status", 1);// status
		waifuheHead.set("dingdanbianhao", head.getStr("dingdanbianhao"));// 订单编号
		waifuheHead.set("xianlumingcheng", head.getStr("xianlumingcheng"));// 线路名称
		waifuheHead.set("huozumingcheng", "小药药");// 名称 货主
		waifuheHead.set("kehumingcheng", head.getStr("kehumingcheng"));// 客户名称
		waifuheHead.set("zancunqu", head.getStr("zancunqu"));// 暂存区
		waifuheHead.set("orgId", head.getStr("orgId"));
		waifuheHead.set("orgCode", head.getStr("orgCode"));
		waifuheHead.set("rowID", head.getStr("rowID"));
		waifuheHead.set("kehubeizhu", head.getStr("kehubeizhu"));
		// 保存外复核表头信息
		logger.info("插入外复核表头信息》》》》》》》》》》》" + waifuheHead);
		Db.save("xyy_wms_bill_chukuwaifuhe", waifuheHead);
		return waifuheHead;
	}

	// 插入明细信息
	private void copyBody(Record head, List<Record> bodyList) {
		// List<Record> waifuheBody = new ArrayList<Record>();
		BigDecimal zhengjianshu = BigDecimal.ZERO;
		for (Record record : bodyList) {
			String shangpinbianhao = record.getStr("shangpinbianhao");
			String shangpingpihao = record.getStr("shangpingpihao");
			String dingdanbianhao = record.getStr("dingdanbianhao");
			StringBuffer sb = new StringBuffer(
					"SELECT * from xyy_wms_bill_chukuwaifuhe_details WHERE shangpinbianhao =? AND shangpingpihao = ?");
			sb.append(" and dingdanbianhao = ?");
			Record wfhDetail = Db.findFirst(sb.toString(), shangpinbianhao, shangpingpihao, dingdanbianhao);
			if (wfhDetail == null) {
				wfhDetail = new Record();
				wfhDetail.set("BillDtlID", UUIDUtil.newUUID());
				wfhDetail.set("BillID", head.getStr("BillID"));
				wfhDetail.set("shangpinbianhao", record.getStr("shangpinbianhao"));// 商品编号
				wfhDetail.set("goodsid", record.getStr("goodsid"));// 商品id
				wfhDetail.set("shangpinmingcheng", record.getStr("shangpinmingcheng"));// 商品名称
				wfhDetail.set("guige", record.getStr("guige"));// 规格
				wfhDetail.set("danwei", record.getInt("danwei"));// 单位
				wfhDetail.set("dingdanbianhao", record.getStr("dingdanbianhao"));// 订单编号
				wfhDetail.set("zhengjianshu", record.getBigDecimal("shijianjianshu"));// 整件件数
				BigDecimal shijianjianshu = record.getBigDecimal("shijianjianshu");
				zhengjianshu = zhengjianshu.add(shijianjianshu);
				// 整件数量
				wfhDetail.set("shuliang", record.getBigDecimal("jianshu")
						.multiply(new BigDecimal(String.valueOf(record.getInt("baozhuangshuliang")))));
				wfhDetail.set("orgId", record.getStr("orgId"));// 组织id
				wfhDetail.set("shengchanchangjia", record.getStr("shengchanchangjia"));// 生产厂家
				wfhDetail.set("shangpingpihao", record.getStr("shangpingpihao"));// 批号id
				wfhDetail.set("shangpingpihaosn", record.getStr("shangpingpihaosn"));// 批号
				wfhDetail.set("shengchanriqi", record.getDate("shengchanriqi"));// 生产日期
				wfhDetail.set("youxiaoqizhi", record.getDate("youxiaoqizhi"));// 有效期至
				wfhDetail.set("orgCode", record.getStr("orgCode"));// 组织编码
				wfhDetail.set("rowID", record.getStr("rowID"));// 行标志
				wfhDetail.set("caozuoren", record.getStr("caozuoren"));// 拣货员
				logger.info("插入外复核表行明细》》》》》》》》》》》》》》》》" + wfhDetail.toString());
				Db.save("xyy_wms_bill_chukuwaifuhe_details", wfhDetail);
			} else {
				wfhDetail.set("zhengjianshu", record.getBigDecimal("shijianjianshu"));// 整件件数
				BigDecimal shijianjianshu = record.getBigDecimal("shijianjianshu");
				zhengjianshu = zhengjianshu.add(shijianjianshu);
				BigDecimal newshuliang = record.getBigDecimal("jianshu")
						.multiply(new BigDecimal(String.valueOf(record.getInt("baozhuangshuliang"))));
				BigDecimal oldshuliang = wfhDetail.getBigDecimal("shuliang");
				wfhDetail.set("shuliang", newshuliang.add(oldshuliang));
				wfhDetail.set("caozuoren", record.getStr("caozuoren"));// 拣货员
				logger.info("更新外复核表行明细》》》》》》》》》》》》》》》》" + wfhDetail);
				Db.update("xyy_wms_bill_chukuwaifuhe_details", "BillDtlID", wfhDetail);
			}
		}
		StringBuffer sb = new StringBuffer(
				"UPDATE xyy_wms_bill_chukuwaifuhe SET zhengjianshu = ?,zongjianshu = zongjianshu + ? ");
		String dingdanbianhao = head.getStr("dingdanbianhao");
		if (isCheckCompleted(dingdanbianhao)) {
			// 整件拣货，内复核全部完成 外复核表的状态为45
			sb.append(",status = 45 WHERE dingdanbianhao = ? ");
		} else {
			sb.append(" WHERE dingdanbianhao = ?");
		}
		Db.update(sb.toString(), zhengjianshu, zhengjianshu, dingdanbianhao);
	}

	// 判断是否能进行外复核
	public static boolean isCheckCompleted(String dingdanbianhao) {
		List<Record> list = Db.find("select * from xyy_wms_bill_dabaorenwu WHERE dingdanbianhao = ?", dingdanbianhao);
		boolean flag = true;
		for (Record record : list) {
			// 整件任务，状态为已拣货
			if (record.getInt("taskType") == 20) {
				if(record.getInt("status") == 36){
					continue;
				}else{
					flag = false;
				}
			}
			// 零件任务，状态为内复核完成
			if (record.getInt("taskType") == 10) {
				if(record.getInt("status") == 43){
					continue;
				}else{
					flag = false;
				}
			}
		}
		return flag;
	}

	// 判断单个订单是否拣货完成
	private boolean isPickingCompleted(String dingdanbianhao) {
		boolean flag = true;
		List<Record> list = Db.find("select * from xyy_wms_bill_dabaorenwu WHERE dingdanbianhao = ?", dingdanbianhao);
		for (Record record : list) {
			if (record.getInt("status") != 36) {
				flag = false;
			}
		}
		return flag;
	}
	
	//更新【商品帐页表】，insert处理
	public static void insertZhangye(Record head,List<Record> body) {
		if(head == null || body.size() == 0){
			return;
		}
		if(head.getInt("status") == 28){//下架确认(出库)
			List<Record> xiajiazhangyelist = new ArrayList<Record>();
			for(Record r : body){
				Record zy = new Record();
				Record kuqu = Db.findFirst("SELECT t1.* from xyy_wms_dic_kuqujibenxinxi t1 WHERE t1.ID = ?", r.getStr("kuquId"));
				String cangkuId = kuqu.getStr("cangkuID");
				zy.set("orgId", r.getStr("orgId"));
				zy.set("orgCode", r.getStr("orgCode"));
				zy.set("danjubianhao", null);
				zy.set("zhidanriqi", new Date());
				zy.set("zhidanren", r.getStr("caozuoren"));
				zy.set("zhaiyao", 7);//补货
				zy.set("status", 10);
				zy.set("createTime", new Date());
				zy.set("updateTime", new Date());
				zy.set("rowID",  UUIDUtil.newUUID());
				zy.set("shangpinId", r.getStr("goodsid"));//商品id
				zy.set("rukushuliang", BigDecimal.ZERO);//入库数量
				zy.set("chukushuliang", new BigDecimal(r.getInt("shuliang")));//出库数量
				zy.set("pihaoId",r.getStr("shangpingpihao"));//批号id
				zy.set("huoweiId",r.getStr("xiajiahuowei"));//下架货位id
				zy.set("huozhuId","0001");//货主id
				zy.set("cangkuId",cangkuId);//仓库id
				xiajiazhangyelist.add(zy);
				//被动补货修改商品库存(下架，删除预扣)
				KuncunParameter kc =new KuncunParameter(r.get("orgId"),null, r.get("goodsid"), r.get("shangpingpihao"),
						r.get("xiajiahuowei"), r.get("bocibianhao"), r.get("dingdanbianhao"),
						 4,null);
				KucuncalcService.kcCalc.deleteKCRecord(kc);
//				Db.update("UPDATE xyy_wms_bill_shangpinpihaohuoweikucun SET kucunshuliang = kucunshuliang - ? WHERE pihaoId = ? and shangpinId = ? and huoweiId = ?",
//						new BigDecimal(r.getInt("shuliang")),r.getStr("shangpingpihao"),r.getStr("goodsid"),r.getStr("xiajiahuowei"));//减去商品批号库存
			}
			EDB.batchSave("xyy_wms_bill_shangpinzhangye", xiajiazhangyelist);
		}
		 if(head.getInt("status") == 26){//上架成功
			 List<Record> shangjiazhangyelist = new ArrayList<Record>();
			for(Record r : body){
				Record zy = new Record();
				Record kuqu = Db.findFirst("SELECT t1.* from xyy_wms_dic_kuqujibenxinxi t1 WHERE t1.ID = ?", r.getStr("kuquId"));
				String cangkuId = kuqu.getStr("cangkuID");
				zy.set("orgId", r.getStr("orgId"));
				zy.set("orgCode", r.getStr("orgCode"));
				zy.set("danjubianhao",null);
				zy.set("zhidanriqi", new Date());
				zy.set("zhidanren", r.getStr("caozuoren"));
				zy.set("zhaiyao", 7);//补货
				zy.set("status", 10);
				zy.set("createTime", new Date());
				zy.set("updateTime", new Date());
				zy.set("rowID",  UUIDUtil.newUUID());
				zy.set("shangpinId", r.getStr("goodsid"));//商品id
				zy.set("rukushuliang", new BigDecimal(r.getInt("shuliang")));//入库数量
				zy.set("chukushuliang",BigDecimal.ZERO);//出库数量
				zy.set("pihaoId",r.getStr("shangpingpihao"));//批号id
				zy.set("huoweiId",r.getStr("shangjiahuowei"));//上架货位id
				zy.set("huozhuId","0001");//货主id
				zy.set("cangkuId",cangkuId);//仓库id
				shangjiazhangyelist.add(zy);
				//上架(删除预占库存) 
				KuncunParameter kc =new KuncunParameter(r.get("orgId"),null, r.get("goodsid"), r.get("shangpingpihao"),
						r.get("shangjiahuowei"), r.get("bocibianhao"), r.get("dingdanbianhao"),
						 3,null);
				KucuncalcService.kcCalc.deleteKCRecord(kc);
			}
			EDB.batchSave("xyy_wms_bill_shangpinzhangye", shangjiazhangyelist);
		 }
  }
}