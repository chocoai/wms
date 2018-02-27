package com.xyy.bill.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class SysSelectCachePlug implements IPlugin {

	private final static Map<String, String> sysSelectMap = new HashMap<>();

	public static String getValue(String type, String key) {
		String result="";
      for(String strKey:sysSelectMap.keySet()){
			if ((type + "^" + key).equals(strKey)) {
    		  result=sysSelectMap.get(strKey);
    		  break;
    	  }
      }
      return result;
	}
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		try {
		List<Record> records = Db.find("select * from tb_sys_select");
		for(Record r:records){
			sysSelectMap.put(r.getStr("type") + "^" + r.getInt("key"), r.getStr("value"));
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		sysSelectMap.clear();
		return true;
	}

}
