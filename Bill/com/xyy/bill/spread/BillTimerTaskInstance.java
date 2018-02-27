package com.xyy.bill.spread;

import com.xyy.bill.meta.TimerTask;

public class BillTimerTaskInstance extends AbstractComponent {
	private TimerTask timerTask;

	public BillTimerTaskInstance(TimerTask timerTask) {
		super();
		this.timerTask = timerTask;
		this.initialize();
	}

	@Override
	protected void initialize() {

	}

	@Override
	public String getFlag() {
		return "BillTimerTask";
	}

	public TimerTask getTimerTask() {
		return timerTask;
	}

}
