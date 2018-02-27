package com.xyy.bill.instance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Test {
	private static final Pattern PAT_SQL = Pattern.compile("\\$\\{.*?\\}");
	private static final Pattern REF_PAT_SQL=Pattern.compile("@\\w*");

	public static void main(String[] args) {

		ParamSQL result = new ParamSQL();
		String sql = "select * | from ${abc+2} where @abc @mm order by ${abc+2-1}";
		Matcher matcher = REF_PAT_SQL.matcher(sql);
		while (matcher.find()) {
			String matcherStr = matcher.group();
			System.out.println(matcherStr);
			sql = sql.replace(matcherStr, "?");
			
		}
		System.out.println(sql);
	}
}
