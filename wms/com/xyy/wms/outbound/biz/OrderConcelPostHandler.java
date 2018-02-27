package com.xyy.wms.outbound.biz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.UUIDUtil;

/**
 * @author zhang.wk
 *	销售订单冲红
 */
public class OrderConcelPostHandler implements PostSaveEventListener{

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
	    Map<String, Object> result = getOrderStatus(body);
	    boolean cancelAll = (boolean) result.get("cancelAll");
	    if(cancelAll){
    	//整单全部冲红
	    	insertWaifuhe(head,body);
	    }else{
	    //部分全部冲红
	    	@SuppressWarnings("unchecked")
			ArrayList<Record> redGoods = (ArrayList<Record>) result.get("part");
	    	isInsertWaifuhe(head,redGoods);
	    }
	}
	
	/**
	 * @param head
	 * @param body
	 * 判断是否插入外复核数据（有表头则插入表头，没有则插入表头，表体）
	 */
	private void isInsertWaifuhe(Record head, List<Record> redGoods) {
		String dingdanbianhao = head.getStr("danjubianhao");
		Record waifuheHead = Db.findFirst("SELECT * from xyy_wms_bill_chukuwaifuhe WHERE dingdanbianhao = ?", dingdanbianhao);
		if(waifuheHead == null){
			//同时插入表头，表体
			insertWaifuhe(head,redGoods);
		}else{
			//找到表头的BillId，插入表体
			String BillID = waifuheHead.getStr("BillID");
			   List<Record> wfhDetails = new ArrayList<Record>();
				for (Record record : redGoods) {
						Record	wfhDetail = new Record();
						wfhDetail.set("BillDtlID", UUIDUtil.newUUID());
						wfhDetail.set("BillID",BillID);
						wfhDetail.set("shangpinbianhao", record.getStr("shangpinbianhao"));// 商品编号
						wfhDetail.set("goodsid", record.getStr("goodsid"));// 商品id
						wfhDetail.set("shangpinmingcheng", record.getStr("shangpinmingcheng"));// 商品名称
						wfhDetail.set("guige", record.getStr("guige"));// 规格
						wfhDetail.set("danwei", record.getInt("danwei"));// 单位
						wfhDetail.set("dingdanbianhao", record.getStr("danjubianhao"));// 订单编号
						wfhDetail.set("orgId", record.getStr("orgId"));// 组织id
						wfhDetail.set("shengchanchangjia", record.getStr("shengchanchangjia"));// 生产厂家
						wfhDetail.set("shangpingpihao", record.getStr("shangpingpihao"));// 批号id
						wfhDetail.set("shangpingpihaosn", record.getStr("shangpingpihaosn"));// 批号
						wfhDetail.set("shengchanriqi", record.getDate("shengchanriqi"));// 生产日期
						wfhDetail.set("youxiaoqizhi", record.getDate("youxiaoqizhi"));// 有效期至
						wfhDetail.set("orgCode", record.getStr("orgCode"));// 组织编码
						wfhDetail.set("rowID", record.getStr("rowID"));// 行标志
						wfhDetail.set("chonghongshuliang", record.getBigDecimal("chonghongshuliang"));// 冲红数量
						wfhDetails.add(wfhDetail);
					}
				Db.batchSave("xyy_wms_bill_chukuwaifuhe_details", wfhDetails, wfhDetails.size());
			}
	}

	/**
	 * @param head
	 * @param body
	 * 外复核中插入数据
	 */
	private void insertWaifuhe(Record head, List<Record> redGoods) {
		Record waifuheHead = new Record();
		waifuheHead.set("BillID", UUIDUtil.newUUID());// BillID
		waifuheHead.set("status", 49);// status
		waifuheHead.set("dingdanbianhao", head.getStr("danjubianhao"));// 订单编号
		waifuheHead.set("huozumingcheng", "小药药");// 名称 货主
		waifuheHead.set("kehumingcheng", head.getStr("kehumingcheng"));// 客户名称
		waifuheHead.set("orgId", head.getStr("orgId"));
		waifuheHead.set("orgCode", head.getStr("orgCode"));
		waifuheHead.set("rowID", head.getStr("rowID"));
		waifuheHead.set("kehubeizhu", head.getStr("kehubeizhu"));
		// 保存外复核表头信息
		logger.info("插入外复核表头信息》》》》》》》》》》》" + waifuheHead);
		Db.save("xyy_wms_bill_chukuwaifuhe", waifuheHead);
	    List<Record> wfhDetails = new ArrayList<Record>();
		for (Record record : redGoods) {
				Record	wfhDetail = new Record();
				wfhDetail.set("BillDtlID", UUIDUtil.newUUID());
				wfhDetail.set("BillID", waifuheHead.getStr("BillID"));
				wfhDetail.set("shangpinbianhao", record.getStr("shangpinbianhao"));// 商品编号
				wfhDetail.set("goodsid", record.getStr("goodsid"));// 商品id
				wfhDetail.set("shangpinmingcheng", record.getStr("shangpinmingcheng"));// 商品名称
				wfhDetail.set("guige", record.getStr("guige"));// 规格
				wfhDetail.set("danwei", record.getInt("danwei"));// 单位
				wfhDetail.set("dingdanbianhao", record.getStr("danjubianhao"));// 订单编号
				wfhDetail.set("orgId", record.getStr("orgId"));// 组织id
				wfhDetail.set("shengchanchangjia", record.getStr("shengchanchangjia"));// 生产厂家
				wfhDetail.set("shangpingpihao", record.getStr("shangpingpihao"));// 批号id
				wfhDetail.set("shangpingpihaosn", record.getStr("shangpingpihaosn"));// 批号
				wfhDetail.set("shengchanriqi", record.getDate("shengchanriqi"));// 生产日期
				wfhDetail.set("youxiaoqizhi", record.getDate("youxiaoqizhi"));// 有效期至
				wfhDetail.set("orgCode", record.getStr("orgCode"));// 组织编码
				wfhDetail.set("rowID", record.getStr("rowID"));// 行标志
				wfhDetail.set("chonghongshuliang", record.getBigDecimal("chonghongshuliang"));// 冲红数量
				wfhDetails.add(wfhDetail);
			}
		Db.batchSave("xyy_wms_bill_chukuwaifuhe_details", wfhDetails, wfhDetails.size());
		}

	/**
	 * @param body
	 * @return
	 * 判断部分整个冲红，还是全部商品整个冲红
	 */
	private Map<String,Object> getOrderStatus(List<Record> body){
		List<Record> redGoods = new ArrayList<Record>();
		Map<String,Object> result = new HashMap<String,Object>();
		for (Record r : body) {
			BigDecimal shuliang = r.getBigDecimal("shuliang");
			BigDecimal chonghongshuliang = r.getBigDecimal("chonghongshuliang");
			if(shuliang.compareTo(chonghongshuliang)== 0){
				//整个商品全部冲红
				redGoods.add(r);
			}
		}
		if(redGoods.size() > 0){
			if(redGoods.size() == body.size()){
				//全部商品整个冲红
				result.put("cancelAll", true);
			}else{
				//部分商品整个冲红
				result.put("part", redGoods);
				result.put("cancelAll", false);
			}
		}
		return result;
	}
}