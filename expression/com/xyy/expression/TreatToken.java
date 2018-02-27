package com.xyy.expression;

import java.util.Stack;

import com.xyy.expression.parser.Parser;
/**
 * Token处理器框架
 * @author evan
 *
 */
public abstract class TreatToken {
	protected Parser context;
	protected TreatToken next;

	public TreatToken(Parser context) {
		super();
		this.context = context;
	}

	public abstract void treat(Token token, Stack<Token> stTemp,
			Stack<Token> stOutput) throws Exception;

	public TreatToken getNext() {
		return next;
	}

	public void setNext(TreatToken next) {
		this.next = next;
	}

	public Parser getContext() {
		return context;
	}

}
