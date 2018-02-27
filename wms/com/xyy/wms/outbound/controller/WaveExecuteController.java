package com.xyy.wms.outbound.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.util.SequenceBuilder;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.erp.platform.common.tools.ToolWeb;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.outbound.biz.KucuncalcService;
import com.xyy.wms.outbound.util.KuncunParameter;

/**波次执行action
 * 
 * create 2017/11/20
 * */
public class WaveExecuteController extends Controller {
	
	private StringBuffer error;
	private List<Record>  buHuoHuoweiOrderList=null;//缓存补货新增货位
	private List<Record> buHuoHuoweiList=null;//缓存补货新增货位
	//private List<Record> buHuoOrderList=null;//缓存补货新增货位预减库存
	private List<Record> paraList=null;//缓存预占预扣记录
	private String orgId="";//机构ID
	public void waveExecute(){
		error=new StringBuffer();
		orgId = ToolWeb.getCookieValueByName(getRequest(), "orgId");
		String billID=this.getPara("billID");//主键
		int orderNum=0;
		buHuoHuoweiList=new ArrayList<Record>();
		//分配集货区
		Record wareRecord=Db.findById("xyy_wms_bill_bocijihua","BillID",billID);//获取波次
		List<Record> orderList=Db.find("select * from xyy_wms_bill_bocijihua_details where BillID=? and orgId=?  and dingdanzhuangtai=20 order by dingdanriqi,kehubianhao ", billID,orgId);//波次订单
		//缓存所有的集货位
		Map<String,List<Record>> JHWRecordMap=new HashMap<String, List<Record>>();
		List<Record> records=null;
		if(orderList.size()>0){
			Record executeRule=Db.findFirst("select * from xyy_wms_dic_bocizhixingweihu where orgId=? and qiyong=1 ",orgId);//获取有用的波次执行规则
			if(executeRule==null){
				this.setAttr("status", 2);//无可用波次执行规则
				this.setAttr("error", "无可用的波次执行规则");//无可用波次执行规则
				this.renderJson();
				return;
			}else{//有可用的波次执行规则
				switch (executeRule.getInt("tyjhw")){//允许同一集货位（超过集货位体积继续堆放）
				case 1://按订单
					allotJHWbyOrder(orderList,executeRule,wareRecord,JHWRecordMap);
					break;
				case 2://按客户
					allotJHWbyCustomer(orderList,executeRule,wareRecord,JHWRecordMap);
					break;
				case 3://按路线
					break;
				default://考虑集货位体积
					allotJHWbyVolume(orderList,executeRule,wareRecord,JHWRecordMap);
					break;
				}
				
				//分配货位
				//1、出库商品列表
				List<Record> dabaoList=new ArrayList<Record>();
				for(Record order:orderList){
				if(StringUtils.isNotEmpty(order.getStr("qsjhw"))&&StringUtils.isNotEmpty(order.getStr("zzjhw"))){//如果没有集货区则不分配集货位
					List<Record> shangPinList=Db.find("select * "
							+ " from xyy_wms_bill_bocijihua_details2 t1"
							+ "	WHERE shangpinzhuangtai=20 and danjubianhao=? and dingdanbianhao=? and t1.orgId='"+orgId+"'", wareRecord.getStr("danjubianhao"), order.getStr("dingdanbianhao"));
					if(shangPinList!=null&&shangPinList.size()>0){
					    //2、组装打包任务明细信息
						installDabaoTask(shangPinList,executeRule,wareRecord,order);
						Db.batchUpdate("xyy_wms_bill_bocijihua_details2", "BillDtlID", shangPinList, shangPinList.size());//更新订单详情状态
						correlationDabaoTask(executeRule,order,wareRecord,dabaoList);
						}
					}
				}
				if(dabaoList.size()>0){
					Db.batchSave("xyy_wms_bill_dabaorenwu", dabaoList, dabaoList.size());//保存打包任务
				}
				if(buHuoHuoweiList.size()>0){
					for(Record huowei:buHuoHuoweiList){
						huowei.remove("kqlbbh").remove("huoweibianhao").remove("kuqubianhao").remove("mykuquId").remove("pihao").remove("tiji");
					}
					Db.batchSave("xyy_wms_bill_shangpinpihaohuoweikucun", buHuoHuoweiList, buHuoHuoweiList.size());//保存新增货位
				}
				dabaoBuhuoRenwu(wareRecord);//被动补货打包任务
				//释放集货位
				for(Record order:orderList){
					if(order.getInt("dingdanzhuangtai")==20||order.getInt("dingdanzhuangtai")==29){
						records=JHWRecordMap.get(order.getStr("dingdanbianhao"));
						if(records!=null&&records.size()>0){
							for(Record jhw:records){
								jhw.set("shifousuoding", 0);
							}
							Db.batchUpdate("xyy_wms_dic_jihuowei","ID",records,records.size());//更新该集货位未锁定状态
						}
						order.set("qsjhw", "");//设置开始集货位
						order.set("zzjhw", "");//设置终止集货位
						if(order.getInt("dingdanzhuangtai")==20)
							orderNum++;
					}
				}
				Db.batchUpdate("xyy_wms_bill_bocijihua_details", "BillDtlID", orderList, orderList.size());//更新订单集货位
				
				if(orderList.size()==orderNum){
					wareRecord.set("status", 20);//已计划
				}else if(orderNum==0){
					wareRecord.set("status", 21);//已执行
				}else{
					wareRecord.set("status", 23);//部分执行
				}
			}	
		}
		wareRecord.set("error", error.toString());
		Db.update("xyy_wms_bill_bocijihua","BillID",wareRecord);//更新波次状态
		this.setAttr("status", 1);
		this.renderJson();
	}
	
	
	private int getListByShangpin(Record shangpin){
		int num=0;
		if(buHuoHuoweiList.size()>0){
			for(Record huowei:buHuoHuoweiList){
				if(huowei.getStr("shangpinId").equals(shangpin.getStr("goodsid"))){
					num++;
				}
			}
		}
		return num;
	}

	/**
	 * 针对于订单补货
	 * 生成被动补货任务
	 * @param wareRecord 波次
	 * @param ljhuoweiList  零件货位
	 * @param zjhuoweiList  整件货位
	 * @param shangpin 需补货商品
	 * @param linghuoshu 库存零件数
	 * @param zhengjianjianxuanList 
	 * */
	private void correlationBuHuoTask(Record wareRecord,Record order, Record shangpin, List<Record> zjhuoweiList,
				List<Record> ljhuoweiList, BigDecimal linghuoshu, List<Record> zhengjianjianxuanList) {
		//需要补货数量为 波次需要订单数-实际库存数
		BigDecimal quehuo=shangpin.getBigDecimal("lingjianshu").subtract(linghuoshu);
		boolean bool=true;//判断是否有新增货位
		int buhuojianshu=(int) Math.ceil(quehuo.doubleValue()/shangpin.getInt("dbzsl"));
		int addNum=getListByShangpin(shangpin);
				if(ljhuoweiList.size()==0&&addNum==0){//当前商品没有零件库存货位
					for(Record zjRecord:zjhuoweiList){
						BigDecimal zjkucunshuliang=zjRecord.getBigDecimal("kucunshuliang").add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(order.getStr("orgId"), 
								order.getStr("cangkuID"), shangpin.getStr("goodsid"), zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId")), new Object[]{2,3,4,6,7,8}));//剩余库存
						int zjjianshu=(int) Math.floor(zjkucunshuliang.doubleValue()/shangpin.getInt("dbzsl"));
						if(zjjianshu>=buhuojianshu){//当前货位满足补货要求
						//当前货位下架商品 下架数量为buhuojianshu*shangpin.getInt("dbzsl")
						KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
								zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
								shangpin.getStr("dingdanbianhao"), 4,  
								new BigDecimal(buhuojianshu*shangpin.getInt("dbzsl"))));
						addNewHuoweiDetail(order,shangpin,zjRecord,null,buhuojianshu,zhengjianjianxuanList);
					    buhuojianshu=0;
					    break;
						}

					}
				}else{//当前商品有零件库存货位
					//优先在已新增的货位上进行补货
					if(addNum>0){
						for(Record huowei:buHuoHuoweiList){
							if(huowei.getStr("shangpinId").equals(shangpin.getStr("goodsid"))){
								for(Record zjRecord:zjhuoweiList){
									if(huowei.getStr("pihaoId").equals(zjRecord.getStr("pihaoId"))){//有相同批号
										BigDecimal zjkucunshuliang=zjRecord.getBigDecimal("kucunshuliang").add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(order.getStr("orgId"), 
												order.getStr("cangkuID"), shangpin.getStr("goodsid"), zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId")), new Object[]{2,3,4,6,7,8}));//剩余库存
										int zjjianshu=(int) Math.floor(zjkucunshuliang.doubleValue()/shangpin.getInt("dbzsl"));
										if(zjjianshu>=buhuojianshu){//当前货位满足补货要求
											KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
													zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
													shangpin.getStr("dingdanbianhao"), 4,  
													new BigDecimal(buhuojianshu*shangpin.getInt("dbzsl"))));//整件补出预扣
											KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
													huowei.getStr("pihaoId"), huowei.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
													shangpin.getStr("dingdanbianhao"), 3,  
													new BigDecimal(buhuojianshu*shangpin.getInt("dbzsl"))));//零件补入预占
											insertDabaorenwuDetai(shangpin, zjRecord, 40, new BigDecimal(buhuojianshu*shangpin.getInt("dbzsl")),zhengjianjianxuanList, 1,huowei);//被动补货
											buhuojianshu=0;
											bool=false;
											break;
										}
									}
								}
							}
							if(buhuojianshu==0)break;	
						}
					}
					if(buhuojianshu>0){					
						for(Record zjRecord:zjhuoweiList){//按整件近效期查询对应的零件
								for(Record ljRecord:ljhuoweiList){
									if(ljRecord.getStr("pihaoId").equals(zjRecord.getStr("pihaoId"))){//有相同批号
										BigDecimal zjkucunshuliang=zjRecord.getBigDecimal("kucunshuliang").add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(order.getStr("orgId"), 
												order.getStr("cangkuID"), shangpin.getStr("goodsid"), zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId")), new Object[]{2,3,4,6,7,8}));//剩余库存
										int zjjianshu=(int) Math.floor(zjkucunshuliang.doubleValue()/shangpin.getInt("dbzsl"));
										if(zjjianshu>=buhuojianshu){//当前货位满足补货要求
											KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
													zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
													shangpin.getStr("dingdanbianhao"), 4,  
													new BigDecimal(buhuojianshu*shangpin.getInt("dbzsl"))));//整件补出预扣
											KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
													ljRecord.getStr("pihaoId"), ljRecord.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
													shangpin.getStr("dingdanbianhao"), 3,  
													new BigDecimal(buhuojianshu*shangpin.getInt("dbzsl"))));//零件补入预占
											
											insertDabaorenwuDetai(shangpin, zjRecord, 40, new BigDecimal(buhuojianshu*shangpin.getInt("dbzsl")),zhengjianjianxuanList, 1,ljRecord);//被动补货
											buhuojianshu=0;
											bool=false;
											break;
										}
									}
								}
								if(buhuojianshu>0){//该商品批号没有对应的零件货位
									BigDecimal zjkucunshuliang=zjRecord.getBigDecimal("kucunshuliang").add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(order.getStr("orgId"), 
											order.getStr("cangkuID"), shangpin.getStr("goodsid"), zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId")), new Object[]{2,3,4,6,7,8}));//剩余库存
									int zjjianshu=(int) Math.floor(zjkucunshuliang.doubleValue()/shangpin.getInt("dbzsl"));
									if(zjjianshu>=buhuojianshu){//当前货位满足补货要求
									//当前货位下架商品 下架数量为buhuojianshu*shangpin.getInt("dbzsl")
									KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
											zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
											shangpin.getStr("dingdanbianhao"), 4,  
											new BigDecimal(buhuojianshu*shangpin.getInt("dbzsl"))));
									addNewHuoweiDetail(order,shangpin,zjRecord,null,buhuojianshu,zhengjianjianxuanList);
								    buhuojianshu=0;
									}
								}
							if(buhuojianshu==0)break;
						}
					}
					//如果需要补货件数>0 说明要么零件中找不到对应的整件批号，要么是库存不够,需新增批号货位库存
					if(buhuojianshu>0){
						for(Record zjRecord:zjhuoweiList){
								BigDecimal zjkucunshuliang=zjRecord.getBigDecimal("kucunshuliang").add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(order.getStr("orgId"), 
										order.getStr("cangkuID"), shangpin.getStr("goodsid"), zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId")), new Object[]{2,3,4,6,7,8}));//剩余库存
								int zjjianshu=(int) Math.floor(zjkucunshuliang.doubleValue()/shangpin.getInt("dbzsl"));
								if(zjjianshu>=buhuojianshu){//当前货位满足补货要求
									//当前货位下架商品 下架数量为buhuojianshu*shangpin.getInt("dbzsl")
									KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
											zjRecord.getStr("pihaoId"), zjRecord.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
											shangpin.getStr("dingdanbianhao"), 4,  
											new BigDecimal(buhuojianshu*shangpin.getInt("dbzsl"))));
									addNewHuoweiDetail(order,shangpin,zjRecord,null,buhuojianshu,zhengjianjianxuanList);
								    buhuojianshu=0;
								    break;
							}

						}
					}
				}
				//库存不够 挂单
				if(buhuojianshu>0){
					shangpin.set("shangpinzhuangtai", 29);//挂单
					error.append("订单【"+order.getStr("dingdanbianhao")+"】中商品【"+shangpin.getStr("shangpinbianhao")+"】整件库存货位不足！    ");
				}else{
					//扣除整件补货商品出库数量
					subtractShangPinBuhuo(shangpin,zjhuoweiList,zhengjianjianxuanList);
					//扣除零件补货商品出库数量
					if(bool){
						subtractShangPinBuhuo(shangpin,buHuoHuoweiOrderList,zhengjianjianxuanList);
					}else{
						subtractShangPinBuhuo(shangpin,ljhuoweiList,zhengjianjianxuanList);
					}
					
		}
	}



	private void subtractShangPinBuhuo(Record shangpin, List<Record> zjhuoweiList, List<Record> zhengjianjianxuanList) {
		BigDecimal shengyuzhengjian=shangpin.getBigDecimal("zhengjianshu");
		BigDecimal shengyulingjian=shangpin.getBigDecimal("lingjianshu");
		for(Record huowei:zjhuoweiList){
		if(huowei.getStr("shangpinId").equals(shangpin.getStr("goodsid"))){
			BigDecimal shijikucun=huowei.getBigDecimal("kucunshuliang")
					.add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
							huowei.getStr("pihaoId"), huowei.getStr("huoweiId")),new Object[]{2,3,4,6,7,8}));
			//此处还需要加上还未保存到数据库中的零件货位的补入预占
			if(paraList.size()>0&&huowei.getInt("kqlbbh")==2){
				shijikucun=shijikucun.add(calcBuruyuzhanKC(huowei));
			}
			if(shijikucun.compareTo(BigDecimal.ZERO)>0){
				if(huowei.getInt("kqlbbh")==1&&shengyuzhengjian.compareTo(BigDecimal.ZERO)>0){//整件货位
					if(shengyuzhengjian.compareTo(shijikucun)>=0){
						//插入打包任务明细
						insertDabaorenwuDetai(shangpin,huowei,20,shijikucun,zhengjianjianxuanList,1,null);
						KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
								huowei.getStr("pihaoId"), huowei.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
								shangpin.getStr("dingdanbianhao"), 2, shijikucun));
					}else{
						//插入打包任务明细
						insertDabaorenwuDetai(shangpin,huowei,20,shengyuzhengjian,zhengjianjianxuanList,1,null);
						KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
								huowei.getStr("pihaoId"), huowei.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
								shangpin.getStr("dingdanbianhao"), 2, shengyuzhengjian));
					}
					shengyuzhengjian=shengyuzhengjian.subtract(shijikucun);
				}else if(huowei.getInt("kqlbbh")==2&&shengyulingjian.compareTo(BigDecimal.ZERO)>0){//零件货位
					if(shengyulingjian.compareTo(shijikucun)>=0){
						//插入打包任务明细
						insertDabaorenwuDetai(shangpin,huowei,10,shijikucun,zhengjianjianxuanList,2,null);
						KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
								huowei.getStr("pihaoId"), huowei.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
								shangpin.getStr("dingdanbianhao"), 2,  shijikucun));
					}else{
						//插入打包任务明细
						insertDabaorenwuDetai(shangpin,huowei,10,shengyulingjian,zhengjianjianxuanList,2,null);
						KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
								huowei.getStr("pihaoId"), huowei.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
								shangpin.getStr("dingdanbianhao"), 2,  shengyulingjian));
					}
					shengyulingjian=shengyulingjian.subtract(shijikucun);
				}
				}
			}
		}
		
		if(shengyulingjian.compareTo(BigDecimal.ZERO)>0){//需要从零件补货里面出
			if(buHuoHuoweiList.size()>0){
				for(Record buHuoHuowei:buHuoHuoweiList){
					if(shengyulingjian.compareTo(BigDecimal.ZERO)>0&&buHuoHuowei.getStr("shangpinId").equals(shangpin.getStr("goodsid"))){
						BigDecimal shijikucun=buHuoHuowei.getBigDecimal("kucunshuliang").add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(shangpin.getStr("orgId"), 
								shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"), buHuoHuowei.getStr("pihaoId"), buHuoHuowei.getStr("huoweiId")), new Object[]{2,3,4,6,7,8}));//剩余库存
						if(shengyulingjian.compareTo(shijikucun)>=0){
							//插入打包任务明细
							insertDabaorenwuDetai(shangpin,buHuoHuowei,10,shijikucun,zhengjianjianxuanList,2,null);
							KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
									buHuoHuowei.getStr("pihaoId"), buHuoHuowei.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
									shangpin.getStr("dingdanbianhao"), 2,  
									shijikucun));
						}else{
							//插入打包任务明细
							insertDabaorenwuDetai(shangpin,buHuoHuowei,10,shengyulingjian,zhengjianjianxuanList,2,null);
							KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
									buHuoHuowei.getStr("pihaoId"), buHuoHuowei.getStr("huoweiId"), shangpin.getStr("danjubianhao"), 
									shangpin.getStr("dingdanbianhao"), 2,  
									shengyulingjian));
						}
						//addBuHuoOrderList(buHuoHuowei, shijikucun);
						shengyulingjian=shengyulingjian.subtract(shijikucun);
					}
				}
			}
		}
	}
	
	/**
	 * 多订单补货同一商品时安订单补货
	 * 缓存当前订单出库预扣库存，当订单非挂单情况下可以同步到buHuoHuoweiList里面
	 * */
/*	private void addBuHuoOrderList(Record buHuoHuowei,BigDecimal shijikucun){
		Record r=new Record();
		r.set("shangpinId", buHuoHuowei.getStr("shangpinId"));
		r.set("pihaoId", buHuoHuowei.getStr("pihaoId"));
		r.set("huoweiId", buHuoHuowei.getStr("huoweiId"));
		r.set("chukuyukou", buHuoHuowei.getBigDecimal("chukuyukou").add(shijikucun));
		buHuoOrderList.add(r);
		
	}*/

	private BigDecimal calcBuruyuzhanKC(Record huowei) {
		BigDecimal bigDecimal=BigDecimal.ZERO;
		for(Record record:paraList){
				if(record.getStr("goodsid").equals(huowei.getStr("shangpinId"))
						&&record.getStr("pihaoId").equals(huowei.getStr("pihaoId"))
						&&record.getStr("huoweiId").equals(huowei.getStr("huoweiId"))){
					if(record.getInt("zuoyeleixing")==3){
						bigDecimal=bigDecimal.add(record.getBigDecimal("shuliang"));
					}
					if(record.getInt("zuoyeleixing")==2){
						bigDecimal=bigDecimal.subtract(record.getBigDecimal("shuliang"));
					}
			}
		}
		return bigDecimal;
	}


	/**
	 * 分配被动补货上架货位
	 * @param shangpin 商品
	 * @param zjRecord 整件下架货位
	 * @param zhengjianjianxuanList 
	 * @param zhengjianjianxuanList 
	 * @jianshu 下架件数
	 * */
	private boolean addNewHuoweiDetail(Record order,Record shangpin, Record zjRecord,Record ljRecord, 
			int jianshu, List<Record> zhengjianjianxuanList) {
		if(ljRecord==null){//则需要先分货位
			List<Record> kyHuoweilist=getHouwei(shangpin.getStr("shangpinbianhao"), 2);
				if(kyHuoweilist.size()>0){
					ljRecord=kyHuoweilist.get(0);
					createNewHuoweiKC(ljRecord,jianshu*shangpin.getInt("dbzsl"),shangpin,zjRecord);
					insertDabaorenwuDetai(shangpin, zjRecord, 40, new BigDecimal(jianshu*shangpin.getInt("dbzsl")),zhengjianjianxuanList, 1,ljRecord);//主动补货
				}else{
					shangpin.set("shangpinzhuangtai", 29);//挂单
				}
		}
		return true;	
	}


	/**
	 * 货位库存表新增新的零件库存
	 * 
	 * @param shangpinbianhao
	 * @param kuquleibie
	 * @return
	 */
	private void createNewHuoweiKC(Record huowei, int shuliang, Record shangpin,Record zjRecord) {
		huowei.set("huoweiId", huowei.getStr("ID"));
		huowei.set("mykuquId", huowei.getStr("kuquId"));
		Record newhuowei=new Record();
		newhuowei.set("ID", UUIDUtil.newUUID());
		newhuowei.set("huoweiId", huowei.getStr("ID"));
		newhuowei.set("pihaoId", zjRecord.getStr("pihaoId"));
		newhuowei.set("shangpinId", zjRecord.getStr("shangpinId"));
		//newhuowei.set("buruyuzhan", new BigDecimal(shuliang) );
		newhuowei.set("kucunshuliang", new BigDecimal(0) );
		//newhuowei.set("chukuyukou", new BigDecimal(0) );
		//newhuowei.set("buchuyukou", new BigDecimal(0) );
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		newhuowei.set("status",40);
		newhuowei.set("orgId", zjRecord.getStr("orgId"));
		newhuowei.set("orgCode", zjRecord.getStr("orgCode"));
		newhuowei.set("createTime", currentTime);
		newhuowei.set("updateTime", currentTime);
		newhuowei.set("rowID", UUIDUtil.newUUID());
		newhuowei.set("cangkuId", zjRecord.getStr("cangkuId"));
		newhuowei.set("kqlbbh", 2);
		newhuowei.set("huoweibianhao", huowei.getStr("huoweibianhao"));
		newhuowei.set("kuqubianhao", huowei.getStr("kuqubianhao"));
		newhuowei.set("mykuquId", huowei.getStr("kuquId"));
		newhuowei.set("pihao", zjRecord.getStr("pihao"));
		newhuowei.set("zhiliangzhuangtai", 1);//货位质量状态
		newhuowei.set("huozhuId", "0001");
		newhuowei.set("shengchanriqi", zjRecord.getDate("shengchanriqi"));
		newhuowei.set("youxiaoqizhi", zjRecord.getDate("youxiaoqizhi"));
		
		buHuoHuoweiOrderList.add(newhuowei);
		
		//新增货位预扣预占
		KucuncalcService.kcCalc.insertCacheKCRecord(paraList,new KuncunParameter(shangpin.getStr("orgId"),shangpin.getStr("cangkuID"), shangpin.getStr("goodsid"),
				zjRecord.getStr("pihaoId"), huowei.getStr("ID"), shangpin.getStr("danjubianhao"), 
				shangpin.getStr("dingdanbianhao"), 3,  
				new BigDecimal(shuliang)));
	}


	/**
	 * 根据商品编号和库区类别查询所有的空货位
	 * 
	 * @param shangpinbianhao
	 * @param kuquleibie
	 * @return
	 */
	public List<Record> getHouwei(String shangpinbianhao, int kuquleibie) {
		List<Record> list = new ArrayList<Record>();
		String sql = "select a.lluojiquyu from xyy_wms_dic_spccxdwh_list a inner join xyy_wms_dic_spccxdwh b on a.id = b.id"
				+ " and  b.shangpinbianhao = ? and a.orgId='"+orgId+"' and a.lluojiquyu is not null";
		Record r = Db.findFirst(sql, shangpinbianhao);
		if(r!=null){//该商品分配了逻辑区
			String lluojiquyu = r.get("lluojiquyu");
			String sql2 = "SELECT 	t3.*,t5.kuqubianhao,t5.kuqumingcheng FROM 	xyy_wms_dic_ljqhwgxwh_list t1,	xyy_wms_dic_ljqhwgxwh t2,xyy_wms_dic_huoweiziliaoweihu t3,xyy_wms_dic_kuqujibenxinxi t5  WHERE "
					+ " t1.ID = t2.ID and t1.huoweibianhao=t3.huoweibianhao and t1.orgId=?  and t3.kuquId=t5.ID AND NOT EXISTS (SELECT 1 FROM xyy_wms_bill_shangpinpihaohuoweikucun t4 where t4.huoweiId=t3.ID) and t2.ljqbh=? "
					+ " and t3.huoweiqiyong =1 ORDER BY t3.kuquId asc,t3.xiangdao asc,t3.pai asc,t3.ceng asc,t3.lie asc";
			list = Db.find(sql2,orgId, lluojiquyu);
			if(list.size()==0){
			  error.append("商品【"+shangpinbianhao+"】被动补货无可用货位！");
			}
		}else{
			 error.append("商品【"+shangpinbianhao+"】未分配逻辑区，无法分配零件补货货位！");
		}
		/*else{
			String sql2 = "SELECT 	t3.*,t.kuqubianhao,t.kuqumingcheng FROM xyy_wms_dic_huoweiziliaoweihu t3,xyy_wms_dic_kuqujibenxinxi t WHERE  t3.kuquId=t.ID and"
					+ " EXISTS (SELECT 1 FROM xyy_wms_bill_shangpinpihaohuoweikucun t4 where t4.huoweiId=t3.ID) and t3.huoweibianhao like '%LJ%' "
					+ " and t3.huoweiqiyong =1 and t.kqlbbh in(1,2)  ORDER BY t3.kuquId asc,t3.xiangdao asc,t3.pai asc,t3.ceng asc,t3.lie asc";
			list = Db.find(sql2);
			if(list.size()==0){
				  error.append("商品【"+shangpinbianhao+"】被动补货无可用货位！");
			}
		}*/
		return list;
	}
	/**关联被动补货打包任务
	 * @param wareRecord 
	 * 
	 * 
	 * */
	private void dabaoBuhuoRenwu(Record wareRecord) {
		List<Record> detailList=Db.find("select t1.* from xyy_wms_bill_dabaorenwu_details t1 where t1.bocibianhao=? "
				+ " and t1.status=37 and t1.taskType=40 and t1.orgId=?  order by t1.goodsid",wareRecord.getStr("danjubianhao"),orgId);
		String billId=UUIDUtil.newUUID();
		String oldshangpinId="";
		int i=0;
		List<Record> buhuoTotalList=new ArrayList<Record>();
		if(detailList!=null&&detailList.size()>0){
			for(Record renwuDetail:detailList){
				if(i==0){
					oldshangpinId=renwuDetail.getStr("goodsid");
					dabaoBuhuoRenwuTotal(billId, wareRecord, renwuDetail,buhuoTotalList);
				}else{
					if(!oldshangpinId.equals(renwuDetail.getStr("goodsid"))){
						billId=UUIDUtil.newUUID();
						oldshangpinId=renwuDetail.getStr("goodsid");
						dabaoBuhuoRenwuTotal(billId, wareRecord, renwuDetail,buhuoTotalList);
					}
				}
				renwuDetail.set("BillID", billId);
				i++;
			}
			Db.batchUpdate("xyy_wms_bill_dabaorenwu_details", "BillDtlID", detailList, detailList.size());//更新任务明细
		}
		if(buhuoTotalList.size()>0)
		Db.batchSave("xyy_wms_bill_dabaorenwu", buhuoTotalList, buhuoTotalList.size());//保存打包任务	
	}
	
	private void dabaoBuhuoRenwuTotal(String billId,Record wareRecord,Record renwuDetail,List<Record> buhuoTotalList){
		Record nre=new Record();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		nre.set("status",32);//补货任务时，拣货任务看不到
		nre.set("taskCode", "JXD"+SequenceBuilder.nextSequence("orderNo_"+TimeUtil.format(new Date(), "yyyyMMdd"), "", 10));
		nre.set("BillID", billId);
		nre.set("createTime", currentTime);
		nre.set("updateTime", currentTime);
		nre.set("rowID", UUIDUtil.newUUID());
		nre.set("bocibianhao", wareRecord.getStr("danjubianhao"));
		nre.set("taskType", 40);
		nre.set("orgId", renwuDetail.getStr("orgId"));
		nre.set("orgCode", renwuDetail.getStr("orgCode"));
		nre.set("shangjiakuqu", renwuDetail.getStr("shangjiakuqubianhao"));
		nre.set("xiajiakuqu", renwuDetail.getStr("kuqubianhao"));
		buhuoTotalList.add(nre);
	}
	/**关联打包任务
	 * @param order 订单
	 * @param executeRule 执行策略
	 * @param wareRecord 
	 * 
	 * 
	 * */
	private void correlationDabaoTask(Record executeRule, Record order, Record wareRecord,List<Record> dabaoList) {
		double zjxtj=executeRule.getBigDecimal("clbzxtj").doubleValue();//集装箱体积
		List<Record> detailList=Db.find("select t1.* from (select t.*,((shuliang/baozhuangshuliang)*dbztj) rtiji from xyy_wms_bill_dabaorenwu_details t) t1,xyy_wms_dic_huoweiziliaoweihu t2 where t1.bocibianhao=? "
				+ "and t1.dingdanbianhao=? and t1.orgId=? and t1.status=37 and t1.taskType in(10,20) and t1.xiajiahuowei = t2.ID ORDER BY kuquId asc,t1.rtiji asc,t2.xiangdao asc,"
				+ " t2.pai asc,t2.ceng asc, t2.lie asc,shangpingpihao asc ",wareRecord.getStr("danjubianhao"),order.getStr("dingdanbianhao"),orgId);
		if(detailList!=null&&detailList.size()>0){
			double lingjianTiji=0;
			String zjbillId=UUIDUtil.newUUID();
			String ljbillId=UUIDUtil.newUUID();
			int zj=0;
			int lj=0;
			String oldKuquId="";
			for(Record renwuDetail:detailList){
				if("1".equals(renwuDetail.getStr("kuquleibie"))){//一个订单所有整件 单独打包一个任务
					renwuDetail.set("BillID", zjbillId);
					if(zj==0){
						insertDabaorenwuTotal(renwuDetail,zjbillId,dabaoList,order);
						zj++;
					}
				}else{
					if(lj==0){
						oldKuquId=renwuDetail.getStr("kuquId");
					}
					lingjianTiji=lingjianTiji+((double)renwuDetail.getInt("shuliang")/renwuDetail.getInt("baozhuangshuliang"))*renwuDetail.getBigDecimal("dbztj").doubleValue();
					if(oldKuquId.equals(renwuDetail.getStr("kuquId"))&&lingjianTiji<=zjxtj){
						renwuDetail.set("BillID", ljbillId);
						if(lj==0){
							insertDabaorenwuTotal(renwuDetail,ljbillId,dabaoList,order);
						}
					}else{
						oldKuquId=renwuDetail.getStr("kuquId");
						ljbillId=UUIDUtil.newUUID();
						lingjianTiji=((double)renwuDetail.getInt("shuliang")/renwuDetail.getInt("baozhuangshuliang"))*renwuDetail.getBigDecimal("dbztj").doubleValue();
						renwuDetail.set("BillID", ljbillId);
						insertDabaorenwuTotal(renwuDetail,ljbillId,dabaoList,order);
					}
					lj++;
				}
				renwuDetail.remove("rtiji");
			}
			Db.batchUpdate("xyy_wms_bill_dabaorenwu_details", "BillDtlID", detailList, detailList.size());//更新任务明细
		}
	}
	/**
	 * 插入打包任务汇总
	 * @param billId 
	 * @param dabaoList 
	 * @param shangpin 商品
	 * */
	private void insertDabaorenwuTotal(Record renwuDetail, String billId, List<Record> dabaoList,Record order) {
		Record nre=new Record();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		if(order.getInt("dingdanzhuangtai")==22){
			nre.set("status",30);//补货任务时，拣货任务看不到
		}else{
			nre.set("status",32);
		}
		nre.set("taskCode", "JXD"+SequenceBuilder.nextSequence("orderNo_"+TimeUtil.format(new Date(), "yyyyMMdd"), "", 10));
		nre.set("orgId", renwuDetail.getStr("orgId"));
		nre.set("orgCode", renwuDetail.getStr("orgCode"));
		nre.set("BillID", billId);
		nre.set("createTime", currentTime);
		nre.set("updateTime", currentTime);
		nre.set("rowID", UUIDUtil.newUUID());
		nre.set("bocibianhao", renwuDetail.getStr("bocibianhao"));
		nre.set("taskType", renwuDetail.getInt("taskType"));
		nre.set("dingdanbianhao", renwuDetail.getStr("dingdanbianhao"));
		nre.set("bocibianhao", renwuDetail.getStr("bocibianhao"));
		nre.set("kehubianhao", order.getStr("kehubianhao"));
		nre.set("kehumingcheng", order.getStr("kehumingcheng"));
		nre.set("xianlumingcheng", order.getStr("luxianmingcheng"));
		nre.set("zancunqu", order.getStr("qsjhw")+"/"+order.getStr("zzjhw"));
		nre.set("tihuofangshi", order.getStr("tihuofangshi"));
		nre.set("kehubeizhu", order.getStr("remark"));
		nre.set("shangjiakuqu", renwuDetail.getStr("shangjiakuqubianhao"));
		nre.set("xiajiakuqu", renwuDetail.getStr("kuqubianhao"));
		nre.set("youxianji", order.getStr("youxianji"));
		dabaoList.add(nre);
	}

	/**
	 * 出库商品零整拆分
	 * 按商品大包装数量拆分出库商品为整件和零件
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param dabaorenwuId 
	 * @param preBool true 判断是否有被动补货 false 波次执行
	 * */
	
	private void installDabaoTask(List<Record> shangPinList,
			Record executeRule, Record wareRecord,Record order) {
		StringBuffer sb=null;
		List<Record> huoweiList=null;
		List<Record> zhengjianjianxuanList=null;
		buHuoHuoweiOrderList=new ArrayList<Record>();
		//buHuoOrderList=new ArrayList<Record>();
		paraList=new ArrayList<Record>();
		boolean gdBool=false;
		boolean bhBool=false;
		//Map<String,List<Record>> huoweiListMap=new HashMap<String, List<Record>>();
		Map<String,List<Record>> zjjxMap=new HashMap<String, List<Record>>();
		for(Record shangpin:shangPinList){
			if(shangpin.getBigDecimal("shuliang").compareTo(shangpin.getBigDecimal("chonghongshuliang"))>0){
			//查询商品所有整件区和零件区的货位
			sb=new StringBuffer();
			sb.append("SELECT t1.*,t2.kqlbbh,t3.huoweibianhao,t2.kuqubianhao,t3.kuquId mykuquId,t4.pihao from ");
			sb.append(" xyy_wms_bill_shangpinpihaohuoweikucun t1,");
			sb.append(" xyy_wms_dic_kuqujibenxinxi t2,");
			sb.append(" xyy_wms_dic_huoweiziliaoweihu  t3,	xyy_wms_dic_shangpinpihao t4 WHERE ");
			sb.append(" t1.huoweiId=t3.ID and t2.ID=t3.kuquId  and t1.shangpinId=t4.goodsId and t1.pihaoId=t4.pihaoId and t2.kqlbbh in(1,2) and t1.shangpinId=? and t1.orgId=? order by t1.youxiaoqizhi asc,t1.kucunshuliang desc ");
			huoweiList=Db.find(sb.toString(),shangpin.getStr("goodsid"),orgId);//获取所有库存货位
			//3、定位批次库区货位
			if(huoweiList.size()>0){
				zhengjianjianxuanList=locationGoodsPosition(shangpin,huoweiList,wareRecord,executeRule,order);
				if(zhengjianjianxuanList!=null&&zhengjianjianxuanList.size()>0){
					//huoweiListMap.put(shangpin.getStr("goodsid"), huoweiList);
					zjjxMap.put(shangpin.getStr("goodsid"), zhengjianjianxuanList);
				}
				if(shangpin.getInt("shangpinzhuangtai")==22){
					bhBool=true;
				}
				if(shangpin.getInt("shangpinzhuangtai")==29){
					gdBool=true;
				}
			}else{
				gdBool=true;
				shangpin.set("shangpinzhuangtai",29);
				error.append("订单【"+order.getStr("dingdanbianhao")+"】中商品【"+shangpin.getStr("shangpinbianhao")+"】未定位到库存货位！    ");
			}
			}else{
				shangpin.set("shangpinzhuangtai",30);//设置冲红
			}
		}
		if(gdBool){
			order.set("dingdanzhuangtai", 29);//设置该订单为缺货挂单
			for(Record shangpin:shangPinList){
				if(shangpin.getInt("shangpinzhuangtai")!=29&&shangpin.getInt("shangpinzhuangtai")!=30){
					shangpin.set("shangpinzhuangtai",20);//设置为已计划
				}
			}
		}else{
			if(paraList.size()>0){
				//List<Record> list=mapToList(huoweiListMap,true);
				List<Record> list=mapToList(zjjxMap,false);
				//Db.batchUpdate("xyy_wms_bill_shangpinpihaohuoweikucun", "ID",list,list.size());//更新商品批号库存
				//Db.batch("update xyy_wms_bill_shangpinpihaohuoweikucun set chukuyukou=?,buruyuzhan =?,buchuyukou =? where ID=?", "chukuyukou,buruyuzhan,buchuyukou,ID", list, list.size());
				//记录预占预扣
				KucuncalcService.kcCalc.insertBatchKCRecord(paraList);
				Db.batchSave("xyy_wms_bill_dabaorenwu_details", list, list.size());//保存打包任务
				copyOrderlist();
			}
			if(bhBool){
				order.set("dingdanzhuangtai", 22);//设置该订单为被动补货
			}
		}
	}
	/**
	 * 同步本地缓存
	 * */
	private void copyOrderlist() {
		/*for(Record d:buHuoOrderList){//当前订单出库预扣
			for(Record dd:buHuoHuoweiList){//波次订单新增货位缓存
				if(d.getStr("shangpinId").equals(dd.getStr("shangpinId"))
						&&d.getStr("pihaoId").equals(dd.getStr("pihaoId"))
						&&d.getStr("huoweiId").equals(dd.getStr("huoweiId"))){
					dd.set("chukuyukou", dd.getBigDecimal("chukuyukou").add(d.getBigDecimal("chukuyukou")));
				}
			}
		}*/
		for(Record r:buHuoHuoweiOrderList){
			buHuoHuoweiList.add(r);
		}
		
	}
	/**
	 * 定位商品货位
	 * @param shangpin 商品
	 * @param huoweiList 货位
	 * @param executeRule 
	 * @param wareRecord 
	 * @param order 
	 * @param dabaorenwuId 
	 * */
	private List<Record> locationGoodsPosition(Record shangpin, List<Record> huoweiList, Record wareRecord, 
			Record executeRule, Record order) {
		BigDecimal linghuoshu=BigDecimal.ZERO;//零货数
		BigDecimal zhengjianshu=BigDecimal.ZERO;//整件数
		BigDecimal shijilinghuoshu=BigDecimal.ZERO;//不加补入预占
		List<Record> zjhuoweiList=new ArrayList<Record>();
		List<Record> ljhuoweiList=new ArrayList<Record>();
		List<Record> zhengjianjianxuanList=new ArrayList<Record>();
		for(Record huowei:huoweiList){
			BigDecimal shijikucun=huowei.getBigDecimal("kucunshuliang")
					.add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(order.getStr("orgId"),order.getStr("cangkuID"), 
							shangpin.getStr("goodsid"), huowei.getStr("pihaoId"), huowei.getStr("huoweiId")),new Object[]{2,3,4,6,7,8}));
			if(huowei.getInt("kqlbbh")==1){//整件货位
				zhengjianshu=zhengjianshu.add(shijikucun);
				zjhuoweiList.add(huowei);
			}else{//零件货位
				BigDecimal shijikucunOutBuHuo=huowei.getBigDecimal("kucunshuliang")
						.add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(order.getStr("orgId"),order.getStr("cangkuID"), 
								shangpin.getStr("goodsid"), huowei.getStr("pihaoId"), huowei.getStr("huoweiId")),new Object[]{2,4,6,7,8}));
				shijilinghuoshu=shijilinghuoshu.add(shijikucunOutBuHuo);
				linghuoshu=linghuoshu.add(shijikucun);
				ljhuoweiList.add(huowei);
			}
		}
		//零件库存还需要加上还没保存的新增零件货位库存 buHuoHuoweiList
		if(buHuoHuoweiList.size()>0){
			for(Record buHuoHuowei:buHuoHuoweiList){
				if(buHuoHuowei.getStr("shangpinId").equals(shangpin.getStr("goodsid"))){
					linghuoshu=linghuoshu.add(KucuncalcService.kcCalc.getYuzhanZongKC(new KuncunParameter(order.getStr("orgId"),order.getStr("cangkuID"), 
							shangpin.getStr("goodsid"), buHuoHuowei.getStr("pihaoId"), buHuoHuowei.getStr("huoweiId")),new Object[]{2,3,4,6,7,8}));
				}
			}
		}
		
		//1.整件零件都满足出库数量
		if(shangpin.getBigDecimal("zhengjianshu").compareTo(zhengjianshu)<=0&&shangpin.getBigDecimal("lingjianshu").compareTo(linghuoshu)<=0){
			subtractShangPinBuhuo(shangpin, huoweiList, zhengjianjianxuanList);
			if(shangpin.getBigDecimal("lingjianshu").compareTo(BigDecimal.ZERO)>0&&shangpin.getBigDecimal("lingjianshu").compareTo(shijilinghuoshu)>0){
				shangpin.set("shangpinzhuangtai", 22);//被动补货
			}else{
				shangpin.set("shangpinzhuangtai", 21);//已执行
			}
		//2.整件满足，零件不满足，则需要零件补货
		}else if(shangpin.getBigDecimal("zhengjianshu").compareTo(zhengjianshu)<0
				&&shangpin.getBigDecimal("lingjianshu").compareTo(linghuoshu)>0
				&&shangpin.getBigDecimal("zhengjianshu").doubleValue()/shangpin.getInt("dbzsl")<=(zhengjianshu.doubleValue()/shangpin.getInt("dbzsl"))-1){//需要触发被动补货任务
			shangpin.set("shangpinzhuangtai", 22);//被动补货
			correlationBuHuoTask(wareRecord,order,shangpin,zjhuoweiList,ljhuoweiList,linghuoshu,zhengjianjianxuanList);
		//3.整件不满足 零件满足 需扣除所有整件在用零货抵扣整件
		}else if(shangpin.getBigDecimal("zhengjianshu").compareTo(zhengjianshu)>0&&shangpin.getBigDecimal("lingjianshu").compareTo(linghuoshu)<0){
			error.append("订单【"+shangpin.getStr("dingdanbianhao")+"】中商品【"+shangpin.getStr("shangpinbianhao")+"】整件库存不足！    ");
			shangpin.set("shangpinzhuangtai", 29);// 缺货挂单
		}else{
			error.append("订单【"+shangpin.getStr("dingdanbianhao")+"】中商品【"+shangpin.getStr("shangpinbianhao")+"】库存不足！    ");
			shangpin.set("shangpinzhuangtai", 29);// 缺货挂单
		}
		return zhengjianjianxuanList;
		
	}



	/**
	 * 插入打包任务明细表
	 * @param shangpin 商品
	 * @param huoweiList 货位
	 * @param taskType 任务类型 10零件 20整件  30主动补货 40被动补货
	 * @param kuquleibie 库区类别 1 整件 2零件 
	 * @param dabaorenwuId 打包任务ID
	 * @param sjhuowei 补货上架货位
	 * */
	private void insertDabaorenwuDetai(Record shangpin,Record huowei, int taskType, BigDecimal shuliang,
				List<Record> zhengjianjianxuanList, int kuquleibie,Record sjhuowei) {
		Record nre=new Record();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		nre.set("status",37);
		nre.set("orgId", shangpin.getStr("orgId"));
		nre.set("orgCode", shangpin.getStr("orgCode"));
		nre.set("BillDtlID", UUIDUtil.newUUID());
		nre.set("BillID", "0");
		nre.set("createTime", currentTime);
		nre.set("updateTime", currentTime);
		nre.set("rowID", UUIDUtil.newUUID());
		nre.set("bocibianhao", shangpin.getStr("danjubianhao"));
		nre.set("dingdanbianhao", shangpin.getStr("dingdanbianhao"));
		nre.set("goodsid", shangpin.getStr("goodsid"));
		nre.set("shangpinbianhao", shangpin.getStr("shangpinbianhao"));
		nre.set("shangpinmingcheng", shangpin.getStr("shangpinmingcheng"));
		nre.set("shengchanchangjia", shangpin.getStr("shengchanchangjia"));
		nre.set("guige", shangpin.getStr("guige"));
		nre.set("shuliang", shuliang);
		if(sjhuowei!=null){
			nre.set("zongshuliang", shuliang);
		}else{
			nre.set("zongshuliang", shangpin.getBigDecimal("shuliang").intValue()-shangpin.getBigDecimal("chonghongshuliang").intValue());
		}
		nre.set("baozhuangshuliang", shangpin.getInt("dbzsl"));
		nre.set("danwei", shangpin.getInt("danwei"));
		nre.set("taskType", taskType);
		nre.set("kuquleibie", kuquleibie+"");
		nre.set("jianshu", new BigDecimal(shuliang.doubleValue()/shangpin.getInt("dbzsl")));
		nre.set("shangpingpihao", huowei.getStr("pihaoId"));
		nre.set("shangpingpihaosn", huowei.getStr("pihao"));
		nre.set("kuquId", huowei.getStr("mykuquId"));
		nre.set("kuqubianhao", huowei.getStr("kuqubianhao"));
		
		nre.set("xiajiahuowei", huowei.getStr("huoweiId"));
		nre.set("xiajiahuoweibianhao", huowei.getStr("huoweibianhao"));
		
		if(sjhuowei!=null){
			nre.set("shangjiahuowei", sjhuowei.getStr("huoweiId"));
			nre.set("shangjiahuoweibianhao", sjhuowei.getStr("huoweibianhao"));
			nre.set("shangjiakuqu", sjhuowei.getStr("mykuquId"));
			nre.set("shangjiakuqubianhao", sjhuowei.getStr("kuqubianhao"));
		}else{
			nre.set("shangjiahuowei", "");
			nre.set("shangjiahuoweibianhao", "");
			nre.set("shangjiakuqu", "");
			nre.set("shangjiakuqubianhao","");
		}
		nre.set("dbzzl", shangpin.getBigDecimal("dbzzl"));
		nre.set("dbztj", shangpin.getBigDecimal("dbztj"));
		nre.set("shengchanriqi", huowei.getDate("shengchanriqi"));
		nre.set("youxiaoqizhi", huowei.getDate("youxiaoqizhi"));
		nre.set("pizhunwenhao", shangpin.getStr("pizhunwenhao"));
		zhengjianjianxuanList.add(nre);
	}

	/**
	 * 允许同一集货位 /体积(同一个订单)
	 * 一个订单按容量分多个集货位
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param jHWRecordMap 
	 */
	private void allotJHWbyVolume(List<Record> orderList, Record executeRule,
			Record wareRecord, Map<String, List<Record>> jHWRecordMap) {
		
		String jhwpplx=executeRule.getStr("jhwpplx");
		if(jhwpplx.contains("2")&&jhwpplx.contains("6")){//按客户和路线进行集货位分配
			allotJHWOfVolumeSix(orderList,executeRule,wareRecord,jHWRecordMap,26);
			return;
		}else if(jhwpplx.contains("2")){//按客户类别分配
			allotJHWOfVolumeTwo(orderList,executeRule,wareRecord,jHWRecordMap);
		}else if(jhwpplx.contains("6")){//按路线分配
			allotJHWOfVolumeSix(orderList,executeRule,wareRecord,jHWRecordMap,6);
		}else{
			
		}
	
	}


	/**
	 * 按客户类别分配集货位
	 * 按订单体积分配集货位
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param jHWRecordMap 
	 * */
	private void allotJHWOfVolumeTwo(List<Record> orderList,
			Record executeRule, Record wareRecord,
			Map<String, List<Record>> jHWRecordMap) {
		StringBuffer sb=null;
		List<Record> JHWRecords=null;
		List<Record> newJHWRecords=null;
		for(Record orderRecord:orderList){
			newJHWRecords=new ArrayList<Record>();
			sb=new StringBuffer();
			if(StringUtils.isEmpty(orderRecord.getStr("luxianId"))){
				getKhxl(orderRecord.getStr("kehubianhao"),orderRecord);
			}
				sb=getJhwSql("", orderRecord.getInt("kehuleixing"));
				JHWRecords=Db.find(sb.toString(),orderRecord.getInt("kehuleixing"));//获取可用的集货位
				if(JHWRecords==null||JHWRecords.size()==0){//没有可用的集货位
					error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】无可用的集货位！    ");
				}else{
					double jhwVolume=0;
					for(Record JHWRecord:JHWRecords){
						jhwVolume=jhwVolume+JHWRecord.getBigDecimal("tiji").doubleValue();
						if(orderRecord.getBigDecimal("zongtiji").doubleValue()<=jhwVolume){
							if(StringUtils.isEmpty(orderRecord.getStr("qsjhw"))){//最后一次不用设置
								orderRecord.set("qsjhw", JHWRecord.getStr("jihuoweibianhao"));//设置开始集货位
							}
							orderRecord.set("zzjhw", JHWRecord.getStr("jihuoweibianhao"));//设置终止集货位
							orderRecord.set("dingdanzhuangtai", 21);//设置订单已执行
							JHWRecord.set("shifousuoding", 1);
							newJHWRecords.add(JHWRecord);
							break;
						}else{
							if(StringUtils.isEmpty(orderRecord.getStr("qsjhw"))){
								orderRecord.set("qsjhw", JHWRecord.getStr("jihuoweibianhao"));//设置开始集货位
							}
							JHWRecord.set("shifousuoding", 1);
							newJHWRecords.add(JHWRecord);
						}
						
					}
					if(jhwVolume>=orderRecord.getBigDecimal("zongtiji").doubleValue()){
						jHWRecordMap.put(orderRecord.getStr("dingdanbianhao"), newJHWRecords);
						Db.batchUpdate("xyy_wms_dic_jihuowei","ID",newJHWRecords,newJHWRecords.size());//更新该集货位被锁定状态
					}else{//所有可用集货位的提交之和小于订单商品体积
						orderRecord.set("qsjhw", "");//设置开始集货位
						orderRecord.set("zzjhw", "");//设置终止集货位
						orderRecord.set("dingdanzhuangtai", 20);
						error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】所有可用集货位的体积之和小于订单商品体积,无法分配集货位！  ");
					}
			}
		}
	
	}


	/**
	 * 允许同一集货位 /客户
	 * 一个客户多个订单一个集货位 不受订单体积和集货位容量体积限制
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param jHWRecordMap 
	 */
	private void allotJHWbyCustomer(List<Record> orderList, Record executeRule,
			Record wareRecord, Map<String, List<Record>> jHWRecordMap) {

		String jhwpplx=executeRule.getStr("jhwpplx");
		if(jhwpplx.contains("2")&&jhwpplx.contains("6")){//按客户和路线进行集货位分配
			allotJHWOfCustomerSix(orderList,executeRule,wareRecord,jHWRecordMap,26);
			return;
		}else if(jhwpplx.contains("2")){//按客户类别分配
			allotJHWOfCustomerTwo(orderList,executeRule,wareRecord,jHWRecordMap);
		}else if(jhwpplx.contains("6")){//按路线分配
			allotJHWOfCustomerSix(orderList,executeRule,wareRecord,jHWRecordMap,6);
		}else{
			
		}
	}

	/**
	 * 按客户类别分配集货位
	 * 一个客户分配一个集货位
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param jHWRecordMap 
	 * */
	private void allotJHWOfCustomerTwo(List<Record> orderList,
			Record executeRule, Record wareRecord,
			Map<String, List<Record>> jHWRecordMap) {
		StringBuffer sb=null;
		Record JHWRecord=null;
		String oldCustomerId="";
		String oldjihuoweibianhao="";
		List<Record> records=null;
		for(Record orderRecord:orderList){
			sb=new StringBuffer();
			records=new ArrayList<Record>();
			if(oldCustomerId.equals(orderRecord.getStr("kehubianhao"))){//当前客户订单预上次订单客户一样
				orderRecord.set("qsjhw", oldjihuoweibianhao);//设置开始集货位
				orderRecord.set("zzjhw", oldjihuoweibianhao);//设置终止集货位
				orderRecord.set("dingdanzhuangtai", 21);//设置订单已执行
			}else{
				oldCustomerId=orderRecord.getStr("kehubianhao");//记录客户编号
				if(StringUtils.isEmpty(orderRecord.getStr("luxianId"))){
					getKhxl(orderRecord.getStr("kehubianhao"),orderRecord);
				}
				sb=getJhwSql("", orderRecord.getInt("kehuleixing"));
				JHWRecord=Db.findFirst(sb.toString(),orderRecord.getInt("kehuleixing"));//获取可用的集货位
				if(JHWRecord==null){//没有可用的集货位
					error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】无可用的集货位！    ");
				}else{
					orderRecord.set("qsjhw", JHWRecord.getStr("jihuoweibianhao"));//设置开始集货位
					orderRecord.set("zzjhw", JHWRecord.getStr("jihuoweibianhao"));//设置终止集货位
					orderRecord.set("dingdanzhuangtai", 21);//设置订单已执行
					oldjihuoweibianhao=JHWRecord.getStr("jihuoweibianhao");//记录集货位编号
					JHWRecord.set("shifousuoding", 1);
					records.add(JHWRecord);
					jHWRecordMap.put(orderRecord.getStr("dingdanbianhao"), records);
					Db.update("xyy_wms_dic_jihuowei","ID",JHWRecord);//更新该集货位被锁定状态
				}
			}
		}
	
	}


	/**
	 * 允许同一集货位 /订单
	 * 一个订单一个集货位 不受订单体积和集货位容量体积限制
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param jHWRecordMap 
	 */
	private void allotJHWbyOrder(List<Record> orderList, Record executeRule, Record wareRecord, Map<String, List<Record>> jHWRecordMap) {
		String jhwpplx=executeRule.getStr("jhwpplx");
		if(jhwpplx.contains("2")&&jhwpplx.contains("6")){//按客户和路线进行集货位分配
			allotJHWOfOrderSix(orderList,executeRule,wareRecord,jHWRecordMap,26);
			return;
		}else if(jhwpplx.contains("2")){//按客户类别分配
			allotJHWOfOrderTwo(orderList,executeRule,wareRecord,jHWRecordMap);
		}else if(jhwpplx.contains("6")){//按路线分配
			allotJHWOfOrderSix(orderList,executeRule,wareRecord,jHWRecordMap,6);
		}else{
			
		}
	}

	
	/**
	 * 按客户类别分配集货位
	 * 一个订单分配一个集货位
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param jHWRecordMap 
	 * */
	private void allotJHWOfOrderTwo(List<Record> orderList, Record executeRule,
			Record wareRecord, Map<String, List<Record>> jHWRecordMap) {
		StringBuffer sb=null;
		Record JHWRecord=null;
		List<Record> records=null;
		for(Record orderRecord:orderList){
			records=new ArrayList<Record>();
			if(StringUtils.isEmpty(orderRecord.getStr("luxianId"))){
				getKhxl(orderRecord.getStr("kehubianhao"),orderRecord);
			}
			sb=getJhwSql("", orderRecord.getInt("kehuleixing"));
			JHWRecord=Db.findFirst(sb.toString(),orderRecord.getInt("kehuleixing"));//获取可用的集货位
			if(JHWRecord==null){//没有可用的集货位
				error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】无可用的集货位！    ");
			}else{
				orderRecord.set("qsjhw", JHWRecord.getStr("jihuoweibianhao"));//设置开始集货位
				orderRecord.set("zzjhw", JHWRecord.getStr("jihuoweibianhao"));//设置终止集货位
				orderRecord.set("dingdanzhuangtai", 21);//设置订单已执行
				JHWRecord.set("shifousuoding", 1);
				records.add(JHWRecord);
				jHWRecordMap.put(orderRecord.getStr("dingdanbianhao"), records);
				Db.update("xyy_wms_dic_jihuowei","ID",JHWRecord);//更新该集货位被锁定状态
			}
		}
	
	}

	/**
	 * 获取集货位sql
	 * */
	private StringBuffer getJhwSql(String luxianid,int kehuleixing){
		StringBuffer str=new StringBuffer();
		str.append("select * from  xyy_wms_dic_jihuowei where orgId='"+orgId+"' and shifousuoding=0 and yewuleixing=4 and qiyong=1 ");
		if(StringUtils.isNotEmpty(luxianid)){
			str.append(" and luxianid=? ");
		}
		if(kehuleixing!=0){
			str.append(" and jihuoweileixing=? ");
		}
		str.append(" order by xiangdao asc,pai asc, ceng asc,lie asc ");
		return str;
	}

	/**
	 * 允许同一集货位 /订单/路线
	 * 一个订单一个集货位 不受订单体积和集货位容量体积限制
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param jHWRecordMap 
	 * @param type 
	 */
	private void allotJHWOfOrderSix(List<Record> orderList, Record executeRule,
			Record wareRecord, Map<String, List<Record>> jHWRecordMap, int type) {
		StringBuffer sb=null;
		Record JHWRecord=null;
		List<Record> records=null;
		for(Record orderRecord:orderList){
			records=new ArrayList<Record>();
			if(StringUtils.isEmpty(orderRecord.getStr("luxianId"))){
				getKhxl(orderRecord.getStr("kehubianhao"),orderRecord);
			}
			if(StringUtils.isNotEmpty(orderRecord.getStr("luxianId"))){
				if(type==26){
					sb=getJhwSql(orderRecord.getStr("luxianId"), orderRecord.getInt("kehuleixing"));
					JHWRecord=Db.findFirst(sb.toString(),orderRecord.getStr("luxianId"),orderRecord.getInt("kehuleixing"));//获取可用的集货位
				}else{
					sb=getJhwSql(orderRecord.getStr("luxianId"), 0);
					JHWRecord=Db.findFirst(sb.toString(),orderRecord.getStr("luxianId"));//获取可用的集货位
				}
				if(JHWRecord==null){//没有可用的集货位
					error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】无可用的集货位！    ");
				}else{
					orderRecord.set("qsjhw", JHWRecord.getStr("jihuoweibianhao"));//设置开始集货位
					orderRecord.set("zzjhw", JHWRecord.getStr("jihuoweibianhao"));//设置终止集货位
					orderRecord.set("dingdanzhuangtai", 21);//设置订单已执行
					JHWRecord.set("shifousuoding", 1);
					records.add(JHWRecord);
					jHWRecordMap.put(orderRecord.getStr("dingdanbianhao"), records);
					Db.update("xyy_wms_dic_jihuowei","ID",JHWRecord);//更新该集货位被锁定状态
					}
				}else{
				  error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】未配置客户线路，无法分配集货位！    ");
			}
		}
	}
	
	/**
	 * 允许同一集货位 /客户/路线
	 * 一个客户一个集货位 不受订单体积和集货位容量体积限制
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param type 
	 */
	private void allotJHWOfCustomerSix(List<Record> orderList,
			Record executeRule, Record wareRecord, Map<String, List<Record>> jHWRecordMap, int type) {

		StringBuffer sb=null;
		Record JHWRecord=null;
		String oldCustomerId="";
		String oldjihuoweibianhao="";
		List<Record> records=null;
		for(Record orderRecord:orderList){
			sb=new StringBuffer();
			records=new ArrayList<Record>();
			if(oldCustomerId.equals(orderRecord.getStr("kehubianhao"))){//当前客户订单预上次订单客户一样
				orderRecord.set("qsjhw", oldjihuoweibianhao);//设置开始集货位
				orderRecord.set("zzjhw", oldjihuoweibianhao);//设置终止集货位
				orderRecord.set("dingdanzhuangtai", 21);//设置订单已执行
			}else{
				oldCustomerId=orderRecord.getStr("kehubianhao");//记录客户编号
				if(StringUtils.isEmpty(orderRecord.getStr("luxianId"))){
					getKhxl(orderRecord.getStr("kehubianhao"),orderRecord);
				}
				if(StringUtils.isNotEmpty(orderRecord.getStr("luxianId"))){
					if(type==26){
						sb=getJhwSql(orderRecord.getStr("luxianId"), orderRecord.getInt("kehuleixing"));
						JHWRecord=Db.findFirst(sb.toString(),orderRecord.getStr("luxianId"),orderRecord.getInt("kehuleixing"));//获取可用的集货位
					}else{
						sb=getJhwSql(orderRecord.getStr("luxianId"), 0);
						JHWRecord=Db.findFirst(sb.toString(),orderRecord.getStr("luxianId"));//获取可用的集货位
					}
					if(JHWRecord==null){//没有可用的集货位
						error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】无可用的集货位！    ");
					}else{
						orderRecord.set("qsjhw", JHWRecord.getStr("jihuoweibianhao"));//设置开始集货位
						orderRecord.set("zzjhw", JHWRecord.getStr("jihuoweibianhao"));//设置终止集货位
						orderRecord.set("dingdanzhuangtai", 21);//设置订单已执行
						oldjihuoweibianhao=JHWRecord.getStr("jihuoweibianhao");//记录集货位编号
						JHWRecord.set("shifousuoding", 1);
						records.add(JHWRecord);
						jHWRecordMap.put(orderRecord.getStr("dingdanbianhao"), records);
						Db.update("xyy_wms_dic_jihuowei","ID",JHWRecord);//更新该集货位被锁定状态
					}
				}else{
					  error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】未配置客户线路，无法分配集货位！    ");
				}
			}
		}
	}
	/**
	 * 允许同一集货位 /体积/路线
	 * 一个订单多个集货位 
	 * @param orderList 波次下所以订单
	 * @param executeRule 波次执行规则
	 * @param wareRecord 波次
	 * @param jHWRecordMap 
	 * @param type 
	 */
	private void allotJHWOfVolumeSix(List<Record> orderList,
			Record executeRule, Record wareRecord, Map<String, List<Record>> jHWRecordMap, int type) {
		StringBuffer sb=null;
		List<Record> JHWRecords=null;
		List<Record> newJHWRecords=null;
		for(Record orderRecord:orderList){
			newJHWRecords=new ArrayList<Record>();
			sb=new StringBuffer();
			if(StringUtils.isEmpty(orderRecord.getStr("luxianId"))){
				getKhxl(orderRecord.getStr("kehubianhao"),orderRecord);
			}
			if(StringUtils.isNotEmpty(orderRecord.getStr("luxianId"))){
				if(type==26){
					sb=getJhwSql(orderRecord.getStr("luxianId"), orderRecord.getInt("kehuleixing"));
					JHWRecords=Db.find(sb.toString(),orderRecord.getStr("luxianId"),orderRecord.getInt("kehuleixing"));//获取可用的集货位
				}else{
					sb=getJhwSql(orderRecord.getStr("luxianId"), 0);
					JHWRecords=Db.find(sb.toString(),orderRecord.getStr("luxianId"));//获取可用的集货位
				}
				if(JHWRecords==null||JHWRecords.size()==0){//没有可用的集货位
					error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】无可用的集货位！    ");
				}else{
					double jhwVolume=0;
					for(Record JHWRecord:JHWRecords){
						jhwVolume=jhwVolume+JHWRecord.getBigDecimal("tiji").doubleValue();
						if(orderRecord.getBigDecimal("zongtiji").doubleValue()<=jhwVolume){
							if(StringUtils.isEmpty(orderRecord.getStr("qsjhw"))){//最后一次不用设置
								orderRecord.set("qsjhw", JHWRecord.getStr("jihuoweibianhao"));//设置开始集货位
							}
							orderRecord.set("zzjhw", JHWRecord.getStr("jihuoweibianhao"));//设置终止集货位
							orderRecord.set("dingdanzhuangtai", 21);//设置订单已执行
							JHWRecord.set("shifousuoding", 1);
							newJHWRecords.add(JHWRecord);
							break;
						}else{
							if(StringUtils.isEmpty(orderRecord.getStr("qsjhw"))){
								orderRecord.set("qsjhw", JHWRecord.getStr("jihuoweibianhao"));//设置开始集货位
							}
							JHWRecord.set("shifousuoding", 1);
							newJHWRecords.add(JHWRecord);
						}
						
					}
					if(jhwVolume>=orderRecord.getBigDecimal("zongtiji").doubleValue()){
						jHWRecordMap.put(orderRecord.getStr("dingdanbianhao"), newJHWRecords);
						Db.batchUpdate("xyy_wms_dic_jihuowei","ID",newJHWRecords,newJHWRecords.size());//更新该集货位被锁定状态
					}else{//所有可用集货位的提交之和小于订单商品体积
						orderRecord.set("qsjhw", "");//设置开始集货位
						orderRecord.set("zzjhw", "");//设置终止集货位
						orderRecord.set("dingdanzhuangtai", 20);
						error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】所有可用集货位的体积之和小于订单商品体积,无法分配集货位！  ");
					}
			}
			}else{
				error.append("订单【"+orderRecord.getStr("dingdanbianhao")+"】未配置客户线路，无法分配集货位！    ");
			}
		}
	
	}
	
	private List<Record> mapToList(Map<String, List<Record>>map,boolean bool){
		List<Record> list=new ArrayList<Record>();
		 Iterator it = map.keySet().iterator();  
	       while (it.hasNext()) {  
	           String key = it.next().toString();  
	           List<Record> rList=map.get(key);
	           for(Record r:rList ){
	        	   if(bool){
	        		   list.add(r.remove("kqlbbh").remove("huoweibianhao").remove("kuqubianhao").remove("mykuquId").remove("pihao").remove("tiji"));
	        	   }else{
	        		   list.add(r);
	        	   }
	           }  
	       }  
		return list;
	}

	private void getKhxl(String kehubianhao,Record orderRecord){
		StringBuffer sb=new StringBuffer("select  t2.ID,t2.xianlubianhao,t2.xianlumingcheng,t2.xlyxj");
		sb.append("  from xyy_wms_dic_kehuxianlu t1,xyy_wms_dic_peisongxianlu t2,xyy_wms_dic_kehuxianlu_list t3  where t1.xianluID = t2.ID and t1.ID=t3.ID  ");
		sb.append("  and t3.kehubianhao=? and t1.orgId=? and t1.shifouhuodong=1 and t2.shifouhuodong=1 order by t2.xlyxj ");
		Record record=Db.findFirst(sb.toString(),kehubianhao,orgId);
		if(record!=null){
			orderRecord.set("luxianId", record.getStr("ID"));//设置线路ID
			orderRecord.set("luxian", record.getStr("xianlubianhao"));//设置线路code
			orderRecord.set("luxianmingcheng", record.getStr("xianlumingcheng"));//设置线路名称
			if(StringUtils.isEmpty(orderRecord.getStr("youxianji"))){
				orderRecord.set("youxianji", record.getInt("xlyxj"));//设置线路优先级
			}
		}
	}
}
