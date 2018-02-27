package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;

/**
 * 保存前验证库区作业权限
 * @author dell
 *
 */
public class DickqzyqxPreHandler implements PreSaveEventListener{

	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        String zhiyuanmingcheng = head.getStr("zhiyuanmingcheng");
        String kuqumingcheng = head.getStr("kuqumingcheng");
        String gonghao = head.getStr("gonghao");
        int zuoyeshunxu = head.getInt("zuoyeshunxu");
        if (StringUtil.isEmpty(zhiyuanmingcheng) || StringUtil.isEmpty(kuqumingcheng) ) {
            event.getContext().addError("员工名称", "库区名称不能为空");
            return;
        }
		
        String sql = "select * from xyy_wms_dic_kqzyqxwh where gonghao = ?";
        List<Record> list = Db.find(sql, gonghao);
        if(!list.isEmpty()){
	        for (Record record : list) {
				int shunxu =  record.getInt("zuoyeshunxu");
				String mingcheng = record.getStr("kuqumingcheng");
				if(!kuqumingcheng.equals(mingcheng) && zuoyeshunxu==shunxu){
					event.getContext().addError("同一员工在不同库区作业不能相同", "同一员工在不同库区作业不能相同");
		            return;
				}
			}
		}
	}
}
