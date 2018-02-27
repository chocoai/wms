package com.xyy.bill.services.util;

public class BILLConstant {
	//UserAgent常量
	public static final String UA_WEB = "WEB";
	public static final String UA_PAD = "PAD";
	public static final String UA_MOBILE = "MOBILE";

	//单据转化常量
	public static final String EDGE_CODE_BILL = "bill";
	public static final String EDGE_CODE_BILLDtl = "details";
	public static final String EDGE_CODE_MULTIBILL = "multiBill";
	public static final String EDGE_CODE_DIC = "dic";
	
	// 视图常量
	public static final String UI_CODE_BILL = "bill";
	public static final String UI_CODE_BILLS = "bills";
	public static final String UI_CODE_DIC = "dic";
	public static final String UI_CODE_DICSITEM = "dics-item";
	public static final String UI_CODE_DICS = "dics";
	public static final String UI_CODE_MULTIBILL = "multibill";
	public static final String UI_CODE_VIEW = "viewport";
	public static final String UI_CODE_REPORT = "report";
	public static final String UI_CODE_WORKFORM = "workform";
	public static final String UI_CODE_DATAFORMS = "dataforms";
	public static final String UI_CODE_CHARTS = "charts";

	//数据操作常量
	public static final String DATA_CODE_BILL = "bill";
	public static final String DATA_CODE_BILLS = "bills";
	public static final String DATA_CODE_BILL_SAVE = "bill-save";
	public static final String DATA_CODE_BILL_SUBMIT = "bill-submit";
	public static final String DATA_CODE_BILL_DELETE = "bill-delete";
	public static final String DATA_CODE_DIC = "dic";
	public static final String DATA_CODE_DICSITEM = "dics-item";
	public static final String DATA_CODE_DICS_ITEM_DELETE = "dics-item-delete";
	public static final String DATA_CODE_DICS = "dics";
	public static final String DATA_CODE_DIC_ITEM = "dic-item";
	public static final String DATA_CODE_DIC_LIKE = "dic-like";
	public static final String DATA_CODE_DIC_EQ = "dic-eq";
	public static final String DATA_CODE_DIC_ITEM_SAVE = "dic-item-save";
	public static final String DATA_CODE_DIC_ITEM_DELETE = "dic-item-delete";
	public static final String DATA_CODE_MULTIBILL = "multibill";
	public static final String DATA_CODE_REPORT = "report";
	public static final String DATA_CODE_VIEW = "viewport";
	public static final String DATA_CODE_REFRESH = "refresh";
	public static final String DATA_CODE_CHARTS = "charts";
	public static final String DATA_CODE_RULE = "rule";
	public static final String DATA_CODE_PROCESS = "process";//审核流程
	public static final String DATA_CODE_FUNC = "func";//函数
	public static final String DATA_CODE_SQLFUNC = "SqlFunc";//函数
	public static final String DATA_CODE_EDGEBILL = "edgeBill";//函数
	public static final String DATA_CODE_MULTISELECT = "multi-select";//多行选择器
	
	public static final String CHART_DATA_SORUCE="datasoruce";
	public static final String CHART_DATA_MODEL="model";
	
	public static final String EXCEL_OPTION_UPLOAD="upload";
	public static final String EXCEL_OPTION_EXPORT="download";
	//单据状态常量
	/**
	 * 单据状态编码： 0-草稿状态：编辑、保存、删除（物理删除） 10 审核驳回编辑状态 20-已提交，待审核===》单据不能编辑、删除 30-审核中===》
	 * 40-审核通过===》数据下推===》关联生成【推式生成】 50-审核未通过===》 60-归档===>参与的业务已完结 auditStatus
	 * ===》由审核流程来维护字段 ===》审核打回
	 */
	public static final int BILL_STATE_DEFAULT = 0;
	public static final int BILL_STATE_EDIT = 10;
	public static final int BILL_STATE_SUBMIT = 20;
	public static final int BILL_STATE_AUDITINT = 30;
	public static final int BILL_STATE_AUDIT_ACCEPT = 40;
	public static final int BILL_STATE_AUDIT_REJECT = 50;
	public static final int BILL_STATE_ARCHIVE = 60;

}

