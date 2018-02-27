package com.xyy.expression.computer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.xyy.expression.Comma;
import com.xyy.expression.Context;
import com.xyy.expression.DataHelper;
import com.xyy.expression.ILib;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.Rem;
import com.xyy.expression.Token;
import com.xyy.expression.TreatComp;
/**
 * 计算器类，用于表达式的计算
 * @author evan
 *
 */
public class Computer {
	private TreatComp tc;
	private Map<String, List<ILib>> nsLibs = new HashMap<String, List<ILib>>();

	public Computer() {
		super();
		this.buildcomputer();
	}

	// 鏋勫缓computer澶勭悊閾�
	private void buildcomputer() {
		ComputerHandler handler = new ComputerHandler(this);
		List<TreatComp> comps = handler.buildHandlers();
		assert !comps.isEmpty();
		for (int i = 0; i < comps.size(); i++) {
			if (i + 1 < comps.size()) {
				comps.get(i).setNext(comps.get(i + 1));
			}
		}
		this.tc = comps.get(0);
	}

	private static String _defaultNS = "_$default";

	public void registerLib(String ns, ILib lib) {
		if (ns == null) {
			ns = _defaultNS;
		}
		if (!this.nsLibs.containsKey(ns)) {
			this.nsLibs.put(ns, new ArrayList<ILib>());
		}
		if (!this.nsLibs.get(ns).contains(lib)) {
			this.nsLibs.get(ns).add(lib);
		}
	}

	public void unRegisterLib(String ns, ILib lib) {
		if (ns == null) {
			ns = _defaultNS;
		}
		if (this.nsLibs.containsKey(ns)) {
			this.nsLibs.get(ns).remove(lib);
		}
	}

	
	protected Object callFuntion(String funName, Context ctx,
			Stack<OperatorData> outStack) throws Exception {
		String ns = _defaultNS;
		String fn = null;
		if (funName.contains(".")) {
			ns = funName.substring(0, funName.lastIndexOf("."));
			fn = funName.substring(funName.lastIndexOf(".") + 1);
		} else {
			fn = funName;
		}
		if (!this.nsLibs.containsKey(ns)) {
			throw new Exception("function not found:" + funName);
		}
		int m_id = -1;
		ILib callLib = null;
		for (ILib lib : this.nsLibs.get(ns)) {
			m_id = lib.getMethodID(fn);
			if (m_id > 0) {
				callLib = lib;
				break;
			}
		}
		if (callLib == null) {
			throw new Exception("function not found:" + funName);
		}
		Stack<OperatorData> m_params = this.getFunctionArgs(fn, outStack);
		return callLib.call(ctx,m_params, m_id);
	}

	protected Stack<OperatorData> getFunctionArgs(String funName,
			Stack<OperatorData> outStack) {
		Stack<OperatorData> result = new Stack<OperatorData>();
		OperatorData curOd = outStack.pop();
		OperatorData preOd = null;
		while (curOd.clazz != Rem.class
				|| (curOd.clazz == Rem.class && !curOd.value.toString().equals(
						"rem begin_args"))) {
			if (curOd.clazz != Rem.class && curOd.clazz != Comma.class) {
				result.add(curOd);
			} else if (preOd != null && curOd.clazz == Comma.class
					&& preOd.clazz == Comma.class) {
				result.add(DataHelper.createOperatorData(new NullRefObject()));
			}
			preOd = curOd;
			curOd = outStack.pop();// 涓嶆柇寮瑰嚭
		}
		return result;
	}

	public TreatComp getTc() {
		return tc;
	}

	public void setTc(TreatComp tc) {
		this.tc = tc;
	}

	// 璁＄畻涔�
	public void comp(Stack<Token> tokens, Context ctx,
			Stack<OperatorData> stOutput) throws Exception {
		while (!tokens.isEmpty()) {
			this.tc.comp(tokens.pop(), ctx, stOutput);
		}
	}

	



}
