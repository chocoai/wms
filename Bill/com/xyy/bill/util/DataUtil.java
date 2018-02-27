package com.xyy.bill.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.xyy.bill.meta.DataTableMeta.FieldType;
import com.xyy.bill.meta.Parameter.ParameterType;
import com.xyy.erp.platform.common.tools.NumberUtil;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.DateTimeUtil;

public class DataUtil {

	/**
	 * 转换expr值为type String, Integer, Decimal, Datetime, Bool
	 * 
	 * @param expr
	 * @param type
	 * @return
	 */
	public static Object convert(String expr, ParameterType type) {
		switch (type) {
		case Integer:
			return getInteger(expr);
		case Decimal:
			return getDecimal(expr);
		case Datetime:
			return getDatetime(expr);
		case Bool:
			return getBoolean(expr);
		case String:
		default:
			return expr;
		}
	}

	public static Object convert(Object expr, ParameterType type) {
		switch (type) {
		case Integer:
			if (expr.getClass() == int.class || expr.getClass() == Integer.class) {
				return expr;
			} else {
				return getInteger(expr.toString());
			}
		case Decimal:
			if (!isNumber(expr)) {
				return new BigDecimal(expr.toString());
			} else {
				return getDecimal(expr.toString());
			}

		case Datetime:
			if (expr instanceof Date) {
				return expr;
			} else {
				return getDatetime(expr.toString());
			}
		case Bool:
			if (expr.getClass() == Boolean.class || expr.getClass() == boolean.class) {
				return expr;
			} else {
				return getBoolean(expr.toString());
			}

		case String:
		default:
			return expr;
		}
	}

	private static Object getBoolean(String expr) {
		if (expr.toLowerCase().equals("true")) {
			return true;
		} else if (expr.toLowerCase().equals("false")) {
			return false;
		} else {
			return false;
		}
	}

	private static Object getDatetime(String expr) {
		Date date = DateTimeUtil.getDateTime(expr);
		if (date == null) {
			date = new Date();
		}
		return new Timestamp(date.getTime());
	}

	private static Object getDecimal(String expr) {
		try {
			return new BigDecimal(expr);
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	private static Object getInteger(String expr) {
		try {
			return Integer.parseInt(expr);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 转换value的值为对应fieldType
	 * 
	 * @param value
	 * @param fieldType
	 * @return
	 */
	public static Object convert(Object value, FieldType fieldType) {
		switch (fieldType) {
		// BillID, BillDtlID, ItemID, Int, Long, Decimal, Varchar, Date,
		// TimeStamp, Text
		case Int:
			if (value == null) {
				return 0;
			} else if (value.getClass() == int.class || value.getClass() == Integer.class) {
				return value;
			} else if (value.getClass() == Long.class || value.getClass() == long.class) {
				long result = (long) value;
				if (result > Integer.MAX_VALUE) {
					return Integer.MAX_VALUE;
				} else {
					return (int) result;
				}
			} else {
				try {
					return Integer.parseInt(value.toString());
				} catch (Exception e) {
					return 0;
				}
			}
		case Long:
			if (value == null) {
				return 0l;
			} else if (value.getClass() == int.class || value.getClass() == Integer.class
					|| value.getClass() == Long.class || value.getClass() == long.class) {
				return (Long) value;
			} else {
				try {
					return Long.parseLong(value.toString());
				} catch (Exception e) {
					return 0l;
				}
			}
		case Decimal:
			if (value == null) {
				return BigDecimal.ZERO;
			} else {
				try {
					return new BigDecimal(value.toString());
				} catch (Exception e) {
					return BigDecimal.ZERO;
				}
			}
		case Date:
			if (value == null) {
				return new Date();
			} else if (value.getClass() == Date.class) {
				return value;
			} else {
				Date result = DateTimeUtil.getDate(value.toString());
				if (result == null) {
					result = new Date();
				}
				return result;
			}
		case TimeStamp:
			if (value == null||StringUtil.isEmpty(value.toString())) {
				return new Timestamp(System.currentTimeMillis());
			} else if (value.getClass() == Timestamp.class) {
				return value;
			} else {
				Date result = DateTimeUtil.getDateTime(value.toString());
				if (result == null) {
					return new Timestamp(System.currentTimeMillis());
				}
				return new Timestamp(result.getTime());
			}
		case BillID:
		case BillDtlID:
		case ItemID:
		case Varchar:
		case Text:
		default:
			if (value == null) {
				return "";
			} else {
				return value.toString();
			}
		}
	}

	/**
	 * 判断value是否是数字
	 * 
	 * @param value
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNumber(Object value) {
		if (value != null) {
			Class clazz = value.getClass();
			if (clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class
					|| clazz == float.class || clazz == Float.class || clazz == double.class || clazz == Double.class
					|| clazz == BigDecimal.class)
				return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isInteger(Object value) {
		if (value != null) {
			Class clazz = value.getClass();
			if (clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class)
				return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isFloat(Object value) {
		if (value != null) {
			Class clazz = value.getClass();
			if (clazz == float.class || clazz == Float.class || clazz == double.class || clazz == Double.class
					|| clazz == BigDecimal.class)
				return true;
		}
		return false;
	}

	/**
	 * 判断
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isDate(Object value) {
		if (value.getClass() == Date.class) {
			return true;
		} else {
			Date date = DateTimeUtil.getDate(value.toString());
			if (date == null) {
				return false;
			} else {
				return true;
			}

		}
	}

	public static boolean isTimeStamp(Object value) {
		if (value.getClass() == Timestamp.class) {
			return true;
		} else {
			Date date = DateTimeUtil.getDateTime(value.toString());
			if (date == null) {
				return false;
			} else {
				return true;
			}

		}

	}

	public static boolean lessThan(Number n1, Number n2) {
		return NumberUtil.getDoubleValue(n1.toString()) < NumberUtil.getDoubleValue(n2.toString());

	}

	public static boolean isBoolean(Object value) {
		return value.getClass() == boolean.class || value.getClass() == Boolean.class;

	}

}
