package com.xyy.erp.platform.common.func;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.jmx.snmp.Timestamp;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.bill.meta.DataTableMeta.FieldType;
import com.xyy.bill.util.DataUtil;

public class UpdateFunc implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject re = new JSONObject();
		JSONArray data = param.getJSONArray("data");
		String tableName = param.getString("tableName");
		String billKey = param.getString("billKey");
		String type = param.getString("type");
		String billType = param.getString("billType");
		int[] result = null;
		if (type.equals("save")) {
			result = Db.batchSave(tableName, this.getList(data,billKey,billType), 500);
		}else if(type.equals("update")){
			if (billType.equals("Bill")) {
				result = Db.batchUpdate(tableName,"BillID", this.getList(data,billKey,billType), 500);
			}
			if (billType.equals("Dic")) {
				result = Db.batchUpdate(tableName,"ID", this.getList(data,billKey,billType), 500);
			}
		}
		if (result!=null&&result.length>0) {
			re.put("flag", true);
		}else {
			re.put("flag", false);
		}
		
		return re;
	}

	private List<Record> getList(JSONArray data, String billKey, String billType) {
		List<Field> Fields = new ArrayList<>();
		if (billType.equals("Bill")) {
			BillEngine engine = BillPlugin.engine;// 获取引擎
			BillDef billDef = engine.getBillDef(billKey);
			Fields = billDef.getDataSet().getHeadDataTable().getFields();
		}
		if (billType.equals("Dic")) {
			BillEngine engine = BillPlugin.engine;// 获取引擎
			DicDef dicDef = engine.getDicDef(billKey);
			Fields = dicDef.getDataSet().getHeadDataTable().getFields();
		}
		
		
		List<Record> list = new ArrayList<>();
		for (Object object : data) {
			JSONObject jsonObject = (JSONObject) object;
			Record record = new Record();
			for (Field field : Fields) {
				String key = field.getFieldKey();
				record.set(key, this.convertValue(jsonObject.get(key), field.getFieldType()));
			}
			list.add(record);
		}
		
		return list;
	}

	private Object convertValue(Object value, FieldType fieldType) {
		switch (fieldType) {
		case Int:
			if (value != null && (value.getClass() != int.class || value.getClass() != Integer.class)) {
				return Integer.parseInt(value.toString());
			}
			return value;
		case Long:
			if (value != null && (value.getClass() != long.class || value.getClass() != Long.class)) {
				return Long.parseLong(value.toString());
			}
			return value;
		case Decimal:
			if (value != null && !DataUtil.isNumber(value)) {
				return new BigDecimal(value.toString());
			}
			return value;
		case Date:
			if (value != null && !DataUtil.isDate(value)) {
				return new Date();// 当前日期
			}
			return value;
		case TimeStamp:
			if (value != null && !DataUtil.isTimeStamp(value)) {
				return new Timestamp(System.currentTimeMillis());// 当前日期
			}
			return value;

		case BillID:
		case BillDtlID:
		case ItemID:
		case Varchar:
		case Text:
		default:
			if (value != null && value.getClass() != String.class) {
				return value.toString();
			}
			return value;

		}
	}

}
