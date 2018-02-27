package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillBuHeGePinPreHandler implements PreSaveEventListener {

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

		for (int i = 0; i < bodys.size(); i++) {
			BigDecimal shuliang = bodys.get(i).getBigDecimal("shuliang");
			String shijihuowei = bodys.get(i).getStr("shijihuowei");
			String buhegemxID = bodys.get(i).getStr("buhegemxID");
			Integer yanshoupingding = bodys.get(i).getInt("yanshoupingding");
			Integer pingdingyuanyin = bodys.get(i).getInt("pingdingyuanyin");
			Integer xiaohuifangshi = bodys.get(i).getInt("xiaohuifangshi");

			// 不允许重复保存
			String dingdanbianhao = head.getStr("dingdanbianhao");
			String findBuhegeBillDtlID = Db
					.queryStr(
							"select BillDtlID from xyy_wms_bill_buhegepinxiaohui_details where BillDtlID =? and xiaohuimxzhuangtai =? ",
							buhegemxID, 1);
			if (findBuhegeBillDtlID != null
					&& findBuhegeBillDtlID != "") {
				event.getContext().addError(dingdanbianhao, "不合格销毁单不能重复保存");
				return;
			}

			// 判断非空
			if (shijihuowei == null || shijihuowei.equals("") || shuliang == null||
					xiaohuifangshi == null||xiaohuifangshi==0) {
				event.getContext().addError(
						bodys.get(i).getStr("shangpinbianhao"),
						"第" + (i + 1)
								+ "行的实际货位、数量、销毁方式不能为空。");
				return;
			}

			// 判断数量是否为负数
			if (shuliang.compareTo(BigDecimal.ZERO) <= 0){
				event.getContext().addError(
						bodys.get(i).getStr("shangpinbianhao"),
						"第" + (i + 1) + "行的数量不能为零或者负数。");
				return;
			}

			// 销毁单的数量不能大于不合格品的数量
			Record record = Db.findFirst
					("select shuliang,xiaohuishuliang from xyy_wms_bill_rukushangjia_details where BillDtlID =?",
							buhegemxID);
			BigDecimal buhegeshuliang = record.get("shuliang");
			BigDecimal xiaohuishuliang = record.get("xiaohuishuliang");
			BigDecimal newxiaohuishu = xiaohuishuliang.add(shuliang);
			if (shuliang.compareTo(buhegeshuliang) == 1 || newxiaohuishu.compareTo(buhegeshuliang)==1) {
				event.getContext().addError(
						bodys.get(i).getStr("shangpinbianhao"),
						"第" + (i + 1) + "行的销毁数量不能大于不合格数量。");
				return;
			}

			/*//验收评定为待验和不合格时，评定原因不能为空
			if(yanshoupingding!=2 && pingdingyuanyin==0) {
				event.getContext().addError(
						bodys.get(i).getStr("shangpinbianhao"),
						"第" + (i + 1) + "行的质检结论应该为不合格，评定原因不能为空。");
				return;
			}*/
		}
	}
}
