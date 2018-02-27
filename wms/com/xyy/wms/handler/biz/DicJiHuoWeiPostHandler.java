package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.util.StringUtil;

/**
 * 
 * @author wbc 内复核保存后处理
 *
 */
public class DicJiHuoWeiPostHandler implements PostSaveEventListener {

	private static Logger LOGGER = Logger.getLogger(DicJiHuoWeiPostHandler.class);
	
	

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		// 单据头数据
		Record head = getHead(dsi);

		copyHead(head);

	}

	private void copyHead(Record head) {
		/* Random ran1 = new Random(9);
		for (int i = 0; i < 100; i++) {
			// 出库外复核汇总
			Record jihuowei = new Record();
			jihuowei.set("ID",  UUIDUtil.newUUID());
			jihuowei.set("orgId", UUIDUtil.newUUID());
			jihuowei.set("orgCode","A-B-001");
			jihuowei.set("status", "40");
			jihuowei.set("rowID", UUIDUtil.newUUID());
			jihuowei.set("jihuoweibianhao", "JHW-100"+(ran1.nextInt(4)+1) );
			jihuowei.set("xiangdao",ran1.nextInt(9)+1 );
			jihuowei.set("pai", ran1.nextInt(9)+1 );
			jihuowei.set("ceng",ran1.nextInt(9)+1 );
			jihuowei.set("lie",ran1.nextInt(9)+1 );
			jihuowei.set("tiji",ran1.nextInt(70)+1 );
			jihuowei.set("cangkuID", head.get("cangkuID"));
			jihuowei.set("luxianid",head.get("luxianid"));
			jihuowei.set("jihuoweileixing",ran1.nextInt(3)+1);
			jihuowei.set("qiyong",ran1.nextInt(2));
			jihuowei.set("tihuofangshi",ran1.nextInt(3)+1);
			if (jihuowei != null) {
				Db.save("xyy_wms_dic_jihuowei", jihuowei);
			}*/
		List<String> luxianIDs= new ArrayList<String>();
		luxianIDs.add("0fd2dc6590674155bc6ccecdb8b8c12a");
		luxianIDs.add("204d1e56e84b4c88bffe7b00194f689a");
		luxianIDs.add("2111a504bca64baaaba76bbdd8fa4e4d");
		luxianIDs.add("26998dac53e141e8826a654626bc42df");
		luxianIDs.add("41c16685343045a495359bf615653384");
		luxianIDs.add("5abf7edf1c0448c288073a771c161861");
		luxianIDs.add("84a8ae7ccb544a5e868a359c8460994e");
		luxianIDs.add("892b813c89504b7b9b02f1b75d3290c8");
		luxianIDs.add("9fef097376804e5b9997dc9b81b09724");
		luxianIDs.add("a362f885a8534998b73d9b1794fb5680");
		luxianIDs.add("ae75227dc6d447d0a532855cf1129fee");
		luxianIDs.add("b121d41c259e422aaa8630a83a863820");
		luxianIDs.add("d90a43d7ecc54bbf8c9c83f0a57a6a41");
		luxianIDs.add("de57aa51affb44f89d59e17ea21664c3");
		int b =170;
		int a=0;
		for (int i = 0;i<luxianIDs.size();i++) {
			List<Record> jihuoweis = Db.find("select * from xyy_wms_dic_jihuowei limit ?,?",a,b);
			a=a+170;
			for (Record jihuowei : jihuoweis) {
				String jihuoweibianhao = jihuowei.getStr("jihuoweibianhao");
				String xiangdao = jihuowei.getStr("xiangdao");
				String pai = jihuowei.getStr("pai");
				String ceng = jihuowei.getStr("ceng");
				String lie = jihuowei.getStr("lie");
				String chang = jihuowei.getStr("chang");
				String kuan = jihuowei.getStr("kuan");
				String gao = jihuowei.getStr("gao");
				BigDecimal tiji = jihuowei.getBigDecimal("tiji");
				Integer jihuoweileixing = jihuowei.getInt("jihuoweileixing");
				Integer yewuleixing = jihuowei.getInt("yewuleixing");
				Integer tihuofangshi = jihuowei.getInt("tihuofangshi");
				String shunxuhao = jihuowei.getStr("shunxuhao");
				
				Record newJHW = new Record();
				newJHW.set("ID",UUIDUtil.newUUID());
				newJHW.set("status",40);
				newJHW.set("orgId","d57e905010e34c64939d04fb5ef403d2");
				newJHW.set("orgCode","A-B-003");
				newJHW.set("nodeType",2);
				newJHW.set("rowID",UUIDUtil.newUUID());
				newJHW.set("jihuoweibianhao",jihuoweibianhao);
				newJHW.set("qiyong",1);
				newJHW.set("xiangdao",xiangdao);
				newJHW.set("pai",pai);
				newJHW.set("ceng",ceng);
				newJHW.set("lie",lie);
				newJHW.set("chang",chang);
				newJHW.set("kuan",kuan);
				newJHW.set("gao",gao);
				newJHW.set("tiji",tiji);
				newJHW.set("jihuoweileixing",jihuoweileixing);
				newJHW.set("yewuleixing",yewuleixing);
				newJHW.set("tihuofangshi",tihuofangshi);
				newJHW.set("shunxuhao",shunxuhao);
				newJHW.set("shifousuoding",0);
				newJHW.set("cangkuID","4e2be093bf314e9c857fac7eba14b28d");
				newJHW.set("luxianid",luxianIDs.get(i));
				if (newJHW != null) {
					Db.save("xyy_wms_dic_jihuowei", newJHW);
				}
			}
		}
		}

	}


