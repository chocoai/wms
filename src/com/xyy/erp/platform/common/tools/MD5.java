package com.xyy.erp.platform.common.tools;

import java.security.MessageDigest;

public class MD5 {
	public static String encodeByMD5(String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] b = md.digest(str.getBytes("utf-8"));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			String s = Integer.toHexString(b[i] & 0xFF);
			if (s.length() == 1) {
				sb.append("0");
			}

			sb.append(s.toUpperCase());
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(MD5.encodeByMD5("000000"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
