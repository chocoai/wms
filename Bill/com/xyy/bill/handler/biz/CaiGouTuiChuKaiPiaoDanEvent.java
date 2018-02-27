package com.xyy.bill.handler.biz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.util.PullWMSDataUtil;
import com.xyy.bill.util.RecordDataUtil;
import com.xyy.erp.platform.common.tools.StringUtil;

/**
 * 采购退出开票单生成WMS中间表的购进退出(条件：采购退出开票单审核通过后，自动下推)
 */
public class CaiGouTuiChuKaiPiaoDanEvent implements StatusChangedEventListener,PreSaveEventListener {

	@Override
	public void execute(StatusChangedEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		System.out.println(event.getOldStatus());
		System.out.println(event.getNewStatus());
		Record head = getHead(dsi);
		List<Record> body = getBody(dsi);
		if (event.getNewStatus() == 40) {//审核通过后保存到中间表
			//生产中间库数据采购退出
			PullWMSDataUtil.generateCaiGouChuKuDan(head,body);
		}
		
		//提交状态回写
		if(event.getNewStatus() == 20){
			// 回写采购入库单明细
			backWriteDetails(head,body);
			//预占库存， 商品库存数量占用表,
			RecordDataUtil.operateGoodsStockUses(head,body, 2);
		}
	}
	
	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		Record head = getHead(dsi);
		Integer status = head.getInt("status");
		if(status==1||status==20){//只有保存和提交需要拦截
			List<Record> body = getBody(dsi);
			String sjdjbh = head.getStr("sjdjbh");//上级单据编号
			if(!StringUtil.isEmpty(sjdjbh)){
				for (Record record : body) {
					BigDecimal yuandanshuliang = record.getBigDecimal("yuandanshuliang");//原单数量
					BigDecimal ydytsl = record.getBigDecimal("ydytsl");//原单已退数量
					BigDecimal shuliang = record.getBigDecimal("shuliang");//数量
					BigDecimal ketuishuliang = yuandanshuliang.subtract(ydytsl);
					if(shuliang.compareTo(ketuishuliang)>0){
						String shangpinmingcheng = record.getStr("shangpinmingcheng");
						event.getContext().addError("999", "商品名称【"+shangpinmingcheng+"】的数量不能大于原单可退数量！");
						return;
					}
				}
			}
			
			//商品批号库存-商品占用库存 xyy_erp_bill_shangpinkucunzhanyong
			for (Record record : body) {
				BigDecimal shuliang = record.getBigDecimal("shuliang");//数量
				String shangpinbianhao = record.getStr("shangpinbianhao");
				String shangpinmingcheng = record.getStr("shangpinmingcheng");
				String pihao = record.getStr("pihao");
				Record goods = getGoods(shangpinbianhao);
				if(goods==null){
					event.getContext().addError("999", "没有找到，商品名称【"+shangpinmingcheng+"】！");
				}
				int kucunzhuangtai = record.getInt("kucunzhuangtai");
				String goodsId = goods.getStr("goodsid");
				//商品批号
				Record goodsBatch = findGoodsBatch(goodsId, pihao);
				if(goodsBatch==null){
					event.getContext().addError("999", "【商品名称】："+shangpinmingcheng+",【批号】："+pihao+",没有该商品的批号！");
					return;
				}
				String pihaoId = goodsBatch.getStr("ID");
				
				//商品占用
				Record zRecord = findZhanyong(goods.getStr("goodsid"), pihaoId, kucunzhuangtai);
				if(zRecord.get("shuliang") == null){
					return;
				}
				
				//商品库存
				Record bRecord = findBatchStore(goodsId, pihaoId, kucunzhuangtai);
				if(bRecord==null){
					event.getContext().addError("999", "商品名称【"+shangpinmingcheng+"】,没有批号库存！");
					return;
				}
				
				BigDecimal kucunshuliang =  bRecord.getBigDecimal("kucunshuliang");
				BigDecimal zhanyong =  zRecord.getBigDecimal("shuliang");
				BigDecimal subtract = kucunshuliang.subtract(zhanyong);
				// 数量大于（库存数量-占用数量），则拦截
				if(shuliang.compareTo(subtract)>0){
					event.getContext().addError("999", "商品名称【"+shangpinmingcheng+"】的数量大于实际批号库存数量！");
					return;
				}
			}
		}
		
	}
	
	/**
	 * 占用数量
	 * @param goodsId
	 * @param pihaoId
	 * @return
	 */
	private Record findZhanyong(String goodsId,String pihaoId,int kucunzhuangtai){
		String sql ="select SUM(zhanyongshuliang) AS shuliang FROM xyy_erp_bill_shangpinkucunzhanyong WHERE shangpinId=? AND pihaoId =? AND kucunzhuangtai=?";
		Record zRecord = Db.findFirst(sql,goodsId,pihaoId,kucunzhuangtai);
		return zRecord;
	}
	
	/**
	 * 批号库存库存
	 * @param goodsId
	 * @param pihaoId
	 * @return
	 */
	private Record findBatchStore(String goodsId,String pihaoId,int kucunzhuangtai){
		String sql ="select * FROM xyy_erp_bill_shangpinpihaokucun WHERE shangpinId=? AND pihaoId =? AND kucunzhuangtai=?";
		Record bRecord = Db.findFirst(sql,goodsId,pihaoId,kucunzhuangtai);
		return bRecord;
	}
	
	
	/**
	 * 商品批号
	 * @param goodsId
	 * @param pihao
	 * @return
	 */
	private Record findGoodsBatch(String goodsId,String pihao){
		String sql ="select * FROM xyy_erp_dic_shangpinpihao WHERE goodsId=? AND pihao =? ";
		Record bRecord = Db.findFirst(sql,goodsId,pihao);
		return bRecord;
	}
	
	
	
	/**
	 * 回写采购入库单
	 *  采购入库单：采购退出开票单=1:N   一个采购入库单对应多个采购退出开票单
	 *  采购退出开票单多次提取采购入库单，则可以多次回写采购入库单
	 *  回写采购入库单的明细数量
	 *  	采购入库单的某一条明细项的数量小于等于入库数量，则改变【采购入库单明细】的字段“是否上引”为1
	 *  	一个采购入库单的所有明细项的回写数量都等于明细项的数量时，回写这个采购订单的提取状态为【已提取1】
	 */
	public static void backWriteDetails(Record head,List<Record> list){
		List<Record> bodyList = new ArrayList<>();
		String BillID = null;
		for (Record record : list) {
			BigDecimal shuliang = record.getBigDecimal("shuliang");//采购退出开票单的【数量】
			String SourceID = record.getStr("SourceID");//采购入库单明细项ID
			if(StringUtil.isEmpty(SourceID)){
				return;
			}
			Record RKRecord = getCGRKDetailsById(SourceID);//采购入库单明细项
			BillID = RKRecord.getStr("BillID");
			//先判断是否上引
			if(RKRecord.getInt("shifoutiqu")==1){
				continue;
			}
			BigDecimal yituishuliang = RKRecord.getBigDecimal("yituishuliang");
			//已退数量=已退数量+数量
			BigDecimal rl = yituishuliang.add(shuliang);
			RKRecord.set("yituishuliang", rl);
			
			//入库数量
			BigDecimal rkshuliang = RKRecord.getBigDecimal("shuliang");
			
//			RKRecord.set("shuliang", yuandanshuliang-shuliang);
			//采购入库单的数量减已退数量小于等于0，则改变采购入库单的是否上引为1
			if(rkshuliang.subtract(rl).compareTo(BigDecimal.ZERO)==-1||rkshuliang.subtract(rl).compareTo(BigDecimal.ZERO)==0){
				RKRecord.set("shifoutiqu",1);
			}
			bodyList.add(RKRecord);
		}
		int[] batchUpdate = Db.batchUpdate("xyy_erp_bill_caigourukudan_details", "BillDtlID", bodyList, 100);
		for (int i : batchUpdate) {
			System.out.println("【回写采购订单单据体是否成功,个数："+batchUpdate.length+"】："+i);
		}
		if(!StringUtil.isEmpty(BillID)){
			backWriteHead(BillID);
		}
	}
	
	/**
	 * 回写采购入库单头部状态是否已提取
	 */
	public static void backWriteHead(String BillID){
		List<Record> body = getCGRKDetails(BillID);
		//有一个为不等于0都不用执行修改头逻辑
		boolean isUpdate = true;
		for (Record record : body) {
			if(record.getInt("shifoutiqu")!=1){
				isUpdate = false;
				break;
			}
		}
		if(isUpdate){
			String sql =" update xyy_erp_bill_caigourukudan set shifoutiqu = 1 where BillID = '"+BillID+"'";
			int update = Db.update(sql);
			System.out.println("【采购入库单头部是否提取】："+update);
		}
	}
	

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
		String sql = "select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = '" + shangpinbianhao + "'";
		List<Record> goods = Db.find(sql);
		if (goods != null && goods.size() == 1) {
			return goods.get(0);
		}
		return null;
	}

	/**
	 * 查询供应商
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getSupplier(String gysbh) {
		String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where gysbh = '" + gysbh + "'";
		List<Record> gysbhs = Db.find(sql);
		if (gysbhs != null && gysbhs.size() == 1) {
			return gysbhs.get(0);
		}
		return null;
	}
	
	/**
	 * 查询供应商
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getSupplierByName(String gysmc) {
		String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where gysmc = '" + gysmc + "'";
		Record record = Db.findFirst(sql);
		return record;
	}
	
	/**
	 * 查询用户
	 * @param userId
	 * @return
	 */
	public static Record getUserById(String userId) {
		Record record = Db.findById("tb_sys_user", userId);
		return record;
	}
	/**
	 * 名称查询用户
	 * @param userId
	 * @return
	 */
	public static Record getUserByName(String name) {
		String sql = "select * from tb_sys_user where realName = '"+name+"'";
		Record record = Db.findFirst(sql);
		return record;
	}
	
	
	/**
	 * 查询部门
	 * @param userId
	 * @return
	 */
	public static Record getDeptByUserId(String userId) {
		String sql ="SELECT * FROM tb_sys_dept WHERE id = (SELECT r.deptId FROM tb_sys_dept_user_relation r WHERE r.userId ='" +userId+"')";
		Record record = Db.findFirst(sql);
		return record;
	}
	
	/**
	 * 查询采购入库单明细
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	public static List<Record> getCGRKDetails(String BillID) {
		String sql = "select * from xyy_erp_bill_caigourukudan_details where BillID = '"+BillID+"'";
		List<Record> list = Db.find(sql);
		return list;
	}
	
	/**
	 * 查询未提取的采购入库单
	 * @return
	 */
	public static List<Record> getCGRKHead() {
		String sql = "select * from xyy_erp_bill_caigourukudan where shifoutiqu = 0";
		return Db.find(sql);
	}
	
	/**
	 * 查询未提取的采购入库单
	 * @return
	 */
	public static Record getCGRKHeadById(String BillID) {
		return Db.findById("xyy_erp_bill_caigourukudan", "BillID", BillID);
	}
	
	/**
	 * 查询采购入库单明细
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	public static Record getCGRKDetailsById(String BillDtlID) {
		return Db.findById("xyy_erp_bill_caigourukudan_details", "BillDtlID", BillDtlID);
	}




	

}
