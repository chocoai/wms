package com.xyy.workflow.inf;

import com.alibaba.fastjson.JSONObject;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 扩展字段处理器接口
 * @author evan
 *
 */
public interface IExtFieldsHandler {
	JSONObject processExtFields(ExecuteContext ec, TaskInstance ti);
}
