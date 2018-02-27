package com.xyy.expression;

import java.util.Stack;

import com.xyy.expression.computer.Computer;

/**
 * 计算处理器框架
 * @author evan
 *
 */
public abstract class TreatComp {
	protected Computer context;
	protected TreatComp next;

	public TreatComp(Computer context) {
		super();
		this.context = context;
	}

	public Computer getContext() {
		return context;
	}

	public TreatComp getNext() {
		return next;
	}

	public void setNext(TreatComp next) {
		this.next = next;
	}

	public abstract void comp(Token token,Context ctx,Stack<OperatorData> stOutput) throws Exception;

}
