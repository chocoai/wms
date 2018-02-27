package com.xyy.bill.excelTools;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class SQLDateProcessor  implements JsonValueProcessor {
	/**
	 * 字母 日期或时间元素 表示 示例 <br>
	 * G Era 标志符 Text AD <br>
	 * y 年 Year 1996; 96 <br>
	 * M 年中的月份 Month July; Jul; 07 <br>
	 * w 年中的周数 Number 27 <br>
	 * W 月份中的周数 Number 2 <br>
	 * D 年中的天数 Number 189 <br>
	 * d 月份中的天数 Number 10 <br>
	 * F 月份中的星期 Number 2 <br>
	 * E 星期中的天数 Text Tuesday; Tue<br>
	 * a Am/pm 标记 Text PM <br>
	 * H 一天中的小时数（0-23） Number 0 <br>
	 * k 一天中的小时数（1-24） Number 24<br>
	 * K am/pm 中的小时数（0-11） Number 0 <br>
	 * h am/pm 中的小时数（1-12） Number 12 <br>
	 * m 小时中的分钟数 Number 30 <br>
	 * s 分钟中的秒数 Number 55 <br>
	 * S 毫秒数 Number 978 <br>
	 * z 时区 General time zone Pacific Standard Time; PST; GMT-08:00 <br>
	 * Z 时区 RFC 822 time zone -0800 <br>
	 */
	private String format = "yyyy-MM-dd";// 自定义时间格式化的样式

	public SQLDateProcessor () {
		super();
		// TODO Auto-generated constructor stub
	}

	public SQLDateProcessor (String format) {
		this.format = format;
	}

	public Object processArrayValue(Object arg0, JsonConfig arg1) {
		// TODO Auto-generated method stub
		return arg0;
	}

	/**
	 * 处理对象的值 str 这个参数是当前需要处理的属性名
	 */
	public Object processObjectValue(String str, Object obj, JsonConfig arg2) {
		// TODO Auto-generated method stub
		String ret = "";
		if (obj instanceof java.sql.Date) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			ret = sdf.format(new Date(((java.sql.Date) obj).getTime()));
		}
		return ret;
	}
}
