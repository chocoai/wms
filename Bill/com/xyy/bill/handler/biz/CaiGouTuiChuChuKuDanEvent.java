package com.xyy.bill.handler.biz;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.util.RecordDataUtil;

/**
 * 采购退出出库单(条件：提交状态)
 */
public class CaiGouTuiChuChuKuDanEvent implements StatusChangedEventListener {

	@Override
	public void execute(StatusChangedEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		System.out.println(event.getOldStatus());
		System.out.println(event.getNewStatus());
		if (event.getNewStatus() == 20) {//提交状态后保存到中间表
			Record head = getHead(dsi);
			List<Record> body = getBody(dsi);
			
			//商品批号库存处理
			RecordDataUtil.operateBatchStock(head,body,3);
			
			//商品总库存处理
			RecordDataUtil.operateTotalStock(head,body,3);
			
			//商品账页处理
			RecordDataUtil.addGoodsFolio(head,body,3);
			
			//往来账处理
			RecordDataUtil.addDealingFolio(head,body,3);
			
			
			//预清库存， 商品库存数量占用表,
			RecordDataUtil.clearGoodsStockUses(dsi, 3);
		}
	}
	
	
	
	
	
	
	
	
	
	

	/**
	 * 获取头部数据
	 * 
	 * @param dsi
	 *            数据集实例
	 * @return
	 */
	public static Record getHead(DataSetInstance dsi) {
		DataTableInstance headDti = dsi.getHeadDataTableInstance();
		if (headDti.getRecords().size() != 1) {
			return null;
		}
		Record record = headDti.getRecords().get(0);
		return record;
	}

	/**
	 * 获取体部数据
	 * 
	 * @param dsi
	 *            数据集实例
	 * @return
	 */
	public static List<Record> getBody(DataSetInstance dsi) {
		List<DataTableInstance> bodyDataTableInstance = dsi.getBodyDataTableInstance();
		if (bodyDataTableInstance.size() != 1) {
			return null;
		}
		List<Record> records = bodyDataTableInstance.get(0).getRecords();
		return records;
	}

	/**
	 * 查询商品
	 * 
	 * @param shangpinbianhao
	 * @return
	 */
	public static Record getGoods(String shangpinbianhao) {
		String sql = "select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = '" + shangpinbianhao + "'";
		List<Record> goods = Db.find(sql);
		if (goods != null && goods.size() == 1) {
			return goods.get(0);
		}
		return null;
	}

	/**
	 * 查询供应商
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getSupplier(String gysbh) {
		String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where gysbh = '" + gysbh + "'";
		List<Record> gysbhs = Db.find(sql);
		if (gysbhs != null && gysbhs.size() == 1) {
			return gysbhs.get(0);
		}
		return null;
	}
	
	/**
	 * 查询供应商
	 * 
	 * @param gysbh
	 * @return
	 */
	public static Record getSupplierByName(String gysmc) {
		String sql = "select * from xyy_erp_dic_gongyingshangjibenxinxi where gysmc = '" + gysmc + "'";
		Record record = Db.findFirst(sql);
		return record;
	}
	
	/**
	 * 查询用户
	 * @param userId
	 * @return
	 */
	public static Record getUserById(String userId) {
		Record record = Db.findById("tb_sys_user", userId);
		return record;
	}
	/**
	 * 名称查询用户
	 * @param userId
	 * @return
	 */
	public static Record getUserByName(String name) {
		String sql = "select * from tb_sys_user where realName = '"+name+"'";
		Record record = Db.findFirst(sql);
		return record;
	}
	
	
	/**
	 * 查询部门
	 * @param userId
	 * @return
	 */
	public static Record getDeptByUserId(String userId) {
		String sql ="SELECT * FROM tb_sys_dept WHERE id = (SELECT r.deptId FROM tb_sys_dept_user_relation r WHERE r.userId ='" +userId+"')";
		Record record = Db.findFirst(sql);
		return record;
	}
	
	
	

}
