package com.xyy.bill.print.html;

import com.xyy.bill.print.meta.PageSetting;
import com.xyy.bill.print.meta.PrintHead;
import com.xyy.bill.print.spread.PrintBodyInstance;
import com.xyy.bill.print.spread.PrintInstance;
import com.xyy.bill.render.IComponent;
import com.xyy.util.StringUtil;

public class BillPrintBodyRender extends PrintRender {

	public BillPrintBodyRender(PrintDevice context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "PrintBody";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		PrintWriter writer=	this.getContext().getWriter();
		writer.writeBeginTag("div");
		PrintBodyInstance body=(PrintBodyInstance)component;
		PrintInstance printInstance=(PrintInstance)body.getParent();
		PrintHead head=printInstance.getPrint().getHead();
		PageSetting pageSetting=head.getPageSetting();
		
		//paddign and margin
		writer.writeStyle("margin", "0px");		
		StringBuffer padding=new StringBuffer("0mm 0mm 0mm 0mm");
		if(pageSetting!=null){
			String margin=pageSetting.getPageMargin();
			if(!StringUtil.isEmpty(margin)){
				String[] aPadding=margin.split(",");
				if(aPadding.length==4){
					padding.delete(0, padding.length());
					for(String p:aPadding){
						padding.append(p).append("mm ");
					}
				}
			}
		}
		writer.writeStyle("padding", padding.toString());
		
		//width and height
		String pageSize=pageSetting.getPageSize();
		if(StringUtil.isEmpty(pageSize)){
			writer.writeStyle("width", "210mm");
			writer.writeStyle("height", "297mm");
		}else{
			String[] size=pageSize.split(",");
			if(size.length==2){
				writer.writeStyle("width", size[0]+"mm");
				writer.writeStyle("height",size[1]+"mm");
			}else{
				writer.writeStyle("width", "210mm");
				writer.writeStyle("height", "297mm");
			}
		}
	}

	@Override
	protected void onRenderContent(IComponent component) {
		
	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		PrintWriter writer=	this.getContext().getWriter();
		writer.writeEndTag("div");
	}

}
