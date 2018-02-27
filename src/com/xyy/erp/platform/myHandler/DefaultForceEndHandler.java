package com.xyy.erp.platform.myHandler;

import java.sql.Timestamp;

import com.jfinal.core.Controller;
import com.xyy.erp.platform.system.model.Forceendform;
import com.xyy.erp.platform.system.model.User;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.inf.IForceEndHandler;

public class DefaultForceEndHandler implements IForceEndHandler {

	@Override
	public void handle(TaskInstance ti,Controller controller) throws Exception {

		//保存否单理由
		 Forceendform ff=new Forceendform();
		 ff.setReason(controller.getPara("refuseReason"));
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
