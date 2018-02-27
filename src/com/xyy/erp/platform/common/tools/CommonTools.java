package com.xyy.erp.platform.common.tools;

import java.security.MessageDigest;
import java.util.UUID;

import com.xyy.erp.platform.system.model.User;

import net.sourceforge.pinyin4j.PinyinHelper;





public class CommonTools {
	public static String[] codeArray = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
	public static String[] regionArray = new String[]{"主站","鄂西北","鄂东北","鄂东南","鄂西南","鄂中南"};
	/**
	 * 获取UUID
	 * 
	 * @return
	 */
	public static String getUUID() {
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

	public static String createInviteCode(String type) {
		String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String inviteCode = "";
		while (true) {
			inviteCode = "";
			for (int i = 0; i < 7; i++) {
				inviteCode += chars.charAt((int) (Math.random() * 62));
			}

			if ("1".equals(type)) {
				inviteCode = "P" + inviteCode;
			} else if ("2".equals(type)) {
				inviteCode = "D" + inviteCode;
			} else if ("3".equals(type)) {
				inviteCode = "T" + inviteCode;
			} else if ("4".equals(type)) {
				inviteCode = "X" + inviteCode;
			} else {
				inviteCode = "";
				break;
			}

			User user = User.dao.findFirst("select * from cdt_rj_user where inviteCode = ? ", inviteCode);
			if (user == null) {
				break;
			}
		}

		return inviteCode;
	}
	
	public static String generateInviteCode(String type) {
		String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String inviteCode = "";
		while (true) {
			inviteCode = "";
			for (int i = 0; i < 7; i++) {
				inviteCode += chars.charAt((int) (Math.random() * 62));
			}

			if ("1".equals(type)) {
				inviteCode = "P" + inviteCode;
			} else if ("2".equals(type)) {
				inviteCode = "D" + inviteCode;
			} else if ("3".equals(type)) {
				inviteCode = "T" + inviteCode;
			} else if ("4".equals(type)) {
				inviteCode = "X" + inviteCode;
			} else {
				inviteCode = "";
				break;
			}

//			Invite invite = Invite.dao.findFirst("select * from cdt_rj_invite where simpleCode = ? ", inviteCode);
//			if (invite == null) {
//				break;
//			}
		}

		return inviteCode;
	}
	
	public static String createNickName() {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String inviteCode = "";
		for (int i = 0; i < 4; i++) {
			inviteCode += chars.charAt((int) (Math.random() * 52));
		}
		return inviteCode;
	}
	
	// 返回中文的首字母  
	public static String getPinYinHeadChar(String str) {
		String convert = "";
		char word = str.charAt(0);
		String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
		if (pinyinArray != null) {
			convert += pinyinArray[0].charAt(0);
		} else {
			convert += word;
		}
		return convert.toUpperCase();
	}
	
	public static String getPinYinChar(String str) {
		String convert = "";
		for(int i=0;i<str.length();i++){
			char word = str.charAt(i);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert.toUpperCase();
	}
	
}
