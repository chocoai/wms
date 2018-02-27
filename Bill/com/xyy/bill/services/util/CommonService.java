package com.xyy.bill.services.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xyy.erp.platform.common.tools.StringUtil;

public class CommonService {
	
	private static final String BASE_DIC_DATA = "baseDicData";
	private static final String BASE_DIC_DATA_KEY = "baseDicDataKey";
	
	/**
	 * 检查商品库存
	 * @param billID
	 * @return
	 */
	public JSONObject checkKucun(List<Record> spList) {
		JSONObject result = new JSONObject();
		boolean flag = true;
		StringBuffer errorBuffer = new StringBuffer();
		for (Record sp : spList) {
			Record kc = Db.findFirst("select * from xyy_erp_bill_shangpinzongkucun "
					+ "where shangpinId = '"+sp.getStr("goodsid")+"' limit 1");
			Record zy = Db.findFirst("select shangpinId, sum(zhanyongshuliang) as shuliang from xyy_erp_bill_shangpinkucunzhanyong "
					+ "where shangpinId = '"+sp.getStr("goodsid")+"' group by shangpinId");
			if (kc==null) {
				flag = false;
				errorBuffer.append("【"+sp.getStr("shangpinbianhao")+"||商品总库存不存在】");
			}else {
				BigDecimal kucunshuliang = kc.getBigDecimal("kexiaoshuliang");
				if (zy!=null&&zy.getBigDecimal("shuliang")!=null) {
					kucunshuliang = kucunshuliang.subtract(zy.getBigDecimal("shuliang"));
				}
				if (kucunshuliang.intValue()<sp.getBigDecimal("shuliang").intValue()) {
					flag = false;
					errorBuffer.append("【"+sp.getStr("shangpinbianhao")+"】");
				}
			}
			
		}
		if (!flag) {
			JSONObject errorMsg = new JSONObject();
			errorMsg.put("异常原因", "商品"+errorBuffer.toString()+"库存不足");
			result.put("errorMsg", errorMsg);
			result.put("flag", false);
			return result;
		}else {
			result.put("flag", true);
			return result;
		}
		
		
	}

	/**
	 * 检查商品经营范围
	 * @param spList
	 * @param kehu
	 * @return
	 */
	public JSONObject checkJYFW(List<Record> spList, Record kehu) {
		JSONObject result = new JSONObject();
		boolean flag = true;
		StringBuffer errorBuffer = new StringBuffer();
		for (Record sp : spList) {
			Record record = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi "
					+ "where shangpinbianhao = '"+sp.getStr("shangpinbianhao")+"' limit 1");
			String jyfw = ","+kehu.getStr("jingyingfanwei")+",";
			Record leibie = Db.findFirst("select * from tb_sys_select where type = 'shangpinleibie' and `key` = "+record.getInt("shangpinleibie")+" limit 1");
			if (!jyfw.contains(","+leibie.getInt("key")+",")) {
				flag = false;
				errorBuffer.append("|商品【"+sp.getStr("shangpinbianhao")+"】超经营范围");
			}
			
		}
		if (!flag) {
			JSONObject errorMsg = new JSONObject();
			errorMsg.put("异常原因", errorBuffer.toString());
			result.put("errorMsg", errorMsg);
			result.put("flag", false);
			
			return result;
		}else {
			result.put("flag", true);
			return result;
		}
		
	}

	/**
	 * 检查客户资质
	 * @param kehu
	 * @return
	 */
	public JSONObject checkKehuZizhi(Record kehu) {
		JSONObject result = new JSONObject();
		StringBuffer eBuffer = new StringBuffer();
		boolean flag  = true ;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String now = format.format(new Date());
		long times = 0;
		try {
			times = format.parse(now).getTime();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (kehu.getInt("jingyingfangshi")==1) {
			if (kehu.getInt("sfszhy")==0) {
				try {
					if (!StringUtil.isEmpty(kehu.getStr("fzyxq"))&&format.parse(kehu.getStr("fzyxq")).getTime()<times) {
						flag = false;
						eBuffer.append("【组织机构代码证过期】");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				if (!StringUtil.isEmpty(kehu.getStr("zcyxq"))&&format.parse(kehu.getStr("zcyxq")).getTime()<times) {
					flag = false;
					eBuffer.append("【营业执照过期】");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//证照信息
		List<Record> list = Db.find("select * from xyy_erp_dic_kehujibenxinxi_zhiliangzhengzhaoxinxi "
				+ "where ID = '"+kehu.getStr("ID")+"'");
		List<Record> sList = CacheKit.get(BASE_DIC_DATA, BASE_DIC_DATA_KEY);
		for (Record record : list) {
			try {
				if (!StringUtil.isEmpty(record.getStr("youxiaoqizhi"))&&format.parse(record.getStr("youxiaoqizhi")).getTime()<times) {
					flag = false;
					eBuffer.append("【"+this.getName(record.getInt("zhengshuleixing"),sList)+"过期】");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//委托人信息
		List<Record> slist = Db.find("select * from xyy_erp_dic_kehujibenxinxi_kehuweituoren "
				+ "where ID = '"+kehu.getStr("ID")+"'");
		for (Record record : slist) {
			try {
				if (!StringUtil.isEmpty(record.getStr("wtryxq"))&&format.parse(record.getStr("wtryxq")).getTime()<times) {
					flag = false;
					eBuffer.append("【委托人_"+record.getStr("xingming")+"过期】");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (flag) {
			result.put("flag", true);
			return result;
		}else {
			JSONObject errorMsg = new JSONObject();
			errorMsg.put("异常原因", eBuffer.toString());
			result.put("errorMsg", errorMsg);
			result.put("flag", false);
			
			return result;
		}
	}

	private String getName(Integer type, List<Record> sList) {
		for (Record record : sList) {
			if (record.getInt("key")==type&&record.getStr("type").equals("zhengjianmingcheng")) {
				return record.getStr("value");
			}
		}
		return null;
	}

	//检测客户是否有特殊备注：非null，非“”，非“未填写备注信息”
	public JSONObject checkKehuBeizhu(Record head) {
		boolean flag  = true;
		StringBuffer eBuffer = new StringBuffer();
		JSONObject result = new JSONObject();
		String remark = head.getStr("remark");
		String beizhu = head.getStr("beizhu");
		if (!StringUtil.isEmpty(remark) && !remark.equals("未填写备注信息") && !remark.equals("无备注")) {
			flag = false;
			eBuffer.append("_客户有特殊备注【"+remark+"】");
		}
		if (!StringUtil.isEmpty(beizhu) && !beizhu.equals("未填写备注信息") && !beizhu.equals("无备注")) {
			flag = false;
			eBuffer.append("_客服有特殊备注【"+beizhu+"】");
		}
		
		if (flag) {
			result.put("flag", true);
			return result;
		}else {
			JSONObject errorMsg = new JSONObject();
			errorMsg.put("异常原因", eBuffer.toString());
			result.put("errorMsg", errorMsg);
			result.put("flag", false);
			
			return result;
		}
	}
	
	
}

