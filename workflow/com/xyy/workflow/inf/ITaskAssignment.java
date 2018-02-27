package com.xyy.workflow.inf;

import java.util.List;

import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 任务分派器，用于生成并分派任务
 * @author evan
 *
 */
public interface ITaskAssignment {
	/**
	 * 任务分派器，用于分派任务
	 * 根据活动定义的规则，生成并进行任务的指派，默认情况下，task节点，meeting节点都会调用对应的任务分派器，生成任务
	 * 对于task节点，生成一个任务，对于meeting节点，根据meeting节点的任务数，生成对应的任务
	 * @param ec
	 * @throws Exception
	 */
	List<TaskInstance> assignment(ExecuteContext ec) throws Exception;
}
