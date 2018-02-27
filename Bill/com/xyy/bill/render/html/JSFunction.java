package com.xyy.bill.render.html;

import java.util.ArrayList;
import java.util.List;

public class JSFunction {
	private JSWriter jsWriter;
	private String name;// 函数名称
	private List<String> params = new ArrayList<>();

	public JSFunction(String name, String... params) {
		super();
		this.name = name;
		this.jsWriter = new JSWriter();
		if (params != null) {
			for (String param : params) {
				this.params.add(param);
			}
		}
	}
	
	public JSFunction jswrite(JSWrite write) {
		write.write(this.jsWriter);
		return this;
	}
	
	
	public JSWriter append(String jsCode) {
		this.jsWriter.write(jsCode);
		return this.jsWriter;
	}

	public JSFunction() {
		super();
		this.jsWriter = new JSWriter();
	}

	public JSWriter getJsWriter() {
		return jsWriter;
	}

	public void setJsWriter(JSWriter jsWriter) {
		this.jsWriter = jsWriter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	/**
	 * 输出不带函数名形式的函数
	 * function(params){
	 * 
	 * }
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("function(");
		for (String s : this.params) {
			sb.append(s).append(",");
		}
		if (sb.lastIndexOf(",") == sb.length() - 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(")").append("{").append(this.jsWriter.toString()).append("}");

		return sb.toString();
	}

	
	/**
	 * 如果hasFunName=true输出标准的函数形式
	 * 	    function(params){
	 * 
	 * 		}
	 * @param hasFunName
	 * @return
	 */
	public String toString(Boolean hasFunName) {
		if (!hasFunName) {
			return this.toString();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("function ").append(this.name).append("(");
			for (String s : this.params) {
				sb.append(s).append(",");
			}
			if (sb.lastIndexOf(",") == sb.length() - 1) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append(")").append("{").append(this.jsWriter.toString()).append("}");

			return sb.toString();
		}
	}

	/**
	 * 仅输出函数体
	 * @return
	 */
	public String toBareString() {
		return this.jsWriter.toString();
	}

	
	public static interface JSWrite {
		public void write(JSWriter write);
	}

	
}
