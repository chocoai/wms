package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillSptsjssqPostHandler implements PostSaveEventListener{

	@Override
	public void execute(PostSaveEvent event) {
		// TODO Auto-generated method stub
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
		Record head = getHead(dsi);
		List<Record> body = getBody(dsi);
		// 后台校验 YiKuKaiPiaoValidate
		int sqxz=head.getInt("shenqingxingzhi");
		List<Record> upRecord=new ArrayList<>();
		List<Record> phRecord=new ArrayList<>();
		int tingshouleixing=0;
		//	判断申请性质4:停售,1:解售
		if (sqxz==4){
			for (Record r:body){
				Record spRecord=new Record();
				//判断停售类型1:全部,2：批号
                if (r.getInt("tingshouleixing")==1){
					tingshouleixing=1;
					String shangpinbianhao=r.getStr("shangpinbianhao");
					spRecord.set("dongjie",1);
					spRecord.set("shangpinbianhao",shangpinbianhao);
					upRecord.add(spRecord);
				}else {
					tingshouleixing=2;
					Record phCord=new Record();
					phCord.set("shangpinbianhao",r.getStr("shangpinbianhao"));
					phCord.set("pihaoId",r.getStr("pihaoId"));
					phRecord.add(phCord);
				}
			}
			String upSql=null;
			if(tingshouleixing==1){
				//批量修改商品冻结状态
				upSql="update xyy_wms_dic_shangpinziliao set dongjie=? where shangpinbianhao=?";
			//	Db.batch(upSql,"dongjie,shangpinbianhao",upRecord,upRecord.size());
			}else {
				String selSql="SELECT z.rowID from xyy_wms_dic_shangpinziliao z INNER JOIN xyy_wms_dic_shangpinpihao p on z.goodsid=p.goodsId WHERE z.shangpinbianhao=? and p.pihaoId=?";
				List<Record> phRecords=Db.find(selSql,phRecord,phRecord.size());
				upSql="update xyy_wms_dic_shangpinziliao z set z.dongjie=? WHERE z.rowID= ?";
				for (Record r:phRecords){
					r.set("dongjie",1);
				}
			//	Db.batch(upSql,"dongjie,rowID",phRecords,phRecords.size());
			}
		}else {
			for (Record r:body){
				Record spRecord=new Record();
				if (r.getInt("tingshouleixing")==1){
					tingshouleixing=1;
					String shangpinbianhao=r.getStr("shangpinbianhao");
					spRecord.set("dongjie",0);
					spRecord.set("shangpinbianhao",shangpinbianhao);
					upRecord.add(spRecord);
				}else {
					tingshouleixing=2;
					Record phCord=new Record();
					phCord.set("shangpinbianhao",r.getStr("shangpinbianhao"));
					phRecord.add(phCord);
				}
			}
			String upSql=null;
			if(tingshouleixing==1){
				upSql="update xyy_wms_dic_shangpinziliao set dongjie=? where shangpinbianhao=?";
				//Db.batch(upSql,"dongjie,shangpinbianhao",upRecord,upRecord.size());
			}else {
				String selSql="SELECT z.rowID from xyy_wms_dic_shangpinziliao z INNER JOIN xyy_wms_dic_shangpinpihao p on z.goodsid=p.goodsId WHERE z.shangpinbianhao=? and p.pihaoId=?";
				List<Record> phRecords=Db.find(selSql,phRecord,phRecord.size());
				upSql="update xyy_wms_dic_shangpinziliao z set z.dongjie=? WHERE z.rowID= ?";
				for (Record r:phRecords){
					r.set("dongjie",0);
				}
				//Db.batch(upSql,"dongjie,rowID",phRecords,phRecords.size());
			}
		}


	}

}
