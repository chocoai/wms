package com.xyy.bill.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.attribute.FileOwnerAttributeView;

import org.jdom.Element;

import com.xyy.bill.meta.BillFormMeta;
import com.xyy.bill.render.html.HTMLDeviceContext;
import com.xyy.bill.spread.BillFormInstance;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentReader;

public class test {

	public static void main(String[] args) {
		Element root = XMLUtil.readXMLFile("h:/bill.xml");
		ComponentReader cr = new ComponentReader(BillFormMeta.class);
		try {
			BillFormMeta result = (BillFormMeta) cr.readXML(root);
			BillFormInstance instance=new BillFormInstance(result);
			HTMLDeviceContext context = new HTMLDeviceContext();
			context.render(instance);
	
			FileOutputStream fos=new FileOutputStream(new File("E:\\xyyerp_workspace\\XYYErp\\WebRoot\\test.html"));
			fos.write(context.getWriter().toHtml().getBytes());
			
			//System.out.println(context.getWriter().toHtml());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
