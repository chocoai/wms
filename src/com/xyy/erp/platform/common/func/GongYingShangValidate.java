package com.xyy.erp.platform.common.func;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.ToolDateTime;

/**
 * 供应商的资质认证
 * @author SKY
 *
 */
public class GongYingShangValidate implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		String gysbh = param.getString("gysbh");
		String sql ="select * from xyy_erp_dic_gongyingshangjibenxinxi where gysbh = ?";
		Record suppliers = Db.findFirst(sql,gysbh);
		if(suppliers == null){
			result.put("flag", false);
			result.put("mes", "没有该【供应商】信息");
			result.put("back", true);
			return result;
		}
		
		//是否三证合一
		Integer sfszhy = suppliers.getInt("sfszhy");
		
		//校验组织机构代码证“有效期至”是否过期；
		if(sfszhy == 0){
			String fzyxq = suppliers.getStr("fzyxq");
			Date zzDate = ToolDateTime.parse(fzyxq, ToolDateTime.pattern_ymd);
			Date now = ToolDateTime.getDate(new Date(), ToolDateTime.pattern_ymd);
			if(zzDate.getTime()<now.getTime()){
				result.put("flag", false);
				result.put("mes", "【组织机构代码证】有效期【"+fzyxq+"】，已过期！");
				result.put("back", true);
				return result;
			}
		}
		//校验营业执照--有效期至方式
		Integer yxqzfs = suppliers.getInt("yxqzfs");//有效期至方式
		String zcyxq = suppliers.getStr("zcyxq");//注册有效期
		if(zcyxq == null){
			result.put("flag", false);
			result.put("mes", "【营业执照】有效期为空");
			result.put("back", true);
			return result;
		}
		if(yxqzfs != 1){
			Date now = ToolDateTime.getDate(new Date(), ToolDateTime.pattern_ymd);
			Date fzyxqDate = ToolDateTime.parse(zcyxq, ToolDateTime.pattern_ymd);
			if(fzyxqDate.getTime()<now.getTime()){
				result.put("flag", false);
				result.put("mes", "【营业执照】有效期【"+zcyxq+"】，已过期！");
				result.put("back", true);
				return result;
			}
		}
		
		//供应商委托人--证书是否过期
		Record wtrRecord = Db.findFirst("SELECT * FROM xyy_erp_dic_gongyingshang_weituoren gz WHERE"
				+ " gz.ID IN ( SELECT g.ID FROM xyy_erp_dic_gongyingshangjibenxinxi g WHERE g.gysbh = ? ) and sfzlxr = 1",gysbh);
			
		if(wtrRecord == null){
			result.put("flag", false);
			result.put("mes", "该【供应商】没有【委托人】");
			result.put("back", true);
			return result;
		}
		Integer wtryxqfs = wtrRecord.getInt("wtryxqfs");//委托人有效期方式，0：短期，1：长期
			if(0==wtryxqfs){
				String wtryxq = wtrRecord.getStr("wtryxq");
				if(wtryxq == null){
					result.put("flag", false);
					result.put("mes", "主委托人有效期为空");
					result.put("back", true);
					return result;
				}
				Date date = ToolDateTime.parse(wtryxq, ToolDateTime.pattern_ymd);
				Date now = ToolDateTime.getDate(new Date(), ToolDateTime.pattern_ymd);
				if(date.getTime()<now.getTime()){
					result.put("flag", false);
					result.put("mes", "【主委托人证】有效期【"+wtryxq+"】，已过期！");
					result.put("back", true);
					return result;
				}
			}
		
		//供应商-证书是否过期
		List<Record> list = Db.find("SELECT * FROM xyy_erp_dic_gongyingshang_zhiliangrenzheng gz WHERE"
				+ " gz.ID IN ( SELECT g.ID FROM xyy_erp_dic_gongyingshangjibenxinxi g WHERE g.gysbh = ? )",gysbh);
		
		if(list==null || list.size()==0){
			result.put("flag", false);
			result.put("mes", "该【供应商】没有认证信息");
			result.put("back", true);
			return result;
		}
		for (Record record : list) {
			String youxiaoqizhi = null;
			try {
				youxiaoqizhi = record.getStr("youxiaoqizhi");
			} catch (Exception e) {
				result.put("flag", false);
				result.put("mes", "格式不正确");
				result.put("back", true);
				return result;
			}
			
			Date date = ToolDateTime.parse(youxiaoqizhi, "yyyy-MM-dd");
			Date now = ToolDateTime.getDate(new Date(), "yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, 1);
			if(calendar.getTime().getTime()>date.getTime()){
				result.put("mes", "该证照的效期距离效期不足一个月");
			}
			if(date.getTime()<now.getTime()){
				if(record.getInt("zhengshuleixing")==null){
					result.put("flag", false);
					result.put("mes", "格式不正确");
					result.put("back", true);
					return result;
				}
				int zhengshuleixing = record.getInt("zhengshuleixing");
				if(zhengshuleixing==1){
					result.put("mes", "【药品生产许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==2){
					result.put("mes", "【GMP证书】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==3){
					result.put("mes", "【第二类医疗器械经营备案凭证】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==4){
					result.put("mes", "【医疗器械许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==5){
					result.put("mes", "【食品流通许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==6){
					result.put("mes", "【食品经营许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==7){
					result.put("mes", "【药品经营许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==8){
					result.put("mes", "【GSP证书】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==9){
					result.put("mes", "【医疗器械生产企业许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==10){
					result.put("mes", "【医疗器械生产许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==11){
					result.put("mes", "【第一类医疗器械生产备案凭证】有效期【"+youxiaoqizhi+"】，已过期！");
				}else if(zhengshuleixing==12){
					result.put("mes", "【食品生产许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}
				else if(zhengshuleixing==13){
					result.put("mes", "【消毒产品生产企业卫生许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}
				else if(zhengshuleixing==14){
					result.put("mes", "【质量保证协议】有效期【"+youxiaoqizhi+"】，已过期！");
				}
				else if(zhengshuleixing==15){
					result.put("mes", "【卫生许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}
				else if(zhengshuleixing==16){
					result.put("mes", "【医疗机构执业许可证】有效期【"+youxiaoqizhi+"】，已过期！");
				}
				result.put("flag", false);
				result.put("back", true);
				return result;
			}
		}
		result.put("flag", true);
		return result;
	}

}
