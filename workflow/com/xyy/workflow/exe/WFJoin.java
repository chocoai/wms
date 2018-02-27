package com.xyy.workflow.exe;

import java.sql.Timestamp;

import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.inf.AbstractNodeProcessor;

public class WFJoin extends AbstractNodeProcessor {

	@Override
	public void enter(ExecuteContext ec) throws Exception {
		super.enter(ec);

	}

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		Token token = ec.getToken();
		token.setStatus(Token.TOKEN_STATUS_END);// 结束当前的分支
		Token parentToken = ec.getToken().getParentToken();
		ActivityInstance forkActivityInstance = parentToken.getActivityInstance();
		// 得到分支计数
		int count = (Integer) forkActivityInstance.getVariant("_$fork_count");
		count--;
		forkActivityInstance.setVariant("_$fork_count", count);
		super.execute(ec);// 注意join分支会进来多次
		if (count <= 0) {// 令牌转移到下一个节点
			forkActivityInstance.setEndTime(new Timestamp(System.currentTimeMillis()));
			forkActivityInstance.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_FINISHED);// fork节点的leaving事件不会激活
			parentToken.setActivityInstance(ec.getToken().getActivityInstance());// 让父token指向当前的join节点
			parentToken.setStatus(Token.TOKEN_STATUS_EXE);
			parentToken.signal();// 恢复父token的运行，并在父token上发送转移指令
		}
	}

	@Override
	public void leaving(ExecuteContext ec, Transition transitions) throws Exception {
		super.leaving(ec, transitions);
	}

}
