package com.xyy.wms.handler.biz;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.ErrorMsg;
import com.xyy.erp.platform.common.tools.StringUtil;
public class DicJiHuoQuHandler implements PreSaveEventListener {

	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext()
				.get("$model");
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
		if (StringUtil.isEmpty(id)) {
			// 集货位编号进行校验
			String jihuoweibianhao = record.get("jihuoweibianhao");
			if (jihuoweibianhao == null) {
				ErrorMsg Msg = new ErrorMsg();
				Msg.setMessage("集货位编号为空");
				event.getContext().addError(Msg);
				return;
			} else {
				int jihuoweiNum = jihuoweibianhao(jihuoweibianhao);
				if (jihuoweiNum > 0) {
					ErrorMsg Msg = new ErrorMsg();
					Msg.setMessage("集货位编号已被使用,请重新输入！");
					event.getContext().addError(Msg);
					return;
				}
			}
		}
		// 编辑保存校验
		if (!StringUtil.isEmpty(id)) {
			// 集货位编号进行校验
			String jihuoweibianhao = record.get("jihuoweibianhao");
			if (jihuoweibianhao == null) {
				ErrorMsg Msg = new ErrorMsg();
				Msg.setMessage("集货位编号为空");
				event.getContext().addError(Msg);
				return;
			} else {
				Record re = Db.findFirst(
						"SELECT * from xyy_wms_dic_jihuowei WHERE id=?", id);
				// 如果编辑时更改了货位编号，则判断编号是否已经被占用
				if (!jihuoweibianhao.equals(re.get("jihuoweibianhao"))) {
					int jihuoweiNum = jihuoweibianhao(jihuoweibianhao);
					if (jihuoweiNum > 0) {
						ErrorMsg Msg = new ErrorMsg();
						Msg.setMessage("货位编号已被使用,请重新输入！");
						event.getContext().addError(Msg);
						return;
					}
				}
			}
		}

		// 初始化集货位数据;
		initJiHuowei(record);
	}

	// 查询此货位编号的结果条数
	private int jihuoweibianhao(String jihuoweibianhao) {
		List<Record> rs = Db.find(
				"SELECT * from xyy_wms_dic_jihuowei WHERE jihuoweibianhao =?",
				jihuoweibianhao);
		int num = rs.size();
		return num;
	}

	// 36集货位可使用， 37 集货位正在使用中
	private void initJiHuowei(Record record) {
		record.set("zhuangtai", 36);
	}

}
