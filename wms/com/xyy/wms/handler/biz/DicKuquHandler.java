package com.xyy.wms.handler.biz;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.ErrorMsg;

public class DicKuquHandler implements PreSaveEventListener {

	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		if (dsi == null) {
			return;
		}
		List<Record> records = dsi.getHeadDataTableInstance().getRecords();
		if (records.size() != 1) {
			return;
		}
		Record record = records.get(0);
		String id = record.get("ID");
		// 新增保存校验
		// 库区编号进行校验
		String kuqubianhao = record.get("kuqubianhao");
		if (kuqubianhao == null) {
			ErrorMsg Msg = new ErrorMsg();
			Msg.setMessage("库区编号为空");
			event.getContext().addError(Msg);
			return;
		} else {
			StringBuffer sb = new StringBuffer("SELECT * from xyy_wms_dic_kuqujibenxinxi WHERE kuqubianhao =?");
			List<Record> rs = new ArrayList<Record>();
			if (StringUtils.isNotEmpty(id)) {
				sb.append(" and ID !=?");
				rs = Db.find(sb.toString(), kuqubianhao, id);
			} else {
				rs = Db.find(sb.toString(), kuqubianhao);
			}

			if (rs.size() > 0) {
				ErrorMsg Msg = new ErrorMsg();
				Msg.setMessage("库区编号已被使用,请重新输入！");
				event.getContext().addError(Msg);
				return;
			}

		}

			// 初始化库区数据;
			initKuqu(record);
	}

	// 36库区可使用 37 库区正在使用中
	private void initKuqu(Record record) {
		record.set("zhuangtai", 36);
	}

}
