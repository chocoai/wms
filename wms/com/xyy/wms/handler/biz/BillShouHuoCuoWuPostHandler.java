package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.util.PullWMSDataUtil;
import com.xyy.erp.platform.system.task.EDB;
import com.xyy.util.StringUtil;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;

public class BillShouHuoCuoWuPostHandler implements PostSaveEventListener {

	/**
	 * 保存后回写入库上架单的信息
	 */
	private static Logger loger = Logger.getLogger(BillShouHuoCuoWuPostHandler.class);
	@SuppressWarnings("null")
	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance)context.get("$model");
        if(dsi == null || StringUtil.isEmpty(dsi.getFullKey())){
        	return;
        } 
        // 单据头数据
        //Record record = getHead(dsi);
        // 单据体数据
        List<Record> body = getBody(dsi);
        for(Record r :body) {
        	String shangjiaId = r.getStr("shangjiaId");
        	String sjsql = "select * from xyy_wms_bill_rukushangjiadan_details where BillDtlID = ?";
			Record re = Db.findFirst(sjsql, shangjiaId);
			// 上架单数据
			String sjpihao = re.getStr("pihao");
			
			String sjhuowei = re.get("shijihuowei");
			String kuqumingcheng = re.getStr("kuqumingcheng");
    		String jhhuowei = re.getStr("jihuahuowei");
			
			// 收货错误处理单的数据
			BigDecimal zhengjianshu = r.get("zhengjianshu");
        	BigDecimal lingsanshu = r.get("lingsanshu");
        	BigDecimal shuliang = r.get("shuliang");
        	String pihao = r.getStr("pihao");
        	Date shengchanriqi = r.get("shengchanriqi");
        	Date youxiaoqizhi = r.get("youxiaoqizhi");
        	int yanshoupingding = r.getInt("yanshoupingding");
        	int pingdingyuanyin = r.getInt("pingdingyuanyin");
        	String danjubianhao = r.getStr("dingdanbianhao");
        	
        	// 商品对象
    		String shangpinbianhao = r.get("shangpinbianhao");
    		String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? ";
    		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
    		String goodsid = shangpinObj.getStr("goodsid");
    		String orgId = shangpinObj.getStr("orgId");
    		String yewubianhao = r.getStr("shangjiaId");
    		// 批号对象
			String pihaoSql = "SELECT * FROM xyy_wms_dic_shangpinpihao WHERE pihao=? and goodsId =?";
			Record pihaoObj = Db.findFirst(pihaoSql, sjpihao,goodsid);
			String pihaoId = pihaoObj.getStr("pihaoId");
			
			
			
    		// 数量先不做考虑
			if(!sjpihao.equals(pihao)){
    			// 如果批号不相同，有货位时，删除预占，清除货位
    			if(null != jhhuowei && jhhuowei.length()>0){
    				// 删除预占
    				// 货位对象
    				String huoweiSql = "SELECT * FROM xyy_wms_dic_huoweiziliaoweihu WHERE huoweibianhao=? limit 1";
    				Record huoweiObj = Db.findFirst(huoweiSql, jhhuowei);
    				String huoweiId = huoweiObj.getStr("ID");	
    				KuncunParameter paras = new KuncunParameter(orgId,danjubianhao,goodsid,pihaoId,huoweiId,yewubianhao);
    				KucuncalcService.kcCalc.deleteRkKCRecord(paras);
    				//如果商品为新批号时，更新商品批号表xyy_wms_dic_shangpinpihao
	            	boolean falg = BillRuKuShouHuoPostHandler.getShangPinPHById(pihao,shangpinbianhao);
	            	if(!falg){
	            		Record sppihao = new Record();
	            		String pihaoID = "PH" + PullWMSDataUtil.orderno2();//批号id
	            		sppihao.set("ID", UUIDUtil.newUUID());
	            		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
	            		sppihao.set("status",40);
	            		sppihao.set("orgId", orgId);
	            		sppihao.set("createTime", currentTime);
	            		sppihao.set("updateTime", currentTime);
	            		sppihao.set("shengchanriqi", r.get("shengchanriqi")); //生产日期
	            		sppihao.set("youxiaoqizhi", r.get("youxiaoqizhi"));  //有效期至
	            		sppihao.set("pihao", pihao); //批号
	            		sppihao.set("goodsId", goodsid); //商品id
	            		sppihao.set("pihaoId", pihaoID); 
	            		Db.save("xyy_wms_dic_shangpinpihao",sppihao);
	            	}
            	jhhuowei = "";
            	kuqumingcheng= "";
            	sjhuowei = "";
    		}
		}
        	// 更新xyy_wms_bill_rukushangjiadan_details 表信息
        	String sql = "update xyy_wms_bill_rukushangjiadan_details set "
        			+ "zhengjianshu=?,lingsanshu=?,shuliang=?,pihao=?,shengchanriqi=?,youxiaoqizhi=?,"
        			+ "pingdingyuanyin=?,yanshoupingding=?,shijihuowei=?,jihuahuowei=?,kuqumingcheng=? where BillDtlID = ?";
        	EDB.update("xyy_wms_bill_rukushangjiadan_details",sql,zhengjianshu,lingsanshu,shuliang,pihao,shengchanriqi,youxiaoqizhi,
        			pingdingyuanyin,yanshoupingding,sjhuowei,jhhuowei,kuqumingcheng,shangjiaId);
        	Log.info(">>>>>>>>>>>>收货错误处理 +" +shangjiaId);
        }
	}
}
