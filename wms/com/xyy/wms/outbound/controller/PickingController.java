package com.xyy.wms.outbound.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.util.StringUtil;

public class PickingController extends Controller {

    // 拣货单集合
    private static List<Record> jianhuodanList = new ArrayList<>();
    // 拣货商品集合
    private static List<Record> jianhuoshanpinList = new ArrayList<>();
    //整件拣货单集合
    private static List<Record> wholeTaskList = new ArrayList<Record>();
    //最大小车位
    private static final int carNumberCapped = 4;

    private static final String NEXTLINE = "nextLine";
    private static final String PRELINE = "preLine";

    public static final String JH_CODE_WHOLE = "whole";
    public static final String JH_CODE_CHUKUJIANHUO = "chukujianhuo";
    public static final String JH_CODE_GOODSLIST = "goodsList";
    public static final String JH_CODE_GETGOODS = "getGoods";
    public static final String JH_CODE_FUHETAI = "getfuhetai";
    public static final String JH_CODE_COMPLETETASK = "completeTask";
    public static final String JH_CODE_REPICKING = "rePicking";

    /**
     * 公共路由
     */
    public void route() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        String requestCode = msg.getString("requestCode");
        if (StringUtil.isEmpty(requestCode)) {
            this.jumpTo404();
            return;
        }
        switch (requestCode) {
            case JH_CODE_WHOLE:
                this.wholePicking();
                break;
            case JH_CODE_CHUKUJIANHUO:
                this.chukujianhuo();
                break;
            case JH_CODE_GOODSLIST:
                this.goodsList();
                break;
            case JH_CODE_GETGOODS:
                this.getGoods();
                break;
            case JH_CODE_FUHETAI:
                this.getFuhetai();
                break;
            case JH_CODE_COMPLETETASK:
                this.completeTask();
                break;
            case JH_CODE_REPICKING:
            	this.rePicking();
            	break;
            default:
                this.render("/404.html");
                break;
        }
    }

	/**
     * 拆零拣货
     */
    private void chukujianhuo() {
        // 判断小车是否装满
        if (jianhuodanList.size() >= carNumberCapped) {
            this.setAttr("status", 0);
            this.setAttr("result", "小车" + carNumberCapped + "个位置已装满，请开始拣货！");
            this.setAttr("taskList",jianhuodanList);
            this.renderJson();
            return;
        }
        String requestMsg = this.getPara("requestMsg"); 
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject head = msg.getJSONObject("head");
        JSONObject body = msg.getJSONObject("body");
        JSONObject userInformation =JSONObject.parseObject(head.getString("userInformation"));
        String userId = userInformation.getString("id");
        String rongqihao = body.getString("rongqihao");
        //根据userId获取当前登录人信息
        Record user = null;
        if(!StringUtil.isEmpty(userId)){
        	 user = Db.findFirst("select * from tb_sys_user WHERE id = ?",userId);
        }
        //查询当前登录人是否有未完成的拣货任务
        List<Record> leftTasks = Db.find("SELECT * from xyy_wms_bill_dabaorenwu WHERE tasktype = 10 "
        		+ "and status =34 and caozuoren = ? and czrmc = ? LIMIT 4",user.getStr("realName"),user.getStr("userId"));
        if(leftTasks.size() >0){
        	jianhuodanList.addAll(leftTasks);
        }
        if (!StringUtil.isEmpty(rongqihao)) {
        	boolean flag = true;
    		StringBuffer sb = new StringBuffer("SELECT * from xyy_wms_dic_rqzlwh r where r.rongqibianhao=? ");
    		//根据容器号获取基础信息中该容器的信息
    		Record rongqi = Db.findFirst(sb.toString(), rongqihao);
    		if (rongqi == null) {
    			flag = false;
    		}else{
    			if(rongqi.getInt("shifouqiyong")== 0 || rongqi.getInt("shifousuoding") == 1){
    				 this.setAttr("status", 0);
        	         this.setAttr("result",  "此容器已被占用！");
        	         this.renderJson();
        	         return;
    			}
    		}
    		if (flag) {
    			//按优先级定位拣货单
    			Record record = Db.findFirst("SELECT * FROM xyy_wms_bill_dabaorenwu t1 WHERE "
    					+ "t1.status =32 and EXISTS (SELECT 1 FROM "
    					+ "xyy_wms_dic_kqzyqxwh t2,xyy_wms_dic_kuqujibenxinxi t3 WHERE t2.kuqubianhao = t3.kuqubianhao "
    					+ "and t2.gonghao = ? AND (t3.kqlbbh = 2 AND t1.taskType = 10 AND t2.jianhuo = 1 AND t1.xiajiakuqu=t2.kuqubianhao))"
    					+ "and (t1.caozuoren = ? or t1.caozuoren is null)  ORDER BY t1.youxianji Desc ,t1.createTime DESC", user.getStr("userId"),user.getStr("userId"));
    	            if(record == null){
    	            	  this.setAttr("status", 0);
    	                  this.setAttr("result", "当前没有拣货单可拣");
    	                  this.renderJson();
    	                  return;
    	            }else{
    	            	Db.update("UPDATE xyy_wms_bill_dabaorenwu SET rongqihao = ?,czrmc = ?,caozuoren = ? ,startDate = ?, status = 34 WHERE BillID = ?",rongqihao,user.getStr("realName"),user.getStr("userId"),getCurrentTime(),record.getStr("BillID"));
    	            	List<String> pingguishu = new ArrayList<String>();//品规数
    	            	List<Record> jhdDetails = Db.find("SELECT t1.* from xyy_wms_bill_dabaorenwu_details t1 LEFT JOIN xyy_wms_bill_dabaorenwu t2 on t1.billId = t2.BillID WHERE t2.BillID = ?", record.getStr("BillID"));
    	            	for (Record jhdDetail : jhdDetails) {
    	            		if(!pingguishu.contains(jhdDetail.getStr("goodsid"))){
    	            			pingguishu.add(jhdDetail.getStr("goodsid"));
    	            		}
    					}
    	            	record.set("carNumber", jianhuodanList.size() + 1);
    	            	record.set("pingguishu", pingguishu.size());
    	            	record.set("tiaomushu", jhdDetails.size());
    	            	record.set("rongqihao", rongqihao);
    	            	jianhuodanList.add(record);
    	            }
    			// 容器能使用，更新容器状态为已占用
    			StringBuffer sql_rongqi_update = new StringBuffer("update xyy_wms_dic_rqzlwh r SET r.shifousuoding =?  WHERE r.rongqibianhao =?");
    			Db.update(sql_rongqi_update.toString(),1,rongqihao);
    		}else{
    			// 容器已占用，return
    			 this.setAttr("status", 0);
    	         this.setAttr("result",  "容器不存在，请输入正确的容器号！");
    	         this.renderJson();
    	         return;
    		}
        }
        this.setAttr("status", 1);
        this.setAttr("result", jianhuodanList);
        this.renderJson();
    }

    /**
     * 已索取订单下的所有拣货商品列表
     */
    private void goodsList() {
        for (Record jianhuodan : jianhuodanList) {
            StringBuffer sql = new StringBuffer("SELECT "
            		+ "t1.status,t1.guige,t2.rongqihao,t1.BillDtlID,t1.shangpinbianhao,t1.xiajiahuoweibianhao,t1.goodsid,t1.BillID,"
            		+ "t1.shangpinmingcheng,t1.baozhuangshuliang,t1.danwei,t1.shangpingpihaosn,("+jianhuodan.getInt("carNumber")+") carNumber,"
            		+ "t1.jianshu,t1.shuliang,t1.zongshuliang,t1.pizhunwenhao,t1.shengchanriqi,t1.youxiaoqizhi,"
            		+ "t1.shengchanchangjia FROM xyy_wms_bill_dabaorenwu_details t1 "
            		+ "LEFT JOIN xyy_wms_bill_dabaorenwu t2 ON t1.BillID = t2.BillID WHERE "
            		+ "t1.BillID = ? AND t1.STATUS = 37");
            //拣货单下所有明细
            List<Record> taskList = Db.find(sql.toString(), jianhuodan.getStr("BillID"));
            if(taskList != null && taskList.size() > 0) {
                for(Record t : taskList) {
                    // 判断拣货单的商品是否已存在
                    boolean ishas = false;
                    for(Record jhsp : jianhuoshanpinList) {
                        if(jhsp.get("BillDtlID").equals(t.get("BillDtlID"))) {
                            ishas = true;
                            break;
                        }
                    }
                    if(!ishas) jianhuoshanpinList.add(t);
                }
            }
        }
        this.setAttr("status", 1);
        this.setAttr("result", jianhuoshanpinList);
        this.renderJson();
    }

    /**
     * 查询单个商品的明细
     */
    private void getGoods() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        // 上一条 、 下一条
        String handle = body.get("handle") == null ? "" : body.getString("handle");
        String BillDtlID = body.get("BillDtlID") == null ? "" : body.getString("BillDtlID");

        for(int i=0; i<jianhuoshanpinList.size(); i++) {
            if(StringUtil.isEmpty(handle)) {
                if(BillDtlID.equals(jianhuoshanpinList.get(i).getStr("BillDtlID"))) {
                    this.setAttr("status", 1);
                    this.setAttr("result", jianhuoshanpinList.get(i));
                    this.renderJson();
                }
            } else {
                switch (handle) {
                    case NEXTLINE:      // 下一条
                        if(BillDtlID.equals(jianhuoshanpinList.get(i).getStr("BillDtlID"))) {
                            if(i == jianhuoshanpinList.size() - 1) {
                                this.setAttr("status", 0);
                                this.setAttr("result", "当前已经是最后一条");
                                this.renderJson();
                                return;
                            } else {
                                this.setAttr("status", 1);
                                this.setAttr("result", jianhuoshanpinList.get(i+1));
                                this.renderJson();
                                return;
                            }
                        }
                        break;
                    case PRELINE:       // 上一条
                        if(BillDtlID.equals(jianhuoshanpinList.get(i).getStr("BillDtlID"))) {
                            if(i == 0) {
                                this.setAttr("status", 0);
                                this.setAttr("result", "当前已经是第一条");
                                this.renderJson();
                                return;
                            } else {
                                this.setAttr("status", 1);
                                this.setAttr("result", jianhuoshanpinList.get(i-1));
                                this.renderJson();
                                return;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 拣货明细确认
     */
    private void completeTask() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject head = msg.getJSONObject("head");
        JSONObject userInformation =JSONObject.parseObject(head.getString("userInformation"));
        String userId = userInformation.getString("id");
        JSONObject body = msg.getJSONObject("body");
        String BillDtlID = body.get("BillDtlID") == null ? "" : body.getString("BillDtlID");
        Record user = null;
        if(!StringUtil.isEmpty(userId)){
        	 user = Db.findFirst("select * from tb_sys_user WHERE id = ?",userId);
        }
        // 更新体
        StringBuffer sql = new StringBuffer("update xyy_wms_bill_dabaorenwu_details set status = 38,caozuoren= ? WHERE BillDtlID = ?");
        Db.update(sql.toString(),user.getStr("realName"), BillDtlID);

        // 如果 头 单据对应的所有体 都拣货完成，则更新 头 单据状态
        for(Record jhsp : jianhuoshanpinList) {
            if(BillDtlID.equals(jhsp.getStr("BillDtlID"))) {
                // 设置 是否拣货 为是
                jhsp.set("status", 38);
                String BillID = jhsp.getStr("BillID");
                StringBuffer sql_find = new StringBuffer("SELECT * from xyy_wms_bill_dabaorenwu_details WHERE BillID = ?;");
                List<Record> list = Db.find(sql_find.toString(), BillID);
                boolean flag = true;
                for (Record record : list) {
                    if(record.getInt("status") == 38){
                        continue;
                    }else{
                        flag = false;
                        break;
                    }
                }
                // 更新 头 单据状态
                if(flag){
                    Db.update("update xyy_wms_bill_dabaorenwu set status = 36,querenshijian = ? WHERE BillID = ?",getCurrentTime(), BillID);
                }
            }
        }

        // 当前商品拣货完成后，往后选一条未 拣货 的商品
	  for(int i=0; i<jianhuoshanpinList.size(); i++) {
		    Record jhsp = jianhuoshanpinList.get(i);
		    if(BillDtlID.equals(jhsp.getStr("BillDtlID"))) {
		        // 循环当前体后面的 体
		        for(int j=i+1; j<jianhuoshanpinList.size(); j++) {
		            if(jianhuoshanpinList.get(j).getInt("status") == 37) {
		                this.setAttr("status", 1);
		                this.setAttr("result", jianhuoshanpinList.get(j));
		                this.renderJson();
		                return;
		            }
		        }
		        // 当前数据后面的数据循环完了后，没有返回，从头开始遍历
		        for(int j=0; j<jianhuoshanpinList.size(); j++) {
		            // 全部拣货完毕
		            if(BillDtlID.equals(jianhuoshanpinList.get(j).getStr("BillDtlID"))) {
		                this.setAttr("status", 1);
		                this.setAttr("result", "complete");
		                this.renderJson();
		                return;
		            }
		            // 从头开始定位未拣货的数据
		            if(jianhuoshanpinList.get(j).getInt("status") == 37) {
		                this.setAttr("status", 1);
		                this.setAttr("result", jianhuoshanpinList.get(j));
		                this.renderJson();
		                return;
		            }
		        }
		    }
	  	}
    }

    /**
     * 获取复核台
     */
    private void getFuhetai() {
        for (Record record : jianhuodanList){
        	//判断当前拣货单是否已分配复核位
        	if(record.getStr("fuhetai") == null){
        	//第一步，找出订单下所有的拣货单
    		StringBuffer sbu = new StringBuffer("SELECT * from xyy_wms_bill_dabaorenwu where dingdanbianhao = ? "
    				+ "and taskType = 10");
    		List<Record> list = Db.find(sbu.toString(), record.getStr("dingdanbianhao"));
    		//第二步 遍历拣货单列表,所有的拣货单是否已经分配复核位
    		String fhqbh = null;
    		for (Record jianhuodan : list) {
    			if(jianhuodan.getStr("fhqbh") != null){
    				fhqbh = jianhuodan.getStr("fhqbh");
    				break;
    			}else{
    				continue;
    			}
    		}
    		if(StringUtils.isNotEmpty(fhqbh)){
    			//当复核区编号不为空，遍历该去的所有复核位，如果有，分配给他，如果没有，设置下一个复合
    			StringBuffer fhwsql = new StringBuffer("SELECT * from xyy_wms_dic_fuhequjibenxinxi WHERE fhqbh = ? and shifouqiyong = 1 and shifousuoding = 0 "
    					+ "ORDER BY  fhqbh,zuhao,cenghao,liehao");
    			List<Record> fhwList = Db.find(fhwsql.toString(), fhqbh);
    			if(fhwList.size() > 0){
    				record.set("fuhetai",fhwList.get(0).getStr("fhwbh"));
    				record.set("fhqbh",fhwList.get(0).getStr("fhqbh"));
    				Db.update("UPDATE xyy_wms_bill_dabaorenwu SET fuhetai = ? ,fhqbh = ? WHERE BillID = ?", 
    						fhwList.get(0).getStr("fhwbh"),fhwList.get(0).getStr("fhqbh"),record.getStr("BillID"));//根性复核区，复合位信息
    				Db.update("update xyy_wms_dic_fuhequjibenxinxi set shifousuoding = 1 WHERE id = ?",fhwList.get(0).getStr("ID"));
    				
    			}else{
    				Record fuhewei = Db.findFirst("SELECT * from xyy_wms_dic_fuhequjibenxinxi WHERE 1=1 and shifouqiyong = 1 "
    						+ "and shifousuoding = 0 ORDER BY  fhqbh,zuhao,cenghao,liehao");
    				if(fuhewei != null ){
    					record.set("fuhetai",fuhewei.getStr("fhwbh"));
    					record.set("fhqbh",fuhewei.getStr("fhqbh"));
    					Db.update("UPDATE xyy_wms_bill_dabaorenwu SET fuhetai = ? ,fhqbh = ? WHERE BillID = ?", 
    							fuhewei.getStr("fhwbh"),fuhewei.getStr("fhqbh"),record.getStr("BillID"));//根性复核区，复合位信息
    					Db.update("update xyy_wms_dic_fuhequjibenxinxi set shifousuoding = 1 WHERE id = ?",fuhewei.getStr("ID"));
    				}else{
    					return;
    				}
    			}
    		}else{
    			Record fuhewei = Db.findFirst("SELECT * from xyy_wms_dic_fuhequjibenxinxi WHERE 1=1 and shifouqiyong = 1 "
    					+ "and shifousuoding = 0 ORDER BY  fhqbh,zuhao,cenghao,liehao");
    			if(fuhewei != null ){
    				record.set("fuhetai",fuhewei.getStr("fhwbh"));
    				record.set("fhqbh",fuhewei.getStr("fhqbh"));
    				Db.update("UPDATE xyy_wms_bill_dabaorenwu SET fuhetai = ? ,fhqbh = ? WHERE BillID = ?", 
							fuhewei.getStr("fhwbh"),fuhewei.getStr("fhqbh"),record.getStr("BillID"));//根性复核区，复合位信息
    				Db.update("update xyy_wms_dic_fuhequjibenxinxi set shifousuoding = 1 WHERE id = ?",fuhewei.getStr("ID"));
    			}else{
    				return;
    			}
    		}
		}
        }
        this.setAttr("status", 1);
        this.setAttr("result", jianhuodanList);
        this.renderJson();
    }
    
    /**
     * 清空拣货单重新拣货
     */
    private void rePicking() {
    	jianhuodanList.clear();;
    	jianhuoshanpinList.clear();;
    	 this.setAttr("status", 1);
         this.setAttr("result", null);
         this.renderJson();
	}

    /**
     * 整件拣货pda
     */
    private void wholePicking() {
    	  String requestMsg = this.getPara("requestMsg");
          JSONObject msg = JSONObject.parseObject(requestMsg);
          JSONObject head = msg.getJSONObject("head");
          JSONObject userInformation =JSONObject.parseObject(head.getString("userInformation"));
          String userId = userInformation.getString("id");
          //根据userId获取当前登录人信息
          Record user = null;
          if(!StringUtil.isEmpty(userId)){
          	 user = Db.findFirst("select * from tb_sys_user WHERE id = ?",userId);
          }
          Record record = Db.findFirst("SELECT * FROM xyy_wms_bill_dabaorenwu t1 WHERE "
					+ "t1.status =32 and EXISTS (SELECT 1 FROM "
					+ "xyy_wms_dic_kqzyqxwh t2,xyy_wms_dic_kuqujibenxinxi t3 WHERE t2.kuqubianhao = t3.kuqubianhao "
					+ "and t2.gonghao = ? AND ((t3.kqlbbh = 1 AND t1.taskType in (30,40) AND t1.status in (32,27,26) "
					+ "AND t2.buhuo = 1) OR (t3.kqlbbh = 2 AND t1.taskType = 10 AND t2.jianhuo = 1 AND t1.xiajiakuqu=t2.kuqubianhao)"
					+ "OR (t3.kqlbbh = 2 AND t1.taskType in (30,40) AND t1.status in (28,29,26) AND t2.buhuo = 1 "
					+ "and t1.shangjiakuqu=t2.kuqubianhao))and (t1.caozuoren = ? or t1.caozuoren is null)  ORDER BY t1.youxianji Desc ,t1.createTime DESC",
					user.getStr("userId"),user.getStr("userId"));
          //StringBuffer sql = new StringBuffer("select * from xyy_wms_bill_dabaorenwu where status = 32 and taskType in(20,40) order by taskType Desc,youxianji Asc");
		   	if(record != null){
		   		record.set("caozuoren", user.getStr("userId"));
		   		record.set("czrmc", user.getStr("realName"));
		   		record.set("startDate", getCurrentTime());
		   		Db.update("xyy_wms_bill_dabaorenwu", "BillID", record);
		   		wholeTaskList.add(record);
		   	}
    	this.setAttr("status", 1);
    	this.setAttr("result", wholeTaskList);
    	this.renderJson();
    }
    
	/**
	 * 获取拣货完成时间
	 */
	private static String getCurrentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(new Date());
		return currentTime;
	}

    private void jumpTo404() {
        this.render("/404.html");
    }
}
