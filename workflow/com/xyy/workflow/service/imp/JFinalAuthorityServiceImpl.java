package com.xyy.workflow.service.imp;

import com.jfinal.core.Controller;
import com.xyy.erp.platform.system.model.User;

public class JFinalAuthorityServiceImpl extends BaseAuthorityServiceImpl {
	private Controller controller;
	public JFinalAuthorityServiceImpl(Controller controller) {
		super();
		this.controller = controller;
	}
	
	/**
	 * 获取当前的用户，注意该
	 */
	@Override
	public User currentUser() throws Exception {
		User result = null;
		if (this.controller == null) {
			throw new Exception(
					"controller is null,JFinalAuthorityServiceImpl run exception.");
		}
		String userId = this.controller.getCookie("userId");
		if (userId != null) {
			result = User.dao.findById(userId);
		}
		if (result == null) {
			throw new Exception(
					"current members is null,JFinalAuthorityServiceImpl run exception.");
		}
		
		return result;
	}

}
