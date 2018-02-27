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
public class DicHuoWeiPostHandler implements PostSaveEventListener {

	private static Logger LOGGER = Logger.getLogger(DicHuoWeiPostHandler.class);
	
	

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		// 单据头数据
		Record head = getHead(dsi);

		copyHead(head);

	}

	private void copyHead(Record head) {
		List<String> kuquIds= new ArrayList<String>();
		kuquIds.add("f297f1f4-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f2980017-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f29802ab-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f29805e2-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f2980831-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f2980a6c-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f2981159-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f29813a8-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f2b50524-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f2b50a73-0656-11e8-9649-9c5c8e6fe71d");
		kuquIds.add("f2b50ce6-0656-11e8-9649-9c5c8e6fe71d");
		int a=0;
		for (int i = 0;i<kuquIds.size();i++) {
			List<Record> huoweis = Db.find("select * from xyy_wms_dic_huoweiziliaoweihu limit ?,1132",a);
			a=a+1132;
			for (Record huowei : huoweis) {
				String huoweibianhao = huowei.getStr("huoweibianhao");
				String xiangdao = huowei.getStr("xiangdao");
				String pai =huowei.getStr("pai");
				String ceng = huowei.getStr("ceng");
				String lie = huowei.getStr("lie");
				BigDecimal chang = huowei.getBigDecimal("chang");
				BigDecimal kuan = huowei.getBigDecimal("kuan");
				BigDecimal gao = huowei.getBigDecimal("gao");
				BigDecimal tiji = huowei.getBigDecimal("tiji");
				Integer huoweileixing = huowei.getInt("huoweileixing");
				Integer cunchufenlei = huowei.getInt("cunchufenlei");
				Integer cunchutiaojian = huowei.getInt("cunchutiaojian");
				Integer ABC = huowei.getInt("ABC");
				Integer chengzairongqi = huowei.getInt("chengzairongqi");
				Integer chailingfenzu = huowei.getInt("chailingfenzu");
				Integer pinleishangxian = huowei.getInt("pinleishangxian");
				Integer picishangxian = huowei.getInt("picishangxian");
				
				
				Record newHW = new Record();
				newHW.set("ID",UUIDUtil.newUUID());
				newHW.set("status",40);
				newHW.set("orgId","d57e905010e34c64939d04fb5ef403d2");
				newHW.set("orgCode","A-B-003");
				newHW.set("nodeType",2);
				newHW.set("rowID",UUIDUtil.newUUID());
				newHW.set("huoweibianhao",huoweibianhao);
				newHW.set("huoweiqiyong",1);
				newHW.set("xiangdao",xiangdao);
				newHW.set("pai",pai);
				newHW.set("ceng",ceng);
				newHW.set("lie",lie);
				newHW.set("chang",chang);
				newHW.set("kuan",kuan);
				newHW.set("gao",gao);
				newHW.set("tiji",tiji);
				newHW.set("huoweileixing",huoweileixing);
				newHW.set("cunchufenlei",cunchufenlei);
				newHW.set("cunchutiaojian",cunchutiaojian);
				newHW.set("ABC",ABC);
				newHW.set("chengzairongqi",chengzairongqi);
				newHW.set("chailingfenzu",chailingfenzu);
				newHW.set("pinleishangxian",pinleishangxian);
				newHW.set("picishangxian",picishangxian);
				newHW.set("shifousuoding",0);
				newHW.set("cangkuID","4e2be093bf314e9c857fac7eba14b28d");
				newHW.set("kuquId",kuquIds.get(i));
				if (newHW != null) {
					Db.save("xyy_wms_dic_huoweiziliaoweihu", newHW);
				}
			}
		}
		}

	}


