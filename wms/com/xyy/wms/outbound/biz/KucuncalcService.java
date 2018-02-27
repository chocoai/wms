package com.xyy.wms.outbound.biz;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.util.UUIDUtil;
import com.xyy.wms.outbound.util.KuncunParameter;


/**
 * 作业类型
 * 1、入库预占 2、出库预扣 3、补入预占 4、补出预扣 5、移入预占 6、移出预扣7、预计损 8、预计溢 
 * */
public class KucuncalcService {
	public static KucuncalcService kcCalc=new KucuncalcService();
	
	private static String objToString(Object[] objs){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<objs.length;i++){
			if(i==objs.length-1){
				sb.append(objs[i]);
			}else{
				sb.append(objs[i]).append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 商品批号货位预扣预占
	 * 获取各业务下预占预扣总库存
	 * @param objs 形如出库业务为{2,3,4,6,7,8}
	 * */
	public BigDecimal getYuzhanZongKC(KuncunParameter paras,Object[] objs,Object...obj){
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT sum(shuliang) zsl,zuoyeleixing from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=?  and goodsid=?  and pihaoId=? and huoweiId=? and zuoyeleixing in("+objToString(objs)+")");
		if(obj.length>0){//需定位到具体的业务数据
			sb.append(" and danjubianhao=? ");
			if(StringUtils.isNotEmpty(paras.getYewubianhao())){
				sb.append(" and yewubianhao=? ");
			}
		}
		sb.append(" group by zuoyeleixing order by zuoyeleixing asc ");
		List<Record> records=new ArrayList<Record>();
		if(obj.length>0){//需定位到具体的业务数据
			if(StringUtils.isNotEmpty(paras.getYewubianhao())){
				records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId(),
						paras.getDanjubianhao(),paras.getYewubianhao());
			}else{
				records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId(),paras.getDanjubianhao());	
			}
		}else{
			records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId());
		}
		
		BigDecimal zongKC=BigDecimal.ZERO;
		if(records.size()>0){
			return getYuzhanYukouKC(records);
		}
		return zongKC;
	}
	
	/**
	 * 商品批号预扣预占
	 * 获取各业务下预占预扣总库存
	 * @param objs 形如出库业务为{2,3,4,6,7}
	 * */
	public BigDecimal getYuzhanZongKCPh(KuncunParameter paras,Object[] objs,Object...obj){
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT sum(shuliang) zsl,zuoyeleixing from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=?  and goodsid=?  and pihaoId=?  and zuoyeleixing in("+objToString(objs)+") ");
		if(obj.length>0){//需定位到具体的业务数据
			sb.append(" and danjubianhao=? ");
			if(StringUtils.isNotEmpty(paras.getYewubianhao())){
				sb.append(" and yewubianhao=? ");
			}
		}
		sb.append(" group by zuoyeleixing order by zuoyeleixing asc ");
		List<Record> records=new ArrayList<Record>();
		if(obj.length>0){//需定位到具体的业务数据
			if(StringUtils.isNotEmpty(paras.getYewubianhao())){
				records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId(),
						paras.getDanjubianhao(),paras.getYewubianhao());
			}else{
				records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId(),paras.getDanjubianhao());	
			}
		}else{
			records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId());
		}
		
		BigDecimal zongKC=BigDecimal.ZERO;
		if(records.size()>0){
			return getYuzhanYukouKC(records);
		}
		return zongKC;
	}
	
	
	/**
	 * 货位预扣预占
	 * 获取各业务下预占预扣总库存
	 * @param objs 适合入库
	 * */
	public BigDecimal getYuzhanRuKuHW(KuncunParameter paras,Object[] objs,Object...obj){
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT sum(shuliang) zsl,zuoyeleixing from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=?  and goodsid=?  and pihaoId=?  and zuoyeleixing in("+objToString(objs)+") ");
		if(obj.length>0){
			if(StringUtils.isNotEmpty(paras.getHuoweiId())){
				sb.append(" and huoweiId=? ");
			}
		}
		sb.append(" group by zuoyeleixing order by zuoyeleixing asc ");
		List<Record> records=new ArrayList<Record>();
		if(obj.length>0){//需定位到具体的业务数据
			if(StringUtils.isNotEmpty(paras.getYewubianhao())){
				records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId(),
						paras.getHuoweiId());
			}else{
				records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId());	
			}
		}else{
			records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId());
		}
		
		BigDecimal zongKC=BigDecimal.ZERO;
		if(records.size()>0){
			return getYuzhanYukouKC(records);
		}
		return zongKC;
	}

	
	
	/**
	 * 商品预扣预占
	 * 获取各业务下预占预扣总库存
	 * @param objs 形如出库业务为{2,3,4,6,7}
	 * */
	public BigDecimal getYuzhanZongKCSp(KuncunParameter paras,Object[] objs,Object...obj){
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT sum(shuliang) zsl,zuoyeleixing from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=?  and goodsid=?  and zuoyeleixing in("+objToString(objs)+")");
		if(obj.length>0){//需定位到具体的业务数据
			sb.append(" and danjubianhao=? ");
			if(StringUtils.isNotEmpty(paras.getYewubianhao())){
				sb.append(" and yewubianhao=? ");
			}
		}
		sb.append(" group by zuoyeleixing order by zuoyeleixing asc ");
		List<Record> records=new ArrayList<Record>();
		if(obj.length>0){//需定位到具体的业务数据
			if(StringUtils.isNotEmpty(paras.getYewubianhao())){
				records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),
						paras.getDanjubianhao(),paras.getYewubianhao());
			}else{
				records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getDanjubianhao());	
			}
		}else{
			records=Db.find(sb.toString(),paras.getOrgId(),paras.getGoodsid());
		}
		BigDecimal zongKC=BigDecimal.ZERO;
		if(records.size()>0){
			return getYuzhanYukouKC(records);
		}
		return zongKC;
	}
	
	private BigDecimal getYuzhanYukouKC(List<Record> records){
		BigDecimal zongKC=BigDecimal.ZERO;
		for(Record r:records){
			switch (r.getInt("zuoyeleixing")){
			case 1://入库预占 
				zongKC=zongKC.add(r.getBigDecimal("zsl"));
				break;
			case 2://出库预扣
				zongKC=zongKC.subtract(r.getBigDecimal("zsl"));
				break;
			case 3://补入预占
				zongKC=zongKC.add(r.getBigDecimal("zsl"));
				break;
			case 4://补出预扣
				zongKC=zongKC.subtract(r.getBigDecimal("zsl"));
				break;
			case 5://移入预占
				zongKC=zongKC.add(r.getBigDecimal("zsl"));
				break;
			case 6://移出预扣
				zongKC=zongKC.subtract(r.getBigDecimal("zsl"));
				break;
			case 7://预计损
				zongKC=zongKC.subtract(r.getBigDecimal("zsl"));
				break;
			case 8://预计益
				zongKC=zongKC.add(r.getBigDecimal("zsl"));
				break;
			default:
				break;
			}
			
		}
		return zongKC;
	}
	
	/**
	 * 计算单个商品总单作业业务下的总预占或者预扣
	 *@param paras 查询参数
	 * 
	 * */
	public BigDecimal getKucunCalcOfshanpin(KuncunParameter paras){
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT sum(shuliang) zsl from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=? and goodsid=? and zuoyeleixing=?  ");
		if(StringUtils.isNotEmpty(paras.getYewubianhao())){
			sb.append("  and yewubianhao=?  ");
		}
		Record r=new Record();
		r=Db.findFirst(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getZuoyeleixing());
		return r.getBigDecimal("zsl")==null?BigDecimal.ZERO:r.getBigDecimal("zsl");
	}
	
	
	/**
	 * 计算单个商品批号单作业业务下的总预占或者预扣
	*@param paras 查询参数
	 * */
	public BigDecimal getKucunCalcOfshanpinPihao(KuncunParameter paras){
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT sum(shuliang) zsl from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=? and goodsid=?  and pihaoId=? and zuoyeleixing=? ");
		Record r=new Record();
		r=Db.findFirst(sb.toString(),paras.getOrgId(),paras.getGoodsid(),paras.getPihaoId(),paras.getZuoyeleixing());
		return r.getBigDecimal("zsl")==null?BigDecimal.ZERO:r.getBigDecimal("zsl");
	}
	
	/**
	 * 计算单个商品批号单作业业务下的总预占或者预扣
	 *@param paras 查询参数
	 * */
	public BigDecimal getKucunCalcOfshanpinPihaoHuowei(KuncunParameter paras){
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT sum(shuliang) zsl from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=? and goodsid=?  and pihaoId=? and huoweiId=? and zuoyeleixing=? ");
		Record r=new Record();
		r=Db.findFirst(sb.toString(),paras.getOrgId(),
					paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId(),paras.getZuoyeleixing());
		return r.getBigDecimal("zsl")==null?BigDecimal.ZERO:r.getBigDecimal("zsl");
	}

	/**
	 * 单一插入预扣预占表
	 *@param paras 查询参数
	 * */
	public void insertKCRecord(KuncunParameter paras) {
		Record record=new Record();
		record.set("BillID", UUIDUtil.newUUID());
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		record.set("status",40);
		record.set("orgId", paras.getOrgId());
		record.set("createTime", currentTime);
		record.set("updateTime", currentTime);
		record.set("rowID", UUIDUtil.newUUID());
		record.set("cangkuId", paras.getCangkuId());
		record.set("goodsid", paras.getGoodsid());
		record.set("pihaoId", paras.getPihaoId());
		record.set("huoweiId", paras.getHuoweiId());
		record.set("yewubianhao", paras.getYewubianhao());
		record.set("danjubianhao", paras.getDanjubianhao());
		record.set("zuoyeleixing", paras.getZuoyeleixing());
		record.set("shuliang", paras.getShuliang());
		Db.save("xyy_wms_bill_kucunyuzhanyukou", record);
	}
	/**
	 * 缓存插入预扣预占表
	 *@param paras 查询参数
	 * */
	public void insertCacheKCRecord(List<Record> paraList,KuncunParameter paras) {
		Record record=new Record();
		record.set("BillID", UUIDUtil.newUUID());
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		record.set("status",40);
		record.set("orgId", paras.getOrgId());
		record.set("createTime", currentTime);
		record.set("updateTime", currentTime);
		record.set("rowID", UUIDUtil.newUUID());
		record.set("cangkuId", paras.getCangkuId());
		record.set("goodsid", paras.getGoodsid());
		record.set("pihaoId", paras.getPihaoId());
		record.set("huoweiId", paras.getHuoweiId());
		record.set("yewubianhao", paras.getYewubianhao());
		record.set("danjubianhao", paras.getDanjubianhao());
		record.set("zuoyeleixing", paras.getZuoyeleixing());
		record.set("shuliang", paras.getShuliang());
		paraList.add(record);
	}
	/**
	 *批量插入缓存记录
	 *@param paras 查询参数
	 * */
	public void insertBatchKCRecord(List<Record> paraList) {
		Db.batchSave("xyy_wms_bill_kucunyuzhanyukou", paraList, paraList.size());
	}
	/**
	 *删除预扣预占表数据
	 *@param paras 查询参数
	 *不需要同步商品批号库存和商品总库存的业务请使用该方法
	 * */
	public void deleteKCRecord(KuncunParameter paras) {
		//更新商品批号货位库存
		BigDecimal spphhwkc=getYuzhanZongKC(paras, new Object[]{paras.getZuoyeleixing()},true);
		Db.update("update  xyy_wms_bill_shangpinpihaohuoweikucun set kucunshuliang=kucunshuliang+? "
				+ " where orgId=? and shangpinId=? and pihaoId=? and huoweiId=?", spphhwkc,paras.getOrgId(),
				paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId());
		List<Record> paraList=new ArrayList<Record>();
		insertCacheKCRecord(paraList, paras);
		StringBuffer sb=new StringBuffer();
		sb.append("delete from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=?  and danjubianhao=? and goodsid=?  and pihaoId=? and huoweiId=? and zuoyeleixing=? ");
		if(StringUtils.isNotEmpty(paras.getYewubianhao())){
			sb.append("  and yewubianhao=?  ");
		}
		if(StringUtils.isNotEmpty(paras.getYewubianhao())){
			Db.batch(sb.toString(), "orgId,danjubianhao,goodsid,pihaoId,huoweiId,zuoyeleixing,yewubianhao", paraList, paraList.size());
		}else{
			Db.batch(sb.toString(), "orgId,danjubianhao,goodsid,pihaoId,huoweiId,zuoyeleixing", paraList, paraList.size());
		}
		
	}
	
	/**
	 *批量删除预扣预占表数据
	 *@param paras 查询参数
	 * */
	public void deleteKCRecord(KuncunParameter paras,Object[] objs) {
		updateLastKcRecord(paras, objs);
		List<Record> paraList=new ArrayList<Record>();
		insertCacheKCRecord(paraList, paras);
		StringBuffer sb=new StringBuffer();
		sb.append("delete from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=?  and danjubianhao=? and goodsid=?  and pihaoId=? and huoweiId=? and zuoyeleixing in("+objToString(objs)+")");
		if(StringUtils.isNotEmpty(paras.getYewubianhao())){
			sb.append("  and yewubianhao=?  ");
		}
		if(StringUtils.isNotEmpty(paras.getYewubianhao())){
			Db.batch(sb.toString(), "orgId,danjubianhao,goodsid,pihaoId,huoweiId,yewubianhao", paraList, paraList.size());
		}else{
			Db.batch(sb.toString(), "orgId,danjubianhao,goodsid,pihaoId,huoweiId", paraList, paraList.size());
		}
	}
	
	/**
	 * 同步商品批号货位库存 同步商品批号库存 同步商品总库存
	 * 该方法不适合入库预占
	 * */
	public void updateLastKcRecord(KuncunParameter paras,Object[] objs){
		//更新商品批号货位库存
		BigDecimal spphhwkc=getYuzhanZongKC(paras, objs,true);
		Db.update("update  xyy_wms_bill_shangpinpihaohuoweikucun set kucunshuliang=kucunshuliang+? "
				+ " where orgId=? and shangpinId=? and pihaoId=? and huoweiId=?", spphhwkc,paras.getOrgId(),
				paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId());
		
		//同步商品批号库存
		BigDecimal spphkc=getYuzhanZongKCPh(paras, objs,true);
		Db.update("update  xyy_wms_bill_shangpinpihaokucun set kucunshuliang=kucunshuliang+? "
				+ " where orgId=? and shangpinId=? and pihaoId=?", spphkc,paras.getOrgId(),
				paras.getGoodsid(),paras.getPihaoId());
		//同步商品总库存
		BigDecimal spzkc=getYuzhanZongKCSp(paras, objs,true);
		Db.update("update  xyy_wms_bill_shangpinzongkucun set kucunshuliang=kucunshuliang+? "
				+ " where orgId=? and shangpinId=? ", spzkc,paras.getOrgId(),
				paras.getGoodsid());
		
	}
	
	/**
	 * 同步商品批号货位库存 同步商品批号库存 同步商品总库存
	 * 该方法只适合入库预占
	 * */
	public void updateLastRkRecord(KuncunParameter paras){
		Record r=Db.findFirst("SELECT sum(shuliang) zsl from xyy_wms_bill_kucunyuzhanyukou where orgId=? and yewubianhao=?"
				+ " and danjubianhao=? and goodsid=?  and pihaoId=? and huoweiId=? and zuoyeleixing=1", paras.getOrgId(),
				paras.getYewubianhao(),paras.getDanjubianhao(),paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId());
		//更新商品批号货位库存
		if(r!=null&&r.getBigDecimal("zsl")!=null){
			Db.update("update  xyy_wms_bill_shangpinpihaohuoweikucun set kucunshuliang=kucunshuliang+? "
					+ " where orgId=? and shangpinId=? and pihaoId=? and huoweiId=?", r.getBigDecimal("zsl"),paras.getOrgId(),
					paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId());
		}
		Record r2=Db.findFirst("SELECT sum(shuliang) zsl from xyy_wms_bill_kucunyuzhanyukou where orgId=? and yewubianhao=?"
				+ " and danjubianhao=? and goodsid=?  and pihaoId=? and huoweiId is null and zuoyeleixing=1", paras.getOrgId(),
				paras.getYewubianhao(),paras.getDanjubianhao(),paras.getGoodsid(),paras.getPihaoId());
		//同步商品批号库存
		if(r2!=null&&r2.getBigDecimal("zsl")!=null){
			Db.update("update  xyy_wms_bill_shangpinpihaokucun set kucunshuliang=kucunshuliang+? "
				+ " where orgId=? and shangpinId=? and pihaoId=?", r2.getBigDecimal("zsl"),paras.getOrgId(),
				paras.getGoodsid(),paras.getPihaoId());
		}
		Record r3=Db.findFirst("SELECT sum(shuliang) zsl from xyy_wms_bill_kucunyuzhanyukou where orgId=? and yewubianhao=?"
				+ " and danjubianhao=? and goodsid=?  and pihaoId is null and huoweiId is null and zuoyeleixing=1", paras.getOrgId(),
				paras.getYewubianhao(),paras.getDanjubianhao(),paras.getGoodsid());
		//同步商品总库存
		if(r3!=null&&r3.getBigDecimal("zsl")!=null){
			Db.update("update  xyy_wms_bill_shangpinzongkucun set kucunshuliang=kucunshuliang+? "
				+ " where orgId=? and shangpinId=? ", r3.getBigDecimal("zsl"),paras.getOrgId(),
				paras.getGoodsid());
		}
	}
	
	/**
	 *删除预扣预占表数据
	 *@param paras 查询参数
	 *适合入库预占业务
	 * */
	public void deleteRkKCRecord(KuncunParameter paras) {
		List<Record> paraList=new ArrayList<Record>();
		insertCacheKCRecord(paraList, paras);
		StringBuffer sb=new StringBuffer();
		sb.append("delete from xyy_wms_bill_kucunyuzhanyukou  ");
		sb.append(" where orgId=?  and danjubianhao=? and goodsid=? and pihaoId =? and huoweiId=? and zuoyeleixing=1 ");
		sb.append("  and yewubianhao=?  ");
		Db.batch(sb.toString(), "orgId,danjubianhao,goodsid,pihaoId,huoweiId,yewubianhao", paraList, paraList.size());
	}
	
	/**
	 *修改入库预占记录,主要是针对减库存的情况
	 *@param paras 查询参数
	 *适合入库预占业务
	 * */
	public void updateRkRecord(KuncunParameter paras){
		StringBuffer sb=new StringBuffer();
		sb.append("update xyy_wms_bill_kucunyuzhanyukou  set shuliang=shuliang-? ");
		sb.append(" where orgId=? and yewubianhao=? and danjubianhao=?  and zuoyeleixing=1 ");
		if(StringUtils.isNotEmpty(paras.getGoodsid()) && StringUtils.isEmpty(paras.getPihaoId()) && StringUtils.isEmpty(paras.getHuoweiId())){
			sb.append(" and goodsid=? and pihaoId is null and huoweiId is null and goodsid is not null");
			Db.update(sb.toString(), paras.getShuliang(),paras.getOrgId(),paras.getYewubianhao(),paras.getDanjubianhao(),
					paras.getGoodsid());
		}else if(StringUtils.isNotEmpty(paras.getPihaoId()) && StringUtils.isEmpty(paras.getHuoweiId())){
			sb.append(" and goodsid=? and pihaoId=? and huoweiId is null and goodsid is not null and pihaoId is not null");
			Db.update(sb.toString(), paras.getShuliang(),paras.getOrgId(),paras.getYewubianhao(),paras.getDanjubianhao(),
					paras.getGoodsid(),paras.getPihaoId());
		}else if (StringUtils.isNotEmpty(paras.getHuoweiId())){
			sb.append(" and goodsid=? and pihaoId=? and huoweiId=? and goodsid is not null and pihaoId is not null and huoweiId is not null");
			Db.update(sb.toString(),paras.getShuliang(), paras.getOrgId(),paras.getYewubianhao(),paras.getDanjubianhao(),
					paras.getGoodsid(),paras.getPihaoId(),paras.getHuoweiId());
		}
	}

}
