package com.xyy.bill.util;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class SelectTypeUtil {
	public static String getValueByKey(String type,String name){
		String sql="select * from tb_sys_select a where a.type=? and a.key=?";
		Record res=Db.findFirst(sql,type,name);
		if(res==null){
			return null;
		}
		return res.getStr("value");
	}
	
	public static String getKeyByValue(String type,String value){
		String sql="select * from tb_sys_select a where a.type=? and a.value=?";
		Record res=Db.findFirst(sql,type,value);
		if(res==null){
			return null;
		}
		return res.getStr("key");
		
	}
}
