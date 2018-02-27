package com.xyy.util;

import java.util.regex.Pattern;

public class ValidateUtil {

	public static boolean validateRegulars(String regular, String value) {
		return Pattern.matches(regular, value);
	}

	/**
	 * NotNull:非空 LEN(30,50):长度大于30，小于50 RANG(30,50)，范围在30~50之间 EMAIL：电子邮件
	 * WEBURL:网址 IDCARD:身份证号 IDCARD2：老版本身份证号 TELEPHONE：固定电话号码 MOBILE：移动电话号码
	 * NUMBER：数字格式验证 INTEGER: 整数格式验证
	 */
	public static boolean validate(String expr, String value) {
		if (expr.toLowerCase().equals("notnull")) {
			return validateNotNull(value);
		} else if (expr.toLowerCase().startsWith("len(")) {
			int min = Integer.parseInt(expr.substring(expr.indexOf("(") + 1,
					expr.indexOf(",")).trim());
			int max = Integer.parseInt(expr.substring(expr.indexOf(",") + 1,
					expr.indexOf(")")).trim());
			return validateLen(min, max, value);
		} else if (expr.toLowerCase().startsWith("rang(")) {
			int min = Integer.parseInt(expr.substring(expr.indexOf("(") + 1,
					expr.indexOf(",")).trim());
			int max = Integer.parseInt(expr.substring(expr.indexOf(",") + 1,
					expr.indexOf(")")).trim());
			return validateRang(min, max, value);
		} else if (expr.toLowerCase().equals("email")) {
			return validateEmail(value);
		} else if (expr.toLowerCase().equals("weburl")) {
			return validateWeburl(value);
		} else if (expr.toLowerCase().equals("idcard")) {
			return validateIdcard(value);
		} else if (expr.toLowerCase().equals("idcard2")) {
			return validateIdcard2(value);
		} else if (expr.toLowerCase().equals("telephone")) {
			return validateTelephone(value);
		} else if (expr.toLowerCase().equals("mobile")) {
			return validateMobile(value);
		} else if (expr.toLowerCase().equals("number")) {
			return validateNumber(value);
		} else if (expr.toLowerCase().equals("integer")) {
			return validateInteger(value);
		}
		return false;
	}

	private static boolean validateInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private static boolean validateNumber(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private static boolean validateMobile(String value) {
		return Validator.isMobile(value);
	}

	private static boolean validateTelephone(String value) {
		return true;
	}

	private static boolean validateIdcard2(String value) {
		return Validator.isIDCard(value);
	}

	private static boolean validateIdcard(String value) {
		return Validator.isIDCard(value);
	}

	private static boolean validateWeburl(String value) {
		return Validator.isUrl(value);
	}

	private static boolean validateEmail(String value) {
		return Validator.isEmail(value);
	}

	private static boolean validateRang(int min, int max, String value) {
		try {
			Double d = Double.parseDouble(value);
			return d >= min && d <= max;
		} catch (Exception ex) {
			return false;
		}
	}

	private static boolean validateLen(int minLen, int maxLen, String value) {
		if (StringUtil.isEmpty(value)) {
			return false;
		}
		return value.length() >= minLen && value.length() <= maxLen;
	}

	private static boolean validateNotNull(String value) {
		return !StringUtil.isEmpty(value);
	}

}
