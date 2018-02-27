package com.xyy.bill.util;

import java.util.Properties;

public class WebPathUtil {
	public static String getPrintJobPath() {
		// 查询路径
		String classPath = WebPathUtil.class.getResource("/").toString();
		int idx = classPath.indexOf("/WEB-INF/");
		String result = classPath.substring(6, idx + 1) + "printjob/";
		if (isOSLinux()) {
			result = "/" + result;
		}
		return result;
	}
	
	
	public static String getWebRootPath() {
		// 查询路径
		String classPath = WebPathUtil.class.getResource("/").toString();
		int idx = classPath.indexOf("/WEB-INF/");
		String result = classPath.substring(6, idx + 1);
		if (isOSLinux()) {
			result = "/" + result;
		}
		return result;
	}
	
	/**
	 * 判断当前的操作系统
	 * 
	 * @return
	 */
	public static boolean isOSLinux() {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public static void main(String[] args){
		System.out.println(getPrintJobPath());
		System.out.println(getWebRootPath());
	} 
}
