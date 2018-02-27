package com.xyy.bill.event.listener;

import java.sql.Timestamp;
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
 * 销售收款对账单回写迁移表
 * @author 
 *
 */
public class CWSKDZPostSaveListener implements PostSaveEventListener {

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
		List<DataTableInstance> dti = dsi.getBodyDataTableInstance();
		Object[][] params = new Object[dti.size()][4];
		String sql = "update xyy_erp_bill_migratexiaoxiangfapiao set sffkyy = 1,updateTime=?,updator=?,updatorName=? where danjubianhao=?";
		
		int row = 0;
		for (DataTableInstance dataTableInstance : dti) {
			List<Record> records = dataTableInstance.getRecords();
			for (Record record : records) {
				//回写销售迁移表已引用
				params[row][0] = new Timestamp(System.currentTimeMillis());
				if (user != null) {
					params[row][1] = user.getId();
					params[row][2] = user.getRealName();
				}else{
					params[row][1] = "";
					params[row][2] = "";
				}
				params[row][3] = record.get("pipeidianshangbianhao");
				row++;
			}
		}
		
		Db.batch(sql, params, 50);
	}

}
