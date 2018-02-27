package com.xyy.wms.outbound.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ZhangYeParameter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String orgId;
	private String orgCode;
	private String danjubianhao;
	private String shangpinId;
	private BigDecimal rukushuliang;
	private BigDecimal chukushuliang;
	private int zhiliangzhuangtai;
	private String pihaoId;
	private String huoweiId;
	private String huozhuId;
	private String cangkuId;
	private Date zhidanriqi;
	private String zhidanren;
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getDanjubianhao() {
		return danjubianhao;
	}
	public void setDanjubianhao(String danjubianhao) {
		this.danjubianhao = danjubianhao;
	}
	public String getShangpinId() {
		return shangpinId;
	}
	public void setShangpinId(String shangpinId) {
		this.shangpinId = shangpinId;
	}
	public BigDecimal getRukushuliang() {
		return rukushuliang;
	}
	public void setRukushuliang(BigDecimal rukushuliang) {
		this.rukushuliang = rukushuliang;
	}
	public BigDecimal getChukushuliang() {
		return chukushuliang;
	}
	public void setChukushuliang(BigDecimal chukushuliang) {
		this.chukushuliang = chukushuliang;
	}
	public int getZhiliangzhuangtai() {
		return zhiliangzhuangtai;
	}
	public void setZhiliangzhuangtai(int zhiliangzhuangtai) {
		this.zhiliangzhuangtai = zhiliangzhuangtai;
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
	public String getHuozhuId() {
		return huozhuId;
	}
	public void setHuozhuId(String huozhuId) {
		this.huozhuId = huozhuId;
	}
	public String getCangkuId() {
		return cangkuId;
	}
	public void setCangkuId(String cangkuId) {
		this.cangkuId = cangkuId;
	}
	public Date getZhidanriqi() {
		return zhidanriqi;
	}
	public void setZhidanriqi(Date zhidanriqi) {
		this.zhidanriqi = zhidanriqi;
	}
	public String getZhidanren() {
		return zhidanren;
	}
	public void setZhidanren(String zhidanren) {
		this.zhidanren = zhidanren;
	}
	

}
