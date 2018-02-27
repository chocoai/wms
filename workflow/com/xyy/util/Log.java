package com.xyy.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	public static boolean _debug=false;

	public static void log(String msg) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sb = new StringBuffer();
		sb.append("【").append(format.format(new Date())).append("】")
				.append(msg);
		FileWriter fw = null;
		try {
			fw = new FileWriter(getLogFile(), true);
			fw.write(sb.toString());
			fw.write("\n");
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void log(String msg, boolean debug) {
		if (_debug && debug) {
			log(msg);
		}
	}

	private static File getLogFile() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HH");
		StringBuffer sb = new StringBuffer();
		sb.append(System.getProperty("user.dir")).append("\\log\\")
				.append("log").append(format.format(new Date())).append(".log");
		File result = new File(sb.toString());
		if (!result.exists())
			try {
				result.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return result;
	}

}
