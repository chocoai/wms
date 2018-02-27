package com.xyy.bill.render.html;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyy.bill.meta.BillUIExpression;
import com.xyy.bill.meta.BillUIGrid;
import com.xyy.bill.meta.BillUIGrid.GridHead;
import com.xyy.bill.meta.BillUIGrid.HideAddRow;
import com.xyy.bill.meta.BillUIGrid.OptMode;
import com.xyy.bill.meta.BillUIPanel.FlexDirection;
import com.xyy.bill.meta.BillUIWidget;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;
import com.xyy.bill.spread.BillUIExpressionInstance;
import com.xyy.bill.spread.BillUIGridInstance;
import com.xyy.bill.spread.BillUIPanelInstance;
import com.xyy.bill.spread.BillUIWidgetInstance;
import com.xyy.erp.platform.common.tools.NumberUtil;
import com.xyy.util.Base64;
import com.xyy.util.StringUtil;

public class HtmlBillUIGridRender extends HTMLRender {

	public HtmlBillUIGridRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIGrid";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIGridInstance instance = (BillUIGridInstance) component;
		BillUIGrid widget = instance.getBillUIGrid();
		writer.writeBeginTag("div");
		this.doLayoutInParent(component);
		writer.writeProperty("opt-mode", widget.getOptMode()==OptMode.View?"view":"edit");
		writer.writeProperty("hide-add-row", widget.getHideAddRow()==HideAddRow.Show?"show":"hide");
		writer.writeProperty("select-mode", widget.getSelectMode().toString().toLowerCase());
		writer.writeProperty("table-key", widget.getDataTableKey());
		writer.writeProperty("row-id", widget.getRowId());
		if (!StringUtil.isEmpty(widget.getCalcButton())) {
			writer.writeProperty("calcButton", widget.getCalcButton());
		}
		if (!StringUtil.isEmpty(widget.getIsFull())) {
			writer.writeProperty("isfull", widget.getIsFull());
		}
		writer.writeProperty("table-type", widget.getTableType());
		// key--唯一识别key
		writer.writeProperty("key", widget.getKey());
		writer.writeProperty("renderId", writer.getRenderID());
		this.writerInstructAndSuperProperties(writer, widget);
		//输出GridTable的表头信息
		this.writerTableHead(writer, widget);
	}

	/**
	 * 输出表头部分的信息
	 * heads=[
	 * 	        ｛
	 * 			 Col：""，
	 * 			 Caption：""，	 * 			
	 * 			 Width：""，
	 * 			 Height：""， 
	 * 			 Order：""，
	 * 			 widget:""//base64
	 * 		｝
	 * 
	 * ]
	 * 
	 * 
	 * @param writer
	 * @param widget
	 */
	private void writerTableHead(HtmlWriter writer, BillUIGrid widget) {
		JSONArray result=new JSONArray();
		List<GridHead> heads=widget.getGridHeads();
		for(GridHead head:heads){
			JSONObject ho=new JSONObject();
			ho.put("col", head.getCol());
			ho.put("caption", head.getCaption());
			ho.put("width", head.getWidth());
			
			ho.put("align", head.getAlign());
			ho.put("height", head.getHeight());
			ho.put("order", head.getOrder());
			ho.put("bgcolor", head.getBgColor());
			ho.put("isShow", head.getShow());
			ho.put("formularMode", head.getFormulaMode());
			ho.put("formularType", head.getFormulaType());
			ho.put("colType", head.getColType());
			if(!StringUtil.isEmpty(head.getTargetView())){
				ho.put("relationQuery", "relationQuery");
				ho.put("targetView", head.getTargetView());
				ho.put("viewType", head.getViewType());
			}
			HtmlWriter widgetWriter=new HtmlWriter();
			widgetWriter.setRenderID(writer.getRenderID());
			HTMLDeviceContext context=new HTMLDeviceContext(widgetWriter);
			BillUIWidget headwidget=head.getWidget();
			if(headwidget!=null){
				BillUIWidgetInstance instance=new BillUIWidgetInstance(headwidget);
				context.render(instance);
			}
			BillUIExpression headExpr=head.getBillUIExpression();
			if(headExpr!=null){
				BillUIExpressionInstance instance=new BillUIExpressionInstance(headExpr);
				context.render(instance);
			}
			//渲染widget
			ho.put("widget", Base64.encoder(widgetWriter.toString()));	
			result.add(ho);
		}
		writer.writeProperty("heads",Base64.encoder(result.toJSONString()));
		
	}

	// bound,triggers,checkRules,init,formatters,properties
	private void writerInstructAndSuperProperties(HtmlWriter writer, BillUIGrid widget) {
		// 指令，属性标识性指令
		writer.writeProperty("BillUIGrid".toLowerCase(), null);

		// widget高级属性--triggers
		if (widget.getTriggers() != null && widget.getTriggers().size() > 0) {
			writer.writeProperty("triggers", Base64.encoder(widget.getTriggersJSONString()));
		}
		
		// widget高级属性--deltriggers
		if (widget.getDelTriggers() != null && widget.getDelTriggers().size()>0) {
			writer.writeProperty("deltriggers", Base64.encoder(widget.getDelTriggersJSONString()));
		}
		
		// widget高级属性--rules
		if (widget.getRules() != null && widget.getRules().size() > 0) {
			writer.writeProperty("rules", Base64.encoder(widget.getRulesJSONString()));
		}

		// widget高级属性--checkRules
		if (!StringUtil.isEmpty(widget.getRowCheckRule())) {
			writer.writeProperty("row-check-rule", Base64.encoder(widget.getRowCheckRule()));
		}
		
		// widget高级属性--sorts
		if (!StringUtil.isEmpty(widget.getSorts())){
			writer.writeProperty("sorts", Base64.encoder(widget.getSorts()));
		}
		
		// widget高级属性--doubleClickHandler
		if(widget.getDoubleClickHandlers() != null && widget.getDoubleClickHandlers().size() > 0){
			writer.writeProperty("double-click-handler", Base64.encoder(widget.getDoubleClickHandlerJSONString()));
		}
		
		// widget高级属性--onClickHandler
		if(widget.getOnClickHandlers() != null && widget.getOnClickHandlers().size() > 0){
			writer.writeProperty("on-click-handler", Base64.encoder(widget.getOnClickHandlerJSONString()));
		}
				
	}

	@Override
	protected void onRenderContent(IComponent component) {

	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		writer.writeEndTag("div");

	}

	/**
	 * 在父环境中布局我自己
	 * 
	 * @param component
	 */
	private void doLayoutInParent(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIGridInstance instance = (BillUIGridInstance) component;
		BillUIGrid widget = instance.getBillUIGrid();
		/*
		 * 查找我工作的容器上下文
		 */
		IContainer parentContainer = this.getContext().getNearestParentContainer(component);
		if (parentContainer instanceof BillUIPanelInstance) {// widget需要放置在面板中
			// 我先布局我自己
			BillUIPanelInstance parent = (BillUIPanelInstance) parentContainer;
			switch (parent.getBillUIPanel().getLayout()) {
			case flex:
				// flex-basis:80%;
				if (parent.getBillUIPanel().getFlexDirection() == FlexDirection.row) {
					writer.writeStyle("flex-basis", widget.getWidth() + "");
				} else {
					writer.writeStyle("flex-basis", widget.getHeight() + "");
				}
				break;
			case grid:
				// grid-column:1/ 5; grid-row: 1/3;
				int width = widget.getLeft() + Integer.parseInt(widget.getWidth());
				int height = widget.getTop() + Integer.parseInt(widget.getHeight());
				writer.writeStyle("grid-column", widget.getLeft() + "/" + width);
				writer.writeStyle("grid-row", widget.getTop() + "/" + height);
				break;
			case tab:
				// 控件不能放到tab布局中
				break;
			default:
				break;
			}
		}else{
			String width=widget.getWidth();
			if(width.endsWith("px") || width.endsWith("%")){
				writer.writeStyle("width",width);
			}else{
				Integer iWidth=NumberUtil.getIntegerValue(width);
				if(iWidth!=null){
					writer.writeStyle("width",iWidth+"px");
				}else{
					Float fWidth=NumberUtil.getFloatValue(width);
					if(fWidth!=null){
						writer.writeStyle("width",(fWidth*100)+"%");
					}else{
						writer.writeStyle("width","100%");
					}
				}
			}
			
			String height=widget.getHeight();
			if(height.endsWith("px") || height.endsWith("%")){
				writer.writeStyle("height",height);
			}else{
				Integer iHeight=NumberUtil.getIntegerValue(height);
				if(iHeight!=null){
					writer.writeStyle("height",iHeight+"px");
				}else{
					Float fHeight=NumberUtil.getFloatValue(height);
					if(fHeight!=null){
						writer.writeStyle("height",(fHeight*100)+"%");
					}else{
						writer.writeStyle("height","100%");
					}
				}
			}
			
			
		
		}

	}

}
