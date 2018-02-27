package com.xyy.expression;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
/**
 * 操作符对象
 * @author evan
 *
 */
@XmlComponent(tag = "operator", type = Operator.class)
public class Operator {
	private String ope;
	private int level;

	public Operator(String ope, int level) {
		super();
		this.ope = ope;
		this.level = level;
	}

	public Operator() {

	}

	public Token getToken() {
		return new Token(this.ope, TokenType.TOKEN_TYPE_OPE);
	}

	@XmlAttribute(name = "ope", tag = "name", type = String.class)
	public String getOpe() {
		return ope;
	}

	public void setOpe(String ope) {
		this.ope = ope;
	}

	@XmlAttribute(name = "level", tag = "priority", type = int.class)
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
