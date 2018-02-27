package com.xyy.workflow.inf;

import com.jfinal.core.Controller;
import com.xyy.workflow.definition.TaskInstance;

public interface IForceEndHandler {

	void handle( TaskInstance ti,Controller controller) throws Exception;
}
