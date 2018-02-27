package com.xyy.bill.event.listener;

import java.sql.Timestamp;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.StringUtil;

/**
 * 销售收款冲红单回写迁移表信息
 * @author 
 *
 */
public class CWXSCHPostSaveListener implements PostSaveEventListener{

	/**
	 */
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
		
		String sourceID = head.getStr("SourceID");
		if (StringUtil.isEmpty(sourceID)) {
			return;
		}
		
		//根据原单ID查询出需要回写的迁移表ID
		List<Record> list = Db.find("select * from xyy_erp_bill_xiaoshoushoukuandan_details2 where BillID=?", sourceID);
		if (list.size() == 0) {
			return;
		}
		String OID = list.get(0).getStr("SourceID");
		String tableName = list.get(0).getStr("SrcTableName");
		//回写迁移表
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE ").append(tableName).append(" set yjsje = yjsje+?,").append(" wjsje=wjsje-?,");
		if (user != null) {
			sb.append(" updator='").append(user.getId()).append("', updatorName='").append(user.getRealName()).append("', ");
		}
		sb.append(" jiesuanzhuangtai = 0,sffkyy = 0,").append("updateTime = '").append(new Timestamp(System.currentTimeMillis())).append("' where OID=?");
		Db.update(sb.toString(), head.get("jszje"),head.get("jszje"),OID);
	}

}
