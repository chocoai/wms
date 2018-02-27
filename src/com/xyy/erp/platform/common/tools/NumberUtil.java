package com.xyy.erp.platform.common.tools;

public class NumberUtil {
		public static Integer getIntegerValue(String value){
			try {
				return Integer.parseInt(value);
				
			} catch (Exception e) {
				return null;
			}
		}
		
		
		public static Float getFloatValue(String value){
			try {
				return Float.parseFloat(value);
				
			} catch (Exception e) {
				return null;
			}
		}
		
		
		public static Double getDoubleValue(Object value){
			try {
				return Double.parseDouble(value.toString());
				
			} catch (Exception e) {
				return null;
			}
		}
}
