package com.xyy.bill.render.html;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyy.bill.meta.BillUIChart;
import com.xyy.bill.meta.BillUIChart.Category;
import com.xyy.bill.meta.BillUIChart.Series;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.spread.BillUIChartInstance;
import com.xyy.erp.platform.common.tools.NumberUtil;
import com.xyy.util.Base64;
import com.xyy.util.StringUtil;

public class HtmlBillUIChartRender extends HTMLRender {

	public HtmlBillUIChartRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIChart";
	}

	/**
	 * <div width="" height="" ></div>
	 */
	@Override
	protected void onRenderBeginTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIChartInstance instance = (BillUIChartInstance) component;
		BillUIChart billUIChart = instance.getBillUIChart();
		writer.writeBeginTag("div");
		writer.writeProperty("title", billUIChart.getTitle());
		this.doLayoutInParent(component);
		writer.writeProperty("legend", billUIChart.getLegend());
		if (billUIChart.getKey() != null) {
			writer.writeProperty("key", billUIChart.getKey());
		}
		writer.writeProperty("renderId", writer.getRenderID());
		writer.writeProperty("tooltip", billUIChart.getTooltip());
		writer.writeProperty("toolbox", billUIChart.getToolbox());
		writer.writeProperty("chartType", billUIChart.getChartType());
		writer.writeProperty("dataSetType", billUIChart.getDataSetType());
		writer.writeProperty("dataSetKey", billUIChart.getDataSetkey());
		writer.writeProperty("dataTableKey", billUIChart.getDataTableKey());
		writer.writeProperty("float", billUIChart.getFloatType());
		writer.writeProperty(this.getFlag().toLowerCase(), null);
		this.writerInstructAndSuperProperties(writer, billUIChart);
		switch (billUIChart.getChartType()) {
		case GenerialChart:
			// 常用图表分类从一级分类开始取
			// 输出一级分组信息
			this.writeCategory(writer, billUIChart);
			// 输出二级分组信息
			this.writeSeries(writer, billUIChart);
			break;
		case ScatterChart:
		case ScatterPlotChart:
			// 散点、气泡取分类从二级开始取
			// 输出二级分组信息
			this.writeSeries(writer, billUIChart);
			break;
		default:
			break;
		}
	}

	private void writerInstructAndSuperProperties(HtmlWriter writer, BillUIChart widget) {
		// widget高级属性--initValue
		if (widget.getInitFunction() != null) {
			if (!StringUtil.isEmpty(widget.getInitFunction().getInitExpr())) {
				writer.writeProperty("init", Base64.encoder(widget.getInitFunction().getInitExpr()));
			}
		}
	}

	private void writeSeries(HtmlWriter writer, BillUIChart billUIChart) {
		JSONArray result = new JSONArray();
		List<Series> series = billUIChart.getCategories().get(0).getSeries();
		for (Series serie : series) {
			JSONObject json = new JSONObject();
			json.put("chart", serie.getChart());
			json.put("groupRule", serie.getGroupRule());
			json.put("group", serie.getGroup());
			switch (billUIChart.getChartType()) {
			case GenerialChart:
				json.put("gatherFieldKey", serie.getGatherFieldKey());
				json.put("gatherType", serie.getGatherType());
				break;
			case ScatterChart:
				json.put("gatherFieldKey", serie.getGatherFieldKey());
				json.put("gatherType", serie.getGatherType());
				json.put("xGatherFieldKey", serie.getXgatherFieldKey());
				json.put("xGatherType", serie.getXgatherType());
				json.put("yGatherFieldKey", serie.getYgatherFieldKey());
				json.put("yGatherType", serie.getYgatherType());
				break;
			case ScatterPlotChart:
				json.put("gatherFieldKey", serie.getGatherFieldKey());
				json.put("gatherType", serie.getGatherType());
				json.put("xGatherFieldKey", serie.getXgatherFieldKey());
				json.put("xGatherType", serie.getXgatherType());
				json.put("yGatherFieldKey", serie.getYgatherFieldKey());
				json.put("yGatherType", serie.getYgatherType());
				json.put("zGatherFieldKey", serie.getZgatherFieldKey());
				json.put("zGatherType", serie.getZgatherType());
				break;
			default:
				break;
			}
			result.add(json);
		}
		writer.writeProperty("series", Base64.encoder(result.toJSONString()));
	}

	private void writeCategory(HtmlWriter writer, BillUIChart billUIChart) {
		List<Category> categorys = billUIChart.getCategories();
		// 定义分类轴
		writer.writeProperty("axis", categorys.get(0).getAxis());
		// 获取一级分组规则
		writer.writeProperty("groupType", categorys.get(0).getGroupType());
		writer.writeProperty("groudBy", categorys.get(0).getGroudBy());
	}

	/**
	 * 在父布局中布局自己
	 * 
	 * @param component
	 */
	private void doLayoutInParent(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIChartInstance instance = (BillUIChartInstance) component;
		BillUIChart billUIChart = instance.getBillUIChart();

		String width = billUIChart.getWidth();
		if (width.endsWith("px") || width.endsWith("%")) {
			writer.writeStyle("width", width);
		} else {
			Integer iWidth = NumberUtil.getIntegerValue(width);
			if (iWidth != null) {
				writer.writeStyle("width", iWidth + "px");
			} else {
				Float fWidth = NumberUtil.getFloatValue(width);
				if (fWidth != null) {
					writer.writeStyle("width", (fWidth * 100) + "%");
				} else {
					writer.writeStyle("width", "100%");
				}
			}
		}

		String height = billUIChart.getHeight();
		if (height.endsWith("px") || height.endsWith("%")) {
			writer.writeStyle("height", height);
		} else {
			Integer iHeight = NumberUtil.getIntegerValue(height);
			if (iHeight != null) {
				writer.writeStyle("height", iHeight + "px");
			} else {
				Float fHeight = NumberUtil.getFloatValue(height);
				if (fHeight != null) {
					writer.writeStyle("height", (fHeight * 100) + "%");
				} else {
					writer.writeStyle("height", "100%");
				}
			}
		}
	}

	@Override
	protected void onRenderContent(IComponent component) {

	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		writer.writeEndTag("div");
	}

}
