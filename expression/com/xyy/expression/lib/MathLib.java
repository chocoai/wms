package com.xyy.expression.lib;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.xyy.expression.Context;
import com.xyy.expression.ILib;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
/**
 * 数学库
 * @author evan
 *
 */
public class MathLib implements ILib {
	public MathLib() {

	}

	private static Map<String, Integer> functionMaps = new HashMap<String, Integer>();
	static {
		functionMaps.put("abs", 1);
		functionMaps.put("ceil", 2);
		functionMaps.put("cos", 3);
		functionMaps.put("exp", 4);
		functionMaps.put("fact", 5);
		functionMaps.put("floor", 6);
		functionMaps.put("log", 7);
		functionMaps.put("log10", 8);
		functionMaps.put("pi", 9);
		functionMaps.put("pow", 10);
		functionMaps.put("rand", 11);
		functionMaps.put("round", 12);
		functionMaps.put("sign", 13);
		functionMaps.put("sin", 14);
		functionMaps.put("sqrt", 15);
		functionMaps.put("tan", 16);
		functionMaps.put("div", 17);
		functionMaps.put("toString", 18);
		functionMaps.put("percentum", 19);
		functionMaps.put("if", 20);
		functionMaps.put("ff", 21);
		functionMaps.put("list", 22);
	}

	@Override
	public int getMethodID(String name) {
		if (functionMaps.containsKey(name)) {
			return functionMaps.get(name);
		}
		return -1;
	}

	@Override
	public Object call(Context ctx, Stack<OperatorData> para, int methodID) {
		switch (methodID) {
		case 1:
			return this.preAbs(para);
		case 2:
			return this.preCeil(para);
		case 3:
			return this.preCos(para);
		case 4:
			return this.preExp(para);
		case 5:
			return this.preFact(para);
		case 6:
			return this.preFloor(para);
		case 7:
			return this.preLog(para);
		case 8:
			return this.preLog10(para);
		case 9:
			return this.prePi(para);
		case 10:
			return this.prePow(para);
		case 11:
			return this.preRand(para);
		case 12:
			return this.preRound(para);
		case 13:
			return this.preSign(para);
		case 14:
			return this.preSin(para);
		case 15:
			return this.preSqrt(para);
		case 16:
			return this.preTan(para);
		case 17:
			return this.preDiv(para);
		case 18:
			return this.preToString(para);
		case 19:
			return this.prePercentum(para);
		case 20:
			return this.preIff(para);
		case 21:
			return this.preff(para);
		case 22:
			return this.preList(para);
		}
		return null;
	}

	/**
	 * if
	 */
	public Object ff(Object v1) {
		String v11 = v1.toString();
		try {
			double result = Double.parseDouble(v11);
			DecimalFormat df = new DecimalFormat("0.00");
			return df.format(result);
		} catch (Exception e) {
			return v1;
		}
	}

	public Object preff(Stack<OperatorData> para) {
		Object v1 = para.pop().value;
		return this.ff(v1);
	}

	/**
	 * if
	 */
	public Object iif(boolean condition, Object v1, Object v2) {
		if (condition) {
			return v1;
		} else {
			return v2;
		}
	}

	public Object preIff(Stack<OperatorData> para) {
		Boolean c = (Boolean) para.pop().value;
		Object v1 = para.pop().value;
		Object v2 = para.pop().value;
		return this.iif(c, v1, v2);
	}

	/**
	 * 计算参数的绝对值
	 * 
	 * @param bgs
	 * @return
	 */
	public double abs(double bgs) {
		return Math.abs(bgs);
	}

	public double preAbs(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		return this.abs(bgs);
	}

	/**
	 * 对数据在指定位置上进行截取，剩余部分只要有值就进位
	 * 
	 * @param bgs
	 * @param i
	 * @return
	 */
	public double ceil(double bgs, int i) {
		if (i > 0) {
			for (int n = i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.multiply(b_2).doubleValue();
			}
			bgs = Math.ceil(bgs);
			for (int n = i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.divide(b_2).doubleValue();
			}
		} else if (i < 0) {
			for (int n = -i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.divide(b_2).doubleValue();
			}
			bgs = Math.ceil(bgs);
			for (int n = -i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.multiply(b_2).doubleValue();
			}
		} else {
			bgs = Math.ceil(bgs);
		}
		return bgs;
	}

	public double preCeil(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		int i = 0;
		if (!para.isEmpty()) {
			i = Integer.parseInt(para.pop().value.toString());
		}
		return this.ceil(bgs, i);
	}

	/**
	 * 计算参数的余弦值，其中参数以弧度为单位
	 * 
	 * @param bgs
	 * @return
	 */
	public double cos(double bgs) {
		return Math.cos(bgs);
	}

	public double preCos(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		return this.cos(bgs);
	}

	/**
	 * 计算 e 的n 次幂
	 * 
	 * @param bgs
	 * @return
	 */
	public double exp(double bgs) {
		return Math.exp(bgs);
	}

	public double preExp(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		return this.exp(bgs);
	}

	/**
	 * 计算参数的阶乘
	 * 
	 * @param itg
	 * @return
	 */
	public Integer fact(Integer itg) {
		Integer res = 1;
		for (Integer i = 2; i <= itg; i++) {
			res = res * i;
		}
		return res;
	}

	public Integer preFact(Stack<OperatorData> para) {
		Integer itg = Integer.parseInt(para.pop().value.toString());
		return this.fact(itg);
	}

	/**
	 * 对数据在指定位置上进行截取，剩余部分只要有值全舍去
	 * 
	 * @param bgs
	 * @param i
	 * @return
	 */
	public double floor(double bgs, int i) {
		if (i > 0) {
			for (int n = i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.multiply(b_2).doubleValue();
			}
			bgs = Math.floor(bgs);
			for (int n = i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.divide(b_2).doubleValue();
			}
		} else if (i < 0) {
			for (int n = -i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.divide(b_2).doubleValue();
			}
			bgs = Math.floor(bgs);
			for (int n = -i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.multiply(b_2).doubleValue();
			}
		} else {
			bgs = Math.floor(bgs);
		}
		return bgs;
	}

	public double preFloor(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		int i = 0;
		if (!para.isEmpty()) {
			i = Integer.parseInt(para.pop().value.toString());
		}
		return this.floor(bgs, i);
	}

	/**
	 * 计算参数的自然对数
	 * 
	 * @param bgs
	 * @return
	 */
	public double log(double bgs) {
		return Math.log(bgs);
	}

	public double preLog(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		return this.log(bgs);
	}

	/**
	 * 计算以 10 为底的对数
	 * 
	 * @param bgs
	 * @return
	 */
	public double log10(double bgs) {
		return Math.log10(bgs);
	}

	public double preLog10(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		return this.log10(bgs);
	}

	/**
	 * 计算圆周率及其倍数
	 * 
	 * @return
	 */
	public double pi() {
		return Math.PI;
	}

	/**
	 * 计算圆周率及其倍数
	 * 
	 * @param i
	 * @return
	 */
	public double pi(int i) {
		double bgs = Math.PI;
		BigDecimal bg_1 = new BigDecimal(Double.toString(bgs));
		BigDecimal bg_2 = new BigDecimal(Double.toString((double) i));
		return bg_1.multiply(bg_2).doubleValue();
	}

	public double prePi(Stack<OperatorData> para) {
		if (para.empty() || para == null) {
			return this.pi();
		} else {
			int i = Integer.parseInt(para.pop().value.toString());
			return this.pi(i);
		}
	}

	/**
	 * 计算 x 的n 次幂
	 * 
	 * @param bgs
	 * @param i
	 * @return
	 */
	public double pow(double bgs, double i) {
		return Math.pow(bgs, i);
	}

	public double prePow(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		double i = Double.parseDouble(para.pop().value.toString());
		return pow(bgs, i);
	}

	/**
	 * 取得 0-1.0 之间的一个随机数
	 * 
	 * @return
	 */
	public double rand() {
		double res = Math.random() * (1.0);
		return res;
	}

	public double preRand(Stack<OperatorData> para) {
		return this.rand();
	}

	/**
	 * 对数据在指定位置上进行截取，剩余部分四舍五入
	 * 
	 * @param bgs
	 * @param i
	 * @return
	 */
	public double round(double bgs, int i) {
		if (i > 0) {
			for (int n = i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.multiply(b_2).doubleValue();
			}
			BigDecimal bg = new BigDecimal(bgs).setScale(0,
					BigDecimal.ROUND_HALF_UP);
			bgs = bg.doubleValue();
			for (int n = i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.divide(b_2).doubleValue();
			}
		} else if (i < 0) {
			for (int n = -i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.divide(b_2).doubleValue();
			}
			BigDecimal bg = new BigDecimal(bgs).setScale(0,
					BigDecimal.ROUND_HALF_UP);
			bgs = bg.doubleValue();
			for (int n = -i; n > 0; n--) {
				BigDecimal b_1 = new BigDecimal(Double.toString(bgs));
				BigDecimal b_2 = new BigDecimal(Double.toString(10.0));
				bgs = b_1.multiply(b_2).doubleValue();
			}
		} else {
			BigDecimal bg = new BigDecimal(bgs).setScale(0,
					BigDecimal.ROUND_HALF_UP);
			bgs = bg.doubleValue();
		}
		return bgs;
	}

	public double preRound(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		int i = 0;
		if (!para.isEmpty()) {
			i = Integer.parseInt(para.pop().value.toString());
		}
		return this.round(bgs, i);
	}

	/**
	 * 确定参数是正数还是负数还是 0，当正数时返回 1，负数时返回-1，0 时返回0
	 * 
	 * @param bgs
	 * @return
	 */
	public int sign(double bgs) {
		int i = 0;
		if (bgs > 0) {
			i = 1;
		} else if (bgs < 0) {
			i = -1;
		}
		return i;
	}

	public int preSign(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		return this.sign(bgs);
	}

	/**
	 * 计算参数的正弦值，其中参数以弧度为单位
	 * 
	 * @param bgs
	 * @return
	 */
	public double sin(double bgs) {
		return Math.sin(bgs);
	}

	public double preSin(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		return this.sin(bgs);
	}

	/**
	 * 计算平方根
	 * 
	 * @param bgs
	 * @return
	 */
	public double sqrt(double bgs) {
		return Math.sqrt(bgs);
	}

	public double preSqrt(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		return this.sqrt(bgs);
	}

	/**
	 * 计算参数的正切值，其中参数以弧度为单位
	 * 
	 * @param bgs
	 * @return
	 */
	public double tan(double bgs) {
		return Math.tan(bgs);
	}

	public double preTan(Stack<OperatorData> para) {
		double bgs = Double.parseDouble(para.pop().value.toString());
		return this.tan(bgs);
	}

	/**
	 * 计算2个数相除
	 * 
	 * @param bgs
	 * @return
	 */
	public double div(double v1, double v2) {
		return v1 / v2;
	}

	public double preDiv(Stack<OperatorData> para) {
		double v1 = Double.parseDouble(para.pop().value.toString());
		double v2 = Double.parseDouble(para.pop().value.toString());
		return this.div(v1, v2);
	}

	/**
	 * Object转字符串
	 * 
	 * @param bgs
	 * @return
	 */
	public String toString(Object v1) {
		if (v1 == null) {
			return NullRefObject.class.toString();
		}
		return v1.toString();
	}

	public String preToString(Stack<OperatorData> para) {
		Object v1 = para.pop().value;

		return this.toString(v1);
	}

	/**
	 * 小锟斤拷转锟斤拷锟缴百分憋拷(锟斤拷锟斤拷锟斤拷锟矫憋拷锟斤拷小锟斤拷位锟斤拷)
	 * 
	 * @param v1
	 * @return
	 */
	public String percentum(double v1, String v2) {
		String sNumber;
		NumberFormat format = NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(Integer.parseInt(v2));// 锟斤拷锟�锟斤拷锟斤拷识锟角憋拷锟斤拷锟斤拷小锟斤拷锟斤拷位锟斤拷锟斤拷锟斤拷锟截憋拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟窖撅拷锟饺ｏ拷100锟剿★拷
		sNumber = format.format(v1);
		return sNumber;
	}

	public String prePercentum(Stack<OperatorData> para) {
		OperatorData vo1 = para.pop();
		if (vo1.clazz == NullRefObject.class || vo1.value == null) {
			return " ";
		}
		double v1 = Double.parseDouble(vo1.value.toString());
		String v2 = null;
		if (!para.isEmpty()) {
			v2 = para.pop().value.toString();
		} else {
			v2 = "2";
		}
		return this.percentum(v1, v2);
	}

	public List list(int start, int end, int step) {
		List list = new ArrayList();
		list.add(start);
		while ((start += step) <= end) {
			list.add(start);
		}
		return list;
	}

	public List preList(Stack<OperatorData> para) {
		int start = Integer.parseInt(para.pop().value.toString());
		int end = Integer.parseInt(para.pop().value.toString());
		int step = 1;
		if (!para.isEmpty()) {
			step = Integer.parseInt(para.pop().value.toString());
		}
		return this.list(start, end, step);
	}

}
