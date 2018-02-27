package com.xyy.erp.platform.system.service;

import com.jfinal.plugin.activerecord.Page;
import com.xyy.workflow.definition.TaskInstance;

public class TaskinstanceService {

	private static final String PREFIX_SQL = "select *";
	
	/**
	 * 返回分页数据
	 */
	public Page<TaskInstance> paginate(int pageNumber, int pageSize, String userId, int status) {
		StringBuilder buffer = new StringBuilder("from tb_pd_taskinstance where 1 = 1 ");
		if (userId != null) {
			if (status==0) {
				buffer.append(" and mIds like '%"+userId+"%' ");
			}else {
				buffer.append(" and executor = '"+userId+"' ");
			}
		}
		buffer.append(" and status = "+status+" ");
		buffer.append("order by createTime desc");
		return TaskInstance.dao.paginate(pageNumber, pageSize, PREFIX_SQL, buffer.toString());
	}
	
}
