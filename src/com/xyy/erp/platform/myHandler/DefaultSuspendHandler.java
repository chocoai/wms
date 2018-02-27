package com.xyy.erp.platform.myHandler;

import java.sql.Timestamp;

import com.jfinal.core.Controller;
import com.xyy.erp.platform.system.model.Suspendform;
import com.xyy.erp.platform.system.model.User;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.inf.ISuspendHandler;

public class DefaultSuspendHandler implements ISuspendHandler {

	@Override
	public void handle(TaskInstance ti,Controller controller) throws Exception {

		//保存否单理由
		 Suspendform ff=new Suspendform();
		 ff.setReason(controller.getPara("reason"));
		 ff.setPi(ti.getPiId());
		 ff.setTi(ti.getId());
		 ff.setExecutor(ti.getExecutor());
		 ff.setUpdateTime(new Timestamp(System.currentTimeMillis())  );
		 User m=User.dao.findById(ti.getExecutor());
		 if(m!=null)
			 ff.setExecutorName(m.getRealName() );
		 ff.save();
		
	}
	
}
