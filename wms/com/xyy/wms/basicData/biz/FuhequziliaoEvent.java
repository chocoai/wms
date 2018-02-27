package com.xyy.wms.basicData.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.util.UUIDUtil;

public class FuhequziliaoEvent implements PostSaveEventListener{

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		DicDef dicdef=BillPlugin.engine.getDicDef("fuhequjibenxinxi");
		List<Field> list=dicdef.getDataSet().getHeadDataTable().getFields();
		String fhwbh=head.getStr("fhwbh");
		String fhqbianhao=head.getStr("fhqbh");//复核区编号
		String cangkuId=head.getStr("cangkuID");//仓库ID
		Db.update("delete from xyy_wms_dic_fuhequjibenxinxi where fhwbh is null or fhwbh='' ");
		if(StringUtils.isEmpty(fhwbh)){
			List<Record> fhqbhList=Db.find("select * from xyy_wms_dic_fuhequjibenxinxi where 1=1 and cangkuID=? and fhqbh=?",cangkuId,fhqbianhao);
			if(fhqbhList!=null&&fhqbhList.size()>0){
				event.getContext().addError("999","该复核区编号已存在！");
				return;
			}
		}
		if(StringUtils.isEmpty(fhwbh)){
		int qishizuhao=head.getInt("qishizuhao");
		int zongzizuhao=head.getInt("zongzizuhao");
		int qishicenghao=head.getInt("qishicenghao");
		int zhongzicenghao=head.getInt("zhongzicenghao");
		int qishileihao=head.getInt("qishileihao");
		int zongziliehao=head.getInt("zongziliehao");
		StringBuffer fhqbh=null;
		for(int i=qishizuhao;i<=zongzizuhao;i++){
			for(int j=qishicenghao;j<=zhongzicenghao;j++){
				for(int k=qishileihao;k<=zongziliehao;k++){
					fhqbh=new StringBuffer();
				 	fhqbh.append("FH").append(i).append("-").append(j).append("-").append(k);
				 	insertFuhequziliao(fhqbh.toString(),list,head,i,j,k);
				}
			}
		}
		}
		
		//插入货位test
		//insert();
	}


	private void insertFuhequziliao(String fhqbh, List<Field> list, Record head, int i, int j, int k) {
		Record nre=new Record();//头
		for(Field f:list){
			if(head.get(f.getFieldKey())!=null){
				nre.set(f.getFieldKey(), head.get(f.getFieldKey()));
			}
		}
		nre.set("status",40);
		nre.set("orgId", head.getStr("orgId"));
		nre.set("orgCode", head.getStr("orgCode"));
		String ID=UUIDUtil.newUUID();
		nre.set("ID", ID);
		nre.set("zuhao", i);
		nre.set("cenghao", j);
		nre.set("liehao", k);
		nre.set("fhwbh", fhqbh);
		nre.set("rowID", ID);
		nre.set("nodeType", head.getInt("nodeType"));
		nre.set("createTime", head.getTimestamp("createTime"));
		nre.set("updateTime", head.getTimestamp("updateTime"));
		nre.set("creator", head.getStr("creator"));
		nre.set("creatorName", head.getStr("creatorName"));
		nre.set("creatorTel", head.getStr("creatorTel"));
		Db.save("xyy_wms_dic_fuhequjibenxinxi", nre);
	}
	
	private void insert() {
		List<Record> huoweiList=Db.find("select * from xyy_wms_dic_huoweiziliaoweihu where huoweiqiyong=1");
		List<Record> kuncunList=Db.find("select * from  xyy_wms_bill_shangpinpihaohuoweikucun   order by shangpinId asc,pihaoId asc");
		
		Map<Integer,Record> zjMap=new HashMap<Integer, Record>();
		Map<Integer,Record> ljMap=new HashMap<Integer, Record>();
		Integer zj=new Integer(0);
		Integer lj=new Integer(0);
		for(Record huowei:huoweiList){
			if(huowei.getStr("huoweibianhao").startsWith("LJ")){
				lj++;
				ljMap.put(lj, huowei);
			}else{
				zj++;
				zjMap.put(zj, huowei);
			}
		}
		Random random = new Random();
		Integer[] keys = ljMap.keySet().toArray(new Integer[0]);
		Integer[] keys2 = zjMap.keySet().toArray(new Integer[0]);
		
		for(int i=0;i<kuncunList.size();i++){
			Record record=kuncunList.get(i);
			record.set("cangkuId", "394b2a54e5e04c098a117550d249a267");
			if(i%2==0){//零件
				Integer randomKey = keys[random.nextInt(keys.length)];
				Record randomValue = ljMap.get(randomKey);
				record.set("huoweiId", randomValue.getStr("ID"));
				record.set("kucunshuliang", 1000);
			}else{
				Integer randomKey = keys2[random.nextInt(keys2.length)];
				Record randomValue = zjMap.get(randomKey);
				record.set("huoweiId", randomValue.getStr("ID"));
				record.set("kucunshuliang", 10000);
			}
		}
		Db.batchUpdate("xyy_wms_bill_shangpinpihaohuoweikucun", "ID", kuncunList, kuncunList.size());
	}

}
