package com.xyy.wms.outbound.biz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.system.model.User;

/**
 * @author zhang.wk
 * 拣货单完成前的处理
 */
public class PickingPreEvent implements PreSaveEventListener {

	private static Logger logger = Logger.getLogger(PickingPreEvent.class);

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		User user =(User)context.get("user");
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		List<Record> records = dsi.getBodyDataTableInstance().get(0).getRecords();
		Integer taskType = head.getInt("taskType");
		Integer status = head.getInt("status");
		head.set("caozuoren", user.getUserId());
		//拆零拣货
		if(taskType == 10){
			// 校验容器
			checkContianerOrder(head.get("rongqihao"),context);
			// 开始拣货
			boolean flag = startPicking(records,head,user);
			// 拣货单状态status( 32 任务为索取 34 已索取拣货未完成 36 拣货完成)
			if (flag) {
				head.set("status", 36);
				head.set("querenshijian", getCompleteDate());
				logger.info("零件拣货任务完成！");
				// 分配复核台
				distributeFuHeTai(head,context);
			} else {
				//该单未拣货完成 
				head.set("status", 34);
				logger.info("零件拣货任务尚未完成！");
			}
		}
		
		//整件出货
		if(taskType == 20){
			// 开始拣货
			boolean flag = startPicking(records,head,user);
			if(flag){
				head.set("status", 36);
				head.set("querenshijian", getCompleteDate());
				logger.info("整件拣货任务完成！");
			}else{
				//该单未拣货完成
				head.set("status", 34);
				logger.info("整件拣货任务尚未完成！");
			}
		}
		
		//主动补货
		if(taskType == 30){
		}
		
		//被动补货
		if(taskType == 40){
			/*	
			 * 任务状态(32未索取，27下架索取，28下架成功，29上架索取，26上架成功(任务完成))
		     * 开始拣货
			 */
			boolean flag = startPicking(records,head,user);
			/* 拣货单状态status( 32 任务未索取 34 已索取拣货未完成 36 拣货完成)
			 * 判断当前是上架还是下架
			 */
			if(status == 27){
				//下架任务已索取，可进行下架确认
				if (flag) {
					head.set("status", 28);
					head.set("xiajiariqi", getCompleteDate());
					logger.info("被动补货拣货中下架任务完成！");
				} else {
					//该单未拣货完成 
					logger.info("被动补货拣货中下架任务尚未完成！");
				}
			}
			if(status == 29){
				//上架任务已索取，可进行上架确认
				if (flag) {
					head.set("status", 26);//上架成功
					head.set("querenshijian", getCompleteDate());
					head.set("shangjiariqi", getCompleteDate());
					logger.info("被动补货拣货中下架任务完成！");
				} else {
					//该单未拣货完成 
					logger.info("被动补货拣货中下架任务尚未完成！");
				}
			}
		}
	}

	/**
	 * @param head
	 * 分配复核台
	 */
	private static void distributeFuHeTai(Record head,BillContext context) {
		//第一步，找出订单下所有的拣货单
		StringBuffer sbu = new StringBuffer("SELECT * from xyy_wms_bill_dabaorenwu where dingdanbianhao = ? "
				+ "and taskType = 10");
		List<Record> jianhuodanList = Db.find(sbu.toString(), head.getStr("dingdanbianhao"));
		//第二步 遍历拣货单列表,所有的拣货单是否已经分配复核位
		String fhqbh = null;
		for (Record jianhuodan : jianhuodanList) {
			if(jianhuodan.getStr("fhqbh") != null){
				fhqbh = jianhuodan.getStr("fhqbh");
				break;
			}else{
				continue;
			}
		}
		if(StringUtils.isNotEmpty(fhqbh)){
			//当复核区编号不为空，遍历该去的所有复核位，如果有，分配给他，如果没有，设置下一个复合
			StringBuffer fhwsql = new StringBuffer("SELECT * from xyy_wms_dic_fuhequjibenxinxi WHERE fhqbh = ? and shifouqiyong = 1 and shifousuoding = 0 "
					+ "ORDER BY  fhqbh,zuhao,cenghao,liehao");
			List<Record> fhwList = Db.find(fhwsql.toString(), fhqbh);
			if(fhwList.size() > 0){
				head.set("fuhetai",fhwList.get(0).getStr("fhwbh"));
				head.set("fhqbh",fhwList.get(0).getStr("fhqbh"));
				Db.update("update xyy_wms_dic_fuhequjibenxinxi set shifousuoding = 1 WHERE id = ?",fhwList.get(0).getStr("ID"));
			}else{
				Record fuhewei = Db.findFirst("SELECT * from xyy_wms_dic_fuhequjibenxinxi WHERE 1=1 and shifouqiyong = 1 "
						+ "and shifousuoding = 0 ORDER BY  fhqbh,zuhao,cenghao,liehao");
				if(fuhewei != null ){
					head.set("fuhetai",fuhewei.getStr("fhwbh"));
					head.set("fhqbh",fuhewei.getStr("fhqbh"));
					Db.update("update xyy_wms_dic_fuhequjibenxinxi set shifousuoding = 1 WHERE id = ?",fuhewei.getStr("ID"));
				}else{
					context.addError(null, "暂无可用的复核台！");
					return;
				}
			}
		}else{
			Record fuhewei = Db.findFirst("SELECT * from xyy_wms_dic_fuhequjibenxinxi WHERE 1=1 and shifouqiyong = 1 "
					+ "and shifousuoding = 0 ORDER BY  fhqbh,zuhao,cenghao,liehao");
			if(fuhewei != null ){
				head.set("fuhetai",fuhewei.getStr("fhwbh"));
				head.set("fhqbh",fuhewei.getStr("fhqbh"));
				Db.update("update xyy_wms_dic_fuhequjibenxinxi set shifousuoding = 1 WHERE id = ?",fuhewei.getStr("ID"));
			}else{
				context.addError(null, "暂无可用的复核台！");
				return;
			}
		}
	}
	
	/**
	 * @param rongqihao
	 * @return 容器号校验
	 */
	public static void checkContianerOrder(String rongqihao,BillContext context) {
		boolean flag = true;
		StringBuffer sb = new StringBuffer("SELECT * from xyy_wms_dic_rqzlwh r where r.rongqibianhao=?");
		//根据容器号获取基础信息中该容器的信息
		Record record = Db.findFirst(sb.toString(), rongqihao);
		if (record == null) {
			flag = false;
		}else{
			//如果容器被没有启用或者被占用，返回容器不可用提示
			if(record.getInt("shifouqiyong")== 0 || record.getInt("shifousuoding") == 1){
				flag = false;
				// 容器已占用，return
				context.addError(null, "此容器已被占用！");
				return;
			}
		}
		if (flag) {
			// 容器能使用，更新容器状态为已占用
			StringBuffer sql_rongqi_update = new StringBuffer("update xyy_wms_dic_rqzlwh r SET r.shifousuoding =?  WHERE r.rongqibianhao =?");
			Db.update(sql_rongqi_update.toString(),1,rongqihao);
		}else{
			// 容器已占用，return
			context.addError(null, "容器不存在，请输入正确的容器号！");
			return;
		}
	}
	
	/**
	 * 拣货
	 * @param records
	 * @param taskType
	 */
	private static boolean startPicking(List<Record> records,Record head,User user){
		// 开始拣货
		boolean flag = true;
		for (Record record : records) {
			Integer shuliang = record.getInt("shuliang");
			BigDecimal jianshu = record.getBigDecimal("jianshu");
			record.set("shijianshuliang", shuliang);
			record.set("shijianjianshu", jianshu);
			record.set("status", 38);
			record.set("caozuoren",user.getRealName());
		}
		return flag;
	}
	
	/**
	 * 获取拣货完成时间
	 */
	private static String getCompleteDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String confirmTime = sdf.format(new Date());
		return confirmTime;
	}
	
	
}
