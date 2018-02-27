package com.xyy.expression.lib;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.ILib;
import com.xyy.expression.OperatorData;
/**
 * 日期时间库
 * @author evan
 *
 */
public class DateTimeLib implements ILib {
	public DateTimeLib() {

	}

	private static Map<String, Integer> functionMaps = new HashMap<String, Integer>();
	static {
		functionMaps.put("age", 1);
		functionMaps.put("day", 2);
		functionMaps.put("dayName", 3);
		functionMaps.put("dayNo", 4);
		functionMaps.put("daysAfter", 5);
		functionMaps.put("daysInMonth", 6);
		functionMaps.put("daysInYear", 7);
		functionMaps.put("hour", 8);
		functionMaps.put("lastDay", 9);
		functionMaps.put("lastMonth", 10);
		functionMaps.put("lastYear", 11);
		functionMaps.put("minute", 12);
		functionMaps.put("month", 13);
		functionMaps.put("monthBegin", 14);
		functionMaps.put("monthEnd", 15);
		functionMaps.put("now", 16);
		functionMaps.put("quaterBegin", 17);
		functionMaps.put("quaterEnd", 18);
		functionMaps.put("relDate", 19);
		functionMaps.put("relTime", 20);
		functionMaps.put("second", 21);
		functionMaps.put("secondsAfter", 22);
		functionMaps.put("time", 23);
		functionMaps.put("weekBegin", 24);
		functionMaps.put("weekEnd", 25);
		functionMaps.put("year", 26);
		functionMaps.put("firstdayofmonth", 27);
	}

	@Override
	public int getMethodID(String name) {
		if (functionMaps.containsKey(name)) {
			return functionMaps.get(name);
		}
		return -1;
	}

	@Override
	public Object call(Context ctx,Stack<OperatorData> para, int methodID) {
		switch (methodID) {
		case 1:
			return this.preAge(para);
		case 2:
			return this.preDay(para);
		case 3:
			return this.preDayName(para);
		case 4:
			return this.preDayNo(para);
		case 5:
			return this.preDaysAfter(para);
		case 6:
			return this.preDaysInMonth(para);
		case 7:
			return this.preDaysInYear(para);
		case 8:
			return this.preHour(para);
		case 9:
			return this.preLastDay(para);
		case 10:
			return this.preLastMonth(para);
		case 11:
			return this.preLastYear(para);
		case 12:
			return this.preMinute(para);
		case 13:
			return this.preMonth(para);
		case 14:
			return this.preMonthBegin(para);
		case 15:
			return this.preMonthEnd(para);
		case 16:
			return this.now();
		case 17:
			return this.preQuaterBegin(para);
		case 18:
			return this.preQuaterEnd(para);
		case 19:
			return this.preRelDate(para);
		case 20:
			return this.preRelTime(para);
		case 21:
			return this.preSecond(para);
		case 22:
			return this.preSecondsAfter(para);
		case 23:
			return this.preTime(para);
		case 24:
			return this.preWeekBegin(para);
		case 25:
			return this.preWeekEnd(para);
		case 26:
			return this.preYear(para);
		case 27:
			return this.getFirstDayOfMonth();
		}
		return null;
	}

	/**
	 * ʱ���ʽת��
	 * 
	 * @param dateStr
	 * @param formatStr
	 * @return
	 */
	public Date dateTimeS(String dateStr, String formatStr) {
		Date date = null;
		DateFormat df = new SimpleDateFormat(formatStr);
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * ʱ���ʽת��
	 * 
	 * @param dateL
	 * @return
	 */
	public Date dateTimeL(long dateL) {
		Date date = new Date();
		date.setTime(dateL);
		return date;
	}

	/**
	 * ����Ӳ������ʱ�䵽��ǰ�����������
	 * 
	 * @param date
	 * @return
	 */
	public int age(Date date) {
		int i = 1;
		return age(date, i);
	}

	/**
	 * ����Ӳ������ʱ�䵽��ǰ�����������
	 * 
	 * @param date
	 * @param i
	 * @return
	 */
	public int age(Date date, int i) {
		int s = 0;
		String dfStr = null;
		Date now = new Date();
		DateFormat dfy = new SimpleDateFormat("yyyy");
		String str_1 = dfy.format(now);
		String str_2 = dfy.format(date);
		switch (i) {
		case 1:
			dfStr = "MM-dd";
		case 2:
			dfStr = "MM";
		case 3:
			s = Integer.parseInt(str_1) - Integer.parseInt(str_2);
		}
		if (dfStr != null) {
			DateFormat df = new SimpleDateFormat(dfStr);
			String str_3 = df.format(now);
			String str_4 = df.format(date);
			int c = str_3.compareTo(str_4);
			if (c >= 0) {
				s = Integer.parseInt(str_1) - Integer.parseInt(str_2);
			} else if (c < 0) {
				s = Integer.parseInt(str_1) - Integer.parseInt(str_2) - 1;
			}
		}
		return s;
	}

	public int preAge(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		if (!para.isEmpty()) {
			return age(date);
		} else {
			int i = Integer.parseInt(para.pop().value.toString());
			return age(date, i);
		}
	}

	/**
	 * ������������л�ø����ڱ������Ǽ���
	 * 
	 * @param date
	 * @return
	 */
	public int day(Date date) {
		int s = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		s = c.get(Calendar.DAY_OF_MONTH);
		return s;
	}

	public int preDay(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return day(date);
	}

	/**
	 * ������������л�ø��յ��������
	 * 
	 * @param date
	 * @return
	 */
	public String dayName(Date date) {
		String s = null;
		int week = dayNo(date);
		switch (week) {
		case 1:
			s = "������";
		case 2:
			s = "����һ";
		case 3:
			s = "���ڶ�";
		case 4:
			s = "������";
		case 5:
			s = "������";
		case 6:
			s = "������";
		case 7:
			s = "������";
		}
		return s;
	}

	public String preDayName(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return dayName(date);
	}

	/**
	 * ������������У���ø���λ��һ�������еĵڼ���
	 * 
	 * @param date
	 * @return
	 */
	public int dayNo(Date date) {
		int s = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		s = c.get(Calendar.DAY_OF_WEEK);
		return s;
	}

	public int preDayNo(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return dayNo(date);
	}

	/**
	 * ���������������������
	 * 
	 * @param date_1
	 * @param date_2
	 * @return
	 */
	public int daysAfter(Date date_1, Date date_2) {
		int s = 0;
		long time_1 = date_1.getTime();
		long time_2 = date_2.getTime();
		s = (int) ((time_1 - time_2) / (1000 * 60 * 60 * 24));
		return s;
	}

	public int preDaysAfter(Stack<OperatorData> para) {
		String dateStr_1 = para.pop().value.toString();
		String formatStr_1 = para.pop().value.toString();
		Date date_1 = dateTimeS(dateStr_1, formatStr_1);
		String dateStr_2 = para.pop().value.toString();
		String formatStr_2 = para.pop().value.toString();
		Date date_2 = dateTimeS(dateStr_2, formatStr_2);
		return daysAfter(date_1, date_2);
	}

	/**
	 * ���ָ�����������µ�����
	 * 
	 * @param date
	 * @return
	 */
	public int daysInMonth(Date date) {
		int s = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			s = 31;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			s = 30;
		} else {
			if (year % 4 != 0) {
				s = 28;
			} else {
				s = 29;
			}
		}
		return s;
	}

	public int preDaysInMonth(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return daysInMonth(date);
	}

	/**
	 * ���ָ���������������ָ����ݵ�����
	 * 
	 * @param date
	 * @return
	 */
	public int daysInYear(Date date) {
		int s = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		if (year % 4 != 0) {
			s = 365;
		} else {
			s = 366;
		}
		return s;
	}

	public int preDaysInYear(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return daysInYear(date);
	}

	/**
	 * ������ʱ��������У���õ�ǰʱ��λ��һ���еĵڼ���ʱ��
	 * 
	 * @param date
	 * @return
	 */
	public int Hour(Date date) {
		int s = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		s = c.get(Calendar.HOUR_OF_DAY);
		return s;
	}

	public int preHour(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return Hour(date);
	}

	/**
	 * ���ָ�����ڵ���һ��
	 * 
	 * @param date
	 * @return
	 */
	public Date lastDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, -1);
		date = c.getTime();
		return date;
	}

	public Date preLastDay(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return lastDay(date);
	}

	/**
	 * ȡ��ָ�����������µ�ͬ�գ�����ͬһ�գ��򷵻�������ĩ
	 * 
	 * @param date
	 * @return
	 */
	public Date lastMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, -1);
		date = c.getTime();
		return date;
	}

	public Date preLastMonth(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return lastMonth(date);
	}

	/**
	 * ȡ��ָ��������ȥ���ͬ��ͬ�գ�����ͬ��ͬ�գ��򷵻�ȥ��ͬ�����һ��
	 * 
	 * @param date
	 * @return
	 */
	public Date lastYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, -1);
		date = c.getTime();
		return date;
	}

	public Date preLastYear(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return lastYear(date);
	}

	/**
	 * ������ʱ��������У���÷��ӵ���Ϣ
	 * 
	 * @param date
	 * @return
	 */
	public int minute(Date date) {
		int s = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		s = c.get(Calendar.MINUTE);
		return s;
	}

	public int preMinute(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return minute(date);
	}

	/**
	 * ȡ��ָ���������ڵ��·�
	 * 
	 * @param date
	 * @return
	 */
	public int month(Date date) {
		int s = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		s = c.get(Calendar.MONTH);
		return s+1;
	}

	public int preMonth(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return month(date);
	}

	/**
	 * ȡ��ָ�����������µ�����
	 * 
	 * @param date
	 * @return
	 */
	public Date monthBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		date = c.getTime();
		return date;
	}

	public Date preMonthBegin(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return monthBegin(date);
	}

	/**
	 * ȡ��ָ�����������µ���ĩ
	 * 
	 * @param date
	 * @return
	 */
	public Date monthEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = daysInMonth(date);
		c.set(Calendar.DAY_OF_MONTH, month);
		date = c.getTime();
		return date;
	}

	public Date preMonthEnd(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return monthEnd(date);
	}

	/**
	 * ���ϵͳ�˿̵�����ʱ��
	 * 
	 * @return
	 */
	public Date now() {
		Date date = new Date();
		return date;
	}

	/**
	 * ȡ��ָ���������ڼ��ȵ�����
	 * 
	 * @param date
	 * @return
	 */
	public Date quaterBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		if (month <= 3) {
			c.set(Calendar.MONTH, 0);
		} else if (month > 3 && month <= 6) {
			c.set(Calendar.MONTH, 3);
		} else if (month > 6 && month <= 9) {
			c.set(Calendar.MONTH, 6);
		} else if (month > 9 && month <= 12) {
			c.set(Calendar.MONTH, 9);
		}
		c.set(Calendar.DAY_OF_MONTH, 1);
		date = c.getTime();
		return date;
	}

	public Date preQuaterBegin(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return quaterBegin(date);
	}

	/**
	 * ȡ��ָ���������ڼ��ȵ�ĩ��
	 * 
	 * @param date
	 * @return
	 */
	public Date quaterEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		if (month <= 3) {
			c.set(Calendar.MONTH, 2);
			c.set(Calendar.DAY_OF_MONTH, 31);
		} else if (month > 3 && month <= 6) {
			c.set(Calendar.MONTH, 5);
			c.set(Calendar.DAY_OF_MONTH, 30);
		} else if (month > 6 && month <= 9) {
			c.set(Calendar.MONTH, 8);
			c.set(Calendar.DAY_OF_MONTH, 30);
		} else if (month > 9 && month <= 12) {
			c.set(Calendar.MONTH, 11);
			c.set(Calendar.DAY_OF_MONTH, 31);
		}
		date = c.getTime();
		return date;
	}

	public Date preQuaterEnd(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return quaterEnd(date);
	}

	/**
	 * �Ӹ������������У������� n�����µ��������
	 * 
	 * @param date
	 * @param i
	 * @return
	 */
	public Date relDate(Date date, int i) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, i);
		date = c.getTime();
		return date;
	}

	public Date preRelDate(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		int i = Integer.parseInt(para.pop().value.toString());
		return relDate(date, i);
	}

	/**
	 * �Ӹ������ʱ��������У������� n �����µ�����ʱ�����
	 * 
	 * @param date
	 * @param i
	 * @return
	 */
	public Date relTime(Date date, int i) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, i);
		date = c.getTime();
		return date;
	}

	public Date preRelTime(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		int i = Integer.parseInt(para.pop().value.toString());
		return relTime(date, i);
	}

	/**
	 * ������ʱ��������У��������Ϣ
	 * 
	 * @param date
	 * @return
	 */
	public int second(Date date) {
		int s = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		s = c.get(Calendar.SECOND);
		return s;
	}

	public int preSecond(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return second(date);
	}

	/**
	 * ������������ʱ�����������
	 * 
	 * @param date_1
	 * @param date_2
	 * @return
	 */
	public int secondsAfter(Date date_1, Date date_2) {
		int s = 0;
		long time_1 = date_1.getTime();
		long time_2 = date_2.getTime();
		s = (int) ((time_1 - time_2) / 1000);
		return s;
	}

	public int preSecondsAfter(Stack<OperatorData> para) {
		String dateStr_1 = para.pop().value.toString();
		String formatStr_1 = para.pop().value.toString();
		Date date_1 = dateTimeS(dateStr_1, formatStr_1);
		String dateStr_2 = para.pop().value.toString();
		String formatStr_2 = para.pop().value.toString();
		Date date_2 = dateTimeS(dateStr_2, formatStr_2);
		return secondsAfter(date_1, date_2);
	}

	/**
	 * ���ַ�ת����ʱ�������
	 * 
	 * @param timeStr
	 * @return
	 */
	public Date time(String timeStr, String formatStr) {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(formatStr);
		try {
			date = df.parse(timeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public Date preTime(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		return time(dateStr, formatStr);
	}

	/**
	 * ���ָ�������������ڵ������죬�����϶�������Ϊһ�ܵĿ�ʼ
	 * 
	 * @param date
	 * @return
	 */
	public Date weekBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, 1);
		date = c.getTime();
		return date;
	}

	public Date preWeekBegin(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return weekBegin(date);
	}

	/**
	 * ���ָ�������������ڵ������������϶�������Ϊһ�ܵĽ���
	 * 
	 * @param date
	 * @return
	 */
	public Date weekEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, 7);
		date = c.getTime();
		return date;
	}

	public Date preWeekEnd(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return weekEnd(date);
	}

	/**
	 * ������������л������Ϣ
	 * 
	 * @param date
	 * @return
	 */
	public int year(Date date) {
		int s = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		s = c.get(Calendar.YEAR);
		return s;
	}

	public int preYear(Stack<OperatorData> para) {
		String dateStr = para.pop().value.toString();
		String formatStr = para.pop().value.toString();
		Date date = dateTimeS(dateStr, formatStr);
		return year(date);
	}
	
	/**
	 * 返回当月第一天，格式yyyy-MM-dd
	 * @return
	 */
	public String getFirstDayOfMonth() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();    
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
        String date ="";
        date = format.format(c.getTime());
        
        return date;
	}
	
}
