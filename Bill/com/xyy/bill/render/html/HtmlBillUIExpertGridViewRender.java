package com.xyy.bill.render.html;

import com.xyy.bill.meta.BillUIExpertGridView;
import com.xyy.bill.meta.BillUIExpertGridView.Cell;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.spread.BillUIChartInstance;
import com.xyy.bill.spread.BillUIExpertGridViewInstance;

public class HtmlBillUIExpertGridViewRender extends HTMLRender {

	public HtmlBillUIExpertGridViewRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIExpertGridView";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		HtmlWriter writer=this.getContext().getWriter();
		BillUIExpertGridViewInstance instance=(BillUIExpertGridViewInstance)component;
		BillUIExpertGridView widget=instance.getBillUIExpertGridView();
		writer.writeBeginTag("table");
		writer.writeProperty("BillUIExpertGridView", "BillUIExpertGridView");
		writer.writeProperty("Key", widget.getKey());
		writer.writeProperty("DefaultColWidth", widget.getDefaultColWidth());
		writer.writeProperty("DefaultRowHeigh", widget.getDefaultRowHeigh());
		writer.writeProperty("Height", widget.getHeight());
		writer.writeProperty("Width", widget.getWidth());
		writer.writeProperty("Left", widget.getLeft());
		writer.writeProperty("Top", widget.getTop());	
		writer.writeProperty("class", widget.getS());
	}

	@Override
	protected void onRenderContent(IComponent component) {
		HtmlWriter writer=this.getContext().getWriter();
		BillUIExpertGridViewInstance instance=(BillUIExpertGridViewInstance)component;
		BillUIExpertGridView widget=instance.getBillUIExpertGridView();
		for(Cell cell:widget.getCells()){
			writer.writeBeginTag("cell");			
			writer.writeProperty("r", cell.getR());
			writer.writeProperty("c",cell.getC() );
			writer.writeProperty("rs", cell.getRs());
			writer.writeProperty("cs",cell.getCs() );
			writer.writeProperty("expand",cell.getExpand() );
			writer.writeProperty("lefthead", cell.getLeftHead());
			writer.writeProperty("tophead", cell.getTopHead());
			writer.writeProperty("InheritParentCondition", cell.getInheritParentCondition());
			writer.writeProperty("O", cell.getO());
			writer.writeProperty("class", cell.getS());
			writer.writeBeginTag("chart");
			if(cell.getChart()!=null){
				this.getContext().render(new BillUIChartInstance(cell.getChart()));
			}
			writer.writeEndTag("chart");
			writer.writeEndTag("cell");
		}
	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer=this.getContext().getWriter();
		writer.writeEndTag("table");
	}

}
