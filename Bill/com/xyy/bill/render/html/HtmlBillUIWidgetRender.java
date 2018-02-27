package com.xyy.bill.render.html;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.xyy.bill.meta.BillUIPanel.FlexDirection;
import com.xyy.bill.meta.BillUIWidget;
import com.xyy.bill.meta.Property;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;
import com.xyy.bill.spread.BillUIPanelInstance;
import com.xyy.bill.spread.BillUIWidgetInstance;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.util.Base64;
import com.xyy.util.StringUtil;

public class HtmlBillUIWidgetRender extends HTMLRender {

	public HtmlBillUIWidgetRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIWidget";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		// 输出我自己
		try {
			this.renderUIWidget(component);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onRenderContent(IComponent component) {

	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		// 其次输出我自己
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		switch (widget.getWidgetType()) {
		case BillUIString:// 字符串
		case BillUIInteger:// 整形
		case BillUILong:// 长整型
		case BillUIDecimal:// 小数
		case BillUIExpression:	
		case BillUIBarcode:
			writer.writeEndTag();
			if(!StringUtil.isEmpty(widget.getUnit())){
				writer.writeBeginTag("div");
				writer.writeProperty("class", "input-group-addon");
				writer.writeText(widget.getUnit());
				writer.writeEndTag("div");
			}
			writer.writeEndTag("div");
			break;
		case BillUITextArea:
			writer.writeEndTag("textarea");
			writer.writeEndTag("div");
			break;
		case BillUIRichText:
			writer.writeEndTag("div");
			break;
		case BillUIDateTime:
			writer.writeEndTag();
			writer.writeEndTag("div");
			break;
		case BillUIImage:
			writer.writeEndTag();
			break;
		case BillUIQRCode:

			break;
		case BillUISelect:
			writer.writeEndTag("div");
			writer.writeEndTag("div");
			break;
		case BillUIPopupWindow:
			writer.writeEndTag();
			writer.writeEndTag("div");
			break;
		case BillUILabel:
			writer.writeEndTag("label");
			break;
		case BillUIVideo:
			writer.writeEndTag("video");
			break;
		case BillUISound:
			writer.writeEndTag("audio");
			break;
		case BillUILink:
			break;
		case BillUIView:
			writer.writeEndTag("div");
			break;
		case BillUIButton:
			writer.writeEndTag("button");
			break;
		case BillUITree:
			writer.writeEndTag("div");
			break;
		case BillUIFormula:
			break;
		case BillUICheckbox:
			writer.writeEndTag("div");
			writer.writeEndTag("div");
			break;
		case BillUIRadio:
			writer.writeEndTag("div");
			writer.writeEndTag("div");
			break;
		case BillUITemplate:
			writer.writeEndTag("div");
			break;
		case BillUIButtonGroup:
			writer.writeEndTag("div");
		case BillUIAttachment:
			writer.writeEndTag("div");
		default:
			break;
		}
	}

	/**
	 *
	 * @param component
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	private void renderUIWidget(IComponent component) throws UnsupportedEncodingException {
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		switch (widget.getWidgetType()) {
		case BillUIString:// 字符串
		case BillUIExpression:
			this.doRenderInputWidget(component);
			break;
		case BillUIInteger:// 整形
		case BillUILong:// 长整型
		case BillUIDecimal:// 小数
			this.doRenderInputNumberWidget(component);
			break;
		case BillUITextArea:
			this.doRenderTextAreaWidget(component);
			break;
		case BillUIRichText:
			this.doRenderRichTextWidget(component);
			break;
		case BillUIDateTime:
			this.doRenderDateTimeWidget(component);
			break;
		case BillUIImage:
			this.doRenderImageWidget(component);
			break;
		case BillUIBarcode:
			this.doRenderBarcodeWidget(component);
			break;
		case BillUIQRCode:
			this.doRenderQRCodeWidget(component);
			break;
		case BillUISelect:
			this.doRenderSelectWidget(component);
			break;
		case BillUIPopupWindow:
			this.doRenderPopupWindowWidget(component);
			break;
		case BillUILabel:
			this.doRenderLabelWidget(component);
			break;
		case BillUIVideo:
			this.doRenderVideoWidget(component);
			break;
		case BillUISound:
			this.doRenderSoundWidget(component);
			break;
		case BillUILink:
			this.doRenderLinkWidget(component);
			break;
		case BillUIView:
			this.doRenderViewWidget(component);
			break;
		case BillUIButton:
			this.doRenderButtonWidget(component);
			break;
		case BillUITree:
			this.doRenderTreeWidget(component);
			break;
		case BillUIFormula:
			this.doRenderFormualWidget(component);
			break;
		case BillUICheckbox:
			this.doRenderCheckboxWidget(component);
			break;
		case BillUIRadio:
			this.doRenderRadioWidget(component);
			break;
		case BillUITemplate:
			this.doRenderBillUITemplateWidget(component);
			break;
		case BillUIButtonGroup:
			this.doRenderButtonGroupWidget(component);
			break;
		case BillUIAttachment:
			this.doRenderBillUIAttachment(component);
			break;
		default:
			break;
		}

	}

	private void doRenderBillUIAttachment(IComponent component) throws UnsupportedEncodingException {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		
		writer.writeBeginTag("div");
		writer.writeProperty("billuiattachment", null);
		writer.writeProperty("files", "main.myfile");
		writer.writeProperty("count", 10);
		
		this.doLayoutInParent(component);// 渲染布局指令

		writer.writeProperty("key", widget.getKey());
		writer.writeProperty("renderId", writer.getRenderID());

		if(PropertiesPlugin.getParamMapValue(DictKeys.config_urlPath) != null){
			writer.writeProperty("url", Base64.encoder(PropertiesPlugin.getParamMapValue(DictKeys.config_urlPath).toString()));
		}

		if (null != widget.getDisable()) {
			writer.writeProperty("disabled", "true");
		}
		if (null != widget.getParseExcel()) {
			writer.writeProperty("parseexcel", "true");
		}
		if (!StringUtil.isEmpty(widget.getTempletUrl())) {
			writer.writeProperty("templetUrl", widget.getTempletUrl());
		}
		
		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);
	}

	/**
	 * <div class="btn-group" role="group">
			<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			  Dropdown
			  <span class="caret"></span>
			</button>
		    <ul class="dropdown-menu">
		      <li><a href="#">Dropdown link</a></li>
		      <li><a href="#">Dropdown link</a></li>
		    </ul>
	  </div>
	  <BillUIWidget Key="submit" Caption="提交" Src="" WidgetType="BillUIButtonGroup">
	  	<Properties>
	  		<Property Name="">
	  			1
	  		</Property>
	  		<Property Name="">
	  			2
	  		</Property>
	  	</Properties>
	  	<Triggers>
			<Trigger>
				1
			</Trigger>
			<Trigger>
				2
			</Trigger>
		</Triggers>
	  </BillUIWidget>
	 * 按钮组控件
	 * @param component
	 */
	private void doRenderButtonGroupWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		writer.writeBeginTag("div");
		if(!StringUtil.isEmpty(widget.getAuxiliaryDir())){
			writer.writeProperty(widget.getAuxiliaryDir(), null);
		}
		if(!StringUtil.isEmpty(widget.getSrc())){
			writer.writeProperty("src", widget.getSrc());
		}
		// key--唯一识别key
		writer.writeProperty("key", widget.getKey());
		writer.writeProperty("renderId", writer.getRenderID());
		writer.writeProperty("class", "btn-group");
		writer.writeProperty("role", "group");
		
		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);
		
		this.writeButtonMenu(writer, widget);
		this.wirteDropdownMenu(writer, widget);
		
	}

	private void wirteDropdownMenu(HtmlWriter writer, BillUIWidget widget) {
		/**
		 *  <ul class="dropdown-menu">
		      <li><a href="#">Dropdown link</a></li>
		      <li><a href="#">Dropdown link</a></li>
			</ul>
		 */
		writer.writeBeginTag("ul");
		writer.writeProperty("class", "dropdown-menu");
		
		List<Property> propertyList = widget.getProperties();
		if(propertyList != null && propertyList.size() > 0){
			for (int i=0;i<propertyList.size();i++) {
				writer.writeBeginTag("li");
				writer.writeProperty("index", i);
				writer.writeBeginTag("a");
				writer.writeProperty("href", propertyList.get(i).getVal());
				writer.writeText(propertyList.get(i).getName());
				writer.writeEndTag("a");
				writer.writeEndTag("li");
			}
		}
		
		writer.writeEndTag("ul");
	}

	private void writeButtonMenu(HtmlWriter writer, BillUIWidget widget) {
		writer.writeBeginTag("button");
		writer.writeProperty("type", "button");
		writer.writeProperty("class", "btn btn-primary dropdown-toggle");
		writer.writeProperty("data-toggle", "dropdown");
		writer.writeProperty("aria-haspopup", "true");
		writer.writeProperty("aria-expanded", "false");
		
		if(!StringUtil.isEmpty(widget.getCaption())){
			writer.writeText(widget.getCaption());
		}else{
			writer.writeText("关联生成");
		}
		
		//<span class="caret"></span>
		writer.writeBeginTag("span");
		writer.writeProperty("class", "caret");
		writer.writeEndTag("span");
		
		writer.writeEndTag("button");
	}

	private void doRenderBillUITemplateWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		writer.writeBeginTag("div");
		writer.writeProperty(widget.getWidgetType().toString().toLowerCase(), null);

	}

	private void doRenderRadioWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		this.doLayoutInParent(component);// 渲染布局指令
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			if (!StringUtil.isEmpty(widget.getRequired())) {
				writer.writeBeginTag("span");
				writer.writeProperty("class", "text-error");
				
				writer.writeText("*");
				writer.writeEndTag("span");
			}
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}
		writer.writeBeginTag("div");
		
		if(!StringUtil.isEmpty(widget.getS())){
			writer.writeProperty("class", widget.getS());
		}else{
			writer.writeProperty("class","form-control");
		}
		writer.writeProperty("source-src", widget.getSrc());

		writer.writeProperty("key", widget.getKey());
		if (!StringUtil.isEmpty(widget.getRequired())) {
			writer.writeProperty("required", widget.getRequired());
		}
		writer.writeProperty("renderId", writer.getRenderID());
		if (null != widget.getDisable()) {
			writer.writeProperty("disabled", "true");
		}
		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);
	}

	private void doRenderCheckboxWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		writer.writeProperty("table-key", widget.getDataTableKey());
		this.doLayoutInParent(component);// 渲染布局指令
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			if (!StringUtil.isEmpty(widget.getRequired())) {
				writer.writeBeginTag("span");
				writer.writeProperty("class", "text-error");
				
				writer.writeText("*");
				writer.writeEndTag("span");
			}
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}
		writer.writeBeginTag("div");
		
		if(!StringUtil.isEmpty(widget.getS())){
			writer.writeProperty("class", widget.getS());
		}else{
			writer.writeProperty("class","form-control");
		}

		writer.writeProperty("source-src", widget.getSrc());

		writer.writeProperty("k", widget.getK());
		writer.writeProperty("v", widget.getV());

		writer.writeProperty("key", widget.getKey());
		if (!StringUtil.isEmpty(widget.getRequired())) {
			writer.writeProperty("required", widget.getRequired());
		}
		writer.writeProperty("renderId", writer.getRenderID());
		if (null != widget.getDisable()) {
			writer.writeProperty("disabled", "true");
		}
		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);
	}

	private void doRenderTreeWidget(IComponent component) {
		// widget基础属性
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		writer.writeBeginTag("div");
		writer.writeProperty("billuitree", null);
		writer.writeProperty("key", widget.getKey());
		writer.writeProperty("id", widget.getKey());
		writer.writeProperty("table-key", widget.getDataTableKey());
		writer.writeProperty("class", "ztree");
		writer.writeProperty("treemodel", "treemodel");
		writer.writeProperty("treeoptions", "treeoptions");
		writer.writeProperty("multiselect", widget.getMultiselect());
		writer.writeProperty("renderId", writer.getRenderID());
		this.doLayoutInParent(component);

		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);
	}

	private void doRenderButtonWidget(IComponent component) {
		// widget基础属性
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		/*
		 * <!-- 提供额外的视觉效果，标识一组按钮中的原始动作 --> <button type="button" class=
		 * "btn btn-primary">原始按钮</button>
		 */

		writer.writeBeginTag("button");

		this.doLayoutInParent(component);
		if (!StringUtil.isEmpty(widget.getS())) {
			writer.writeProperty("class", widget.getS());
		} else {
			writer.writeProperty("class", "btn btn-primary");
		}
		
		if(!StringUtil.isEmpty(widget.getAuxiliaryDir())){
			writer.writeProperty(widget.getAuxiliaryDir(), null);
		}
		
		if(!StringUtil.isEmpty(widget.getSrc())){
			writer.writeProperty("src", widget.getSrc());
		}

		if (!StringUtil.isEmpty(widget.getPagging())) {
			writer.writeProperty("pagging", widget.getPagging());
		}
		if (!StringUtil.isEmpty(widget.getFileName())) {
			writer.writeProperty("fileName", widget.getFileName());
		}
		// key--唯一识别key
		writer.writeProperty("key", widget.getKey());
		writer.writeProperty("renderId", writer.getRenderID());
		if (null != widget.getDisable()) {
			writer.writeProperty("disabled", "true");
		}

		if (!StringUtil.isEmpty(widget.getBillUIPopupWindow())) {
			writer.writeProperty("BillUIPopupWindow", "true");
		}

		this.writerInstructAndSuperProperties(writer, widget);

		if (!StringUtil.isEmpty(widget.getIcon())) {
			writer.writeBeginTag("i");
			writer.writeProperty("class", widget.getIcon());
			writer.writeEndTag("i");
		}

		writer.writeText(widget.getCaption() == null ? "" : widget.getCaption());

	}

	private void doRenderViewWidget(IComponent component) {
		// widget基础属性
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		writer.writeBeginTag("div");
		// key--唯一识别key

		writer.writeProperty("key", widget.getKey());
		writer.writeProperty("renderId", writer.getRenderID());
		writer.writeProperty("src", widget.getSrc());
		writer.writeProperty(widget.getWidgetType().toString().toLowerCase(), null);
		this.doLayoutInParent(component);

	}

	private void doRenderDateTimeWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		this.doLayoutInParent(component);
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			if (!StringUtil.isEmpty(widget.getRequired())) {
				writer.writeBeginTag("span");
				writer.writeProperty("class", "text-error");
				
				writer.writeText("*");
				writer.writeEndTag("span");
			}
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}
		writer.writeBeginTag("input");
		writer.writeProperty("class", "form-control");
		writer.writeProperty("type", "text");

		writer.writeProperty("key", widget.getKey());
		if (!StringUtil.isEmpty(widget.getRequired())) {
			writer.writeProperty("required", widget.getRequired());
		}
		writer.writeProperty("renderId", writer.getRenderID());
		String dateFormat = widget.getDateFormat();
		if (StringUtil.isEmpty(dateFormat)) {
			writer.writeProperty("data-date-format", "yyyy-mm-dd");
		}else {
			writer.writeProperty("data-date-format", dateFormat);
		}
		if (null != widget.getDisable()) {
			writer.writeProperty("readonly", "readonly");
		}
		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);

	}

	private void doRenderFormualWidget(IComponent component) {

	}

	private void doRenderLinkWidget(IComponent component) {

	}

	private void doRenderSoundWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		if (!StringUtil.isEmpty(widget.getEditable())) {
			if (!Boolean.parseBoolean(widget.getEditable())) {
				writer.writeBeginTag("audio");
				writer.writeProperty("controls", "controls");
				writer.writeProperty("src", widget.getSrc());
			} else {

			}
		} else {
			writer.writeBeginTag("audio");
			writer.writeProperty("controls", "controls");
			writer.writeProperty("src", widget.getSrc());
		}

	}

	private void doRenderVideoWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		if (!StringUtil.isEmpty(widget.getEditable())) {
			if (!Boolean.parseBoolean(widget.getEditable())) {
				writer.writeBeginTag("video");
				writer.writeProperty("controls", "controls");
				writer.writeProperty("poster", widget.getPoster());
				writer.writeProperty("src", widget.getSrc());
			} else {

			}
		} else {
			writer.writeBeginTag("video");
			writer.writeProperty("controls", "controls");
			writer.writeProperty("poster", widget.getPoster());
			writer.writeProperty("src", widget.getSrc());
		}

	}

	private void doRenderLabelWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		writer.writeBeginTag("label");
		this.doLayoutInParent(component);
		writer.writeProperty("key", widget.getKey());
		writer.writeProperty("renderId", writer.getRenderID());

		if (!StringUtil.isEmpty(widget.getS())) {
			writer.writeProperty("class", widget.getS());
		}
		this.writerInstructAndSuperProperties(writer, widget);
		writer.writeText(widget.getCaption());
	}

	private void doRenderPopupWindowWidget(IComponent component) {
		// widget基础属性
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		this.doLayoutInParent(component);
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			if (!StringUtil.isEmpty(widget.getRequired())) {
				writer.writeBeginTag("span");
				writer.writeProperty("class", "text-error");
				
				writer.writeText("*");
				writer.writeEndTag("span");
			}
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}

		writer.writeBeginTag("input");

		writer.writeProperty("class", "form-control");
		
		writer.writeProperty("viewsize",widget.getViewSize());
		writer.writeProperty("type", "text");
		writer.writeProperty("title", widget.getCaption() == null ? "" : widget.getCaption());
		// key--唯一识别key
		writer.writeProperty("key", widget.getKey());
		if (!StringUtil.isEmpty(widget.getRequired())) {
			writer.writeProperty("required", widget.getRequired());
		}
		writer.writeProperty("renderId", writer.getRenderID());
		if (null != widget.getDisable()) {
			writer.writeProperty("readonly", "readonly");
		}
		if (!StringUtil.isEmpty(widget.getSrc())) {
			writer.writeProperty("src", widget.getSrc());
		}

		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);

	}

	/**
	 * * <div class="input-group"> <span class="input-group-addon">@</span>
	 * <input type="text" class="form-control" placeholder="twitterhandle">
	 * </div>
	 * 
	 * @param component
	 */
	private void doRenderSelectWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		this.doLayoutInParent(component);// 渲染布局指令
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			if (!StringUtil.isEmpty(widget.getRequired())) {
				writer.writeBeginTag("span");
				writer.writeProperty("class", "text-error");
				
				writer.writeText("*");
				writer.writeEndTag("span");
			}
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}

		writer.writeBeginTag("div");
		writer.writeProperty("class", "form-control");
	
		writer.writeProperty("source-src", widget.getSrc());

		writer.writeProperty("k", widget.getK());
		writer.writeProperty("v", widget.getV());

		writer.writeProperty("key", widget.getKey());
		if (!StringUtil.isEmpty(widget.getRequired())) {
			writer.writeProperty("required", widget.getRequired());
		}
		writer.writeProperty("renderId", writer.getRenderID());
		if (null != widget.getDisable()) {
			writer.writeProperty("disabled", "true");
		}
		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);

	}

	private void doRenderQRCodeWidget(IComponent component) {

	}

	private void doRenderBarcodeWidget(IComponent component) {
		// widget基础属性
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		this.doLayoutInParent(component);
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			if (!StringUtil.isEmpty(widget.getRequired())) {
				writer.writeBeginTag("span");
				writer.writeProperty("class", "text-error");
				
				writer.writeText("*");
				writer.writeEndTag("span");
			}
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}

		writer.writeBeginTag("input");

		writer.writeProperty("class", "form-control");
		writer.writeProperty("type", "text");
		writer.writeProperty("datatablekey", widget.getDataTableKey());
		// AuxiliaryDir页面指定指令
		writer.writeProperty(widget.getAuxiliaryDir(), null);
		writer.writeProperty("title", widget.getCaption() == null ? "" : widget.getCaption());
		// key--唯一识别key
		writer.writeProperty("key", widget.getKey());
		writer.writeProperty("renderId", writer.getRenderID());
		if (null != widget.getDisable()) {
			writer.writeProperty("readonly", "readonly");
		}

		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);
		
	}

	private void doRenderImageWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		if (!StringUtil.isEmpty(widget.getEditable())) {
			if (!Boolean.parseBoolean(widget.getEditable())) {
				writer.writeBeginTag("img");
				writer.writeProperty("src", widget.getSrc());
				writer.writeProperty("alt", widget.getCaption());
			} else {

			}
		} else {
			writer.writeBeginTag("img");
			writer.writeProperty("src", widget.getSrc());
			writer.writeProperty("alt", widget.getCaption());
		}

	}

	private void doRenderRichTextWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
		writer.writeBeginTag("div");

		writer.writeProperty("richtext", null); // 富文本域指令
		writer.writeProperty("id", "editor");
		writer.writeProperty("ueditor", "editor");
		if (!StringUtil.isEmpty(widget.getDataTableKey()) && !StringUtil.isEmpty(widget.getDataTableColumn())) {
			// 绑定数据
			writer.writeProperty("ng-model", widget.getDataTableKey() + "." + widget.getDataTableColumn());
		} else if (!StringUtil.isEmpty(widget.getParameterKey())) {
			// 绑定参数
			writer.writeProperty("ng-model", "param." + widget.getParameterKey() + ".value");
		}
	}

	/*
	 * 渲染textArea <textarea rows="20" cols="200"></textarea>
	 */
	private void doRenderTextAreaWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		this.doLayoutInParent(component);
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			if (!StringUtil.isEmpty(widget.getRequired())) {
				writer.writeBeginTag("span");
				writer.writeProperty("class", "text-error");
				
				writer.writeText("*");
				writer.writeEndTag("span");
			}
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}

		writer.writeBeginTag("textarea");
		// writer.writeProperty("textarea", null); // 文本域指令
		writer.writeProperty("rows", widget.getRows());
		writer.writeProperty("cols", widget.getCols());
		writer.writeProperty("class", "form-control");
		writer.writeProperty("title", widget.getCaption() == null ? "" : widget.getCaption());
		if (null != widget.getDisable()) {
			writer.writeProperty("readonly", "readonly");
		}
		if (!StringUtil.isEmpty(widget.getRequired())) {
			writer.writeProperty("required", widget.getRequired());
		}
		// writer.writeProperty("sourceType",
		// widget.getWidgetType().toString());
		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);
	}

	/**
	 *
	 * @param component
	 */
	private void doRenderInputWidget(IComponent component) {
		// widget基础属性
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		this.doLayoutInParent(component);
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			if (!StringUtil.isEmpty(widget.getRequired())) {
				writer.writeBeginTag("span");
				writer.writeProperty("class", "text-error");
				
				writer.writeText("*");
				writer.writeEndTag("span");
			}
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}

		writer.writeBeginTag("input");

		writer.writeProperty("class", "form-control");
		writer.writeProperty("type", "text");
		// AuxiliaryDir页面指定指令
		writer.writeProperty(widget.getAuxiliaryDir(), null);
		writer.writeProperty("title", widget.getCaption() == null ? "" : widget.getCaption());
		// key--唯一识别key
		writer.writeProperty("key", widget.getKey());
		if (!StringUtil.isEmpty(widget.getRequired())) {
			writer.writeProperty("required", widget.getRequired());
		}
		writer.writeProperty("renderId", writer.getRenderID());
		if (null != widget.getDisable()) {
			writer.writeProperty("readonly", "readonly");
		}

		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);

	}

	private void writerDataBindingProperties(HtmlWriter writer, BillUIWidget widget) {
		// 数据绑定属性
		if (!StringUtil.isEmpty(widget.getDataTableKey()) && !StringUtil.isEmpty(widget.getDataTableColumn())) {
			// 绑定数据
			writer.writeProperty("ng-model", widget.getDataTableKey() + "." + widget.getDataTableColumn());
		} else if (!StringUtil.isEmpty(widget.getParameterKey()) && !StringUtil.isEmpty(widget.getDataTableKey())) {
			// 绑定参数
			writer.writeProperty("ng-model", "$" + widget.getDataTableKey() + ".params." + widget.getParameterKey());
		} else {
			// 动态模型绑定
			writer.writeProperty("ng-model", "__$d_model$__");
		}

	}

	// bound,triggers,checkRules,init,formatters,properties
	private void writerInstructAndSuperProperties(HtmlWriter writer, BillUIWidget widget) {
		// 指令，属性标识性指令
		writer.writeProperty(widget.getWidgetType().toString().toLowerCase(), null);
		// widget高级属性--bound
		if (widget.getBound() != null) {
			writer.writeProperty("bound", Base64.encoder(widget.getBound().toJSONString()));
		}

		// widget高级属性--triggers
		if (widget.getTriggers() != null && widget.getTriggers().size() > 0) {
			writer.writeProperty("triggers", Base64.encoder(widget.getTriggersJSONString()));
		}

		// widget高级属性--checkRules
		if (widget.getCheckRules() != null && widget.getCheckRules().size() > 0) {
			writer.writeProperty("checkRules", Base64.encoder(widget.getCheckRulesJSONString()));
		}
		
		// widget高级属性--Rules
		if (widget.getRules() != null && widget.getRules().size() > 0) {
			writer.writeProperty("Rules", Base64.encoder(widget.getRulesJSONString()));
		}

		// widget高级属性--initValue
		if (widget.getInitFunction() != null) {
			if (!StringUtil.isEmpty(widget.getInitFunction().getInitExpr())) {
				writer.writeProperty("init", Base64.encoder(widget.getInitFunction().getInitExpr()));
			}
		}

		// widget高级属性--fromatters
		if (widget.getFormatters() != null && widget.getFormatters().size() > 0) {
			writer.writeProperty("formatters", Base64.encoder(widget.getFormattersJSONString()));
		}

		// widget高级属性--properties
		if (widget.getProperties() != null && widget.getProperties().size() > 0) {
			writer.writeProperty("properties", Base64.encoder(widget.getPropertiesJSONString()));
		}
		
		// widget高级属性--ImportSheets
		if (widget.getImportSheets() != null && widget.getImportSheets().size() > 0) {
			writer.writeProperty("importSheets", Base64.encoder(widget.getImportSheetsJSONString()));
		}
		
		// widget高级属性--ExportSheets
		if (widget.getExportSheets() != null && widget.getExportSheets().size() > 0) {
			writer.writeProperty("exportSheets", Base64.encoder(widget.getExportColumnsJSONString()));
		}
		
		// widget高级属性--Mappings
		if (widget.getMappings() != null && widget.getMappings().size()>0) {
			writer.writeProperty("mappings", Base64.encoder(widget.getMappingsJSONString()));
		}
		
		// widget高级属性--onClickHandler
		if(widget.getOnClickHandlers() != null && widget.getOnClickHandlers().size() > 0){
			writer.writeProperty("on-click-handler", Base64.encoder(widget.getOnClickHandlerJSONString()));
		}
	}

	private void doRenderInputNumberWidget(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();

		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		this.doLayoutInParent(component);
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			if (!StringUtil.isEmpty(widget.getRequired())) {
				writer.writeBeginTag("span");
				writer.writeProperty("class", "text-error");
				
				writer.writeText("*");
				writer.writeEndTag("span");
			}
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}
		writer.writeBeginTag("input");
		writer.writeProperty("class", "form-control");
		writer.writeProperty("type", "number");
		writer.writeProperty("title", widget.getCaption() == null ? "" : widget.getCaption());
		writer.writeProperty("string-to-number", null);
		if (null != widget.getDisable()) {
			writer.writeProperty("readonly", "readonly");
		}

		writer.writeProperty("key", widget.getKey());
		if (!StringUtil.isEmpty(widget.getRequired())) {
			writer.writeProperty("required", widget.getRequired());
		}
		writer.writeProperty("renderId", writer.getRenderID());
		this.writerInstructAndSuperProperties(writer, widget);
		this.writerDataBindingProperties(writer, widget);

	}

	/**
	 * 在父环境中布局我自己
	 * 
	 * @param component
	 */
	private void doLayoutInParent(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIWidgetInstance instance = (BillUIWidgetInstance) component;
		BillUIWidget widget = instance.getBillUIWidget();
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
		}

	}

}
