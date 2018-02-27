package com.xyy.bill.map;

import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentReader;
import com.xyy.util.xml.ComponentWriter;

public class Test {

	public static void main(String[] args) {
		try {
			ComponentReader cr=new ComponentReader(FeedbackObject.class);
			FeedbackObject fo=(FeedbackObject)cr.readXML(XMLUtil.readXMLFile(Test.class.getResource("aa.xml")));
			
			ComponentWriter cw=new ComponentWriter(FeedbackObject.class);
			XMLUtil.save(cw.writeXML(fo), "d:/test.xml");
			
			System.out.println(fo.getFeedbackTables().size());
		} catch (Exception e) {
			// TODO: handle exception
		}
		

	}

}
