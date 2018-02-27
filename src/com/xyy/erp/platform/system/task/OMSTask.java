package com.xyy.erp.platform.system.task;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.cron4j.ITask;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.system.task.Task.TaskType;

/**
 * 电商系统定时任务
 * @author Chen
 *
 */
public class OMSTask implements ITask{

	@Override
	public void run() {
		//拉取中间表电商订单
		List<Record> orderList = Db.use(DictKeys.db_dataSource_wms_mid).find("select   * from tb_order_tmp where STATE = 0 and KFSTATE = 0 ");
		for (Record record : orderList) {
			if (OMSTaskManager.instance().get_maps().get(record.getStr("ORDERNO"))==null) {
				OMSTaskManager.instance().get_maps().put(record.getStr("ORDERNO"), new Task(record.getStr("ORDERNO"), record, TaskType.ORDER));
			}
		}
		
		//拉取电商订单
		List<Record> dsList = Db.find("select * from xyy_erp_bill_dianshangdingdan where status = 20");
		for (Record record : dsList) {
			if (OMSTaskManager.instance().get_maps().get(record.getStr("BillID"))==null) {
				OMSTaskManager.instance().get_maps().put(record.getStr("BillID"), new Task(record.getStr("BillID"), record, TaskType.DS));
			}
		}
		
		//拉取销售订单
		List<Record> xsList = Db.find("select * from xyy_erp_bill_xiaoshoudingdan where status = 20 and shifouzhifu = 1 and shifouxiatui = 0 ");
		for (Record record : xsList) {
			if (OMSTaskManager.instance().get_maps().get(record.getStr("BillID"))==null) {
				OMSTaskManager.instance().get_maps().put(record.getStr("BillID"), new Task(record.getStr("BillID"), record, TaskType.SALE));
			}
		}
		
		//拉取销售退回单
		List<Record> thList = Db.find("select * from xyy_erp_bill_xiaoshoutuihuidan where status = 40 and shifouxiatui = 0 ");
		for (Record record : thList) {
			if (OMSTaskManager.instance().get_maps().get(record.getStr("BillID"))==null) {
				OMSTaskManager.instance().get_maps().put(record.getStr("BillID"), new Task(record.getStr("BillID"), record, TaskType.REBACK));
		
			}
		}
		
		//拉取中间表销售出库单
		List<Record> chukuList = Db.use(DictKeys.db_dataSource_wms_mid).find("select djbh,sum(SL) as sl,MIN(ZXS) as zxs,MIN(ZCQMC) as zcqmc from int_wms_xsck_bill where is_zx = '否' group by DJBH");
		for (Record record : chukuList) {
			if (OMSTaskManager.instance().get_maps().get(record.getStr("djbh"))==null) {
				OMSTaskManager.instance().get_maps().put(record.getStr("djbh"), new Task(record.getStr("djbh"), record, TaskType.OUT));
		
			}
		}
		//拉取中间表销售退回入库单
		List<Record> thrkList = Db.use(DictKeys.db_dataSource_wms_mid).find("select  djbh,count(*) from int_wms_xsth_bill where is_zx = '否' group by DJBH");
		for (Record record : thrkList) {
			if (OMSTaskManager.instance().get_maps().get(record.getStr("djbh"))==null) {
				OMSTaskManager.instance().get_maps().put(record.getStr("djbh"), new Task(record.getStr("djbh"), record, TaskType.IN));
			}
		}
		
//		SQLService service = new SQLService();
//		service.DSDDFun();//拉取电商订单
//		service.XSCKDFun();//拉取销售出库单
//		service.XSTHRKFun();//拉取销售退回入库单
//		service.XSDDFun();//推销售订单到中间库
//		service.XSTHFun();//推销售退回单到中间库
		
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
