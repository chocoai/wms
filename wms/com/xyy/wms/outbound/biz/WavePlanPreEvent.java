package com.xyy.wms.outbound.biz;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;

public class WavePlanPreEvent implements PreSaveEventListener{

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		List<DataTableInstance> bodylist=dsi.getBodyDataTableInstance();
		String danjubianhao=head.getStr("danjubianhao") ;//波次编号   
		Set<String> shangpingSet=new HashSet<String>();
		double  spzsl=0;
		for(DataTableInstance dataTableInstance:bodylist){
			List<Record> recoredList=dataTableInstance.getRecords();
			for(Record body:recoredList){
				body.set("danjubianhao", danjubianhao);//设置波次编号关联订单和商品
				if("bocijihua_details2".equals(dataTableInstance.getKey())){
					shangpingSet.add(body.getStr("shangpinbianhao"));//波次品规数
					spzsl=spzsl+body.getBigDecimal("shuliang").doubleValue();//计算出库总数量
				}else{
					body.set("dingdanleixing", 3);//设置订单类型
					body.set("dingdanzhuangtai", 20);//设置订单状态
					Record xlRecord=Db.findFirst(getKhxlSql(),body.getStr("kehubianhao"),context.getString("orgId"));//获取客户路线
					if(xlRecord!=null){
						body.set("luxianId", xlRecord.getStr("ID"));//设置线路ID
						body.set("luxian", xlRecord.getStr("xianlubianhao"));//设置线路code
						body.set("luxianmingcheng", xlRecord.getStr("xianlumingcheng"));//设置线路名称
						if(StringUtils.isEmpty(body.getStr("youxianji"))){
							body.set("youxianji", xlRecord.getInt("xlyxj"));//设置线路优先级
						}
					}
				}
				
			}
		}
		
		//设置波次品规数
		head.set("pingueishu", shangpingSet.size());
		
		//设置波次总数量
		head.set("zongshuliang", new BigDecimal(spzsl));
		
		
		
	}

	
	private String getKhxlSql(){
		StringBuffer sb=new StringBuffer("select  t2.ID,t2.xianlubianhao,t2.xianlumingcheng,t2.xlyxj ");
		sb.append("  from xyy_wms_dic_kehuxianlu t1,xyy_wms_dic_peisongxianlu t2,xyy_wms_dic_kehuxianlu_list t3  where t1.xianluID = t2.ID and t1.ID=t3.ID  ");
		sb.append("  and t3.kehubianhao=? and t1.orgId=? and t1.shifouhuodong=1 and t2.shifouhuodong=1 order by t2.xlyxj ");
		return sb.toString();
	}
}
