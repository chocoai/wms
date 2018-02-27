package com.xyy.wms.handler.biz;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.util.StringUtil;

import org.apache.log4j.Logger;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class BillChuKuNeiFuHePostHandler implements PostSaveEventListener {

	private static Logger LOGGER = Logger.getLogger(BillChuKuNeiFuHePostHandler.class);
	private int countJushoudan = 0; // 据收单计数

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || StringUtil.isEmpty(dsi.getFullKey()))
			return;
		// 单据头数据
		Record head = getHead(dsi);
		// 单据体数据
		List<Record> bodys = getBody(dsi);

		// 出库外复核汇总
		Record chukuwaifuhe = null;
		// 出库外复核明细
		Record chukuwaifuheDetails = null;

		// 生成出库外复核汇总
		chuKuWaiFuHe(head,bodys);

		for (Record body : bodys) {
		// 生成出库外复核明细
		chuKuWaiFuHeDetails(body);
		}
		
		//释放容器
		String rongqihao = head.get("rongqihao");	
		Object[] params= {1, rongqihao};
		Db.update("update xyy_wms_dic_rqzlwh set shifousuoding=1 where rongqibianhao=?",params);
		LOGGER.info("释放容器");
		
		//释放复核台
		String cangkubianhao = head.getStr("cangkubianhao");
		String fhqbh = head.getStr("fhqbh");
		String fhwbh = head.getStr("fuhetai");
	    Object[] params2= {0, cangkubianhao,fhqbh,fhwbh};
		Db.update("update xyy_wms_dic_fuhequjibenxinxi set shifousuoding=1 where cangkubianhao=? and fhqbh=? and fhwbh=?",params2);
		LOGGER.info("释放复核台");

	}

	private void chuKuWaiFuHeDetails(Record body) {
		Record chukuwaifuheDetails;
		chukuwaifuheDetails = new Record();
		chukuwaifuheDetails.set("billDtlID", UUIDUtil.newUUID());
		chukuwaifuheDetails.set("billID", UUIDUtil.newUUID());
		chukuwaifuheDetails.set("orgId", "08e78dd8ed6b44b2af3cba9ddc521e61");
		chukuwaifuheDetails.set("orgCode", "A-B-001");
		chukuwaifuheDetails.set("dingdanbianhao", body.get("dingdanbianhao"));
		chukuwaifuheDetails.set("shuliang", body.get("jihuashuliang"));
		chukuwaifuheDetails.set("shangpinbianhao", body.get("shangpinbianhao"));
		chukuwaifuheDetails.set("shangpinmingcheng", body.get("shangpinmingcheng"));
		chukuwaifuheDetails.set("shengchanchangjia", body.get("shengchanchangjia"));
		chukuwaifuheDetails.set("guige", body.get("guige"));
		chukuwaifuheDetails.set("danwei", body.get("danwei"));
		chukuwaifuheDetails.set("zhengjianshu", body.get("jihuashuliang"));
		chukuwaifuheDetails.set("fuheyuan", body.get("fuherenyuan"));
		chukuwaifuheDetails.set("shangpingpihao", body.get("shangpingpihao"));
		chukuwaifuheDetails.set("caozuoren", body.get("caozuoren"));
		if (chukuwaifuheDetails != null) {
			Db.save("xyy_wms_bill_chukuwaifuhe_details", chukuwaifuheDetails);
		}
	}

	// 生成出库外复核汇总
	private void chuKuWaiFuHe(Record head,List<Record> bodys) {
		Record chukuwaifuhe;
		// 查询上架汇总单的BillID下有无上架单明细，有明细才生成汇总单
		chukuwaifuhe = new Record();
		chukuwaifuhe.set("billID", UUIDUtil.newUUID());
		chukuwaifuhe.set("orgId", "08e78dd8ed6b44b2af3cba9ddc521e61");
		chukuwaifuhe.set("orgCode", "A-B-001");
		chukuwaifuhe.set("dingdanbianhao", head.get("dingdanbianhao"));
		chukuwaifuhe.set("zancunqu", head.get("zancunqu"));
		chukuwaifuhe.set("huozumingcheng", head.get("huozumingcheng"));
		chukuwaifuhe.set("danweimingcheng", head.get("danweimingcheng"));
		chukuwaifuhe.set("xianlumingcheng", head.get("xianlumingcheng"));
		chukuwaifuhe.set("kehubeizhu", head.get("kehubeizhu"));
		/*
		 * 总件数,整件数,拼箱数
		 */
		Integer jihuashuliang=null;
		for (Record body : bodys) {
			// 生成出库外复核明细
			jihuashuliang += body.getInt("jihuashuliang");
			}
		chukuwaifuhe.set("zongjianshu", jihuashuliang);
		
		

		if (chukuwaifuhe != null) {
			Db.save("xyy_wms_bill_chukuwaifuhe", chukuwaifuhe);
		}
	}

}
