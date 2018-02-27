package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.util.SequenceBuilder;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.util.StringUtil;

public class BillXTSHPreHandler implements PreSaveEventListener {


	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey())) {
			return;

		}
		Date nowDate = new Date();

		
		//格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		// 单据头数据
		Record record = getHead(dsi);
		String yewubianhao = "XT"+SequenceBuilder.nextSequence("orderNo_"+TimeUtil.format(new Date(), "yyyyMMdd"), "", 10);
		record.set("yewubianhao", yewubianhao);
		Date qiyunshijian = record.getDate("qiyunshijian");
		
		if(sdf.format(nowDate).equals(sdf.format(qiyunshijian))){
			Calendar now = Calendar.getInstance();  
			now.set(Calendar.DATE, now.get(Calendar.DATE) - 3);  
			record.set("qiyunshijian",now.getTime());  
		}
		
		// 单据体数据
		List<Record> bodys = getBody(dsi);
		
		// 验收明细单的商品数量不能大于收货明细单的收货数量
		List<Record> caigouDetails = Db.find("select tuihuoshuliang,wmsshuliang,BillDtlID from xyy_wms_bill_xiaoshoutuihuidan_details where BillID =?",record.getStr("rukudanID"));
		for (int j = 0; j < caigouDetails.size(); j++) {
			BigDecimal caigoushuliang = caigouDetails.get(j).get("tuihuoshuliang");
			BigDecimal newrkshoushu = caigouDetails.get(j).get("wmsshuliang"); // 已经入库数量
//			BigDecimal syshuliang = caigoushuliang.subtract(newrkshoushu); // 还能入库多少
					
			String BillDtlID = caigouDetails.get(j).getStr("BillDtlID");
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < bodys.size(); i++) {
				String rongqibianhao = bodys.get(i).get("rongqibianhao");
				if(rongqibianhao.length()<=0){
					bodys.get(i).set("rongqibianhao","020");
				}
				bodys.get(i).set("yewubianhao", yewubianhao);
				BigDecimal baozhuangshuliang = bodys.get(i).getBigDecimal("baozhuangshuliang");
				BigDecimal zhengjianshu = bodys.get(i).getBigDecimal("zhengjianshu");
				BigDecimal lingsanshu = bodys.get(i).getBigDecimal("lingsanshu");
				BigDecimal shuliang = bodys.get(i).getBigDecimal("shuliang");
				String pihao = bodys.get(i).getStr("pihao");
				String rukuID = bodys.get(i).getStr("rukuID");
				
				if(bodys.get(i).get("shengchanriqi").equals("")||bodys.get(i).get("youxiaoqizhi").equals("")){
					event.getContext().addError(bodys.get(i).getStr("shangpinbianhao"), "第" + (i + 1) + "行的日期不能为空！！。");
					return;
				}else{
					Date shengchanriqi=bodys.get(i).get("shengchanriqi");
					Date youxiaoqizhi = bodys.get(i).get("youxiaoqizhi");
					if(shengchanriqi.after(nowDate) || youxiaoqizhi.before(nowDate) || youxiaoqizhi.before(shengchanriqi)){
	        			event.getContext().addError(bodys.get(i).getStr("shangpinbianhao"), "第" + (i + 1) + "行的日期不符合要求！！！！。");
	    				return;
					}
				}
				// 同一条采购订单明细拆分，拆分明细数量之和，不能大于采购数
				if (BillDtlID.equals(rukuID)) {
					newrkshoushu = newrkshoushu.add(shuliang);
					list.add(i+1);
	
					// 每一条条采购订单明西的数量不能大于采购数
					if (shuliang.compareTo(newrkshoushu) == 1) {
						event.getContext().addError(
								bodys.get(i).getStr("shangpinbianhao"),
								"第" + (i + 1) + "行的商品数量不能大于销退数量。");
						return;
					}
	
	
					
					// 判断非空
					if(pihao.equals("")){
						event.getContext().addError(bodys.get(i).getStr("shangpinbianhao"),
								"第"+ (i + 1)+ "行的批号不能为空。");
							return;
					}
					
					// 判断非空
					if (pihao == null || pihao == ""
							|| baozhuangshuliang == null
							|| zhengjianshu == null || lingsanshu == null
							|| shuliang == null
							) {
						event.getContext().addError(bodys.get(i).getStr("shangpinbianhao"),
							"第"+ (i + 1)+ "行的批号、整件数、零散数、抽检数量、数量、验收评定、评定原因不能为空。");
						return;
					}
	
					// 判断数量是否为负数
					if (zhengjianshu.compareTo(BigDecimal.ZERO) == -1
							|| baozhuangshuliang.compareTo(BigDecimal.ZERO) == -1
							|| lingsanshu.compareTo(BigDecimal.ZERO) == -1
							|| shuliang.compareTo(BigDecimal.ZERO) == -1) {
						event.getContext().addError(bodys.get(i).getStr("shangpinbianhao"),
								"第" + (i + 1)
										+ "行的整件数、零散数、数量、抽检数量、包装数量不能为负数。");
						return;
					}
				}
				// 同一条收货明细拆分验收，拆分明细数量之和，不能大于收货数
				if (newrkshoushu.compareTo(caigoushuliang) == 1) {
					event.getContext().addError(BillDtlID,
							"进行拆分的第" + list.toString() + "行的商品数量之和不能大于收货数量。");
					return;
				}
				
			}
			
		}
	}
}
