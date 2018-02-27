package com.xyy.bill.print.html;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.ext.directive.Date;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.render.IComponent;

/**
 * 审核日志渲染器
 * 
 * pageSetting extraAuditLog 设置为true 生效
 * @author caofei
 *
 */
public class BillAndDicAuditLogRender extends PrintRender {
	
	private static final String[] TITLE_STRS = { "审核人", "审核时间", "审核结果", "审核意见", "备注" };
	private static final String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";
	private static final  SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATTER);
	
	public BillAndDicAuditLogRender(PrintDevice context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillAndDicAuditLog";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		PrintWriter writer = this.getContext().getWriter();
		writer.writeBeginTag("br");
		writer.writeEndTag("br");

		writer.writeBeginTag("table");
		writer.writeProperty("cellspacing", "0");
		writer.writeProperty("cellpadding", "0");
		writer.writeProperty("class", "data");

		this.printTitle(writer);
	}

	/**
	 * 打印标题
	 * @param writer
	 */
	private void printTitle(PrintWriter writer) {
		// 标题
		writer.writeBeginTag("thead");
		writer.writeBeginTag("tr");
		writer.writeBeginTag("td");
		writer.writeProperty("class", "auditLogTitle");
		writer.writeProperty("colspan", 5);
		writer.writeProperty("align", "center");
		writer.writeText("审核日志");
		writer.writeEndTag("td");
		writer.writeEndTag("tr");
		writer.writeEndTag("thead");

		// 打印头标题
		writer.writeBeginTag("thead");
		writer.writeBeginTag("tr");
		for (int i = 0; i < TITLE_STRS.length; i++) {
			writer.writeBeginTag("td");
			writer.writeProperty("width", 200);
			writer.writeText(TITLE_STRS[i]);
			writer.writeEndTag("td");
		}
		writer.writeEndTag("tr");
		writer.writeEndTag("thead");
	}

	/**
		context.set("$auditConditionMap", auditConditionMap);
		context.set("$auditRecords", auditRecords);
		context.set("$auditPageCount", auditPageCount++);
		
		map.put("$auditLogStart", pos);   //记录开始下标
		map.put("$auditLogEnd", auditRecords.size());    //记录结束下标
		map.put("$isRemain", false);   //是否剩余
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onRenderContent(IComponent component) {
		PrintWriter writer = this.getContext().getWriter();
		BillContext context = this.getContext().getBillContext();
		List<Record> logRecords = (List<Record>) context.get("$auditRecords");
		Map<String, Object> auditConditionMap = (Map<String, Object>) context.get("$auditConditionMap");
		int auditPageCount = context.getInteger("$auditPageCount");
		Map<String, Object> curPageConditionMap = (Map<String, Object>) auditConditionMap.get(auditPageCount);
		
		if(curPageConditionMap == null)
			return;
		int start = (int) curPageConditionMap.get("auditLogStart");
		int end = (int) curPageConditionMap.get("auditLogEnd");
		// 打印表格内容
		writer.writeBeginTag("tbody");
		for (int i = start; i < end; i++) {
			Record record = logRecords.get(i);
			writer.writeBeginTag("tr");
			Map<String, Object> map = record.getColumns();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				writer.writeBeginTag("td");
				if(entry.getValue() != null){
					if(entry.getValue().getClass() == Date.class || entry.getValue().getClass() == Timestamp.class){
						writer.writeText(entry.getValue() == null ? "" : sdf.format(entry.getValue())  );
					}else{
						writer.writeText(entry.getValue() == null ? "" : entry.getValue().toString());
					}
				}else{
					writer.writeText(entry.getValue() == null ? "" : entry.getValue().toString());
				}
				writer.writeEndTag("td");
			}
			writer.writeEndTag("tr");
		}
		writer.writeEndTag("tbody");
	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		PrintWriter writer = this.getContext().getWriter();
		writer.writeEndTag("table");
	}

}
