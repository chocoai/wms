package com.xyy.http.services;

import java.io.File;
import java.util.Properties;

import com.xyy.http.service.Service;
import com.xyy.http.util.Envelop;

public class ReportFileMonitorService implements Service {

	private static final String PRINT_REPORT_PATH = getRealPath() + "/";

	@Override
	public Envelop service(Envelop request) throws Exception {
		// TODO Auto-generated method stub
		Envelop result = new Envelop();
		String name = "";
		String reportXmlName = request.getBody().getString("reportXmlName");
		File file = new File(PRINT_REPORT_PATH);
		for (File f : file.listFiles()) {
			String str = f.getName().replaceAll(".xml", "");
			if (str.split("\\^")[0].equals(reportXmlName)) {
				// result.getBody().put("createdReportName", name);
				name = str;
				break;
			}
		}
		result.getBody().put("createdReportName", name);
		return result;
	}

	public static void main(String[] args) {
		System.out.println(PRINT_REPORT_PATH);
		File file = new File(PRINT_REPORT_PATH);
		for (File f : file.listFiles()) {
			System.out.println(f.getName().replaceAll(".xml", ""));
		}
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

	public static String getRealPath() {
		// 查询路径
		//String classPath = ClassLoader.getSystemResource("").toString();
		String classPath =ReportFileMonitorService.class.getClassLoader().getResource("").toString(); 
		int idx = classPath.indexOf("/WEB-INF/");
		String result = classPath.substring(6, idx + 1) + "printjob";
		if (isOSLinux()) {
			
			result = "/" + result;
		}
		return result;
	}
}
