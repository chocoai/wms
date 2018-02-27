package com.xyy.expression;

import com.xyy.expression.computer.AddComp;
import com.xyy.expression.computer.AndComp;
import com.xyy.expression.computer.DivisionComp;
import com.xyy.expression.computer.EqualComp;
import com.xyy.expression.computer.GreaterEqualComp;
import com.xyy.expression.computer.GreaterThanComp;
import com.xyy.expression.computer.LessEqualComp;
import com.xyy.expression.computer.LessThanComp;
import com.xyy.expression.computer.ModComp;
import com.xyy.expression.computer.MultiplyComp;
import com.xyy.expression.computer.NotComp;
import com.xyy.expression.computer.NotEqualComp;
import com.xyy.expression.computer.OrComp;
import com.xyy.expression.computer.SubtractComp;

/**
 * 操作数类，用于记录操作数
 * 
 * @author Evan
 * 
 */
public class OperatorData {
	public Class clazz;// 对象类型
	public Object value;// 对象值

	private Object getValue(Object od) {
		Object result = od;
		while (result.getClass() == OperatorData.class) {
			result = ((OperatorData) result).value;
		}
		return result;
	}

	public OperatorData(Class clazz, Object value) {
		super();
		if (value == null) {
			this.clazz = NullRefObject.class;
			this.value = null;
		} else {
			if (value.getClass() != OperatorData.class) {
				this.clazz = clazz;
				this.value = value;
			} else {
				Object v = this.getValue(value);
				this.clazz = v.getClass();
				this.value = value;
			}
		}
	}

	public OperatorData(Object value) {
		if (value != null) {
			Object v = this.getValue(value);
			this.clazz = v.getClass();
			this.value = value;
		} else {
			this.clazz = NullRefObject.class;
			this.value = null;
		}
	}

	@Override
	public int hashCode() {
		return this.clazz.hashCode() + this.value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != OperatorData.class) {
			return false;
		}
		OperatorData od = (OperatorData) obj;
		OperatorData result = this.eq(od);
		if (result.clazz == Boolean.class || result.clazz == boolean.class) {
			return (Boolean) result.value;
		}
		return false;
	}

	/*
	 * 数学计算
	 */
	public OperatorData add(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return AddComp.add(left, right);
	}

	public OperatorData sub(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return SubtractComp.subtract(left, right);
	}

	public OperatorData mul(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return MultiplyComp.multiply(left, right);
	}

	public OperatorData div(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return DivisionComp.division(left, right);
	}

	public OperatorData mod(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return ModComp.mod(left, right);

	}

	/*
	 * 比较计算
	 */

	public OperatorData gt(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return GreaterThanComp.greaterThan(left, right);

	}

	public OperatorData ge(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return GreaterEqualComp.greaterEqual(left, right);

	}

	public OperatorData lt(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return LessThanComp.lessThan(left, right);

	}

	public OperatorData le(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return LessEqualComp.lessThan(left, right);

	}

	public OperatorData eq(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return EqualComp.equal(left, right);
	}

	public OperatorData neq(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return NotEqualComp.notEqual(left, right);
	}

	/*
	 * 逻辑运算
	 */
	public OperatorData and(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return AndComp.and(left, right);
	}

	public OperatorData or(OperatorData od) {
		OperatorData left = this;
		OperatorData right = od;
		return OrComp.or(left, right);
	}

	public OperatorData not() {
		OperatorData right = this;
		return NotComp.not(right);
	}

}
