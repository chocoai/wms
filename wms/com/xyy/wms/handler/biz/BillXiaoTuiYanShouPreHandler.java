package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillXiaoTuiYanShouPreHandler implements PreSaveEventListener {

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
			return;
		// 单据头数据
		Record head = getHead(dsi);
		// 单据体数据
		List<Record> bodys = getBody(dsi);

		// 验收明细单的商品数量不能大于收货明细单的收货数量
		List<Record> shouhuoDetails = Db
				.find("select shuliang,yanshoushuliang,BillDtlID from xyy_wms_bill_xiaotuishouhuo_details where BillID =?",
						head.getStr("rukudanID"));
		for (int j = 0; j < shouhuoDetails.size(); j++) {
			BigDecimal shouhuoshuliang = shouhuoDetails.get(j).get("shuliang");
			BigDecimal newyanshoushu = shouhuoDetails.get(j).get(
					"yanshoushuliang"); // 验收数量的初始值为收货明细表的验收数
			String BillDtlID = shouhuoDetails.get(j).getStr("BillDtlID");
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < bodys.size(); i++) {
				BigDecimal baozhuangshuliang = bodys.get(i).getBigDecimal(
						"baozhuangshuliang");
				BigDecimal zhengjianshu = bodys.get(i).getBigDecimal(
						"zhengjianshu");
				BigDecimal lingsanshu = bodys.get(i)
						.getBigDecimal("lingsanshu");
				BigDecimal choujianshuliang = bodys.get(i).getBigDecimal(
						"choujianshuliang");
				BigDecimal shuliang = bodys.get(i).getBigDecimal("shuliang");

				String pihao = bodys.get(i).getStr("pihao");
				String rukuID = bodys.get(i).getStr("rukuID");
				Integer yanshoupingding = bodys.get(i)
						.getInt("yanshoupingding");
				Integer pingdingyuanyin = bodys.get(i)
						.getInt("pingdingyuanyin");
				Date shengchanriqi = bodys.get(i).getDate("shengchanriqi");
				Date youxiaoqizhi = bodys.get(i).getDate("youxiaoqizhi");
				// 同一条收货明细拆分验收，拆分明细数量之和，不能大于收货数
				if (BillDtlID.equals(rukuID)) {
					newyanshoushu = newyanshoushu.add(shuliang);
					list.add(i+1);

					// 每一条验收明细的数量不能大于收货数量
					if (shuliang.compareTo(shouhuoshuliang) == 1) {
						event.getContext().addError(
								bodys.get(i).getStr("shangpinbianhao"),
								"第" + (i + 1) + "行的商品数量不能大于收货数量。");
						return;
					}

					// 不允许重复保存
					String dingdanbianhao = head.getStr("dingdanbianhao");
					String findYanshoudanBillDtlID = Db
							.queryStr(
									"select BillDtlID from xyy_wms_bill_xiaotuishouhuo_details where BillDtlID =? and yanshouzhuangtai =? ",
									rukuID, 1);
					if (findYanshoudanBillDtlID != null
							&& findYanshoudanBillDtlID != "") {
						event.getContext()
								.addError(dingdanbianhao, "验收单不能重复保存");
						return;
					}

					// 判断非空
					if (pihao == null || pihao == ""
							|| baozhuangshuliang == null
							|| zhengjianshu == null || lingsanshu == null
							|| choujianshuliang == null || shuliang == null
							|| yanshoupingding == null
							|| pingdingyuanyin == null) {
						event.getContext()
								.addError(
										bodys.get(i).getStr("shangpinbianhao"),
										"第"
												+ (i + 1)
												+ "行的批号、包装数量、整件数、零散数、抽检数量、数量、验收评定、评定原因不能为空。");
						return;
					}

					// 判断数量是否为负数
					if (zhengjianshu.compareTo(BigDecimal.ZERO) == -1
							|| baozhuangshuliang.compareTo(BigDecimal.ZERO) == -1
							|| lingsanshu.compareTo(BigDecimal.ZERO) == -1
							|| choujianshuliang.compareTo(BigDecimal.ZERO) == -1
							|| shuliang.compareTo(BigDecimal.ZERO) == -1) {
						event.getContext().addError(
								bodys.get(i).getStr("shangpinbianhao"),
								"第" + (i + 1)
										+ "行的整件数、零散数、数量、抽检数量、包装数量不能为负数。");
						return;
					}

					// 抽检数量/拒收数量不能大于收货数量
					if (choujianshuliang.compareTo(shuliang) == 1) {
						event.getContext().addError(
								bodys.get(i).getStr("shangpinbianhao"),
								"第" + (i + 1) + "行的抽检数量不能大于收货数量。");
						return;
					}

					// 收货数量、包装数量不能为零
					if (shuliang.compareTo(BigDecimal.ZERO) == 0
							|| baozhuangshuliang.compareTo(BigDecimal.ZERO) == 0
							|| choujianshuliang.compareTo(BigDecimal.ZERO) == 0) {
						event.getContext().addError(
								bodys.get(i).getStr("shangpinbianhao"),
								"第" + (i + 1) + "行的包装数量、收货数量、抽检数量不能为零。");
						return;
					}

					// 生产日期不能大于有效期至
					if (shengchanriqi.compareTo(youxiaoqizhi) > 0) {
						event.getContext().addError(
								bodys.get(i).getStr("shangpinbianhao"),
								"第" + (i + 1) + "行的生产日期不能大于有效期至。");
						return;
					}

					// 验收评定为待验和不合格时，评定原因不能为空
					if (yanshoupingding != 1 && pingdingyuanyin == 0) {
						event.getContext().addError(
								bodys.get(i).getStr("shangpinbianhao"),
								"第" + (i + 1) + "行的评定原因不能为空。");
						return;
					}
				}
				// 同一条收货明细拆分验收，拆分明细数量之和，不能大于收货数
				if (newyanshoushu.compareTo(shouhuoshuliang) == 1) {
					event.getContext().addError(BillDtlID,
							"进行拆分的第" + list.toString() + "行的商品数量之和不能大于收货数量。");
					return;
				}

			}
		}

	}
}
