package com.xyy.expression.lib;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.jfinal.kit.PathKit;
import com.xyy.bill.util.SequenceBuilder;
import com.xyy.erp.platform.common.tools.FileUtil;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.expression.Context;
import com.xyy.expression.ILib;
import com.xyy.expression.OperatorData;


public class OrderLib implements ILib {
	
	private static Map<String, Long> orderNoContainer = new HashMap<>();
	
	static {
		try {
			String filePathAndName = PathKit.getRootClassPath() + File.separator + "orderNoContainer.txt";
			String str = FileUtil.readTxt(filePathAndName, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public OrderLib(){
		
	}

	private static Map<String, Integer> functionMaps = new HashMap<String, Integer>();
	static {
		functionMaps.put("orderNo", 1);
		functionMaps.put("shangpin", 2);
		functionMaps.put("isNotNull", 3);
		functionMaps.put("orderNo2", 4);
	
	}

	@Override
	public int getMethodID(String name) {
		if (functionMaps.containsKey(name)) {
			return functionMaps.get(name);
		}
		return -1;
	}

	@Override
	public Object call(Context ctx,Stack<OperatorData> para, int methodID) {
		switch (methodID) {
		case 1:
			return this.orderno(para);
		case 2:
			return this.shangpin(para);
		case 3:
			return this.isNotNull(para);
		case 4:
			return this.orderno2(para);
		}
		return null;
	}

	private String orderno(Stack<OperatorData> para) {// 10
		// 订单编号 varchar 规则：YBM+年月日时分秒+3位随机码, 例如：YBM20160426112240764
		return SequenceBuilder.nextSequence("orderNo_"+TimeUtil.format(new Date(), "yyyyMMdd"), "", 10);
	}

	private String orderno2(Stack<OperatorData> para) {// 12
		// 订单编号 varchar 规则：YBM+年月日+4位随机码, 例如：YBM20160426112240764
		return SequenceBuilder.nextSequence("orderNo2_"+TimeUtil.format(new Date(), "yyyyMMdd"), "", 12);
	}
	
	//测试用例库函数
	private boolean shangpin(Stack<OperatorData> para) {
		if (para.pop().value.toString().equals("湖北")) {
			return true;
		}
		
		return false;
	}
	
	private boolean isNotNull(Stack<OperatorData> para) {
		OperatorData pop = para.pop();
		Object param = pop.value;
		if (param instanceof Integer) {
			int value = ((Integer) param).intValue();
			if(value==0){
				return false;
			}
		}else if (param instanceof String) {
		    String s = (String) param;
		    if(StringUtil.isEmpty(s)|| "0".equals(s)){
		    	return false;
		    }
		}
		return true;
	}
	
	
//	public static void main(String[] args) throws IOException {
//		for (int i = 0; i < 100; i++) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					System.out.println("10位："+SequenceBuilder.nextSequence("orderNo_"+TimeUtil.format(new Date(), "yyyyMMdd"), "", 10));
//					
//					System.out.println("12位："+SequenceBuilder.nextSequence("orderNo2_"+TimeUtil.format(new Date(), "yyyyMMdd"), "", 12));
//				}
//			}).start();
//		}
//	}
	
}
