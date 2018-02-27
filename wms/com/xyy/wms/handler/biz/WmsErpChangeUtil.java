package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.UUIDUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WmsErpChangeUtil {

    /**
     * 移库确认向中间表写数据
     **/
    public List<Record> YkInsertData(Record head, List<Record> bodys) {
        //判断传入参数是否为空
        if (bodys == null || head == null) {
            return null;
        }
        //插入中间表数据集合
        List<Record> zjLists = new ArrayList<>();
        for (Record r : bodys) {
            //中间表数据
            Record zjRecord = new Record();
            String spid = r.getStr("goodsid");
            zjRecord.set("spid", spid);
            zjRecord.set("orgId", r.getStr("orgId"));
            zjRecord.set("orgCode", r.getStr("orgCode"));
            BigDecimal shijishuliang = r.getBigDecimal("shijishuliang") == null ? BigDecimal.ZERO : r.getBigDecimal("shijishuliang");
            zjRecord.set("sl_kc", shijishuliang);
            int yckqlb = r.getInt("yckqlbbh");
            if (yckqlb == 3) {
                zjRecord.set("kczt_old", 2);
            } else if (yckqlb == 12) {
                zjRecord.set("kczt_old", 3);
            } else {
                zjRecord.set("kczt_old", 1);
            }
            int yrkqlb = r.getInt("yrkqlbbh");
            if (yrkqlb == 3) {
                zjRecord.set("kczt_old", 2);
            } else if (yrkqlb == 12) {
                zjRecord.set("kczt_new", 3);
            } else {
                zjRecord.set("kczt_new", 1);
            }
            String ph = r.getStr("pihao");
            zjRecord.set("ph", ph);
            Date shengchanriqi = r.getDate("shengchanriqi");
            zjRecord.set("rq_sc", shengchanriqi);
            Date youxiaoqizhi = r.getDate("youxiaoqizhi");
            zjRecord.set("yxqz", youxiaoqizhi);
            Date yikuTime = r.getDate("zhidanriqi");
            zjRecord.set("change_time", yikuTime);
            zjRecord.set("sp_sort", UUIDUtil.newUUID());
            zjRecord.set("yzid", UUIDUtil.newUUID());
            zjLists.add(zjRecord);
        }
        return zjLists;
    }

    /**
     * 损溢确认向中间表写数据
     **/
    public List<Record> SyInsertData(Record head, List<Record> bodys) {
        //判断传入参数是否为空
        if (bodys == null || head == null) {
            return null;
        }
        //插入中间表数据集合
        List<Record> zjLists = new ArrayList<>();
        for (Record r : bodys) {
            //定义中间表数据对象
            Record zjRecord = new Record();
            String spid = r.getStr("goodsid");
            zjRecord.set("spid", spid); //商品id
            BigDecimal sunyishuliang = r.getBigDecimal("sunyishuliang") == null ? BigDecimal.ZERO : r.getBigDecimal("sunyishuliang");
            if (sunyishuliang.compareTo(BigDecimal.ZERO) > 0) {
                zjRecord.set("psshl", sunyishuliang);                              //盘损
            }
            if (sunyishuliang.compareTo(BigDecimal.ZERO) < 0) {
                sunyishuliang = BigDecimal.ZERO.subtract(sunyishuliang);
                zjRecord.set("pyshl", sunyishuliang);                              //盘溢
            }
            zjRecord.set("rq", head.getDate("zhidanriqi"));               //制单日期

            zjRecord.set("ph", r.getStr("pihao"));                        //批号

            zjRecord.set("yxqz", r.getDate("youxiaoqizhi"));              //有效期至

            zjRecord.set("rq_sc", r.getDate("shengchanriqi"));            //生产日期

            zjRecord.set("lastmodifytime", r.getDate("updateTime"));      //最后修改时间

            int kqlb = r.getInt("kqlbbh");
            if (kqlb == 1 || kqlb == 2) {
                zjRecord.set("kczt", 1);
            }
            if (kqlb == 3) {
                zjRecord.set("kczt", 2);
            }
            if (kqlb == 12) {
                zjRecord.set("kczt", 3);
            }
            zjLists.add(zjRecord);
        }
        return zjLists;
    }
    
    /**
     * 销售退回-->erp销售退回入库单
     * @param head
     * @param bodys
     * @return
     */
    public List<Record> XsthInsertData(Record head, List<Record> bodys) {
    	
    	 //判断传入参数是否为空
        if (bodys == null || head == null) {
            return null;
        }
        //插入中间表数据集合
        List<Record> zjLists = new ArrayList<>();
        
        for(Record r : bodys){
        	//定义中间表数据对象
            Record zjRecord = new Record();
            String shangpinbianhao = r.getStr("shangpinbianhao");
            
            // 商品对象
            String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
    		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
    		String spid = shangpinObj.get("goodsid");
    		zjRecord.set("orgId", head.get("orgId"));
    		zjRecord.set("orgCode", head.get("orgCode"));    		
            zjRecord.set("djbh", head.get("BillID"));   // wms上架单主键
            zjRecord.set("dwbh", head.getStr("danweibianhao"));// 单位编码
            zjRecord.set("rq", head.getDate("dingdanriqi"));// 订单日期
            zjRecord.set("ywy", head.get("yewuyuan")); //业务员
            zjRecord.set("zjy", head.get("yanshouyuan")); // 验收员
//            zjRecord.set("thlb", head.get("shangjiayuan")); // 上架员
            zjRecord.set("yzid", head.get("huozhubianhao")); // 货主编号
            zjRecord.set("dj_sort", UUIDUtil.newUUID()); // 单据行号
            zjRecord.set("spid", spid); //商品id
            zjRecord.set("sl", r.get("shuliang")); // 数量
            zjRecord.set("js", r.get("zhengjianshu")); //整件数
            zjRecord.set("lss", r.get("lingsanshu")); // 零散数
            zjRecord.set("ph", r.get("pihao")); // 批号
            zjRecord.set("rq_sc", r.get("shengchanriqi")); // 生产日期
            zjRecord.set("yxqz", r.get("youxiaoqizhi")); // 有效期至
            zjRecord.set("yspd", r.get("yanshoupingding")); // 验收评定
            zjRecord.set("lastmodifytime", new Date()); //上传单据时间
            zjRecord.set("is_zx", 0); // 状态回写
            zjRecord.set("bmid", UUIDUtil.newUUID());
            zjLists.add(zjRecord);
        }
        
		return zjLists;
    	
    }
    
    
    /**
     * 采购退出-->erp采购入库单
     * @param head
     * @param bodys
     * @return
     */
    public List<Record> CgrkInsertData(Record head, List<Record> bodys) {
    	
    	 //判断传入参数是否为空
        if (bodys == null || head == null) {
            return null;
        }
        //插入中间表数据集合
        List<Record> zjLists = new ArrayList<>();
        
        for(Record r : bodys){
        	//定义中间表数据对象
            Record zjRecord = new Record();
            String shangpinbianhao = r.getStr("shangpinbianhao");
            
            // 商品对象
            String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
    		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
    		String spid = shangpinObj.get("goodsid");
    		zjRecord.set("orgId", head.get("orgId"));
    		zjRecord.set("orgCode", head.get("orgCode"));
            zjRecord.set("djbh", head.get("BillID"));   // wms上架单主键
            zjRecord.set("dwbh", head.getStr("danweibianhao"));// 单位编码
            zjRecord.set("rq", head.getDate("dingdanriqi"));// 订单日期
            zjRecord.set("cgy", head.get("caigouyuan")); //采购员
            zjRecord.set("shy", head.get("shouhuoyuan")); // 收货员
            zjRecord.set("zjy", head.get("yanshouyuan")); // 验收员
            zjRecord.set("czy", head.get("shangjiayuan")); // 上架员
            zjRecord.set("djbh_sj", head.get("dingdanbianhao")); //采购订单据编号
            zjRecord.set("rktype", head.get("dingdanleixing")); //入库类型 
            zjRecord.set("yzid", head.get("huozhubianhao")); // 货主编号
            zjRecord.set("dj_sort", UUIDUtil.newUUID()); // 单据行号
            zjRecord.set("spid", spid); //商品id
            zjRecord.set("sl", r.get("shuliang")); // 数量
            zjRecord.set("js", r.get("zhengjianshu")); //整件数
            zjRecord.set("lss", r.get("lingsanshu")); // 零散数
            zjRecord.set("ph", r.get("pihao")); // 批号
            zjRecord.set("rq_sc", r.get("shengchanriqi")); // 生产日期
            zjRecord.set("yxqz", r.get("youxiaoqizhi")); // 有效期至
            zjRecord.set("cgdd_sort", UUIDUtil.newUUID()); // 采购订单行号
            zjRecord.set("yspd", r.get("yanshoupingding")); // 验收评定
            zjRecord.set("lastmodifytime", new Date()); //上传单据时间
            zjRecord.set("is_zx", 0); // 状态回写
            zjRecord.set("recnum", UUIDUtil.newUUID());
            zjLists.add(zjRecord);
        }
        
		return zjLists;
    	
    }
    
    
    /**
     * 采购退出订单-->erp购进退出入库单
     * @param head
     * @param bodys
     * @return
     */
    public List<Record> GjtcInsertData(Record head, List<Record> bodys) {
    	
    	 //判断传入参数是否为空
        if (bodys == null || head == null) {
            return null;
        }
        //插入中间表数据集合
        List<Record> zjLists = new ArrayList<>();
        
        for(Record r : bodys){
        	//定义中间表数据对象
            Record zjRecord = new Record();
            String shangpinbianhao = r.getStr("shangpinbianhao");
            
            // 商品对象
            String shangpinSql = "SELECT * FROM xyy_wms_dic_shangpinziliao WHERE shangpinbianhao=? limit 1";
    		Record shangpinObj = Db.findFirst(shangpinSql, shangpinbianhao);
    		String spid = shangpinObj.get("goodsid");
    		zjRecord.set("orgId", head.get("orgId"));
    		zjRecord.set("orgCode", head.get("orgCode"));
            zjRecord.set("djbh", head.get("BillID"));   // wms上架单主键
            zjRecord.set("dwbh", head.getStr("danweibianhao"));// 单位编码
            zjRecord.set("rq", head.getDate("dingdanriqi"));// 订单日期
            zjRecord.set("cgy", head.get("caigouyuan")); //采购员
            zjRecord.set("thy", head.get("kaipiaoyuan")); // 开票员
            zjRecord.set("dj_sort", UUIDUtil.newUUID()); // 单据行号
            zjRecord.set("spid", spid); //商品id
            zjRecord.set("sl", r.get("shuliang")); // 数量
            zjRecord.set("js", r.get("zhengjianshu")); //整件数
            zjRecord.set("lss", r.get("lingsanshu")); // 零散数
            zjRecord.set("ph", r.get("pihao")); // 批号
            zjRecord.set("rq_sc", r.get("shengchanriqi")); // 生产日期
            zjRecord.set("yxqz", r.get("youxiaoqizhi")); // 有效期至
            zjRecord.set("lastmodifytime", new Date()); //上传单据时间
            zjRecord.set("is_zx", 0); // 状态回写
            zjRecord.set("billsn", UUIDUtil.newUUID());
            
            zjLists.add(zjRecord);
        }
        
		return zjLists;
    	
    }


    /**
     * @param head
     * @param bodys
     * @return 外复核同步中间表写数据
     */
    public List<Record> WaiFuHeInsertData(Record head, List<Record> bodys) {
        //判断传入参数是否为空
        if (bodys == null || head == null) {
            return null;
        }
        //向集合中插数据
        List<Record> wfhLists = new ArrayList<>();
        for (Record r : bodys) {
            Record wfhRecord = new Record();
            String DJBH = r.get("dingdanbianhao");
            Record record = Db.findFirst("select * from  xyy_wms_bill_xiaoshoudingdan where danjubianhao=?", DJBH);
            String DWBH = record.get("kehubianhao");
            String RQ = record.get("kaipiaoriqi");
            String YWY = record.get("yewuyuan");
            String THFS = record.get("tihuofangshi");
            String beizhu = record.get("remark");
            String BILLSN = record.get("rowID");
            String ddlx = r.get("");
            String yzid = r.get("001");
            String DJ_SORT = r.get("rowID");
            String spid = r.getStr("goodsid");
            String SL = r.get("shuliang");
            String JS = r.get("zhengjianshu");
            String LSS = r.get("lingjianshu");
            String PH = r.get("shangpingpihaosn");
            String lastmodifytime = r.get("createTime");
            String RQ_SC = r.get("shengchanriqi");
            String YXQZ = r.get("youxiaoqizhi");
            String ZCQMC = head.get("zancunqu");
            String ZXS = head.get("zongjianshu");
            String FUHR = head.get("caozuoren");
            String is_zx = r.get("1");

            wfhRecord.set("DJBH", DJBH);
            wfhRecord.set("DWBH", DWBH);
            wfhRecord.set("RQ", RQ);
            wfhRecord.set("YWY", YWY);
            wfhRecord.set("THFS", THFS);
            wfhRecord.set("beizhu", beizhu);
            wfhRecord.set("ddlx", ddlx);
            wfhRecord.set("yzid", yzid);
            wfhRecord.set("DJ_SORT", DJ_SORT);
            wfhRecord.set("SPID", spid);
            wfhRecord.set("SL", SL);
            wfhRecord.set("JS", JS);
            wfhRecord.set("LSS", LSS);
            wfhRecord.set("PH", PH);
            wfhRecord.set("lastmodifytime", lastmodifytime);
            wfhRecord.set("RQ_SC", RQ_SC);
            wfhRecord.set("YXQZ", YXQZ);
            wfhRecord.set("is_zx", is_zx);
            wfhRecord.set("ZCQMC", ZCQMC);
            wfhRecord.set("ZXS", ZXS);
            wfhRecord.set("FUHR", FUHR);
            wfhRecord.set("BILLSN", BILLSN);

            wfhLists.add(wfhRecord);
        }
        return wfhLists;
    }
}