package com.xyy.erp.platform.common.tools;

/**
 * 字符串辅助类
 * 
 * @author evan
 *
 */
public class StringUtil {

	public static boolean isEmpty(String str) {
		return str == null || str.trim().equals("");
	}

	public static boolean isEmpty(String... str) {
		if (str == null || str.length <= 0) {
			return true;
		} else {
			for (String s : str) {
				if (s == null || s.trim().equals("")) {
					return true;
				}
			}
		}
		return false;
	}

	public static String stringForGet(String letter) {
		if (letter != null) {
			return new StringBuffer().append("get").append(letter.substring(0, 1).toUpperCase())
					.append(letter.substring(1)).toString();
		}
		return null;
	}

	public static String stringForSet(String letter) {
		if (letter != null) {
			return new StringBuffer().append("set").append(letter.substring(0, 1).toUpperCase())
					.append(letter.substring(1)).toString();
		}
		return null;
	}

	/**
	 * 处理Spilt函数类，封装方法
	 * 
	 * @author evan
	 */

	public static String[] ingnores_0 = { "[", "]" };

	public static String[] ingnores_1 = { "'", "'" };

	public static String filter_0(String str) {
		int _num = 0;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			String temp = "";
			if (i == str.length() - 1)
				temp = str.substring(i);
			temp = str.substring(i, i + 1);
			if (temp.equals(ingnores_0[0])) {
				_num++;
			}
			if (_num > 0 && temp.equals(";")) {
				temp = "$";
			}
			if (temp.equals(ingnores_0[1])) {
				_num--;
			}
			result.append(temp);
		}
		return result.toString();
	}

	public static String filter_1(String str) {
		int _num = 0;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			String temp = "";
			if (i == str.length() - 1)
				temp = str.substring(i);
			temp = str.substring(i, i + 1);
			if (temp.equals(ingnores_1[0])) {
				_num++;
			}
			if (_num != 0 && _num % 2 != 0 && temp.equals(";")) {
				temp = "$";
			}
			result.append(temp);
		}
		return result.toString();
	}

	/**
	 * 对Json字符串的格式化输出
	 * @param jsonStr
	 * @return
	 */
	public static String formatJson(String jsonStr) {
		if (null == jsonStr || "".equals(jsonStr))
			return "";
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		int indent = 0;
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			// 遇到{ [换行，且下一行缩进
			switch (current) {
			case '{':
			case '[':
				sb.append(current);
				sb.append('\n');
				indent++;
				addIndentBlank(sb, indent);
				break;
			// 遇到} ]换行，当前行缩进
			case '}':
			case ']':
				sb.append('\n');
				indent--;
				addIndentBlank(sb, indent);
				sb.append(current);
				break;
			// 遇到,换行
			case ',':
				sb.append(current);
				if (last != '\\') {
					sb.append('\n');
					addIndentBlank(sb, indent);
				}
				break;
			default:
				sb.append(current);
			}
		}

		return sb.toString();
	}

	/**
	 * 添加space
	 * 
	 * @param sb
	 * @param indent
	 */
	private static void addIndentBlank(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++) {
			sb.append('\t');
		}
	}

}
