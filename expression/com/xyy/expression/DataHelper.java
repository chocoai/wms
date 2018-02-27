package com.xyy.expression;

import java.sql.Time;
import java.util.Date;

public class DataHelper {
	
	public static boolean isNumber(OperatorData od) {
		if (od.clazz == Integer.class || od.clazz == int.class
				|| od.clazz == long.class || od.clazz == Long.class
				|| od.clazz == float.class || od.clazz == Float.class
				|| od.clazz == double.class || od.clazz == Double.class) {
			return true;
		} else {
			try {
				Double.parseDouble(od.value.toString());
				return true;
			} catch (Exception ex) {
				return false;
			}
		}
	}

	public static boolean isInt(OperatorData od) {
		if (od.clazz == Integer.class || od.clazz == int.class
				|| od.clazz == long.class || od.clazz == Long.class) {
			return true;
		} else {
			try {
				Long.parseLong(od.value.toString());
				return true;
			} catch (Exception ex) {
				return false;
			}
		}
	}

	public static boolean isFloat(OperatorData od) {
		if (od.clazz == float.class || od.clazz == Float.class
				|| od.clazz == double.class || od.clazz == Double.class) {
			return true;
		} else {
			try {
				Double.parseDouble(od.value.toString());
				return true;
			} catch (Exception ex) {
				return false;
			}
		}
	}

	public static boolean isBoolean(OperatorData od) {
		if (od.clazz == boolean.class || od.clazz == Boolean.class) {
			return true;
		} else if (od.clazz == String.class) {
			if (od.value.toString().equals("true")) {
				return true;
			} else if (od.value.toString().equals("false")) {
				return false;
			}
		}
		return false;
	}

	public static boolean isDate(OperatorData od) {
		if (od.clazz == Date.class) {
			return true;
		}
		return false;
	}

	public static boolean isTime(OperatorData od) {
		if (od.clazz == Time.class) {
			return true;
		}
		return false;
	}

	public static boolean isString(OperatorData od) {
		if (od.clazz == String.class) {
			return true;
		}
		return false;
	}

	public static OperatorData createOperatorData(Object o) {
		if (o != null && o.getClass() == OperatorData.class) {
			return (OperatorData) o;
		}
		if (o == null) {
			return new OperatorData(NullRefObject.class, new NullRefObject());
		} else {
			return new OperatorData(o.getClass(), o);
		}
	}
}
