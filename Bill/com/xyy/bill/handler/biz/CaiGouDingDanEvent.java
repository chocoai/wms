package com.xyy.bill.handler.biz;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.instance.ErrorMsg;
import com.xyy.bill.util.PullWMSDataUtil;
import com.xyy.bill.util.RecordDataUtil;
import com.xyy.erp.platform.common.tools.ToolDateTime;

/**
 * 采购订单生成WMS中间表的采购订单(条件：采购订单审核通过后，自动下推)
 */
public class CaiGouDingDanEvent implements StatusChangedEventListener,PreSaveEventListener{

	@Override
	public void execute(StatusChangedEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		System.out.println(event.getOldStatus());
		System.out.println(event.getNewStatus());
		Record head = getHead(dsi);
		List<Record> body = getBody(dsi);
		if (event.getNewStatus() == 40) {//审核通过后保存到中间表
			//生成中间表 采购订单
			PullWMSDataUtil.generateCaiGouDingDan(head,body);
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
	 * 拦截供应商-商品的验证（保存前拦截）
	 */
	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		Record head = getHead(dsi);
		List<Record> body = getBody(dsi);
		String gysbh = head.getStr("gysbh");
		
		gongYingShangValidate(event.getContext(), gysbh);
		
		Record supplier = RecordDataUtil.getSupplier(gysbh);
		Integer qiyeleixing = supplier.getInt("qiyeleixing");
		String jingyingfanwei = supplier.getStr("jingyingfanwei");//供应商的经营范围
		if(1==qiyeleixing){//“供货商类别”为“药品生产企业”,判断商品剂型
			try {
				for (Record record : body) {
					String shangpinbianhao = record.getStr("shangpinbianhao");
					String shangpinmingcheng = record.getStr("shangpinmingcheng");
					Record goods = RecordDataUtil.getGoods(shangpinbianhao);
					Integer jixing = goods.getInt("jixing");//商品剂型
					if(jingyingfanwei.indexOf(jixing.toString())==-1){
						event.getContext().addError("999", "【供货商类别】与【"+shangpinmingcheng+"】的【商品剂型】不一致！");
						return;
					}
				}
			} catch (Exception e) {
				event.getContext().addError("999", "【供货商类别】与【商品剂型】不一致！");
				return;
			}
		}else{//其他，判断商品类别
			try {
				for (Record record : body) {
					String shangpinbianhao = record.getStr("shangpinbianhao");
					String shangpinmingcheng = record.getStr("shangpinmingcheng");
					Record goods = RecordDataUtil.getGoods(shangpinbianhao);
					Integer shangpinleibie = goods.getInt("shangpinleibie");//商品类别
					if(jingyingfanwei.indexOf(shangpinleibie.toString())==-1){
						event.getContext().addError("999", "【供货商类别】与【"+shangpinmingcheng+"】的【商品类别】不一致！");
						return;
					}
				}
			} catch (Exception e) {
				event.getContext().addError("999", "【供货商类别】与【商品类别】不一致！");
				return;
			}
		}
		
	}

	/**
	 * 供应商资质验证是否过期
	 */
	public void gongYingShangValidate(BillContext context,String gysbh){
		String sql ="select * from xyy_erp_dic_gongyingshangjibenxinxi where gysbh = ?";
		Record suppliers = Db.findFirst(sql,gysbh);
		if(suppliers == null){
			ErrorMsg error = new ErrorMsg();
			error.setErrNo("999");
			error.setMessage("没有该供应商信息");
			context.addError(error);
		}
		
		//是否三证合一
		Integer sfszhy = suppliers.getInt("sfszhy");
		
		//校验组织机构代码证“有效期至”是否过期；
		if(sfszhy == 0){
			String fzyxq = suppliers.getStr("fzyxq");
			Date zzDate = ToolDateTime.parse(fzyxq, ToolDateTime.pattern_ymd);
			Date now = ToolDateTime.getDate(new Date(), ToolDateTime.pattern_ymd);
			if(zzDate.getTime()<now.getTime()){
				ErrorMsg error = new ErrorMsg();
				error.setErrNo("999");
				error.setMessage("【组织机构代码证】有效期【"+fzyxq+"】，已过期！");
				context.addError(error);
			}
		}
		//校验营业执照--有效期至方式
		Integer yxqzfs = suppliers.getInt("yxqzfs");//有效期至方式
		String zcyxq = suppliers.getStr("zcyxq");//注册有效期
		if(zcyxq == null){
			ErrorMsg error = new ErrorMsg();
			error.setErrNo("999");
			error.setMessage("【营业执照】注册有效期为空！");
			context.addError(error);
		}
		if(yxqzfs != 1){
			Date now = ToolDateTime.getDate(new Date(), ToolDateTime.pattern_ymd);
			Date fzyxqDate = ToolDateTime.parse(zcyxq, ToolDateTime.pattern_ymd);
			if(fzyxqDate.getTime()<now.getTime()){
				ErrorMsg error = new ErrorMsg();
				error.setErrNo("999");
				error.setMessage("【营业执照】有效期【"+zcyxq+"】，已过期！");
				context.addError(error);
			}
		}
		
		//供应商委托人--证书是否过期
		Record wtrRecord = Db.findFirst("SELECT * FROM xyy_erp_dic_gongyingshang_weituoren gz WHERE"
				+ " gz.ID IN ( SELECT g.ID FROM xyy_erp_dic_gongyingshangjibenxinxi g WHERE g.gysbh = ? ) and sfzlxr = 1",gysbh);
			if(wtrRecord == null){
				ErrorMsg error = new ErrorMsg();
				error.setErrNo("999");
				error.setMessage("供应商胡【委托人】为空！");
				context.addError(error);
				return;
			}
			Integer wtryxqfs = wtrRecord.getInt("wtryxqfs");//委托人有效期方式，0：短期，1：长期
			if(0==wtryxqfs){
				String wtryxq = wtrRecord.getStr("wtryxq");
				if(wtryxq == null){
					ErrorMsg error = new ErrorMsg();
					error.setErrNo("999");
					error.setMessage("【委托人】为空！");
					context.addError(error);
				}
				Date date = ToolDateTime.parse(wtryxq, ToolDateTime.pattern_ymd);
				Date now = ToolDateTime.getDate(new Date(), ToolDateTime.pattern_ymd);
				if(date.getTime()<now.getTime()){
					ErrorMsg error = new ErrorMsg();
					error.setErrNo("999");
					error.setMessage("【委托人】有效期【"+wtryxq+"】，已过期！");
					context.addError(error);
				}
			}
			
		
		
		
		//供应商-证书是否过期
		List<Record> list = Db.find("SELECT * FROM xyy_erp_dic_gongyingshang_zhiliangrenzheng gz WHERE"
				+ " gz.ID IN ( SELECT g.ID FROM xyy_erp_dic_gongyingshangjibenxinxi g WHERE g.gysbh = ? )",gysbh);
		
		if(list==null || list.size()==0){
			ErrorMsg error = new ErrorMsg();
			error.setErrNo("999");
			error.setMessage("【供应商】没有认证信息");
			context.addError(error);
		}
		for (Record record : list) {
			String youxiaoqizhi = null;
			try {
				youxiaoqizhi = record.getStr("youxiaoqizhi");
			} catch (Exception e) {
				ErrorMsg error = new ErrorMsg();
				error.setErrNo("999");
				error.setMessage("【供应商】有效期格式不正确");
				context.addError(error);
			}
			
			Date date = ToolDateTime.parse(youxiaoqizhi, "yyyy-MM-dd");
			Date now = ToolDateTime.getDate(new Date(), "yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, 1);
//			if(calendar.getTime().getTime()>date.getTime()){
//				result.put("mes", "该证照的效期距离效期不足一个月");
//			}
			if(date.getTime()<now.getTime()){
				if(record.getInt("zhengshuleixing")==null){
					ErrorMsg error = new ErrorMsg();
					error.setErrNo("999");
					error.setMessage("【供应商】证书类型不正确");
					context.addError(error);
				}
				String name = null;
				Integer zhengshuleixing = record.getInt("zhengshuleixing");
				if(zhengshuleixing==1){
					name="药品生产许可证";
				}else if(zhengshuleixing==2){
					name="GMP证书";
				}else if(zhengshuleixing==3){
					name="第二类医疗器械经营备案凭证";
				}else if(zhengshuleixing==4){
					name="医疗器械许可证";
				}else if(zhengshuleixing==5){
					name="食品流通许可证";
				}else if(zhengshuleixing==6){
					name="食品经营许可证";
				}else if(zhengshuleixing==7){
					name="药品经营许可证";
				}else if(zhengshuleixing==8){
					name="GSP证书";
				}else if(zhengshuleixing==9){
					name="医疗器械生产企业许可证";
				}else if(zhengshuleixing==10){
					name="医疗器械生产许可证";
				}else if(zhengshuleixing==11){
					name="第一类医疗器械生产备案凭证";
				}else if(zhengshuleixing==12){
					name="食品生产许可证";
				}
				else if(zhengshuleixing==13){
					name="消毒产品生产企业卫生许可证";
				}
				else if(zhengshuleixing==14){
					name="质量保证协议";
				}
				else if(zhengshuleixing==15){
					name="卫生许可证";
				}
				else if(zhengshuleixing==16){
					name="医疗机构执业许可证";
				}
				ErrorMsg error = new ErrorMsg();
				error.setErrNo("999");
				error.setMessage("【"+name+"】有效期【"+youxiaoqizhi+"】，已过期！");
				context.addError(error);
			}
		}
	}
	
	
	
	

}
