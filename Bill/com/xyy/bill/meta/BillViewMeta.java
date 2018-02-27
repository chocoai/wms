package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

/**
 * 单据视图定义
 * 
 * @author evan
 *
 */
@XmlComponent(tag = "BillViewMeta", type = BillHeadMeta.class)
public class BillViewMeta {
	private String key;
	private String caption;
	private String version;
	private List<Parameter> parameters = new ArrayList<>();// 视图参数
	private List<BillBodyMeta> billBodyMetas=new ArrayList<BillBodyMeta>();

	public BillViewMeta() {
		super();
	}

	@XmlAttribute(name = "key", tag = "Key", type = String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "caption", tag = "Caption", type = String.class)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@XmlAttribute(name = "version", tag = "Version", type = String.class)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@XmlList(name = "parameters", tag = "Parameters", subTag = "Parameter", type = Parameter.class)
	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	@XmlList(name="billBodyMetas",tag="BillBody",subTag="BillBodyMeta",type=BillBodyMeta.class)
	public List<BillBodyMeta> getBillBodyMetas() {
		return billBodyMetas;
	}

	public void setBillBodyMetas(List<BillBodyMeta> billBodyMetas) {
		this.billBodyMetas = billBodyMetas;
	}

}
