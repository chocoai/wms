package com.xyy.erp.platform.common.tools;

import java.security.MessageDigest;
import java.util.UUID;

public class UUIDUtil {
	/**
	 * 获取UUID
	 * 
	 * @return
	 */
	public static String newUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.substring(0, 8) + uuid.substring(9, 13)
				+ uuid.substring(14, 18) + uuid.substring(19, 23)
				+ uuid.substring(24);
	}

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
	
	
	

}
