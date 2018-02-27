package com.xyy.bill.event.listener;

import java.sql.Timestamp;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.StringUtil;

/**
 * 财务系统往来账信息
 * @author 
 *
 */
public class CWZYStatusChangedListener implements PostSaveEventListener {

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		User user = (User)context.get("user");
		if (dsi == null) {
			return;
		}
		DataTableInstance head = dsi.getHeadDataTableInstance();
		String key = dsi.getFullKey();
		if (StringUtil.isEmpty(key)) {
			return;
		}
		Record record = head.getRecords().get(0);
		int status = record.getInt("status");
		if (status != 20) {
			return;
		}
		Record zhangye = null;//最近一条账页信息
		Record r = new Record();//新的账页信息
		if (key.equals("Bill_caigoufukuandan")) {
			//采购付款
			Record gysRecord = Db.findFirst("select * from xyy_erp_dic_gongyingshangjibenxinxi where gysbh=?",record.getStr("gysbh"));
			zhangye = Db.findFirst("select * from xyy_erp_bill_wanglaizhangye where wlId = ? order by createTime desc limit 1",gysRecord.getStr("suppliersid"));
			r.set("zhaiyao", "采购付款单");
			r.set("wlId", gysRecord.getStr("suppliersid"));
		}else if (key.equals("Bill_xiaoshoushoukuandan")) {
			//销售收款
			Record kehuRecord = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where kehubianhao=?",record.getStr("kehubianhao"));
			zhangye = Db.findFirst("select * from xyy_erp_bill_wanglaizhangye where wlId = ? order by createTime desc limit 1",kehuRecord.getStr("clientid"));
			r.set("zhaiyao", "销售收款单");
			r.set("wlId", kehuRecord.getStr("clientid"));
		}else if (key.equals("Bill_caigoufukuanchonghong")) {
			//采购付款冲红单
			Record gysRecord = Db.findFirst("select * from xyy_erp_dic_gongyingshangjibenxinxi where gysbh=?",record.getStr("gysbh"));
			zhangye = Db.findFirst("select * from xyy_erp_bill_wanglaizhangye where wlId = ? order by createTime desc limit 1",gysRecord.getStr("suppliersid"));
			r.set("zhaiyao", "采购付款冲红单");
			r.set("wlId", gysRecord.getStr("suppliersid"));
		}else if(key.equals("Bill_xiaoshoushoukuanchonghong")){
			//销售收款冲红单
			Record kehuRecord = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where kehubianhao=?",record.getStr("khbh"));
			zhangye = Db.findFirst("select * from xyy_erp_bill_wanglaizhangye where wlId = ? order by createTime desc limit 1",kehuRecord.getStr("clientid"));
			r.set("zhaiyao", "销售收款冲红单");
			r.set("wlId", kehuRecord.getStr("clientid"));
		}
		
		r.set("orgId", "E0EHOUA9KDE");
		r.set("danjubianhao", record.get("danjubianhao"));
		r.set("kaipiaoriqi", record.get("kaipiaoriqi"));
		r.set("kaipiaoyuan", record.get("kaipiaoyuan"));
		r.set("jiefang", 0);
		r.set("daifang", record.get("jszje"));
		if (zhangye == null) {
			r.set("yue", record.getBigDecimal("jszje").negate());
		}else{
			r.set("yue", zhangye.getBigDecimal("yue").subtract(record.getBigDecimal("jszje")));
		}
		r.set("createTime", new Timestamp(System.currentTimeMillis()));
		if (user != null) {
			r.set("creator", user.getId());
			r.set("creatorName", user.getRealName());
		}
		//生成往来账页
		Db.save("xyy_erp_bill_wanglaizhangye", r);
	}

}
