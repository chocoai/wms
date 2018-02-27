package com.xyy.bill.services.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.ChartDataSetDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.def.MultiBillDef;
import com.xyy.bill.def.ViewportDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.meta.BillUIChart;
import com.xyy.bill.meta.BillUIChart.Series;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.util.DataUtil;
import com.xyy.erp.platform.common.tools.NumberUtil;
import com.xyy.expression.Context;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;
import com.xyy.util.Base64;
import com.xyy.util.StringUtil;


public class ChartService {

	/**
	 * 计算图表--基于图表数据集的定义
	 * 
	 * @param context，图表计算上下文
	 * 
	 * @param chartDataSetDef,图表数据集的定义文件
	 * 
	 * @param dataTableMeta，图表的数据表
	 * 
	 * @return
	 */
	public JSONObject calc(BillContext context, ChartDataSetDef chartDataSetDef, DataTableMeta dataTableMeta)
			throws CharException {
		JSONObject json = new JSONObject();
		/**
		 * DataTableInstance是一个单向智能对象
		 */
		DataTableInstance dataTableInstance = new DataTableInstance(context, dataTableMeta);
		// 加载图表数据
		try {
			dataTableInstance.loadCharData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * 获取类别--当前图表仅支持单类别
    	 */
		String groupType = context.getString("groupType");
		Map<String, List<Record>> groupResult = null;
		if ("expression".equals(groupType)) {// 按表达式分组
			String groupBy = context.getString("groudBy");
			if (StringUtil.isEmpty(groupBy)) {
				throw new CharException("groupby is null.");
			}
			groupResult = this.groupByExpression(dataTableInstance, groupBy);

		} else if ("field".equals(groupType)) {// 按字段分组
			String groupBy = context.getString("groudBy");
			if (StringUtil.isEmpty(groupBy)) {
				throw new CharException("groupby is null.");
			}
			groupResult = this.groupByField(dataTableInstance, groupBy);

		} else {// 不做分组
			groupResult = new HashMap<>();
			groupResult.put(GenerialChartBean.DEFAULT_CATEGORY, dataTableInstance.getRecords());
		}
		// 开始计算序列
		List<GenerialChartBean> generialChartBeans = new ArrayList<>();
		Map<String, List<GenerialChartBean>> gcbMap = new HashMap<>();
		List<BillUIChart.Series> series = this.getSeries(context);
		for (String key : groupResult.keySet()) {// 计算每一个类别下面的系列和系列值
			for (BillUIChart.Series cs : series) {// 可能输出是一个bean,beanlist
				generialChartBeans.addAll(this.calSeiral(key, cs, groupResult.get(key)));
				if (!gcbMap.containsKey(cs.getChart())) {
					gcbMap.put(cs.getChart(), new ArrayList<>());
				}
				gcbMap.get(cs.getChart()).addAll(generialChartBeans);
			}
		}
		//组装数据
		json = this.getChartDataJson(gcbMap.keySet(),context,generialChartBeans);
		return json;
	}

	/**
	 * 组装图表数据
	 * @param set 
	 * @param context
	 * @param generialChartBeans
	 * @return
	 */
	private JSONObject getChartDataJson(Set<String> set, BillContext context, List<GenerialChartBean> generialChartBeans) {
		JSONObject json = new JSONObject();
		//generialChartBeans
		//获取图表绘制类型
		for (String chart : set) {
			boolean islegend = context.getBoolean("legend");
			switch (chart) {
			case "line"://折线图
				json = this.getLineData(chart, islegend, generialChartBeans);
				break;
			case "bar"://柱状图
				json = this.getLineOrBarData(chart,islegend,generialChartBeans);
				break;
			case "pie"://饼状图
				json = this.getPieData(chart,islegend,generialChartBeans);
				break;
			case "radar"://雷达图
				json = this.getRadarData(chart,islegend,generialChartBeans);
				break;
			case "scatterPlot"://气泡图
				json = this.getScatterPloatData(chart,islegend,generialChartBeans);
				break;
			case "scatter"://散点图
				json = this.getScatterData(chart,islegend,generialChartBeans);
				break;
			default:
				break;
			}
		}
		return json;
	}


	private List<GenerialChartBean> calSeiral(String category, Series series, List<Record> records) {
		//
		List<GenerialChartBean> result = new ArrayList<>();
		if (category.equals(GenerialChartBean.DEFAULT_CATEGORY)) {
			switch (series.getGroupRule()) {
			case group:
				Map<String,List<Record>> serialGroup=this.serialGroup(series, records);
				for(String s:serialGroup.keySet()){
					GenerialChartBean gcb = new GenerialChartBean();
					gcb.setCategory(category);
					gcb.setSerial(s);
					String groupField = series.getXgatherFieldKey();
					Number number = 0;
					if(!StringUtil.isEmpty(groupField)){
						gcb.setGatherType(series.getXgatherType());
						number = this.gatherFieldValue(groupField, gcb, serialGroup.get(s));
						gcb.setxSerialValue(number);
					}
					groupField = series.getYgatherFieldKey();
					if(!StringUtil.isEmpty(groupField)){
						gcb.setGatherType(series.getYgatherType());
						number = this.gatherFieldValue(groupField, gcb, serialGroup.get(s));
						gcb.setySerialValue(number);
					}
					groupField = series.getGatherFieldKey();
					if(!StringUtil.isEmpty(groupField)){
						gcb.setGatherType(series.getGatherType());
						number = this.gatherFieldValue(groupField, gcb, serialGroup.get(s));
						gcb.setSerialValue(number);
					}
					if (!StringUtil.isEmpty(series.getZgatherFieldKey())) {
						gcb.setGatherType(series.getZgatherType());
						number = this.gatherFieldValue(groupField, gcb, serialGroup.get(s));
						gcb.setzSerialValue(number);
					}
					result.add(gcb);				
				}
				break;
			case field:
			default:
				GenerialChartBean gcb = new GenerialChartBean();
				gcb.setCategory(category);
				gcb.setSerial(GenerialChartBean.DEFAULT_SERIAL);
				String groupField = series.getXgatherFieldKey();
				Number number = 0;
				if(!StringUtil.isEmpty(groupField)){
					gcb.setGatherType(series.getXgatherType());
					number = this.gatherFieldValue(groupField, gcb, records);
					gcb.setxSerialValue(number);
				}
				groupField = series.getYgatherFieldKey();
				if(!StringUtil.isEmpty(groupField)){
					gcb.setGatherType(series.getYgatherType());
					number = this.gatherFieldValue(groupField, gcb, records);
					gcb.setySerialValue(number);
				}
				groupField = series.getGatherFieldKey();
				if(!StringUtil.isEmpty(groupField)){
					gcb.setGatherType(series.getGatherType());
					number = this.gatherFieldValue(groupField, gcb, records);
					gcb.setSerialValue(number);
				}
				if (!StringUtil.isEmpty(series.getZgatherFieldKey())) {
					gcb.setGatherType(series.getZgatherType());
					number = this.gatherFieldValue(groupField, gcb, records);
					gcb.setzSerialValue(number);
				}
				result.add(gcb);
				break;
			}
		}else{
			switch (series.getGroupRule()) {
			case group:
				Map<String,List<Record>> serialGroup=this.serialGroup(series, records);
				for(String s:serialGroup.keySet()){
					GenerialChartBean gcb = new GenerialChartBean();
					gcb.setCategory(category);
					gcb.setSerial(s);
					String groupField = series.getGatherFieldKey();
					gcb.setGatherType(series.getGatherType());
					Number number = this.gatherFieldValue(groupField, gcb, serialGroup.get(s));
					gcb.setSerialValue(number);
					result.add(gcb);				
				}
				break;
			case field:
			default:
				GenerialChartBean gcb = new GenerialChartBean();
				gcb.setCategory(category);
				gcb.setSerial(GenerialChartBean.DEFAULT_SERIAL);
				String groupField = series.getGatherFieldKey();
				gcb.setGatherType(series.getGatherType());
				Number number = this.gatherFieldValue(groupField, gcb, records);
				gcb.setSerialValue(number);
				result.add(gcb);
				break;
			}
		}
		return result;
	}


	/**
	 * @param series
	 * @param records
	 * @return
	 */
	private Map<String, List<Record>> serialGroup(Series series, List<Record> records) {
		Map<String, List<Record>> result=new LinkedHashMap<>();
		String field=series.getGroup();
		for(Record r:records){
			Object object = r.get(field);
			String gn = String.valueOf(object);
			if(!result.containsKey(gn)){
				result.put(gn, new ArrayList<>());
			}
			result.get(gn).add(r);
		}
		return result;
	}

	/**
	 * 计算汇总值
	 * 
	 * @param groupField
	 * @param records
	 * @return
	 */
	private Number gatherFieldValue(String groupField, GenerialChartBean gcb, List<Record> records) {
		switch (gcb.getGatherType()) {
		case sum:
			return this.sumByField(groupField, records);
		case avg:
			return this.avgByField(groupField, records);
		case max:
			return this.maxByField(groupField, records);
		case min:
			return this.minByField(groupField, records);
		case count:
			return records.size();
		default:
			return 0;
		}
	}

	private Number minByField(String field, List<Record> records) {
		if (records.size() <= 0 || !DataUtil.isNumber(records.get(0).get(field))) {
			return 0;
		}
		Number result=records.get(0).get(field);
		for(int i=1;i<records.size();i++){
			if(!DataUtil.lessThan(result,records.get(i).get(field))){
				result=records.get(i).get(field);
			}
		}
		return result;
	}


	private Number maxByField(String field, List<Record> records) {
		if (records.size() <= 0 || !DataUtil.isNumber(records.get(0).get(field))) {
			return 0;
		}
		Number result=records.get(0).get(field);
		for(int i=1;i<records.size();i++){
			if(DataUtil.lessThan(result,records.get(i).get(field))){
				result=records.get(i).get(field);
			}
		}
		return result;
	}

	private Number avgByField(String field, List<Record> records) {
		if (records.size() <= 0 || !DataUtil.isNumber(records.get(0).get(field))) {
			return 0;
		}
		Double sum=NumberUtil.getDoubleValue(records.get(0).get(field));
		for(int i=1;i<records.size();i++){
			Number cur=records.get(i).get(field);				
			sum+=NumberUtil.getDoubleValue(cur.toString());
		}
		return sum/records.size();
	}

	private Number sumByField(String field, List<Record> records) {
		if (records.size() <= 0 || !DataUtil.isNumber(records.get(0).get(field))) {
			return 0;
		}
		Double sum=NumberUtil.getDoubleValue(records.get(0).get(field));
		for(int i=1;i<records.size();i++){
			Number cur=records.get(i).get(field);				
			sum+=NumberUtil.getDoubleValue(cur.toString());
		}
		if(DataUtil.isInteger(sum)){
			return sum.intValue();
		}else{
			return sum;
		}
	}

	/**
	 * 按字段分组计算 ==》一级分组
	 * @param dataTableInstance
	 * @param groupBy
	 * @return
	 */
	private Map<String, List<Record>> groupByField(DataTableInstance dataTableInstance, String groupBy) {
		Map<String, List<Record>> result=new LinkedHashMap<>();
		for(Record record : dataTableInstance.getRecords()){
			Object object = record.get(groupBy);
			String gn = String.valueOf(object);
//			String gn = record.getStr(groupBy);
			if(!result.containsKey(gn)){
				result.put(gn, new ArrayList<>());
			}
			result.get(gn).add(record);
		}
		return result;
	}

	/**
	 * 获取系列分类值属性
	 * @param context
	 * @return
	 */
	private List<Series> getSeries(BillContext context) {
		List<Series> result = new ArrayList<>();
		String serieses = context.getString("series");
		JSONArray jsonArray = JSONArray.parseArray(Base64.decoder(serieses));
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			result.add((Series) JSONObject.parseObject(json.toJSONString(), Series.class));
		}
		return result;
	}

	/**
	 * 分组计算---表达式
	 * 	   groupName,expression | groupName,expression
	 * 
	 * @param dataTableInstance
	 * @param groupBy
	 * @return
	 */
	private Map<String, List<Record>> groupByExpression(DataTableInstance dataTableInstance, String groupBy) {
		Map<String, List<Record>> result=new LinkedHashMap<>();
		//处理表达式
		String[] exprs=groupBy.split("\\|");
		Map<String, String> condtionGroupMap=new HashMap<>();
		for(String expr:exprs){
			String[] exp=expr.split("\\,");
			if(exp.length!=2){
				continue;
			}
			condtionGroupMap.put(exp[0], exp[1]);
		}
		//..
		for(Record record:dataTableInstance.getRecords()){
			//..other
			String gn=this.calCurGroup(record,condtionGroupMap);
			if(!result.containsKey(gn)){
				result.put(gn, new ArrayList<>());
			}
			result.get(gn).add(record);
		}
		
		
		return result;
	}

	/**
	 * 返回当前分组
	 * @param record
	 * @param condtionGroupMap
	 * @return
	 */
	private String calCurGroup(Record record, Map<String, String> condtionGroupMap) {
		for(String group:condtionGroupMap.keySet()){
			String expr=condtionGroupMap.get(group);
			try {
				OperatorData od=ExpService.instance().calc(expr, new Context() {
					@Override
					public void setVariant(String name, Object value) {
						
					}
					@Override
					public void removeVaraint(String name) {
						
					}
					@Override
					public Object getValue(String name) {
						return record.get(name);
					}
				});
				if(od==null || od.clazz==NullRefObject.class || !DataUtil.isBoolean(od.value)){
					return "other";
				}
				if((Boolean)od.value){
					return group;
				}
			} catch (ExpressionCalException e) {
				e.printStackTrace();
			}
		}
		return "other";
	}
	
	private JSONObject getScatterPloatData(String chart, boolean islegend, List<GenerialChartBean> generialChartBeans) {
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		Map<String, List<Number>> serieMap = new LinkedHashMap<>();
		for(GenerialChartBean chartBean : generialChartBeans){
			if (!serieMap.containsKey(chartBean.getSerial())) {
				serieMap.put(chartBean.getSerial(), new ArrayList<>());
			}
			serieMap.get(chartBean.getSerial()).add(chartBean.getxSerialValue());
			serieMap.get(chartBean.getSerial()).add(chartBean.getySerialValue());
			serieMap.get(chartBean.getSerial()).add(chartBean.getzSerialValue());
		}
		
		json.put("legend", serieMap.keySet());
		for(String key : serieMap.keySet()){
			List<Object> list = new ArrayList<>();
			JSONObject jo = new JSONObject();
			jo.put("name", key);
			jo.put("type", "scatter");
			list.add(serieMap.get(key));
			jo.put("symbolSize", serieMap.get(key).get(2));
			jo.put("data", list);
			ja.add(jo);
		}
		json.put("series", ja);
		return json;
	}
	/*
	 * 散点图，无一级分组
	 */
	private JSONObject getScatterData(String chart, boolean islegend, List<GenerialChartBean> generialChartBeans) {
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		Map<String, List<Number>> serieMap = new LinkedHashMap<>();
		for(GenerialChartBean chartBean : generialChartBeans){
			if (!serieMap.containsKey(chartBean.getSerial())) {
				serieMap.put(chartBean.getSerial(), new ArrayList<>());
			}
			serieMap.get(chartBean.getSerial()).add(chartBean.getxSerialValue());
			serieMap.get(chartBean.getSerial()).add(chartBean.getySerialValue());
		}
		json.put("legend", serieMap.keySet());
		for(String key : serieMap.keySet()){
			List<Object> list = new ArrayList<>();
			JSONObject jo = new JSONObject();
			jo.put("name", key);
			jo.put("type", chart);
			list.add(serieMap.get(key));
			jo.put("data", list);
			ja.add(jo);
		}
		json.put("series", ja);
		return json;
	}

	private JSONObject getRadarData(String chart, boolean islegend, List<GenerialChartBean> generialChartBeans) {
		JSONObject json = new JSONObject();
		List<String> legend = new ArrayList<>();//收集分类名
		Set<String> legendSet = new HashSet<>();//分类值去重
		List<String> series = new ArrayList<>();//收集分类名
		Set<String> seriesSet = new HashSet<>();//分类值去重
		Map<String, List<Number>> serieMap = new HashMap<>();
		JSONArray ja = new JSONArray();
		for (GenerialChartBean chartBean : generialChartBeans) {
			legend.add(chartBean.getCategory());
			if (!serieMap.containsKey(chartBean.getCategory())) {
				serieMap.put(chartBean.getCategory(), new ArrayList<>());
			}
			series.add(chartBean.getSerial());
			serieMap.get(chartBean.getCategory()).add(chartBean.getSerialValue());
		}
		legendSet.addAll(legend);
		seriesSet.addAll(series);
		if (islegend) {
			json.put("legend", legendSet);
		}
		for(String key : seriesSet){
			JSONObject jo = new JSONObject();
			jo.put("name", key);
			ja.add(jo);
		}
		json.put("radar", ja);
		ja.clear();
		JSONObject seriJson = new JSONObject();
		for (String key : serieMap.keySet()) {
			JSONObject jo = new JSONObject();
			jo.put("name", key);
			jo.put("value", serieMap.get(key));
			ja.add(jo);
		}
		seriJson.put("type", chart);
		seriJson.put("data", ja);
		ja.clear();
		ja.add(seriJson);
		json.put("series", ja);
		return json;
	}

	private JSONObject getPieData(String chart, boolean islegend, List<GenerialChartBean> generialChartBeans) {
		JSONObject json = new JSONObject();
		List<String> legend = new ArrayList<>();//收集分类名
		Set<String> legendSet = new HashSet<>();//分类值去重
		Map<String, List<Number>> serieMap = new LinkedHashMap<>();
		JSONObject ob = new JSONObject();
		JSONArray array = new JSONArray();
		for (GenerialChartBean chartBean : generialChartBeans) {
			JSONObject ob1 = new JSONObject();
			legend.add(chartBean.getCategory());
			serieMap.put(chartBean.getSerial(), null);
			ob1.put("name", chartBean.getSerial());
			ob1.put("value",chartBean.getSerialValue());
			array.add(ob1);
		}
		legendSet.addAll(legend);
		ob.clear();
		ob.put("name", legendSet);
		ob.put("data", array);
		ob.put("type", chart);
		json.put("series", ob);
		if (islegend) {//收集分类
			json.put("legend", serieMap.keySet());
		}
		return json;
	}
	
	private JSONObject getLineData(String chart, boolean islegend, List<GenerialChartBean> generialChartBeans) {
		JSONObject json = new JSONObject();
		List<String> legend = new ArrayList<>();//收集分类名
		Set<String> legendSet = new HashSet<>();//分类值去重
		Map<String, List<Number>> serieMap = new HashMap<>();
		JSONArray ja = new JSONArray();
		for (GenerialChartBean chartBean : generialChartBeans) {
			legend.add(chartBean.getCategory());
			//收集系列，系列值
			if (!serieMap.containsKey(chartBean.getSerial())) {
				serieMap.put(chartBean.getSerial(), new ArrayList<>());
			}
			serieMap.get(chartBean.getSerial()).add(chartBean.getSerialValue());
		}
		legendSet.addAll(legend);
		if (islegend) {//收集分类
			json.put("legend", serieMap.keySet());
		}
		json.put("axis", legendSet);
		for (String key : serieMap.keySet()) {
			JSONObject jo = new JSONObject();
			jo.put("name", key);
			jo.put("data", serieMap.get(key));
			jo.put("type", chart);
			ja.add(jo);
		}
		json.put("series", ja);
		return json;
	}
	
	private JSONObject getLineOrBarData(String chart,boolean islegend,List<GenerialChartBean> generialChartBeans) {
		JSONObject json = new JSONObject();
		Map<String, GenerialChartBean> categoryMap = new LinkedHashMap<>();
		JSONArray ja = new JSONArray();
		Map<String,GenerialChartBean> serialSet =new LinkedHashMap<>();
		for(GenerialChartBean chartBean : generialChartBeans){
			if(!serialSet.keySet().contains(chartBean.getSerial()))
				serialSet.put(chartBean.getSerial(), chartBean);
		}
		for (GenerialChartBean chartBean : generialChartBeans) {
			categoryMap.put(chartBean.getCategory(), chartBean);
		}
		for (GenerialChartBean serial : generialChartBeans) {//遍历系列
			JSONObject jo = new JSONObject();
			JSONArray cja = new JSONArray();
			jo.put("type", chart);
			for (String category : categoryMap.keySet()) {//遍历类别
				jo.put("name", serial.getSerial());
				if (category.equals(serial.getCategory())) {
					cja.add(serial.getSerialValue());
				}else{
					cja.add(0.0);
				}
			}
			jo.put("data", cja);
			ja.add(jo);
		}
		if (islegend) {//收集分类
			json.put("legend", serialSet.keySet());
		}
		json.put("axis", categoryMap.keySet());
		json.put("series", ja);
		return json;
	}
	/**
	 * 计算图表--基于bill context
	 * 
	 * @param context，图表计算上下文
	 * @return
	 * @throws CharException 
	 */
	public JSONObject calc(BillContext context) throws CharException {
		JSONObject json = new JSONObject();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DataTableMeta dataTableMeta = new DataTableMeta();
		String billType = context.getString("billtype");
		String  datatablekey = context.getString("datatablekey");
		switch (billType) {
		case BILLConstant.DATA_CODE_BILL:
			BillDef billDef = engine.getBillDef(context.getString("billKey"));
			dataTableMeta = billDef.getDataSet().getTable(datatablekey);
			break;
		case BILLConstant.DATA_CODE_BILLS:
			BillDef billDefs = engine.getBillDef(context.getString("billKey"));
			dataTableMeta = billDefs.getBillListDataSet().getTable(datatablekey);
			break;
		case BILLConstant.DATA_CODE_DIC:
			DicDef dicDef = engine.getDicDef(context.getString("billKey"));
			dataTableMeta = dicDef.getDataSet().getTable(datatablekey);
			break;
		case BILLConstant.DATA_CODE_MULTIBILL:
			MultiBillDef multiBillDef = engine.getMultiBillDef(context.getString("billKey"));
			dataTableMeta = multiBillDef.getDataSet().getTable(datatablekey);
			break;
		case BILLConstant.DATA_CODE_VIEW:
			ViewportDef viewportDef = engine.getViewportDef(context.getString("billKey"));
			dataTableMeta = viewportDef.getDataSet().getTable(datatablekey);
			break;
		case BILLConstant.DATA_CODE_REPORT:	
		default:
			break;
		}
		DataTableInstance dataTableInstance = new DataTableInstance(context, dataTableMeta);
		/*
		 * 获取类别--当前图表仅支持单类别
    	 */
		String groupType = context.getString("groupType");
		Map<String, List<Record>> groupResult = null;
		if ("expression".equals(groupType)) {// 按表达式分组
			String groupBy = context.getString("groudBy");
			if (StringUtil.isEmpty(groupBy)) {
				throw new CharException("groupby is null.");
			}
			groupResult = this.groupByExpression(dataTableInstance, groupBy);

		} else if ("field".equals(groupType)) {// 按字段分组
			String groupBy = context.getString("groudBy");
			if (StringUtil.isEmpty(groupBy)) {
				throw new CharException("groupby is null.");
			}
			groupResult = this.groupByField(dataTableInstance, groupBy);

		} else {// 不做分组
			groupResult = new HashMap<>();
			groupResult.put(GenerialChartBean.DEFAULT_CATEGORY, dataTableInstance.getRecords());
		}
		// 开始计算序列
		List<GenerialChartBean> generialChartBeans = new ArrayList<>();
		Map<String, List<GenerialChartBean>> gcbMap = new HashMap<>();
		List<BillUIChart.Series> series = this.getSeries(context);
		for (String key : groupResult.keySet()) {// 计算每一个类别下面的系列和系列值
			for (BillUIChart.Series cs : series) {// 可能输出是一个bean,beanlist
				generialChartBeans.addAll(this.calSeiral(key, cs, groupResult.get(key)));
				if (!gcbMap.containsKey(cs.getChart())) {
					gcbMap.put(cs.getChart(), new ArrayList<>());
				}
				gcbMap.get(cs.getChart()).addAll(generialChartBeans);
			}
		}
		//组装数据
		json = this.getChartDataJson(gcbMap.keySet(),context,generialChartBeans);
		return json;
	}

}
