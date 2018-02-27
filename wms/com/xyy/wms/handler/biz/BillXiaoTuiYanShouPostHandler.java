package com.xyy.wms.handler.biz;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.util.PullWMSDataUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.util.StringUtil;

import org.apache.log4j.Logger;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillXiaoTuiYanShouPostHandler implements PostSaveEventListener {

	private static Logger LOGGER = Logger
			.getLogger(BillXiaoTuiYanShouPostHandler.class);

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
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		count.put("yanshou", 0); // 验收明细计数
		count.put("jushou", 0); // 拒收明细计数
		count.put("buhege", 0); // 不合格明细计数
		count.put("shangjia", 0); // 上架明细计数
		count.put("fujian", 0); // 质量复检明细计数
		String billID1 = UUIDUtil.newUUID(); // 拒收
		String billID2 = UUIDUtil.newUUID(); // 不合格
		String billID3 = UUIDUtil.newUUID(); // 上架单
		String billID4 = UUIDUtil.newUUID(); // 复检单
		String dingdanbianhao = head.getStr("dingdanbianhao"); // 订单编号
		String rukudanID = head.getStr("rukudanID"); // 入库单ID
		
		BillRuKuYanShouPostHandler billRuKuYanShouPostHandler = new BillRuKuYanShouPostHandler();
		// 生成上架单汇总
		billRuKuYanShouPostHandler.newshangjiadan(head, billID3);

		for (Record body : bodys) {
			BigDecimal shuliang = body.get("shuliang"); // 商品总数
			String shangpinbianhao = body.getStr("shangpinbianhao"); // 商品编号
			String pihao = body.getStr("pihao"); // 商品批号
			String rukuID = body.getStr("rukuID"); // 关联入库收货明细单
			int yanshoupingding = body.getInt("yanshoupingding"); // 验收评定（0-待处理，1-合格，2-不合格，3-拒收）
			
			// 商品对象
			String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
			Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
			if(shangpinObj!=null) {
				String goodsId = shangpinObj.getStr("goodsid");
				// 如果商品为新批号时，更新商品批号表xyy_wms_dic_shangpinpihao
				boolean falg = billRuKuYanShouPostHandler.getShangPinPHById(pihao, shangpinbianhao);
				if (!falg) {
					Record sppihao = new Record();

					String newpihaoId = "PH" + PullWMSDataUtil.orderno2();// 批号id
					sppihao.set("ID", UUIDUtil.newUUID());
					sppihao.set("shengchanriqi", body.get("shengchanriqi")); // 生产日期
					sppihao.set("youxiaoqizhi", body.get("youxiaoqizhi")); // 有效期至
					sppihao.set("pihao", pihao); // 批号
					sppihao.set("goodsId", goodsId); // 商品id
					sppihao.set("pihaoId", newpihaoId);
					Db.save("xyy_wms_dic_shangpinpihao", sppihao);
					LOGGER.info("更新商品批号：" + pihao);
					
					// 之前的入库单商品批号
					/*String prePihao = Db
							.queryStr(
									"select pihao from xyy_wms_bill_xiaotuishouhuo_details where shangpinbianhao = ? and BillDtlID = ?",
									shangpinbianhao, rukuID);
					String prePihaoID = Db
							.queryStr(
									"SELECT pihaoId FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId=? limit 1",
									prePihao, goodsId);

					// 更新商品批号库存的入库预占xyy_wms_bill_shangpinpihaokucun
					updatePihaoKuCun(goodsId, newpihaoId, shuliang, prePihaoID);*/
				}
			}

			// 生成据收单明细
			if (yanshoupingding == 3) {
				billRuKuYanShouPostHandler.jushoudanDetails(head, body, billID1, count);
			}

			// 生成不合格品单明细
			if (yanshoupingding == 2) {
				billRuKuYanShouPostHandler.buhegeDetails(head, body, billID2, shuliang, count);
			}

			// 生成上架单明细
			billRuKuYanShouPostHandler.shangjiadanDetails(head, body, billID1, billID2, billID3, billID4,
					count);

			// 回写入库收货明细单的验收数量
			updateRukushuliang(dingdanbianhao, shangpinbianhao, shuliang,
					rukuID, rukudanID);

			// 回写收货明细的验收状态（0未验收，1-已验收）
			Record shuliangCompare = Db
					.findFirst(
							"select shuliang,yanshoushuliang from xyy_wms_bill_xiaotuishouhuo_details where shangpinbianhao = ? and BillDtlID =?",
							shangpinbianhao, rukuID);
			if (shuliangCompare != null) {
				BigDecimal shouhuoshuliang = shuliangCompare.get("shuliang");
				BigDecimal shouhuoyanshoushuliang = shuliangCompare
						.get("yanshoushuliang");
				if (shouhuoshuliang.compareTo(shouhuoyanshoushuliang) == 0) {
					Db.update(
							"update xyy_wms_bill_xiaotuishouhuo_details set yanshouzhuangtai = ? where shangpinbianhao = ? and BillDtlID =?",
							1, shangpinbianhao, rukuID);
				}
			}

			Integer countYanshou = (Integer) count.get("yanshou") + 1;
			count.put("yanshou", countYanshou);// 验收明细计数

			// 更新商品总库存的入库预占
			//updateZongKuCun(goodsId, head, body);

			// 更新商品批号库存的入库预占 
			//updateKuCun(goodsId, head, body);
		}

		// 生成据收单汇总
		if (count.get("jushou") > 0) {
			billRuKuYanShouPostHandler.jushoudandan(head, billID1);
		}

		// 生成不合格品单汇总
		if (count.get("buhege") > 0) {
			billRuKuYanShouPostHandler.buhegepin(head, billID2);
		}

		// 生成质量复检单汇总
		if (count.get("fujian") > 0) {
			billRuKuYanShouPostHandler.zhiliangfujian(head, billID4);
		}

		// 回写入库收货单的验收状态，0-未验收，1-部分验收，2-全部验收
		long queryShuohuoDetails = Db
				.queryLong(
						// 每单明细数
						"select count(*) from xyy_wms_bill_xiaotuishouhuo a ,"
								+ "xyy_wms_bill_xiaotuishouhuo_details b "
								+ "where a.billID =b.billID and a.dingdanbianhao = ? and a.billid = ?",
						dingdanbianhao, rukudanID);
		long queryShuohuoIsYanshou = Db
				.queryLong(
						// 已经验收的单数
						"select count(*) from xyy_wms_bill_xiaotuishouhuo a ,"
								+ "xyy_wms_bill_xiaotuishouhuo_details b "
								+ "where a.billID =b.billID and a.dingdanbianhao = ? and a.billid = ? and b.yanshouzhuangtai=1",
						dingdanbianhao, rukudanID);
		if (queryShuohuoIsYanshou == queryShuohuoDetails) {
			updateRukuZhuangtai(dingdanbianhao, 2, rukudanID);
		} else if (queryShuohuoIsYanshou < queryShuohuoDetails
				&& queryShuohuoIsYanshou > 0) {
			updateRukuZhuangtai(dingdanbianhao, 1, rukudanID);
		} else if (queryShuohuoIsYanshou == 0) {
			updateRukuZhuangtai(dingdanbianhao, 0, rukudanID);
		} else {
			event.getContext().addError(null, "验收单数量异常");
			return;
		}
	}
	
	/**
	 * 回写入库收货汇总单验收状态
	 * 
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public void updateRukuZhuangtai(String dingdanbianhao,
			int yanshouzhuangtai, String rukudanID) {
		String sql = "update xyy_wms_bill_xiaotuishouhuo set yanshouzhuangtai ="
				+ yanshouzhuangtai + " where dingdanbianhao = ? and billid = ?";
		// 回写收货明细单验收数量
		Db.update(sql, dingdanbianhao, rukudanID);
		LOGGER.info("入库收货单验收状态回写成功，订单编号：" + dingdanbianhao);
	}
	
	/**
	 * 回写入库收货明细单验收数量
	 * 
	 * @param BillID
	 * @param rukushuliang
	 * @return
	 */
	public void updateRukushuliang(String dingdanbianhao,
			String shangpinbianhao, BigDecimal shuliang, String rukuID,
			String rukudanID) {
		String sql3 = "update xyy_wms_bill_xiaotuishouhuo_details set yanshoushuliang = yanshoushuliang +"
				+ shuliang
				+ " where billID = ? and shangpinbianhao = ? and BillDtlID = ?";
		// 回写收货明细单验收数量
		Db.update(sql3, rukudanID, shangpinbianhao, rukuID);
		LOGGER.info("入库收货单验收数量回写成功，商品编号：" + shangpinbianhao);
		// 当【入库收货单】所有明细的“收货数量”=“已完成数量”时，回写【入库收货单】汇总的状态为“已完成”
	}

}
