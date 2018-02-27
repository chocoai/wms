package com.xyy.bill.util;

import java.util.Date;

import com.xyy.erp.platform.common.tools.TimeUtil;


public class SequenceBuilder {
	/**
	 * 生成序列：
	 * 
	 * @param type
	 *            :即表名
	 * @param prefix
	 *            ：序列前缀
	 * @return
	 */
	public static String nextSequence(String type, String prefix, int length) {
		String water;
		try {
			water = generatorCode(type, length);
//			return water == null ? null : prefix
//					+ DateTimeUtil.formatDateWithoutLine(new Date()) + water;
			return water == null ? null : prefix
					+ TimeUtil.format(new Date(), "yyMMdd") + water;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String preSequence(String type, String prefix, int length) {
		String water;
		try {
			water = pregeneratorCode(type, length);
//			return water == null ? null : prefix
//					+ DateTimeUtil.formatDateWithoutLine(new Date()) + water;
			return water == null ? null : prefix
					+ TimeUtil.format(new Date(), "yyMMdd") + water;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 生成对应的流水 cdt_sequence_loanApply：借款申请 cdt_sequence_loanOrder：借款项目
	 * cdt_sequence_investOrder：投资项目
	 * 
	 * @return
	 * @throws Exception
	 */
	private static String generatorCode(String type, int length) throws Exception {
		return getSequenceString(SequenceManager.getInstance().nextSequence(
				type), length);

	}

	private static String pregeneratorCode(String type, int length) throws Exception {
		return getSequenceString(SequenceManager.getInstance().preSequence(
				type), length);

	}

	private static String getSequenceString(long cur, int length) {
		String result = Long.toString(cur);
		StringBuffer sb = new StringBuffer();
		if (result.length() < length - 4) {
			for (int i = 0; i < length - 4 - result.length(); i++) {
				sb.append("0");
			}
		}
		sb.append(result);
		return sb.toString();
	}
}
