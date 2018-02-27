package com.xyy.wms.handler.biz;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

import org.apache.log4j.Logger;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillBuHeGePinPostHandler implements PostSaveEventListener {

	private static Logger LOGGER = Logger
			.getLogger(BillBuHeGePinPostHandler.class);

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
			return;
		// 单据头数据
		Record head = getHead(dsi);
		// 单据体数据
		List<Record> bodys = getBody(dsi);
		// 判断状态，1-录入，20-正式，40-审核通过
		/*
		 * int status = head.getInt("status"); if (status != 20) { return; }
		 */

		String dingdanbianhao = head.getStr("dingdanbianhao"); // 订单编号
		String buhegeID = head.getStr("buhegeID"); // 入库单ID


		for (Record body : bodys) {
			BigDecimal shuliang = body.get("shuliang"); // 商品总数
			String shangpinbianhao = body.getStr("shangpinbianhao"); // 商品编号
			String buhegemxID = body.getStr("buhegemxID"); // 关联入库收货明细单

			// 回写不合格品明细单的销毁数量
			updateBuhegeshuliang(dingdanbianhao, shangpinbianhao, shuliang,
					buhegeID,buhegemxID);// ？这里收货的商品有拆分，验收时没有拆分，所以回写有差异

			// 回写不合格品明细单的销毁状态（0未销毁，1-已销毁）
			Record shuliangCompare = Db
					.findFirst(
							"select shuliang,xiaohuishuliang from xyy_wms_bill_rukushangjia_details where shangpinbianhao = ? and BillDtlID =?",
							shangpinbianhao, buhegemxID);
			if (shuliangCompare != null) {
				BigDecimal buhegeshuliang = shuliangCompare.get("shuliang");
				BigDecimal buhegexiaohuishuliang = shuliangCompare
						.get("xiaohuishuliang");
				if (buhegeshuliang.compareTo(buhegexiaohuishuliang) == 0) {
					Db.update(
							"update xyy_wms_bill_rukushangjia_details set xiaohuimxzhuangtai = ? where shangpinbianhao = ? and BillDtlID =?",
							1, shangpinbianhao, buhegemxID);
				}
			}
		}
	}

	/**
	 * 回写不合格明细单销毁数量
	 * 
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public static void updateBuhegeshuliang(String dingdanbianhao,
			String shangpinbianhao, BigDecimal shuliang, String ID,
			String mxID) {
		String sql3 = "update xyy_wms_bill_rukushangjia_details set xiaohuishuliang = xiaohuishuliang +"
				+ shuliang
				+ " where billID = ? and shangpinbianhao = ? and BillDtlID = ?";
		// 回写不合格明细单验收数量
		Db.update(sql3, ID, shangpinbianhao, mxID);
		LOGGER.info("不合格明细单销毁数量回写成功，商品编号：" + shangpinbianhao);
		// 当【入库收货单】所有明细的“收货数量”=“已完成数量”时，回写【入库收货单】汇总的状态为“已完成”
	}

	/**
	 * 回写不合格单销毁状态
	 * 
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public static void updateXiaohuiZhuangtai(String dingdanbianhao,
			int xiaohuizhuangtai, String ID) {
		String sql = "update xyy_wms_bill_rukushangjia set xiaohuizhuangtai ="
				+ xiaohuizhuangtai + " where dingdanbianhao = ? and billid = ?";
		// 回写不合格单销毁状态
		Db.update(sql, dingdanbianhao, ID);
		LOGGER.info("不合格单销毁状态回写成功，订单编号：" + dingdanbianhao);
	}
}
