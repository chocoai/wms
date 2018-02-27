package com.xyy.expression.lib;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xyy.expression.Context;
import com.xyy.expression.ILib;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.util.StringUtil;

/**
 * 字符串处理库函数
 * @author evan
 *
 */
public class StringLib implements ILib {
	public StringLib() {

	}

	private static Map<String, Integer> functionMaps = new HashMap<String, Integer>();

	static {
		functionMaps.put("getAsc", 1);
		functionMaps.put("getChar", 2);
		functionMaps.put("getChn", 3);
		functionMaps.put("getFill", 4);
		functionMaps.put("getHexstring", 5);
		functionMaps.put("getLeft", 6);
		functionMaps.put("getLen", 7);
		functionMaps.put("getLike", 8);
		functionMaps.put("getLink", 9);
		functionMaps.put("getlTrim", 10);
		functionMaps.put("getLower", 11);
		functionMaps.put("getMid", 12);
		functionMaps.put("getPos", 13);
		functionMaps.put("getRight", 14);
		functionMaps.put("getRmQuote", 15);
		functionMaps.put("getRmb", 16);
		functionMaps.put("getRplc", 17);
		functionMaps.put("getRtrim", 18);
		functionMaps.put("getSpace", 19);
		functionMaps.put("getSplit", 20);
		functionMaps.put("getTrim", 21);
		functionMaps.put("getUpper", 22);
		functionMaps.put("getChar", 22);
		functionMaps.put("getWordCap", 23);
		functionMaps.put("getBigint", 24);
		functionMaps.put("getDate", 25);
		functionMaps.put("getDateTime", 26);
		functionMaps.put("getDateTime2", 27);
		functionMaps.put("getDecimal", 28);
		functionMaps.put("getDouble", 29);
		functionMaps.put("getFloat", 30);
		functionMaps.put("getInteger", 31);
		functionMaps.put("getLong", 32);
		functionMaps.put("getNumber", 33);
		functionMaps.put("getStr", 34);
		
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
		case 1:// asc
			return this.parGetAsc(para);
		case 2:// char
			return this.parGetChar(para);
		case 3:
			return null;
		case 4:
			return this.parGetFill(para);
		case 5:
			return this.parGetHexstring(para);
		case 6:
			return this.parGetLeft(para);
		case 7:
			return this.paraGetLen(para);
		case 8:
			return this.paraGetLike(para);
		case 9:
			return this.paraGetLink(para);
		case 10:
			return this.paraGetLtrim(para);
		case 11:
			return this.paraGetLower(para);
		case 12:
			return this.paraGetMid(para);
		case 13:
			return this.paraGetPos(para);
		case 14:
			return this.paraGetRight(para);
		case 15:
			return this.paraGetRmQuote(para);
		//case 16:
			//return this.paraGetRmb(para);
		case 17:
			return this.paraGetRplc(para);
		case 18:
			return this.paraGetRtrim(para);
		case 19:
			return this.paraGetSpace(para);
		case 20:
			return this.paraGetSplit(para);
		case 21:
			return this.paraGetTrim(para);
		case 22:
			return this.paraGetUppper(para);
		case 23:
			return this.paraGetWordCap(para);
		case 24:
			return this.paraGetBigint(para);
		case 25:
			return this.paraGetDate(para);
		case 26:
			return this.paraGetDateTime(para);
		case 27:
			return this.paraGetDateTime2(para);
		case 28:
			return this.paraGetDecimal(para);
		case 29:
			return this.paraGetDouble(para);
		case 30:
			return this.paraGetFloat(para);
		case 31:
			return this.paraGetInteger(para);
		case 32:
			return this.paraGetLong(para);
		case 33:
			return this.paraGetNumber(para);
		case 34:
			return this.paraGetStr(para);
		default:
			return null;
		}
	}

	/**
	 * ============================================ 
	 * 字符串函数
	 * ============================================
	 */

	/**
	 * 取字符串指定位置的字符的 unicode 值，如果是 ascii 字符则返回ascii 码
	 * 
	 * @param str
	 * @param i
	 * @return
	 */
	public int parGetAsc(Stack<OperatorData> para) {
		String str = para.pop().value.toString();
		int str2 = 0;
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				str2 = Integer.valueOf(od2.value.toString());
			}
		}
		return StringLib.getAsc(str, str2);
	}

	public static int getAsc(String str, int ins) {
		Integer index = null;
		int temp = 0;
		if (str != null && !str.equals("") && !str.equals(" ")) {
			if (ins > 0 && ins < 2) {// ȱʡΪ0
				index = str.codePointAt(ins);
			} else if (ins == 0 && ins < 2) {
				index = ins;
				index = str.codePointAt(index);
			}
		}
		if (ins >= 2) {
			return temp;
		} else {
			return index;
		}
	}

	/**
	 * 
	 * 根据给定的 unicode 编码或者 ascii 码取得其对应的字符(一般来说，英文字符及其扩展字符都是 ascii 字符
	 * 
	 * @param str
	 * @return
	 */
	public char parGetChar(Stack<OperatorData> para) {
		String str = para.pop().value.toString();
		return this.getChar(str);
	}

	public char getChar(String str) {
		char ch = 0;
		String likeType = "^[0-9]*$";
		if (str != null && !str.equals("") && !str.equals(" ")) {
			if (str.matches(likeType)) {
				Integer index = Integer.valueOf(str);
				ch = (char) index.parseInt(str);
			}
		}
		return ch;
	}

	/**
	 * 将一个整数转化成汉字大写
	 * 例1：chn(1234567,true) 返回："一百二十三万四千五百六十七"
	 * 例2：chn(1234567)返回："一二三四五六七" 例3：chn(1234567,true,true) 返回："壹佰贰拾叁万肆仟伍佰陆拾柒"
	 * 例4：chn(1234567,,false) 返回："一二三四五六七"
	 * 
	 * @param value
	 * @param bool1
	 * @param bool2
	 * @return
	 */
	public String getChn(double value, boolean bool1, boolean bool2) {

		char[] hunit = { '拾', '佰', '仟' }; // 数字表示
		char[] vunit = { '万', '亿' }; // 段名表示
		char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示

		BigDecimal midVal = new BigDecimal(java.lang.Math.round(value * 100)); // 转化成整形,替换上句
		String valStr = String.valueOf(midVal); // 转化成字符串
		String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
		String rail = valStr.substring(valStr.length() - 2); // 取小数部分

		String prefix = ""; // 整数部分转化的结果
		String suffix = ""; // 小数部分转化的结果
		/**
		 * 处理小数点后面的数
		 * 
		 * if (rail.equals("00")) { // 如果小数部分为0 suffix = "整"; } else { suffix =
		 * digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) - '0'] +
		 * "分"; // 否则把角分转化出来 }
		 */
		// 处理小数点前面的数
		char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
		boolean preZero = false; // 标志当前位的上一位是否为有效0位（如万位的0对千位无效）
		byte zeroSerNum = 0; // 连续出现0的次数
		for (int i = 0; i < chDig.length; i++) { 
			int idx = (chDig.length - i - 1) % 4; 
			int vidx = (chDig.length - i - 1) / 4; 
			if (chDig[i] == '0') { 
				preZero = true;
				zeroSerNum++; 
				if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
					prefix += vunit[vidx - 1];
					preZero = false; 
				}
			} else {
				zeroSerNum = 0; 
				if (preZero) { 
					prefix += digit[0];
					preZero = false;
				}
				prefix += digit[chDig[i] - '0']; 
				if (idx > 0)
					prefix += hunit[idx - 1];
				if (idx == 0 && vidx > 0) {
					prefix += vunit[vidx - 1]; 
				}
			}
		}

		/**
		 * �������ִ���,����Բ������ if (prefix.length() > 0) prefix += 'Բ';
		 */
		return prefix + suffix; 
	}

	/**
	 * ��� n �� s ƴ�ɵ��ַ�
	 * 
	 * @param str
	 * @param space
	 * @return
	 */
	public String parGetFill(Stack<OperatorData> para) {
		String str = para.pop().value.toString();
		int space = 0;
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				space = Integer.valueOf(od2.value.toString());
			}
		}
		return this.getFill(str, space);
	}

	public String getFill(String str, int space) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < space; i++) {// ��ȡҪƴ��str�Ĵ���
			sb.append(str);// StringBuffer ����ƴ��
		}
		return sb.toString();
	}

	/**
	 * ����ݻ��� byte ����ת��Ϊʮ������ַ���ݵ����޷����ݴ��?
	 * 
	 * @param in
	 * @param bool
	 * @return
	 */
	public String parGetHexstring(Stack<OperatorData> para) {
		int ins = Integer.valueOf(para.pop().value.toString());
		boolean bool = false;
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				bool = (Boolean) od2.value;
			}
		}
		return this.getHexstring(ins, bool);
	}

	public String getHexstring(int in, boolean bool) {
		String toHexstr = "";
		String uppStr = "";
		toHexstr = Integer.toHexString(in);
		uppStr = toHexstr.toUpperCase();
		if (bool == true) {
			String a = uppStr.substring(0, 2) + " ";
			String b = uppStr.substring(a.length() - 1, 4) + " ";
			String c = uppStr.substring(b.length() + 1) + " ";
			uppStr = a + b + c;
		} else if (bool == false) {
			return uppStr;
		}
		return uppStr;
	}

	public String getHexstring(int in) {
		return this.getHexstring(in, false);
	}

	/**
	 * 获得源字符串左边的子串
	 * 
	 * @param str
	 * @param lenght
	 * @return
	 */

	public String parGetLeft(Stack<OperatorData> para) {
		String v1 = para.pop().value.toString();
		int ins = 0;
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				ins = Integer.parseInt(od2.value.toString());
			}
		}
		return this.getLeft(v1, ins);
	}

	public String getLeft(String str, int lenght) {
		String sub = "";
		if (str != null && !str.equals("") && !str.equals(" ")) {
			sub = str.substring(0, lenght);
		}
		return sub;
	}

	/**
	 * 
	 * 
	 * @param str
	 * @return
	 */
	public int paraGetLen(Stack<OperatorData> para) {
		String str = para.pop().value.toString();
		return this.getLen(str);

	}

	public int getLen(String str) {
		int len = 0;
		if (str != null && !str.equals("") && !str.equals(" ")) {
			len = str.length();
		}
		return len;
	}

	/**
	 * �ж��ַ��Ƿ�ƥ���ʽ��(*ƥ�� 0 �������ַ�?ƥ�䵥���ַ�)
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public boolean paraGetLike(Stack<OperatorData> para) {
		String str = para.pop().value.toString();
		boolean bool = false;
		String regex = "";
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				regex = od2.value.toString();
			}
		}
		if (!para.isEmpty()) {
			OperatorData od3 = para.pop();
			if (od3.clazz != NullRefObject.class) {
				bool = (Boolean) od3.value;
			}
		}
		return this.getLike(str, regex, bool);
	}

	public boolean getLike(String str, String regex, boolean bool) {
		regex = regex.replaceAll("\\*", ".*");
		regex = regex.replaceAll("\\?", ".");
		Pattern pattern = Pattern.compile(regex,
				bool ? Pattern.CASE_INSENSITIVE : 0);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * ���һ�� url ����˵���� serviceName ������� jspArgNames jsp
	 * ����������飬��Ϊ�գ�Ԫ��������Ϊ�ַ��� jspArgValues jsp ����ֵ���飬����
	 * jspArgNames Ԫ�ظ�����ͬ reportArgNames �������������飬��Ϊ�գ�Ԫ��������Ϊ�ַ���
	 * reportArgValues �������ֵ���飬���� reportArgNames Ԫ�ظ�����ͬ
	 * 
	 * @param serviceName
	 * @param parName
	 * @param parValue
	 * @return
	 */
	public String paraGetLink(Stack<OperatorData> para) {
		String str = para.pop().value.toString();
		List<String> list1 = null;
		List<String> list2 = null;
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				od2.value.toString();
			}
		}
		if (!para.isEmpty()) {
			OperatorData od3 = para.pop();
			if (od3.clazz != NullRefObject.class) {
				od3.value.toString();
			}
		}
		return this.getLink(str, list1, list2);
	}

	public String getLink(String serviceName, List<String> parName,
			List<String> parValue) {
		StringBuffer sb = new StringBuffer();
		List parNamelist = new ArrayList();
		List parValuelist = new ArrayList();
		String temp = "";
		Object[] objName;
		Object[] objVal;
		if (serviceName != null && !serviceName.isEmpty()
				&& !serviceName.equals("") && !serviceName.equals(" ")) {

			if (serviceName.indexOf("?") == -1) {
				for (int i = 0; i < parName.size(); i++) {
					// temp +=parName.get(i).toString()+"&"; //��ȡ������Ƽ���&���
					// a&b&
					// tempVal +=
					// "="+parValue.get(i).toString();//��ȡ����ֵ�ӿ�ͷ����=���
					// =1=2
					parNamelist.add(parName.get(i).toString());
					parValuelist.add("=" + parValue.get(i).toString() + "&");
				}
				objName = parNamelist.toArray();
				objVal = parValuelist.toArray();
				sb.append(serviceName.trim()).append("?");
				for (int j = 0; j < objName.length; j++) {
					sb.append(objName[j].toString()).append(
							objVal[j].toString());
				}
				temp = sb.toString();
			} else {
				if (serviceName.endsWith("?")) {
					for (int i = 0; i < parName.size(); i++) {
						parNamelist.add(parName.get(i).toString());
						parValuelist
								.add("=" + parValue.get(i).toString() + "&");
					}
					objName = parNamelist.toArray();
					objVal = parValuelist.toArray();
					sb.append(serviceName.trim());
					for (int j = 0; j < objName.length; j++) {
						sb.append(objName[j].toString()).append(
								objVal[j].toString());
					}
					temp = sb.toString();
				} else {
					// url = url + "&" + key + "=" + value;
				}
			}
		}
		return temp;
	}

	/**
	 * ȥ����ո�
	 * 
	 * @param str
	 * @return
	 */
	public String paraGetLtrim(Stack<OperatorData> para) {
		String str = "";
		str = para.pop().value.toString();
		return this.getlTrim(str);
	}

	public String getlTrim(String str) {
		if (str == null || str.equals("")) {
			return str;
		} else {
			return str.replaceAll("^[�� ]+", "");
		}
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public String paraGetRtrim(Stack<OperatorData> para) {
		String str = "";
		str = para.pop().value.toString();
		return this.getrTrim(str);

	}

	public String getrTrim(String str) {
		if (str == null || str.equals("")) {
			return str;
		} else {
			return str.replaceAll("[�� ]+$", "");
		}
	}

	/**
	 * 
	 * ��дתСд
	 * 
	 * @param str
	 * @return
	 */
	public String paraGetLower(Stack<OperatorData> para) {
		String str = "";
		str = para.pop().value.toString();
		return this.getLower(str);
	}

	public String getLower(String str) {
		String lower = "";
		lower = str.toLowerCase();
		return lower;
	}

	/**
	 * ��ȡ�ַ�
	 * 
	 * @param str
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public String paraGetMid(Stack<OperatorData> para) {
		String str = "";
		int start = 0;
		int end = 0;
		str = para.pop().value.toString();
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				start = Integer.parseInt(od2.value.toString());
			}
		}
		if (!para.isEmpty()) {
			OperatorData od3 = para.pop();
			if (od3.clazz != NullRefObject.class) {
				end = Integer.parseInt(od3.value.toString());
			}
		}
		return this.getMid(str, start, end);
	}

	public String getMid(String str, int beginIndex, int endIndex) {
		String sub = "";
		if (endIndex == 0) {
			sub = str.substring(beginIndex);
		} else {
			sub = str.substring(beginIndex, endIndex);
		}
		return sub;
	}

	public String getMid(String str, int beginindex) {
		return this.getMid(str, beginindex, 0);
	}

	/**
	 * ����str1��Դ������һ�γ��ֵ�λ��
	 * 
	 * @param str
	 * @param str1
	 * @param index
	 * @return
	 */
	public int paraGetPos(Stack<OperatorData> para) {
		String str = "";
		String str1 = "";
		int index = 0;
		str = para.pop().value.toString();
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				str1 = od2.value.toString();
			}
		}
		if (!para.isEmpty()) {
			OperatorData od3 = para.pop();
			if (od3.clazz != NullRefObject.class) {
				index = Integer.parseInt(od3.value.toString());
			}
		}
		return this.getPos(str, str1, index);
	}

	public int getPos(String str, String str1, int index) {
		int sub = 0;
		if (index == 0) {
			sub = str.indexOf(str1);
		} else if (index != 0) {
			sub = str.indexOf(str1, index);
		}
		return sub;
	}

	public int getPos(String str, String str1) {
		return this.getPos(str, str1, 0);
	}

	/**
	 * �����ȡ�ַ�
	 * 
	 * @param str
	 * @param index
	 * @return
	 */
	public String paraGetRight(Stack<OperatorData> para) {
		String str = "";
		int index = 0;
		str = para.pop().value.toString();
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				index = Integer.parseInt(od2.value.toString());
			}
		}
		return this.getRight(str, index);
	}

	public String getRight(String str, int index) {
		if (str.isEmpty()) {
			return "";
		}
		index = (index > str.length()) ? str.length() : index;
		return str.substring(str.length() - index, str.length());
	}

	/**
	 * ɾ����ʽ�����ţ���������ź�˫���
	 * 
	 * @param str
	 * @return
	 */
	public String paraGetRmQuote(Stack<OperatorData> para) {
		String str = "";
		str = para.pop().value.toString();
		return this.getRmQuote(str);
	}

	public String getRmQuote(String str) {
		str = str.replaceAll("\"", "").replaceAll("\'", "");
		return str;
	}

	/**
	 * ��������ֻ֧��С������λ��ת������Ҵ�д��ʽ
	 * 
	 * @param value
	 * @return
	
	public String paraGetRmb(Stack<OperatorData> para) {
		double db = 0;
		db = Double.parseDouble(para.pop().value.toString());
		return this.getRmb(db);
	}
 */
	/*
	public String getRmb(double value) {

		char[] hunit = { 'ʰ', '��', 'Ǫ' }; // ����λ�ñ�ʾ
		char[] vunit = { '��', '��' }; // �����ʾ
		char[] digit = { '��', 'Ҽ', '��', '��', '��', '��', '½', '��', '��', '��' }; // ���ֱ�ʾ

		BigDecimal midVal = new BigDecimal(java.lang.Math.round(value * 100)); // ת��������,�滻�Ͼ�
		String valStr = String.valueOf(midVal); // ת�����ַ�
		String head = valStr.substring(0, valStr.length() - 2); // ȡ�����
		String rail = valStr.substring(valStr.length() - 2); // ȡС���

		String prefix = ""; // �����ת���Ľ��
		String suffix = ""; // С���ת���Ľ��

		if (rail.equals("00")) { // ���С���Ϊ0
			suffix = "��";
		} else {
			suffix = digit[rail.charAt(0) - '0'] + "��"
					+ digit[rail.charAt(1) - '0'] + "��"; // ����ѽǷ�ת������
		}

		// ����С���ǰ�����
		char[] chDig = head.toCharArray(); // �������ת�����ַ�����
		boolean preZero = false; // ��־��ǰλ����һλ�Ƿ�Ϊ��Ч0λ������λ��0��ǧλ��Ч��
		byte zeroSerNum = 0; // �������0�Ĵ���
		for (int i = 0; i < chDig.length; i++) { // ѭ������ÿ������
			int idx = (chDig.length - i - 1) % 4; // ȡ����λ��
			int vidx = (chDig.length - i - 1) / 4; // ȡ��λ��
			if (chDig[i] == '0') { // ���ǰ�ַ���0
				preZero = true;
				zeroSerNum++; // ����0�������
				if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
					prefix += vunit[vidx - 1];
					preZero = false; // ������һλ�Ƿ�Ϊ0����Ϊ��Ч0λ
				}
			} else {
				zeroSerNum = 0; // ����0��������
				if (preZero) { // ��һλΪ��Ч0λ
					prefix += digit[0]; // ֻ������ط��õ�'��'
					preZero = false;
				}
				prefix += digit[chDig[i] - '0']; // ת�������ֱ�ʾ
				if (idx > 0)
					prefix += hunit[idx - 1];
				if (idx == 0 && vidx > 0) {
					prefix += vunit[vidx - 1]; // �ν���λ��Ӧ�ü��϶�������,��
				}
			}
		}

		if (prefix.length() > 0)
			prefix += 'Բ';

		return prefix + suffix; // ������ȷ��ʾ
	}
	*/

	/**
	 * ���ַ� src �����ַ� a��Ϊ�ַ� b��
	 * 
	 * @param str1
	 * @param str2
	 * @param str3
	 * @param bool
	 * @return
	 */
	public String paraGetRplc(Stack<OperatorData> para) {
		String str1 = "";
		String str2 = "";
		String str3 = "";
		boolean bool = true;
		str1 = para.pop().value.toString();
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				str2 = od2.value.toString();
			}
		}
		if (!para.isEmpty()) {
			OperatorData od3 = para.pop();
			if (od3.clazz != NullRefObject.class) {
				str3 = od3.value.toString();
			}
		}
		if (!para.isEmpty()) {
			OperatorData od4 = para.pop();
			if (od4.clazz != NullRefObject.class) {
				bool = (Boolean) od4.value;
			}
		}
		return this.getRplc(str1, str2, str3, bool);
	}

	public String getRplc(String str1, String str2, String str3, boolean bool) {
		String strs = "";
		if (str1 != null && str2 != null && str3 != null) {
			if (bool == false) {
				strs = str1.replaceFirst(str2, str3);
			} else if (bool == true) {
				strs = str1.replaceAll(str2, str3);
			}
		}
		return strs;
	}

	/**
	 * ��ȡindex�ο��ַ�
	 * 
	 * @param index
	 * @return
	 */
	public String paraGetSpace(Stack<OperatorData> para) {
		int in = 0;
		in = Integer.parseInt(para.pop().value.toString());
		return this.getSpace(in);
	}

	public String getSpace(int index) {
		String str2 = "";
		for (int i = 0; i < index; i++) {
			str2 += " ";
		}
		return str2;
	}

	/**
	 * ���ַ��ָ��ָ�ɶ���Ӵ�
	 * 
	 * @param str1
	 * @param spilt
	 * @return
	 */
	public String[] paraGetSplit(Stack<OperatorData> para) {
		String str = "";
		String split = "";
		boolean bool = false;

		str = para.pop().value.toString();
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				split = od2.value.toString();
			}
		}
		if (!para.isEmpty()) {
			OperatorData od3 = para.pop();
			if (od3.clazz != NullRefObject.class) {
				bool = (Boolean) od3.value;
			}
		}
		return this.getSplit(str, split, bool);
	}

	public String[] getSplit(String str1, String split, boolean bool) {
		String stres[] = null;
		if (bool == true) {
			StringTokenizer toKenizer = new StringTokenizer(str1, split);
			stres = new String[toKenizer.countTokens()];
			int iCount = toKenizer.countTokens();
			for (int i = 0; i < iCount; i++) {
				stres[i] = toKenizer.nextToken();
			}
			return stres;
		} else if (bool == false) {
			String temp_result = StringUtil.filter_0(str1);
			String result = StringUtil.filter_1(temp_result);
			// System.out.println(result);
			String[] strs = result.split(";");
			stres = new String[strs.length];
			String temp = "";
			for (int i = 0; i < strs.length; i++) {
				if (strs[i].indexOf("$") != -1) {
					stres[i] = strs[i].replaceAll("\\$", ";");
				} else {
					stres[i] = strs[i];
				}
			}
		}
		return stres;
	}

	/**
	 * ȥ����β�ո�
	 * 
	 * @param str
	 * @return
	 */
	public String paraGetTrim(Stack<OperatorData> para) {
		String str = "";
		str = para.pop().value.toString();
		return this.getTrim(str);
	}

	public String getTrim(String str) {
		String str2 = "";
		str2 = str.trim();
		return str2;
	}

	/**
	 * �滻������ĸΪ��д
	 * 
	 * @param str
	 * @return
	 */
	public String paraGetUppper(Stack<OperatorData> para) {
		String str = "";
		str = para.pop().value.toString();
		return this.getUpper(str);
	}

	public String getUpper(String str) {
		String str1 = str.toUpperCase();
		return str1;
	}

	/**
	 * ����ĸ ��д
	 * 
	 * @param StrValue
	 * @return
	 */
	public String paraGetWordCap(Stack<OperatorData> para) {
		String str = "";
		str = para.pop().value.toString();
		return this.getWordCap(str);
	}

	public String getWordCap(String StrValue) {
		StringBuffer str2 = new StringBuffer("");

		String[] stres = StrValue.split(" ");
		for (int i = 0; i < stres.length; i++) {
			stres[i] = stres[i].substring(0, 1).toUpperCase()// ����ĸ��д
					+ stres[i].substring(1);// ��ȡ��Ԫ�ص�����Ԫ��
		}
		for (int i = 0; i < stres.length; i++) {
			str2.append(stres[i]);
			str2.append(" ");
		}
		return str2.toString();
	}

	/**
	 * ================================================== �������ת��
	 * ==================================================
	 */

	/**
	 * ���ַ�������ת���ɴ���������ֵ�����ȡ��ش�����
	 */
	public BigInteger paraGetBigint(Stack<OperatorData> para) {
		Object object;
		object = para.pop().value;
		return this.getBigint(object);
	}

	public BigInteger getBigint(Object obj) {

		BigInteger bIn = null;
		BigInteger big = null;
		Integer in = 0;

		if (obj.getClass() == String.class) {
			bIn = new BigDecimal(String.valueOf(obj)).toBigInteger().abs();
			return bIn;
		}
		if (obj.getClass() == Double.class || obj.getClass() == double.class) {
			in = Double.valueOf(obj.toString()).intValue();
			big = big.valueOf(in.longValue()).abs();
		}
		return big;

	}

	/**
	 * ���ַ�ת�������������
	 * 
	 * @param dStr
	 * @return
	 * @throws Exception
	 */
	public String paraGetDate(Stack<OperatorData> para) {
		String str = "";
		str = para.pop().value.toString();
		return this.getDate(str);
	}

	public String getDate(String dStr) {
		DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String newStr = null;
		try {
			newStr = dft.format(DateFormat.getDateInstance().parse(dStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newStr;
	}

	/**
	 * ���ַ������ת��������ʱ��
	 * 
	 * @param obj
	 * @throws Exception
	 */
	public String paraGetDateTime(Stack<OperatorData> para) {
		Object object;
		object = para.pop().value;
		return this.getDateTime(object);
	}

	public String getDateTime(Object obj) {
		Class type = obj.getClass();
		/** ��ȡobj���ݹ������������ */
		String typeName = type.getName();
		/** ��ȡobj���ݹ������������ */
		DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String strDate = "";
		if (obj.getClass() == Integer.class || obj.getClass() == int.class
				|| obj.getClass() == Long.class || obj.getClass() == long.class) {
			long l = Long.valueOf(String.valueOf(obj));
			strDate = dft.format(l);
		} else if (obj.getClass() != Double.class
				&& obj.getClass() != Float.class
				&& obj.getClass() != Boolean.class) {

			String dateStr = String.valueOf(obj);
			try {
				date = dft.parse(dateStr);
				strDate = dft.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return strDate;
	}

	/**
	 * �����ָ�ʽ�������ַ�ת��������ʱ������ݣ�ת��ʱ����ָ���ַ�ĵ�ǰ��ʽ
	 * 
	 * @param str
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public String paraGetDateTime2(Stack<OperatorData> para) {
		String str = "";
		String format = "";
		str = para.pop().value.toString();
		format = para.pop().value.toString();
		return this.getDateTime2(str, format);
	}

	public String getDateTime2(String str, String format) {
		String trimStr = str.replaceAll("\\s", "");// �������ڸ�ʽȥ��ո�
		String nDate = "";
		DateFormat dft = new SimpleDateFormat(format);
		DateFormat dft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		Date date;

		if (trimStr.indexOf("��") != -1 || trimStr.indexOf("��") != -1
				|| trimStr.indexOf("��") != -1) {// �������� �Ƿ�� �� ���� ����
			int Year = trimStr.indexOf("��");// ȡ�������յ����
			int Month = trimStr.indexOf("��");
			int Day = trimStr.indexOf("��");
			// �ֱ�ȥ���� �� �� ��ֵ
			sb.append(trimStr.substring(0, Year + 1).replaceAll("��", "-"))
					.append(trimStr.substring(Year + 1, Month + 1).replaceAll(
							"��", "-"))
					.append(trimStr.substring(Month + 1).replaceAll("��", ""));
			String tempDate = sb.toString();
			calendar.set(1982, 11, 30);// ת����date����
			nDate = dft1.format(calendar.getTime());

		} else if (format.indexOf("hh:mm:ss") != -1
				|| format.indexOf("HH:mm:ss") != -1) {

			Map<String, String> maps = new HashMap<String, String>();
			String[] strs = str.split("/");
			String[] cs = { "��", "��", "��" };

			for (int i = 0; i < strs.length; i++) {
				// �ж�201211:14:14�У�����
				if (strs[i].toString().replaceAll("\\s", "").indexOf(":") > -1) {
					String replace = strs[i].toString().replaceAll("\\s", "-");
					String yStr = replace.substring(0, replace.indexOf("-"));
					maps.put(cs[i], yStr);
					calendar.set(Integer.valueOf(maps.get("��")),
							Integer.valueOf(maps.get("��")) - 1,
							Integer.valueOf(maps.get("��")));
					nDate = dft1.format(calendar.getTime());
				} else {
					maps.put(cs[i], strs[i]);
				}
			}
		} else {
			try {
				date = dft.parse(str);
				nDate = dft1.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		return nDate;
	}

	/**
	 * ���ַ����������ֵת���ɴ󸡵���
	 * 
	 * @param obj
	 * @return
	 */
	public BigDecimal paraGetDecimal(Stack<OperatorData> para) {
		Object object;
		object = para.pop().value;
		return this.getDecimal(object);
	}

	public BigDecimal getDecimal(Object obj) {
		BigDecimal bd = null;
		if (obj.getClass() == Integer.class || obj.getClass() == int.class
				|| obj.getClass() == Long.class || obj.getClass() == long.class) {
			bd = new BigDecimal(String.valueOf(obj));
		} else if (obj.getClass() == String.class) {
			bd = new BigDecimal(String.valueOf(obj));
		}
		return bd;
	}

	/**
	 * ���ַ������ת���� 64λ��˫���ȸ�����
	 * 
	 * @param obj
	 * @return
	 */
	public Double paraGetDouble(Stack<OperatorData> para) {
		Object object;
		object = para.pop().value;
		return this.getDouble(object);
	}

	public Double getDouble(Object obj) {
		Double dble = null;
		if (obj.getClass() == Integer.class || obj.getClass() == int.class
				|| obj.getClass() == Long.class || obj.getClass() == long.class) {
			dble = dble.valueOf(String.valueOf(obj)).doubleValue();
		} else if (obj.getClass() == String.class) {
			dble = dble.valueOf(String.valueOf(obj));
		}
		return dble;
	}

	/**
	 * �������ȸ�����˫���ȸ������ַ�ת��Ϊ 32 λ�ĵ����ȸ�����
	 * 
	 * @param obj
	 * @return
	 */
	public Float paraGetFloat(Stack<OperatorData> para) {
		Object object;
		object = para.pop().value;
		return this.getFloat(object);
	}

	public Float getFloat(Object obj) {
		Float f = null;
		int in = 0;
		if (obj.getClass() == Double.class || obj.getClass() == double.class) {
			f = f.valueOf(obj.toString()).floatValue();
		} else if (obj.getClass() == Long.class || obj.getClass() == long.class) {
			return f = (float) 0;
		} else if (obj.getClass() == String.class) {
			f = f.valueOf(String.valueOf(obj));
		}
		return f;
	}

	/**
	 * ���ַ������ת��������
	 * 
	 * @param obj
	 * @return
	 */
	public int paraGetInteger(Stack<OperatorData> para) {
		Object object;
		object = para.pop().value;
		return this.getInteger(object);
	}

	public int getInteger(Object obj) {
		Integer it = 0;
		if (obj.getClass() == Double.class || obj.getClass() == double.class) {
			String a = obj.toString();
			it = Double.valueOf(a).intValue();// ��double����ǿ��ת����int����
		} else if (obj.getClass() == String.class) {
			it = it.valueOf(String.valueOf(obj)).intValue();
		}
		return it;
	}

	/**
	 * ���ַ������ת���� 64λ������
	 * 
	 * @param obj
	 * @return
	 */
	public long paraGetLong(Stack<OperatorData> para) {
		Object object;
		object = para.pop().value;
		return this.getLong(object);
	}

	public long getLong(Object obj) {
		long l = 0;
		if (obj.getClass() == Double.class || obj.getClass() == double.class) {
			l = Double.valueOf(obj.toString()).longValue();
		} else if (obj.getClass() == String.class) {
			l = Long.valueOf(String.valueOf(obj));
		}
		return l;
	}

	/**
	 * ���ַ�ת������Ӧ�� 32λ����64λ������� 64 λ�����
	 * 
	 * @param str
	 * @return
	 */
	public String paraGetNumber(Stack<OperatorData> para) {
		String str = "";
		str = para.pop().value.toString();
		return this.getNumber(str);
	}

	public String getNumber(String str) {
		String result = "";
		try {
			NumberFormat nFormat = NumberFormat.getInstance();
			// ������������nFormat.setMaximumIntegerDigits(10);
			nFormat.setMaximumFractionDigits(10);// �������С���
			nFormat.setGroupingUsed(false);
			result = nFormat.format(new BigDecimal(str));
		} catch (Exception e) {

		}
		return result;
	}

	/**
	 * ������ת�����ַ��ͣ�ת������п��Խ��и�ʽ��
	 * 
	 * @param obj
	 * @param format
	 * @return
	 */
	public String paraGetStr(Stack<OperatorData> para) {
		Object object;
		String format = "";
		object = para.pop().value;
		if (!para.isEmpty()) {
			OperatorData od2 = para.pop();
			if (od2.clazz != NullRefObject.class) {
				format = od2.value.toString();
			}
		}
		return this.getStr(object, format);
	}

	public String getStr(Object obj, String format) {
		String formatResult = "";
		if (null != obj && null != format) {
			if (obj.getClass().getName().equals(int.class.getName())

			|| obj.getClass() == Integer.class || obj.getClass() == long.class
					|| obj.getClass() == Long.class
					|| obj.getClass() == byte.class
					|| obj.getClass() == Byte.class
					|| obj.getClass() == float.class
					|| obj.getClass() == Float.class
					|| obj.getClass() == double.class
					|| obj.getClass() == Double.class
					|| obj.getClass() == short.class
					|| obj.getClass() == Short.class) {

				// ��ʽ�����
				DecimalFormat nf = new DecimalFormat(format);
				formatResult = nf.format(obj);
			} else if (obj.getClass().getName().equals(Date.class.getName())) {
				// ��ʽ������
				SimpleDateFormat df = new SimpleDateFormat(format);
				return df.format(obj);
			} else {
				DateFormat dft = DateFormat.getDateInstance();
				Date date = null;
				try {
					date = dft.parse(String.valueOf(obj).toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				// ��ʽ������
				SimpleDateFormat df = new SimpleDateFormat(format);
				return df.format(date);
			}
		}
		return formatResult;
	}
	
	
	
	

}
