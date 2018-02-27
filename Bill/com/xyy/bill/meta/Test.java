package com.xyy.bill.meta;


import com.xyy.bill.map.BillMap;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentReader;
import com.xyy.util.xml.ComponentWriter;

public class Test {

	public static void main(String[] args) {
		
		ComponentReader reader=new ComponentReader(BillMap.class);
		ComponentWriter cw=new ComponentWriter(BillMap.class);
		try {
			org.jdom.Element root=XMLUtil.readXMLFile("h:/map1.xml");
			BillMap map=(BillMap)reader.readXML(root);
			XMLUtil.save(cw.writeXML(map), "h:/map2.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
