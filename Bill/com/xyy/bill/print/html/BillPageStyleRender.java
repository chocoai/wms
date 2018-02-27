package com.xyy.bill.print.html;


import com.xyy.bill.print.meta.Style;
import com.xyy.bill.print.meta.Style.Type;
import com.xyy.bill.print.spread.StyleInstance;
import com.xyy.bill.render.IComponent;
import com.xyy.util.StringUtil;

public class BillPageStyleRender extends PrintRender {

	public BillPageStyleRender(PrintDevice context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "Style";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {

	}

	@Override
	protected void onRenderContent(IComponent component) {
		StyleInstance styleInstance=(StyleInstance)component;
		//准备渲染样式
		PrintWriter writer=this.getContext().getWriter();
	    Style style=styleInstance.getStyle();
		if(!StringUtil.isEmpty(style.getKey())){//非空，才处理
			StringBuffer sb=new StringBuffer();
			if(style.getType()==Type.Class){
				sb.append(".").append(style.getKey()).append("{").append(style.toCssString()).append("}");
			}else{
				sb.append(style.getKey()).append("{").append(style.toCssString()).append("}");
			}
			
			writer.registerEmbedCssBlock(sb.toString());
		}
	}

	@Override
	protected void onRenderEndTag(IComponent component) {

	}

}
