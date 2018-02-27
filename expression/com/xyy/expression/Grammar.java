package com.xyy.expression;

import java.util.regex.Pattern;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
/**
 * 语法规则类
 * @author evan
 *
 */
@XmlComponent(tag = "grammar", type = Grammar.class)
public class Grammar {
	private String name;
	private String pattern;
	private int priority;
	private TokenType tokenType;
	private boolean preProcess;
	private String symPrefix;

	public Grammar() {
	}

	@XmlAttribute(name = "name", tag = "name", type = String.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "pattern", tag = "pattern", type = String.class, cdata = true)
	public String getPattern() {
		return pattern;
	}

	private Pattern curPattern;

	public Pattern buildPattern() {
		if (curPattern == null) {
			synchronized (this) {
				if (curPattern == null) {
					this.curPattern = Pattern.compile(this.getPattern());
				}
			}
		}
		return this.curPattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@XmlAttribute(name = "priority", tag = "priority", type = int.class)
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@XmlAttribute(name = "tokenType", tag = "tokenType", type = TokenType.class)
	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	@XmlAttribute(name = "preProcess", tag = "preProcess", type = boolean.class)
	public boolean getPreProcess() {
		return preProcess;
	}

	public void setPreProcess(boolean preProcess) {
		this.preProcess = preProcess;
	}

	@XmlAttribute(name = "symPrefix", tag = "symPrefix", type = String.class)
	public String getSymPrefix() {
		return symPrefix;
	}

	public void setSymPrefix(String symPrefix) {
		this.symPrefix = symPrefix;
	}

}
