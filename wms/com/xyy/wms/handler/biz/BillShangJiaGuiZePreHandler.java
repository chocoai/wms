package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.ErrorMsg;
import com.xyy.util.StringUtil;


import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillShangJiaGuiZePreHandler implements PreSaveEventListener {

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
			return;
		// 单据头数据
		Record head = getHead(dsi);	
		//String kongjianxianzhitiaojian = head.getStr("kongjianxianzhitiaojian");
		String huoweixianzhitiaojian = head.getStr("huoweixianzhitiaojian");
		String kongjianxianzhitiaojian = head.getStr("kongjianxianzhitiaojian");
		
		//校验规则编号是否已经存在
		String guizebianhao = head.get("guizebianhao");
		String id = head.get("BillID");
		// 新增保存校验
		if (StringUtil.isEmpty(id)) {
			// 编号进行校验
			if (guizebianhao == null || StringUtil.isEmpty(guizebianhao)) {
				ErrorMsg Msg = new ErrorMsg();
				Msg.setMessage("规则编号为空");
				event.getContext().addError(Msg);
				return;
			} else {
				Boolean flag = checkGuiZe(guizebianhao);
				if (flag) {
					event.getContext().addError(guizebianhao, "该规则编号已经存在，请重新输入");
					return;
				}
			}
		}
		// 编辑保存校验
		if (!StringUtil.isEmpty(id)) {
			// 编号进行校验
			if (guizebianhao == null) {
				ErrorMsg Msg = new ErrorMsg();
				Msg.setMessage("规则编号为空");
				event.getContext().addError(Msg);
				return;
			} else {
				Record re = Db
						.findFirst(
								"SELECT * from xyy_wms_bill_sjgzwh WHERE BillID=?",
								id);
				// 如果编辑时更改了货位编号，则判断编号是否已经被占用
				if (!guizebianhao.equals(re.get("guizebianhao"))) {
					Boolean flag = checkGuiZe(guizebianhao);
					if (flag) {
						event.getContext().addError(guizebianhao, "该规则编号已经存在，请重新输入");
						return;
					}
				}
			}
		}
		
		
		
		//货位限制条件的保存过滤
		if(kongjianxianzhitiaojian.equals("")||kongjianxianzhitiaojian==null||huoweixianzhitiaojian.equals("")||huoweixianzhitiaojian==null) {
			ErrorMsg Msg = new ErrorMsg();
			Msg.setMessage("货位限制条件和空间限制条件不能为空。");
			event.getContext().addError(Msg);
			return;
		}
		if(huoweixianzhitiaojian.contains("1")) {
			head.set("huoweixianzhitiaojian","1");
			return;
		}
		if(huoweixianzhitiaojian.contains("3")) {
			if(huoweixianzhitiaojian.contains("5")) {
				head.set("huoweixianzhitiaojian", "3,5");
				return;
			}
			if(huoweixianzhitiaojian.contains("4")) {
				head.set("huoweixianzhitiaojian", "3,4");
				return;
			}
			head.set("huoweixianzhitiaojian", "3");
			return;
		}
		if(huoweixianzhitiaojian.contains("5")) {			
			if(huoweixianzhitiaojian.contains("2")) {
				head.set("huoweixianzhitiaojian", "2,5");
				return;
			}
			head.set("huoweixianzhitiaojian", "5");
			return;
		}
		
		
	}
	
	/**
	 * 校验规则编号是否存在
	 * 
	 * @param guizebianhao
	 * @return
	 */
	public boolean checkGuiZe(String guizebianhao) {
		Record record = Db
				.findFirst(
						"SELECT * from xyy_wms_bill_sjgzwh r where r.guizebianhao=?",
						guizebianhao);
		if (record != null) {
			return true;
		}
		return false;
	}
}
