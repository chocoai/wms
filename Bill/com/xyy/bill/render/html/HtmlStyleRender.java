package com.xyy.bill.render.html;

import com.xyy.bill.meta.Style;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.spread.StyleInstance;
import com.xyy.util.StringUtil;

public class HtmlStyleRender extends HTMLRender {

	public HtmlStyleRender(HTMLDeviceContext context) {
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
		HtmlWriter writer=this.getContext().getWriter();
		Style style=styleInstance.getStyle();
		if(!StringUtil.isEmpty(style.getKey())){//非空，才处理
			StringBuffer sb=new StringBuffer();
			sb.append(".").append(style.getKey()).append("{").append(style.toString()).append("}");
			writer.registerEmbedCssBlock(sb.toString());
		}
	}

	@Override
	protected void onRenderEndTag(IComponent component) {

	}

}
