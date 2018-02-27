package com.xyy.bill.render.html;

import com.xyy.bill.meta.TimerTask;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.spread.BillTimerTaskInstance;

public class HtmlTimerTaskRender extends HTMLRender {

	public HtmlTimerTaskRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillTimerTask";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {

	}

	@Override
	protected void onRenderContent(IComponent component) {
		// $scope.timeTask=[];
		BillTimerTaskInstance billTimerTaskInstance = (BillTimerTaskInstance) component;
		TimerTask timerTask = billTimerTaskInstance.getTimerTask();
		this.getContext().getWriter().getMainControllerFunction().append("$scope.timeTask.push(")
				.append(timerTask.toJSONString()).append(")").append(";");

	}

	@Override
	protected void onRenderEndTag(IComponent component) {

	}

}
