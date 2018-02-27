package com.xyy.workflow.exe;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ProcessInstanceThreadLocal extends ThreadLocal<Map> {
	
	@Override
	protected Map initialValue() {
		return new HashMap();
	}

}
