package com.xyy.wms.outbound.util;

import java.io.Serializable;
import java.math.BigDecimal;

public class KuncunParameter implements Serializable{

	private static final long serialVersionUID = 5999166136838934757L;
	private String orgId;//机构ID
	private String cangkuId;//仓库ID
	private String goodsid;//商品ID
	private String pihaoId;//批号ID
	private String huoweiId;//货位ID
	private String yewubianhao;//业务类型编号
	private String danjubianhao;//单据编号
	private int zuoyeleixing;//作业类型
	private BigDecimal shuliang;//数量
	
	public KuncunParameter(String orgId,String cangkuId, String goodsid, String pihaoId,
			String huoweiId, String yewubianhao, String danjubianhao,
			int zuoyeleixing, BigDecimal shuliang) {
		super();
		this.orgId=orgId;
		this.cangkuId = cangkuId;
		this.goodsid = goodsid;
		this.pihaoId = pihaoId;
		this.huoweiId = huoweiId;
		this.yewubianhao = yewubianhao;
		this.danjubianhao = danjubianhao;
		this.zuoyeleixing = zuoyeleixing;
		this.shuliang = shuliang;
	}
	
	public KuncunParameter(String orgId,String cangkuId, String goodsid, String pihaoId,
			String huoweiId) {
		super();
		this.orgId=orgId;
		this.cangkuId = cangkuId;
		this.goodsid = goodsid;
		this.pihaoId = pihaoId;
		this.huoweiId = huoweiId;
	}
	
	public KuncunParameter(BigDecimal shuliang,String orgId, String yewubianhao, String danjubianhao, String goodsid) {
		super();
		this.shuliang = shuliang;
		this.orgId=orgId;
		this.yewubianhao = yewubianhao;
		this.danjubianhao = danjubianhao;
		this.goodsid = goodsid;
	}
	
	public KuncunParameter(BigDecimal shuliang,String orgId, String yewubianhao, String danjubianhao, String goodsid ,String pihaoId) {
		super();
		this.shuliang = shuliang;
		this.orgId=orgId;
		this.yewubianhao = yewubianhao;
		this.danjubianhao = danjubianhao;
		this.goodsid = goodsid;
		this.pihaoId = pihaoId;
	}
	
	public KuncunParameter(BigDecimal shuliang,String orgId, String yewubianhao, String danjubianhao, String goodsid ,String pihaoId,String huoweiId) {
		super();
		this.shuliang = shuliang;
		this.orgId=orgId;
		this.yewubianhao = yewubianhao;
		this.danjubianhao = danjubianhao;
		this.goodsid = goodsid;
		this.pihaoId = pihaoId;
		this.huoweiId = huoweiId;
	}
	
	public KuncunParameter(String orgId,String cangkuId, String goodsid, String pihaoId,
			String huoweiId,int zuoyeleixing)
		{
		super();
		this.orgId=orgId;
		this.cangkuId = cangkuId;
		this.goodsid = goodsid;
		this.pihaoId = pihaoId;
		this.huoweiId = huoweiId;
		this.zuoyeleixing = zuoyeleixing;
	}
	
	public KuncunParameter(String orgId, String goodsid, int zuoyeleixing) {
		super();
		this.orgId=orgId;
		this.goodsid = goodsid;
		this.zuoyeleixing = zuoyeleixing;
	}

	public KuncunParameter(String orgId, String danjubianhao, String goodsid, String pihaoId, String huoweiId,
			String yewubianhao) {
		super();
		this.orgId=orgId;
		this.goodsid = goodsid;
		this.danjubianhao = danjubianhao;
		this.pihaoId = pihaoId;
		this.huoweiId = huoweiId;
		this.yewubianhao = yewubianhao;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getCangkuId() {
		return cangkuId;
	}
	public void setCangkuId(String cangkuId) {
		this.cangkuId = cangkuId;
	}
	public String getGoodsid() {
		return goodsid;
	}
	public void setGoodsid(String goodsid) {
		this.goodsid = goodsid;
	}
	public String getPihaoId() {
		return pihaoId;
	}
	public void setPihaoId(String pihaoId) {
		this.pihaoId = pihaoId;
	}
	public String getHuoweiId() {
		return huoweiId;
	}
	public void setHuoweiId(String huoweiId) {
		this.huoweiId = huoweiId;
	}
	public String getYewubianhao() {
		return yewubianhao;
	}
	public void setYewubianhao(String yewubianhao) {
		this.yewubianhao = yewubianhao;
	}
	public String getDanjubianhao() {
		return danjubianhao;
	}
	public void setDanjubianhao(String danjubianhao) {
		this.danjubianhao = danjubianhao;
	}
	public int getZuoyeleixing() {
		return zuoyeleixing;
	}
	public void setZuoyeleixing(int zuoyeleixing) {
		this.zuoyeleixing = zuoyeleixing;
	}
	public BigDecimal getShuliang() {
		return shuliang;
	}
	public void setShuliang(BigDecimal shuliang) {
		this.shuliang = shuliang;
	}
	
	
	
	
}
