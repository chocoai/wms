package com.xyy.edge.instance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.xyy.bill.meta.DataTableMeta.FieldType;
import com.xyy.util.DateTimeUtil;
import com.xyy.util.StringUtil;

public class BillEdgeUtil {
	/**
	 * 类型转化
	 * 
	 * @param object
	 * @param fieldType
	 * @return
	 */
	public static Object convertValueForEdge(String value, FieldType fieldType) {
		switch (fieldType) {
		case Int:
			if (!StringUtil.isEmpty(value)) {
				try {
					return Integer.parseInt(value.toString());
				} catch (Exception e) {
					return 0;
				}
				
			}
			return 0;
		case Long:
			if (!StringUtil.isEmpty(value)) {
				try {
					return Long.parseLong(value.toString());
				} catch (Exception e) {
					return 0l;
				}
				
			}
			return 0l;
		case Decimal:
			if (!StringUtil.isEmpty(value)) {
				try {
					return new BigDecimal(value.toString());
				} catch (Exception e) {
					return  BigDecimal.ZERO;
				}
				
			}
			return  BigDecimal.ZERO;
		case Date:
			if (!StringUtil.isEmpty(value)) {
				Date date = DateTimeUtil.getDate(value.toString());
				if (date == null)
					date = new Date();
				return date;
			}
			return new Date();// 当前日期
		case TimeStamp:
			if (!StringUtil.isEmpty(value)) {
				Date date=DateTimeUtil.getDateTime(value.toString());
			    if(date!=null){
			    	return new java.sql.Timestamp(date.getTime());
			    }
			}
			return new Timestamp(System.currentTimeMillis());// 当前日期
		case BillID:
		case BillDtlID:
		case ItemID:
		case Varchar:
		case Text:
		default:
			String val=null;
			if (value != null && value.getClass() != String.class) {
				val= value.toString();
			}
			if(!StringUtil.isEmpty(val))
				return value;
			return null;
		}
	}
}
