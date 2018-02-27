package com.xyy.bill.handler;

import com.xyy.bill.def.DicDef;
import com.xyy.bill.event.PostLoadEvent;
import com.xyy.bill.event.PreLoadEvent;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DicInstance;
import com.xyy.bill.instance.DicListInstance;
import com.xyy.bill.services.util.BILLConstant;

/**
 * 字典加载处理器
 * 
 * @author caofei
 *
 */
public class DicLoadHandler extends AbstractHandler {

	public DicLoadHandler(String scope) {
		super(scope);
	}

	@Override
	public void preHandle(BillContext context) {
		DicDef dicDef = (DicDef) context.get("$dicDef");
		dicDef.firePreLoadEvent(new PreLoadEvent(context));
	}

	@Override
	public void handle(BillContext context) {
		DicDef dicDef = (DicDef) context.get("$dicDef");
		// 通过dicDataType获取指定数据集
		try {
			String dicDataType = context.getString("dicDataType");
			DataSetInstance dicDsInst = null;
			DicInstance dicInst = null;
			switch (dicDataType) {
			case BILLConstant.DATA_CODE_DIC:
				dicInst = new DicInstance(dicDef, context);
				dicDsInst = dicInst.loadData();
				context.set("$model", dicDsInst);
				break;
			case BILLConstant.DATA_CODE_DICSITEM:// 加载字典叙事薄数据
				dicDsInst = new DataSetInstance(context, dicDef.getDataSet());
				dicDsInst.loadDicsItemData();
				context.set("$model", dicDsInst);
				break;
			case BILLConstant.DATA_CODE_DIC_ITEM:// 加载字典项数据
				dicInst = new DicInstance(dicDef, context);// 构建单据实例
				dicDsInst = dicInst.loadDicItemData();
				context.set("$model", dicDsInst);
				break;
			case BILLConstant.DATA_CODE_DICS: // 加载字典列表数据
				DicListInstance billListInstance = new DicListInstance(dicDef, context);// 构建字典实例
				DataSetInstance dsi = billListInstance.loadData();// 加载数据集
				context.set("$model", dsi);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			context.addError("9999", e.getMessage());
		}
	}

	@Override
	public void postHandle(BillContext context) {
		DicDef dicDef = (DicDef) context.get("$dicDef");
		dicDef.firePostLoadEvent(new PostLoadEvent(context));
	}

}
