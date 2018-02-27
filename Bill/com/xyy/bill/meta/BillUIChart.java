package com.xyy.bill.meta;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillUIWidget.Bound;
import com.xyy.bill.meta.BillUIWidget.CheckRule;
import com.xyy.bill.meta.BillUIWidget.Formatter;
import com.xyy.bill.meta.BillUIWidget.InitFunction;
import com.xyy.bill.meta.BillUIWidget.Trigger;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

@SuppressWarnings("unused")
@XmlComponent(tag="BillUIChart",type=BillUIChart.class)
public class BillUIChart extends BillUIController {
	private ChartType chartType;
	private boolean D3D;
	private DataSourceType dataSourceType;
	private String dataTableKey;
	private Color bacgroundkColor;
	private Color plotColor;
	private String title;
	private String xAxisLabel;
	private String yAxisLabel;
	private int width;
	private int height;

	private List<Category> categories=new ArrayList<>();
	
	/*
	 * 数据来源类型
	 */
	private DataSetType  dataSetType;
	/*
	 * 数据集定义
	 */
	private String dataSetkey;
	/*
	 * 图表显示方式
	 */
	private Float floatType;
	/*
	 * 图表工具栏
	 */
	private String toolbox;
	/*
	 * 是否显示分类名
	 */
	private boolean legend;
	/*
	 * 是否显示提示框
	 */
	private boolean tooltip;
	
	private InitFunction initFunction;
	
	
	@XmlComponent(name = "initFunction", tag = "Init", type = InitFunction.class)
	public InitFunction getInitFunction() {
		return initFunction;
	}

	public void setInitFunction(InitFunction initFunction) {
		this.initFunction = initFunction;
	}

	/*
	 * 图表类别
	 */
	public BillUIChart() {
		super();
		// TODO Auto-generated constructor stub
	}

	@XmlAttribute(name="chartType",tag="ChartType",type=ChartType.class)
	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}


	@XmlAttribute(name="D3D",tag="D3D",type=boolean.class)
	public boolean getD3D() {
		return D3D;
	}

	public void setD3D(boolean d3d) {
		D3D = d3d;
	}

	@XmlAttribute(name="dataSourceType",tag="DataSourceType",type=DataSourceType.class)
	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	@XmlAttribute(name="dataTableKey",tag="DataTableKey",type=String.class)
	public String getDataTableKey() {
		return dataTableKey;
	}

	public void setDataTableKey(String dataTableKey) {
		this.dataTableKey = dataTableKey;
	}

	@XmlAttribute(name="bacgroundkColor",tag="BacgroundkColor",type=Color.class)
	public Color getBacgroundkColor() {
		return bacgroundkColor;
	}

	public void setBacgroundkColor(Color bacgroundkColor) {
		this.bacgroundkColor = bacgroundkColor;
	}

	@XmlAttribute(name="plotColor",tag="PlotColor",type=Color.class)
	public Color getPlotColor() {
		return plotColor;
	}

	public void setPlotColor(Color plotColor) {
		this.plotColor = plotColor;
	}

	@XmlAttribute(name="title",tag="Title",type=String.class)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlAttribute(name="xAxisLabel",tag="XAxisLabel",type=String.class)
	public String getxAxisLabel() {
		return xAxisLabel;
	}

	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	@XmlAttribute(name="yAxisLabel",tag="YAxisLabel",type=String.class)
	public String getyAxisLabel() {
		return yAxisLabel;
	}

	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	@XmlAttribute(name="dataSetType",tag="DataSetType",type=DataSetType.class)
	public DataSetType getDataSetType() {
		return dataSetType;
	}

	public void setDataSetType(DataSetType dataSetType) {
		this.dataSetType = dataSetType;
	}

	@XmlAttribute(name="floatType",tag="Float",type=Float.class)
	public Float getFloatType() {
		return floatType;
	}

	public void setFloatType(Float floatType) {
		this.floatType = floatType;
	}
	@XmlAttribute(name="tooltip",tag="Tooltip",type=boolean.class)
	public boolean getTooltip() {
		return tooltip;
	}

	public void setTooltip(boolean tooltip) {
		this.tooltip = tooltip;
	}

	@XmlAttribute(name="toolbox",tag="Toolbox",type=String.class)
	public String getToolbox() {
		return toolbox;
	}

	public void setToolbox(String toolbox) {
		this.toolbox = toolbox;
	}

	@XmlAttribute(name="legend",tag="Legend",type=boolean.class)
	public boolean getLegend() {
		return legend;
	}

	public void setLegend(boolean legend) {
		this.legend = legend;
	}

	@XmlList(name="categories",subTag="Category",type=Category.class)
	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	@XmlAttribute(name="dataSetkey",tag="DataSetkey",type=String.class)
	public String getDataSetkey() {
		return dataSetkey;
	}

	public void setDataSetkey(String dataSetkey) {
		this.dataSetkey = dataSetkey;
	}

	@XmlComponent(tag="Category",type=Category.class)
	public static class Category {
		private String key;
		private String caption;
		private String v;

		private String axis;
		private GroupType groupType;
		private String groudBy;
		private List<Series> series = new ArrayList<>();

		public Category() {
			super();
		}

		@XmlAttribute(name="key",tag="Key",type=String.class)
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		@XmlAttribute(name="caption",tag="Caption",type=String.class)
		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		@XmlAttribute(name="v",tag="V",type=String.class)
		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}
 
		@XmlList(name="series",subTag="Series",type=Series.class)
		public List<Series> getSeries() {
			return series;
		}

		public void setSeries(List<Series> series) {
			this.series = series;
		}
		
		@XmlAttribute(name="axis",tag="Axis",type=String.class)
		public String getAxis() {
			return axis;
		}

		public void setAxis(String axis) {
			this.axis = axis;
		}

		@XmlAttribute(name="groupType",tag="GroupType",type=GroupType.class)
		public GroupType getGroupType() {
			return groupType;
		}

		public void setGroupType(GroupType groupType) {
			this.groupType = groupType;
		}

		@XmlAttribute(name="groudBy",tag="GroudBy",type=String.class)
		public String getGroudBy() {
			return groudBy;
		}

		public void setGroudBy(String groudBy) {
			this.groudBy = groudBy;
		}

		public static enum GroupType{
			field,expression
		}

	}

	@XmlComponent(tag="Series",type=Series.class)
	public static class Series {
		private String key;
		private String caption;
		private String v1;
		private String v2;
		private String v3;
		private String v4;
		private String v5;
		private GatherType gatherType;
		
		private GroupRule groupRule;
		private String group;
		private String gatherFieldKey;
		private String chart;
		private String xgatherFieldKey;
		private String ygatherFieldKey;
		private String zgatherFieldKey;
		private GatherType xgatherType;
		private GatherType ygatherType;
		private GatherType zgatherType;
		public Series() {
			super();
			// TODO Auto-generated constructor stub
		}

		@XmlAttribute(name="key",tag="Key",type=String.class)
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		@XmlAttribute(name="caption",tag="Caption",type=String.class)
		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		@XmlAttribute(name="v1",tag="V1",type=String.class)
		public String getV1() {
			return v1;
		}

		public void setV1(String v1) {
			this.v1 = v1;
		}

		@XmlAttribute(name="v2",tag="V2",type=String.class)
		public String getV2() {
			return v2;
		}

		public void setV2(String v2) {
			this.v2 = v2;
		}

		@XmlAttribute(name="v3",tag="V3",type=String.class)
		public String getV3() {
			return v3;
		}

		public void setV3(String v3) {
			this.v3 = v3;
		}

		@XmlAttribute(name="v4",tag="V4",type=String.class)
		public String getV4() {
			return v4;
		}

		public void setV4(String v4) {
			this.v4 = v4;
		}

		@XmlAttribute(name="v5",tag="V5",type=String.class)
		public String getV5() {
			return v5;
		}

		public void setV5(String v5) {
			this.v5 = v5;
		}

		@XmlAttribute(name="gatherType",tag="GatherType",type=GatherType.class)
		public GatherType getGatherType() {
			return gatherType;
		}

		public void setGatherType(GatherType gatherType) {
			this.gatherType = gatherType;
		}

		@XmlAttribute(name="groupRule",tag="GroupRule",type=GroupRule.class)
		public GroupRule getGroupRule() {
			return groupRule;
		}

		public void setGroupRule(GroupRule groupRule) {
			this.groupRule = groupRule;
		}

		@XmlAttribute(name="group",tag="Group",type=String.class)
		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		@XmlAttribute(name="gatherFieldKey",tag="GatherFieldKey",type=String.class)
		public String getGatherFieldKey() {
			return gatherFieldKey;
		}

		public void setGatherFieldKey(String gatherFieldKey) {
			this.gatherFieldKey = gatherFieldKey;
		}

		@XmlAttribute(name="chart",tag="Chart",type=String.class)
		public String getChart() {
			return chart;
		}

		public void setChart(String chart) {
			this.chart = chart;
		}

		@XmlAttribute(name="xgatherFieldKey",tag="XgatherFieldKey",type=String.class)
		public String getXgatherFieldKey() {
			return xgatherFieldKey;
		}

		public void setXgatherFieldKey(String xgatherFieldKey) {
			this.xgatherFieldKey = xgatherFieldKey;
		}

		@XmlAttribute(name="ygatherFieldKey",tag="YgatherFieldKey",type=String.class)
		public String getYgatherFieldKey() {
			return ygatherFieldKey;
		}

		public void setYgatherFieldKey(String ygatherFieldKey) {
			this.ygatherFieldKey = ygatherFieldKey;
		}

		@XmlAttribute(name="zgatherFieldKey",tag="ZgatherFieldKey",type=String.class)
		public String getZgatherFieldKey() {
			return zgatherFieldKey;
		}

		public void setZgatherFieldKey(String zgatherFieldKey) {
			this.zgatherFieldKey = zgatherFieldKey;
		}

		@XmlAttribute(name="xgatherType",tag="XgatherType",type=GatherType.class)
		public GatherType getXgatherType() {
			return xgatherType;
		}

		public void setXgatherType(GatherType xgatherType) {
			this.xgatherType = xgatherType;
		}

		@XmlAttribute(name="ygatherType",tag="YgatherType",type=GatherType.class)
		public GatherType getYgatherType() {
			return ygatherType;
		}

		public void setYgatherType(GatherType ygatherType) {
			this.ygatherType = ygatherType;
		}

		@XmlAttribute(name="zgatherType",tag="ZgatherType",type=GatherType.class)
		public GatherType getZgatherType() {
			return zgatherType;
		}

		public void setZgatherType(GatherType zgatherType) {
			this.zgatherType = zgatherType;
		}

	}

	public static enum GatherType {
		no, sum, avg, max, min, count
	}

	public static enum ChartType {
		GenerialChart,ScatterChart,ScatterPlotChart,
		barChart, lineChart, waterfallChart, areaChart, stackedAreaChart, stackedBarChart, pieChart, ringChart, xyLineChart, xyAreaChart, xyStepAreaChart, scatterPlot, bubbleChart
	}

	public static enum DataSourceType {
		cellset, datatable
	}

	public static enum DataSetType{
		datasoruce,model
	}
	
	public static enum Float{
		suspension,wipe
	}
	
	public static enum GroupRule{
		group,field
	}
}
