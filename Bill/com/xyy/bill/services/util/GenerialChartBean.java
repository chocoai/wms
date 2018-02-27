package com.xyy.bill.services.util;

import com.xyy.bill.instance.BillContext;
import com.xyy.bill.meta.BillUIChart.GatherType;

/**
 * 		  类别实例        系类实例                 系类指标
		  湖北              default     100
		 湖南                default    1000
 * @author evan
 *
 */
public class GenerialChartBean {
	public static final String DEFAULT_CATEGORY="_DEFAULT_CATEGORY";
	public static final String DEFAULT_SERIAL="_DEFAULT_SERIAL";
    private BillContext context;
    private GatherType gatherType;
	private String category;//分类
	private String serial;//系列
	private String seriChart;//系列图型样式
	private Number serialValue;//系列值
	
	private Number xSerialValue;
	private Number ySerialValue;
	private Number zSerialValue;
	
	
	public GenerialChartBean(BillContext context, String category, String serial, Number serialValue) {
		super();
		this.context = context;
		this.category = category;
		this.serial = serial;
		this.serialValue = serialValue;
	}
	public GenerialChartBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BillContext getContext() {
		return context;
	}
	public void setContext(BillContext context) {
		this.context = context;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public Number getSerialValue() {
		return serialValue;
	}
	public void setSerialValue(Number serialValue) {
		this.serialValue = serialValue;
	}
	public String getSeriChart() {
		return seriChart;
	}
	public void setSeriChart(String seriChart) {
		this.seriChart = seriChart;
	}
	public Number getxSerialValue() {
		return xSerialValue;
	}
	public void setxSerialValue(Number xSerialValue) {
		this.xSerialValue = xSerialValue;
	}
	public Number getySerialValue() {
		return ySerialValue;
	}
	public void setySerialValue(Number ySerialValue) {
		this.ySerialValue = ySerialValue;
	}
	public Number getzSerialValue() {
		return zSerialValue;
	}
	public void setzSerialValue(Number zSerialValue) {
		this.zSerialValue = zSerialValue;
	}
	public GatherType getGatherType() {
		return gatherType;
	}
	public void setGatherType(GatherType gatherType) {
		this.gatherType = gatherType;
	}
	
}
