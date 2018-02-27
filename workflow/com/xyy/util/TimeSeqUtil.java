package com.xyy.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeSeqUtil {
	private static SimpleDateFormat datetimeFormat = new SimpleDateFormat(
			"yyyyMMddHHmmssSSS");
	
	public static String getCompactDTSeq(){
		return datetimeFormat.format(new Date());
	}
	
	
	public static void main(String[] args){
		System.out.println(getCompactDTSeq());
	}
}


