package com.xyy.bill.spread;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillHeadMeta;
import com.xyy.bill.meta.BillWorkflow;
import com.xyy.bill.meta.Style;
import com.xyy.bill.meta.TimerTask;

public class BillHeadInstance extends AbstractContainer {
	private BillHeadMeta billHeadMeta;

	private List<StyleInstance> styleInstances = new ArrayList<>();
	private List<BillWorkflowInstance> billWorkflowInstances = new ArrayList<>();
	private List<BillTimerTaskInstance> billTimerTaskInstances = new ArrayList<>();

	public BillHeadInstance(BillHeadMeta billHeadMeta) {
		super();
		this.billHeadMeta = billHeadMeta;
		this.initialize();
	}

	@Override
	protected void initialize() {

		// 处理样式
		for (Style style : this.billHeadMeta.getStyles()) {
			StyleInstance styleInstance = new StyleInstance(style);
			this.styleInstances.add(styleInstance);
			this.addComponent(styleInstance);
		}

	
		// 处理定时任务
		for (TimerTask timerTask : this.billHeadMeta.getTimerTasks()) {
			BillTimerTaskInstance billTimerTaskInstance = new BillTimerTaskInstance(timerTask);
			this.billTimerTaskInstances.add(billTimerTaskInstance);
			this.addComponent(billTimerTaskInstance);
		}

	}

	public BillHeadMeta getBillHeadMeta() {
		return billHeadMeta;
	}

	public List<StyleInstance> getStyleInstances() {
		return styleInstances;
	}

	public List<BillWorkflowInstance> getBillWorkflowInstances() {
		return billWorkflowInstances;
	}

	public List<BillTimerTaskInstance> getBillTimerTaskInstances() {
		return billTimerTaskInstances;
	}

	@Override
	public String getFlag() {
		return "BillHeadMeta";
	}

}
