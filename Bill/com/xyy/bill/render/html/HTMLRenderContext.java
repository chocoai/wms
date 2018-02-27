package com.xyy.bill.render.html;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyy.expression.Context;
import com.xyy.util.StringUtil;

/**
 * 环境变量池
 * @author evan
 *
 */
public class HTMLRenderContext implements Context {
	Map<String, Object> _env = new HashMap<>();//环境参数池,外部环境触发
	
	public boolean containsName(String name) {
		return this._env.containsKey(name.toLowerCase());
	}
	
	/**
	 * 解析JSONObject
	 * @param name
	 * @return
	 */
	public JSONObject getJSONObject(String name){
		String json=this.getString(name);
		if(StringUtil.isEmpty(json) || !json.startsWith("{")){
			return null;
		}else{
			try {
				return 	JSONObject.parseObject(json);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	/**
	 * 解析JSONArray
	 * @param name
	 * @return
	 */
	public JSONArray getJSONArray(String name){
		String json=this.getString(name);
		if(StringUtil.isEmpty(json) || !json.startsWith("[")){
			return null;
		}else{
			try {
				return 	JSONArray.parseArray(json);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	// 获取环境变量值
	public Object get(String name) {
		return this._env.get(name.toLowerCase());
	}

	public String getString(String name) {
		Object value = this._env.get(name.toLowerCase());
		if (value == null) {
			return null;
		}
		return value.toString();
	}

	public Integer getInteger(String name) {
		Object value = this._env.get(name.toLowerCase());
		if (value == null) {
			return null;
		} else if (value.getClass() == Integer.class || value.getClass() == int.class) {
			return (Integer) value;
		} else {
			try {
				return Integer.parseInt(value.toString());
			} catch (Exception ex) {
				return null;

			}

		}
	}

	public Long getLong(String name) {
		Object value = this._env.get(name.toLowerCase());
		if (value == null) {
			return null;
		} else if (value.getClass() == Long.class || value.getClass() == long.class) {
			return (Long) value;
		} else {
			try {
				return Long.parseLong(value.toString());
			} catch (Exception ex) {
				return null;

			}

		}
	}

	public BigDecimal getDecimal(String name) {
		Object value = this._env.get(name.toLowerCase());
		if (value == null) {
			return null;
		} else if (value.getClass() == BigDecimal.class) {
			return (BigDecimal) value;
		} else {
			try {
				return new BigDecimal(value.toString());
			} catch (Exception ex) {
				return null;
			}
		}
	}

	public Boolean getBoolean(String name) {
		Object value = this._env.get(name);
		if (value == null) {
			return false;
		} else if (value.equals("true")) {
			return true;
		} else if (value.equals("false")) {
			return false;
		} else if (value.getClass() == boolean.class || value.getClass() == Boolean.class) {
			return (Boolean) value;
		} else {
			return false;
		}
	}

	// 设置对应的环境变量
	public void set(String name, Object value) {
		this._env.put(name.toLowerCase(), value);
	}

	// 删除环境变量
	public void remove(String name) {
		this._env.remove(name.toLowerCase());
	}

	// 清空环境变量
	public void clear() {
		this._env.clear();
	}

	/**
	 * 输出环境变量中的值，注意，对于私有变量“以__”开头的变量，不输出
	 * 
	 * @return
	 */
	public JSONObject toJSONSObject() {
		JSONObject result = new JSONObject();
		for (String name : this._env.keySet()) {
			if (name.startsWith("__")) {
				continue;
			}
			result.put(name, this._env.get(name));
		}
		return result;
	}

	@Override
	public Object getValue(String name) {
		return this._env.get(name.toLowerCase());
	}

	@Override
	public void setVariant(String name, Object value) {
		this._env.put(name.toLowerCase(), value);
	}

	@Override
	public void removeVaraint(String name) {
		this._env.remove(name.toLowerCase());

	}

}
