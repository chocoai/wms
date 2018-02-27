package com.xyy.bill.event.listener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.erp.platform.system.model.User;

/**
 * 损益单自动下推
 * @author jhipster
 *
 */
public class SYKPAutoZXPostSaveListener implements PostSaveEventListener{

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		User user = (User)context.get("user");
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		int status = head.getInt("status");
		if (status != 20) {
			return;
		}
		
		//自动生成执行单
		List<Record> records = new ArrayList<>();
		List<DataTableInstance> list = dsi.getBodyDataTableInstance();
		for (DataTableInstance dataTableInstance : list) {
			records.addAll(dataTableInstance.getRecords());
		}
		
		if (records.size()>0) {
			Db.save("xyy_erp_bill_sunyizhixingdan", head);
			Db.batchSave("xyy_erp_bill_sunyizhixingdan_details", records, 50);
		}
		
		//商品批号库存处理
		StringBuffer sb = new StringBuffer();
		sb.append("update xyy_erp_bill_shangpinpihaokucun a set a.kucunshuliang = a.kucunshuliang-?");
		if (user != null) {
			sb.append(" ,a.updator=").append(user.getId()).append(" ,a.updatorName='").append(user.getRealName()).append("'");
		}
		sb.append(" ,a.updateTime='").append(new Timestamp(System.currentTimeMillis())).append("' where   a.kucunzhuangtai=? and a.pihaoId=? and a.shangpinId = ?");
		Object[][] params = new Object[records.size()][4];
		
		//处理商品总库存
		StringBuffer sbKC = new StringBuffer();
		sbKC.append("update xyy_erp_bill_shangpinzongkucun set kucunshuliang = kucunshuliang - ?").append(",kexiaoshuliang=kexiaoshuliang-?,bkxsl=bkxsl-?")
		.append(",kchsje=kucunshuliang * chengbendanjia").append(" where shangpinId=?");
		
		Object[][] _arr = new Object[records.size()][4];
		int row = 0;
		//处理商品账页集合
		List<Record> insertRecord = new ArrayList<>();
		for (Record record : records) {
			//商品批号库存处理值集合
			params[row][0] = record.get("shuliang");
			params[row][1] = record.get("kucunzhuangtai");
			params[row][2] = record.get("pihaoId");
			params[row][3] = record.get("shangpinID");
			
			//处理商品总库存值集合
			_arr[row][0] = record.get("shuliang");
			if (record.getInt("kucunzhuangtai") == 1) {
				//合格
				_arr[row][1] = record.get("shuliang");
				_arr[row][2] = 0;
			}else{
				_arr[row][1] = 0;
				_arr[row][2] = record.get("shuliang");
			}
			_arr[row][3] = record.get("shangpinID");
			row ++;
		}
		
		//更新批号库存
		Db.batch(sb.toString(), params, 30);
		//更新商品总库存
		Db.batch(sbKC.toString(), _arr, 30);
		
		//处理商品账页
		for (Record record : records) {
			Record r = new Record();
			r.set("danjubianhao", head.get("danjubianhao"));
			r.set("kaipiaoriqi", head.get("kaipiaoriqi"));
			r.set("kaipiaoyuan", head.get("kaipiaoyuan"));
			r.set("zhaiyao", "损溢执行单");
			r.set("shangpinId", record.get("shangpinID"));
			r.set("shangpinshuilv", record.get("shuilv"));
			r.set("rukushuliang", 0);
			r.set("rkhsj", 0);
			r.set("rkhsje", 0);
			r.set("chukushuliang", record.get("shuliang"));
			r.set("ckhsj", record.get("hanshuijia"));	
			r.set("ckhsje", record.get("hanshuijine"));	
			
			Record r2 = Db.findFirst("select * from xyy_erp_bill_shangpinzongkucun where shangpinId=?", record.getStr("shangpinID"));
			if (r2 != null) {
				r.set("kucunshuliang", r2.get("kucunshuliang"));	
				r.set("kchsj", r2.get("chengbendanjia"));	
				r.set("kchsje", r2.get("kchsje"));	
			}else{
				r.set("kucunshuliang", 0);	
				r.set("kchsj", 0);	
				r.set("kchsje", 0);	
			}
			
			r.set("pihaoId", record.get("pihaoId"));
			r.set("createTime", new Timestamp(System.currentTimeMillis()));
			r.set("orgId", "E0EHOUA9KDE");
			if (user != null) {
				r.set("creator", user.getId());
				r.set("creatorName", user.getRealName());
			}
			insertRecord.add(r);
		}
		
		//处理商品账页
		Db.batchSave("xyy_erp_bill_shangpinzhangye", insertRecord, 30);
	}

}
