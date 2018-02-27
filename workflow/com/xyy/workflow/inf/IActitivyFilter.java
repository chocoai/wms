package com.xyy.workflow.inf;

import java.util.List;

import com.xyy.erp.platform.system.model.User;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 活动过滤器接口
 * @author evan
 *
 */
public interface IActitivyFilter {

	/**
	 * 活动过滤器，对当前候选人进行过滤，以便分配任务
	 * @param context:执行上下文
	 * @param candidateMemebers:候选人列表，需要过滤的名单
	 */
	public void filter(ExecuteContext context,List<User> candidateMemebers);
}