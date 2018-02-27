package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

public class BillShouHuoCuoWuPreHandler implements PreSaveEventListener {

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey())) {
			return;

		}
		Date nowDate = new Date();
		
		Record record = getHead(dsi);
		// 单据体数据
		List<Record> body = getBody(dsi);
		for (Record r : body) {
			String shangjiaId = r.getStr("shangjiaId");
			//修改后的数量
        	BigDecimal shuliang = r.getBigDecimal("shuliang");
        	if(shuliang.compareTo(BigDecimal.ZERO)==0){
        		event.getContext().addError("数量不能为空或者是0！", "数量不能为空或者是0！");
				return;
        	}
			String sql = "select * from xyy_wms_bill_rukushangjiadan_details where BillDtlID = ?";
			Record re = Db.findFirst(sql, shangjiaId);
			BigDecimal sjShuliang =  re.get("shuliang");
			if(shuliang.compareTo(sjShuliang)==1){
				event.getContext().addError("数量不能大于上架数量！", "数量不能大于上架数量！");
				return;
			}
			String pihao = r.getStr("pihao");
			if(pihao == null || pihao.length() == 0){
				event.getContext().addError("批号不能为空", "批号不能为空");
				return;
			}
			
        	java.util.Date shengchanriqi = r.getDate("shengchanriqi");
        	java.util.Date youxiaoqizhi = r.getDate("youxiaoqizhi");
        	
        	if(shengchanriqi != null && !"".equals(shengchanriqi)){
        		if(shengchanriqi.after(nowDate) || youxiaoqizhi.before(nowDate) || youxiaoqizhi.before(shengchanriqi)){
        			event.getContext().addError("日期不符合要求！", "日期不符合要求！！！");
    				return;
        		}
        	}else{
        		event.getContext().addError("日期不能为空！", "日期不能为空！！");
				return;
        	}
        	if(youxiaoqizhi == null && "".equals(youxiaoqizhi)){
        		event.getContext().addError("日期不能为空！", "日期不能为空！！");
				return;
        	}
        	
		}

		
	}

}
