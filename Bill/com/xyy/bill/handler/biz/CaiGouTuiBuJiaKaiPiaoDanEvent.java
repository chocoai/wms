package com.xyy.bill.handler.biz;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.util.RecordDataUtil;

/**
 * 采购退补价开票单(条件：审核通过状态)
 */
public class CaiGouTuiBuJiaKaiPiaoDanEvent implements StatusChangedEventListener,PreSaveEventListener {

	@Override
	public void execute(StatusChangedEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		System.out.println(event.getOldStatus());
		System.out.println(event.getNewStatus());
		if (event.getNewStatus() == 40) {//审核通过状态，生成采购退补价执行单
			Record head = getHead(dsi);
			List<Record> body = getBody(dsi);
			//生成采购退补价执行单
			RecordDataUtil.generateCGTBJZX(head, body);
			
			
			
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


	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		Record head = getHead(dsi);
		Integer status = head.getInt("status");
		if(status==1||status==20){//只有保存和提交需要拦截
			List<Record> body = getBody(dsi);
			//商品批号库存-商品占用库存 xyy_erp_bill_shangpinkucunzhanyong
			for (Record record : body) {
				String shangpinbianhao = record.getStr("shangpinbianhao");
				String shangpinmingcheng = record.getStr("shangpinmingcheng");
				BigDecimal weixiaoshuliang = record.getBigDecimal("weixiaoshuliang");
				String pihao = record.getStr("pihao");
				Record goods = getGoods(shangpinbianhao);
				if(goods==null){
					event.getContext().addError("999", "没有找到，【商品名称】："+shangpinmingcheng+"");
				}
				String goodsid = goods.getStr("goodsid");
				Record goodsBatch = findGoodsBatch(goodsid, pihao);
				if(goodsBatch==null){
					event.getContext().addError("999", "【商品名称】："+shangpinmingcheng+",【批号】："+pihao+",没有该商品的批号！");
					return;
				}
				Record bRecord = findTotalBatchStore(goodsid, goodsBatch.getStr("ID"));
				if(bRecord.get("shuliang") == null){
					event.getContext().addError("999", "【商品名称】："+shangpinmingcheng+",【批号】："+pihao+",没有该商品的批号库存！");
					return;
				}
				BigDecimal totalStore = bRecord.getBigDecimal("shuliang");
				if(totalStore.compareTo(weixiaoshuliang)<0){
					event.getContext().addError("999", "【商品名称】："+shangpinmingcheng+"，【未销数量】不能大于【库存数量】！");
				}
			}
		}
		
		
	}
	
	
	/**
	 * 商品批号库存
	 * @param goodsId
	 * @param pihaoId
	 * @return
	 */
	private Record findTotalBatchStore(String goodsId,String pihaoId){
		String sql ="select SUM(kucunshuliang) AS shuliang FROM xyy_erp_bill_shangpinpihaokucun WHERE shangpinId=? AND pihaoId =? ";
		Record bRecord = Db.findFirst(sql,goodsId,pihaoId);
		return bRecord;
	}
	
	
	/**
	 * 商品批号
	 * @param goodsId
	 * @param pihaoId
	 * @return
	 */
	private Record findGoodsBatch(String goodsId,String pihao){
		String sql ="select * FROM xyy_erp_dic_shangpinpihao WHERE goodsId=? AND pihao =? ";
		Record bRecord = Db.findFirst(sql,goodsId,pihao);
		return bRecord;
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
}
